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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.pecake.paper.R;
import com.pecake.paper.activities.ActivityPostDetail;
import com.pecake.paper.activities.ActivityProfile;
import com.pecake.paper.models.Post;
import com.pecake.paper.viewholder.PostViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter {
    public PostAdapter() {
    }

    List<String> uidArray = new ArrayList<>();
    private static FirebaseRecyclerAdapter<Post, PostViewHolder> adapter;
    Context context;

    public PostAdapter(final Context context, final DatabaseReference dbRef, RecyclerView rvPosts) {
        this.context = context;
        Query postsQuery = dbRef;
        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = null;

                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_row, parent, false);

                return new PostViewHolder(v);
            }


            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder holder, final int position, @NonNull final Post model) {
                final DatabaseReference postRef = getRef(position);
                final String postKey = postRef.getKey();
                if (model.bookMarks.contains(getUid())) {
                    holder.ivBookMark.setImageResource(R.drawable.ic_green_full_bookmark);
                } else {
                    holder.ivBookMark.setImageResource(R.drawable.ic_green_border_bookmark);
                }
                if (model.stars.containsKey(getUid())) {
                    holder.ivStar.setImageResource(R.drawable.love_full_24);
                } else {
                    holder.ivStar.setImageResource(R.drawable.love_border_24);
                }
                holder.ivStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onStarClicked(postRef);
                    }
                });
                holder.tvStarCount.setText(String.valueOf(model.starCount));
                holder.header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!uidArray.isEmpty()) {
                            uidArray.clear();
                        }
//                        pos = position;
                        uidArray = model.bookMarks;
                        Toast.makeText(context, "header", Toast.LENGTH_SHORT).show();
                        Bundle b = new Bundle();
                        b.putStringArrayList("uids", (ArrayList<String>) uidArray);
                        b.putString("uid", model.getUid());
                        b.putString("post_key", postKey);
                        b.putString("category", model.getCategory());
                        Intent postIntent = new Intent(context, ActivityPostDetail.class);
                        postIntent.putExtras(b);
                        context.startActivity(postIntent);
                    }
                });
                if (model.getPostPhotoUri() == null || model.getPostPhotoUri().equals("")) {

                    holder.ivPhoto.setVisibility(View.GONE);
                    holder.tvTitle.setBackgroundColor(0);
                    holder.tvTitle.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                } else {
                    holder.tvTitle.setBackgroundColor(context.getResources().getColor(R.color.tranparent));
                    holder.ivPhoto.setVisibility(View.VISIBLE);
                    Glide.with(context).load(model.getPostPhotoUri()).listener(new RequestListener<Drawable>() {
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
                holder.ivBookMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference globalPostRef = dbRef.child("posts").child(postKey);
                        DatabaseReference userPostRef = dbRef.child("user-posts");
                        onBookMarkClicked(postRef);


                    }
                });
                holder.userInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ActivityProfile.class);
                        i.putExtra("user_id", model.getUid());
                        context.startActivity(i);
                    }
                });
                holder.tvPostCategory.setText(model.getCategory());
                Glide.with(context).load(model.getUserLogoUri()).into(holder.ivUserLogo);
                holder.tvTitle.setText(model.getTitle());
                holder.tvContent.setText(model.getContent());
                holder.tvDate.setText(model.getDate());
                holder.tvUserName.setText(model.getName());

                holder.tvViewer.setText(model.getCountViewer() > 1 ? model.getCountViewer() + " views" : model.getCountViewer() + " view");

            }

        };
        rvPosts.setAdapter(adapter);
    }

    public static void onStartListing() {
        if (adapter != null) {
            adapter.startListening();
        }
    }

    public static void onStopListing() {
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
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

    private void onBookMarkClicked(DatabaseReference postRef) {

        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
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
//    public abstract Query getQuery(DatabaseReference databaseReference, RecyclerView rvPost);
}
