package com.pecake.paper.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.pecake.paper.activities.ActivityProfile;
import com.pecake.paper.R;
import com.pecake.paper.activities.ActivityPostDetail;
import com.pecake.paper.models.Post;
import com.pecake.paper.viewholder.PostViewHolder;

import java.util.ArrayList;
import java.util.List;

public class BookMarkPostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private Context context;
    private List<Post> bookMarkLists;
    private List<String> bookKeyList;
    private List<String> uidArray = new ArrayList<>();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public BookMarkPostAdapter(Context context, List<Post> bookMarkLists, List<String> bookKeyList) {

        this.context = context;
        this.bookMarkLists = bookMarkLists;
        this.bookKeyList = bookKeyList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {
        if (bookMarkLists.get(position).bookMarks.contains(getUid())) {
            holder.ivBookMark.setImageResource(R.drawable.ic_green_full_bookmark);
        } else {
            holder.ivBookMark.setImageResource(R.drawable.ic_green_border_bookmark);
        }
        if (bookMarkLists.get(position).stars.containsKey(getUid())) {
            holder.ivStar.setImageResource(R.drawable.love_full_24);
        } else {
            holder.ivStar.setImageResource(R.drawable.love_border_24);
        }
        holder.userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityProfile.class);
                i.putExtra("user_id", bookMarkLists.get(position).getUid());
                context.startActivity(i);
            }
        });
        holder.tvStarCount.setText(String.valueOf(bookMarkLists.get(position).starCount));
        holder.ivStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference globalPostRef = dbRef.child("posts").child(bookKeyList.get(position));
                DatabaseReference userPostRef = dbRef.child("user-posts").child(getUid()).child(bookKeyList.get(position));
                onStarClicked(globalPostRef);
                onStarClicked(userPostRef);
                notifyItemChanged(position);

            }
        });
        holder.tvPostCategory.setText(bookMarkLists.get(position).getCategory());
        holder.ivBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference globalPostRef = dbRef.child("posts").child(bookKeyList.get(position));
                DatabaseReference userPostRef = dbRef.child("user-posts").child(getUid()).child(bookKeyList.get(position));
                onBookMarkClicked(globalPostRef);
                onBookMarkClicked(userPostRef);
                notifyItemRangeRemoved(0,bookMarkLists.size());
            }
        });
        holder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uidArray.isEmpty()) {
                    uidArray.clear();
                }
                uidArray = bookMarkLists.get(position).bookMarks;
                Bundle b = new Bundle();
                b.putStringArrayList("uids", (ArrayList<String>) uidArray);
                b.putString("uid", bookMarkLists.get(position).getUid());
                b.putString("post_key", bookKeyList.get(position));
                b.putString("user_choice", "posts");
                b.putString("category", bookMarkLists.get(position).getCategory());
                Intent postIntent = new Intent(context, ActivityPostDetail.class);
                postIntent.putExtras(b);
                context.startActivity(postIntent);
            }
        });
        if (bookMarkLists.get(position).getPostPhotoUri() == null || bookMarkLists.get(position).getPostPhotoUri().equals("")) {

            holder.ivPhoto.setVisibility(View.GONE);
            holder.tvTitle.setBackgroundColor(0);
            holder.tvTitle.setText(bookMarkLists.get(position).getTitle());
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.tvTitle.setBackgroundColor(context.getResources().getColor(R.color.tranparent));
            holder.ivPhoto.setVisibility(View.VISIBLE);
            Glide.with(context).load(bookMarkLists.get(position).getPostPhotoUri()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.pgImage.setProgress(View.VISIBLE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.pgImage.setProgress(View.GONE);
                    return false;
                }
            }).into(holder.ivPhoto);
        }

        Glide.with(context).load(bookMarkLists.get(position).getUserLogoUri()).into(holder.ivUserLogo);
        holder.tvTitle.setText(bookMarkLists.get(position).getTitle());
        holder.tvContent.setText(bookMarkLists.get(position).getContent());
        holder.tvDate.setText(bookMarkLists.get(position).getDate());
        holder.tvUserName.setText(bookMarkLists.get(position).getName());
        holder.tvViewer.setText(bookMarkLists.get(position).getCountViewer() > 1 ? bookMarkLists.get(position).getCountViewer() + " views" : bookMarkLists.get(position).getCountViewer() + " view");

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int getItemCount() {
        return bookMarkLists.size();
    }

    private void onBookMarkClicked(DatabaseReference postRef) {

        postRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);

                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p.bookMarks.contains(getUid())) {
                    // Unstar the post and remove self from stars
                    p.bookMarks.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.bookMarks.add(getUid());
                }
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });
    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });
    }
}
