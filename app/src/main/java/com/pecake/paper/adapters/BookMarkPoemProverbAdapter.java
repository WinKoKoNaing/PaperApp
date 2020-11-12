package com.pecake.paper.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.pecake.paper.R;
import com.pecake.paper.activities.ActivityFeedDetail;
import com.pecake.paper.activities.ActivityProfile;
import com.pecake.paper.models.Post;
import com.pecake.paper.viewholder.PostViewHolder;

import java.util.ArrayList;
import java.util.List;

public class BookMarkPoemProverbAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private List<Post> lists;
    private Context context;
    List<String> keyLists;
    List<String> uidArray = new ArrayList<>();
    String userChoice;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public BookMarkPoemProverbAdapter(Context context, List<Post> postList, List<String> keyLists, String userChoice) {
        this.userChoice = userChoice;
        lists = postList;
        this.context = context;
        this.keyLists = keyLists;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poem_row, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, final int position) {
        if (lists.get(position).bookMarks.contains(getUid())) {
            holder.ivBookMark.setImageResource(R.drawable.ic_green_full_bookmark);
        } else {

            holder.ivBookMark.setImageResource(R.drawable.ic_green_border_bookmark);
        }
        if (lists.get(position).stars.containsKey(getUid())) {
            holder.ivStar.setImageResource(R.drawable.love_full_24);
        } else {
            holder.ivStar.setImageResource(R.drawable.love_border_24);
        }
        holder.ivStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        DatabaseReference globalPostRef = dbRef.child("posts").child(postKey);
//                DatabaseReference userPostRef = postRef.child(keyLists.get(position));
//                        onStarClicked(globalPostRef);
//                onStarClicked(postRef);
            }
        });
        if (lists.get(position).getTitle() != null && !lists.get(position).getTitle().equals("")) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(lists.get(position).getTitle());
            holder.tvContent.setGravity(Gravity.CENTER_HORIZONTAL);
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }
        holder.tvStarCount.setText(String.valueOf(lists.get(position).starCount));
        holder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uidArray.isEmpty()) {
                    uidArray.clear();
                }

                uidArray = lists.get(position).bookMarks;
                Bundle b = new Bundle();
                b.putStringArrayList("uids", (ArrayList<String>) uidArray);
                b.putString("uid", lists.get(position).getUid());
                b.putString("post_key", keyLists.get(position));
                Log.d("WKKN", lists.get(position).userChoice + "");
                b.putInt("user_choice", lists.get(position).userChoice);
                Toast.makeText(context, userChoice, Toast.LENGTH_SHORT).show();
                b.putString("category", lists.get(position).getCategory());
                Intent postIntent = new Intent(context, ActivityFeedDetail.class);
                postIntent.putExtras(b);
                context.startActivity(postIntent);
            }
        });

        holder.ivBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        DatabaseReference globalPostRef = dbRef.child("posts").child(postKey);
                DatabaseReference userPostRef = dbRef.child(userChoice).child(keyLists.get(position));
//                        onBookMarkClicked(globalPostRef);
                onBookMarkClicked(userPostRef);


            }
        });
        holder.userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityProfile.class);
                i.putExtra("user_id", lists.get(position).getUid());
                context.startActivity(i);
            }
        });
//        holder.tvPostCategory.setText(lists.get(position).getCategory());
        Glide.with(context).load(lists.get(position).getUserLogoUri()).into(holder.ivUserLogo);


        holder.tvContent.setText(lists.get(position).getContent());

        holder.tvDate.setText(lists.get(position).getDate());
        holder.tvUserName.setText(lists.get(position).getName());

        holder.tvViewer.setText(lists.get(position).getCountViewer() > 1 ? lists.get(position).getCountViewer() + " views" : lists.get(position).getCountViewer() + " view");


    }

    @Override
    public int getItemCount() {
        return lists.size();
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

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
