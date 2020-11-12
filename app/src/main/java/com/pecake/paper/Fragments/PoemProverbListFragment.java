package com.pecake.paper.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.pecake.paper.R;
import com.pecake.paper.activities.ActivityFeedDetail;
import com.pecake.paper.activities.ActivityProfile;
import com.pecake.paper.models.Post;
import com.pecake.paper.viewholder.PostViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class PoemProverbListFragment extends Fragment {

    RecyclerView rvPosts;
    DatabaseReference dbRef;
    LinearLayoutManager layoutManager;
    List<String> uidArray = new ArrayList<>();
    View rootView;
    FirebaseRecyclerAdapter<Post, PostViewHolder> adapter;
    int pos = 0;
    int i = 0;
    String userChoice;
    int fragChange = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_poem_proverb, container, false);
        dbRef = FirebaseDatabase.getInstance().getReference();
        rvPosts = rootView.findViewById(R.id.rvPostList);
        rvPosts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        rvPosts.setLayoutManager(layoutManager);
        rvPosts.setSaveFromParentEnabled(true);

        layoutManager.onSaveInstanceState();
        rvPosts.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

                fragChange = rvPosts.getLayoutManager().getPosition(view);
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });
        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (i > 0) {

                    if (pos != 0) {
                        recyclerView.smoothScrollToPosition(pos - 1);
                    } else {
                        savePosition(0);
                    }

                    i--;
                }

            }
        });


        return rootView;
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentTime() {

        Calendar c = Calendar.getInstance();
        return new SimpleDateFormat("K:mm a").format(c.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentDate() {

        Date currentDate = Calendar.getInstance().getTime();
        return new SimpleDateFormat("dd.MM.yyyy").format(currentDate);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Query postsQuery = getQuery(dbRef);

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poem_row, parent, false);
                return new PostViewHolder(v);
            }


            @SuppressLint("CheckResult")
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
//                        DatabaseReference globalPostRef = dbRef.child("posts").child(postKey);
                        DatabaseReference userPostRef = postRef.child(postKey);
//                        onStarClicked(globalPostRef);
                        onStarClicked(postRef);
                    }
                });
                if (model.getTitle() != null && !model.getTitle().equals("")) {
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(model.getTitle());
                    holder.tvContent.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                }
                holder.tvStarCount.setText(String.valueOf(model.starCount));
                holder.header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!uidArray.isEmpty()) {
                            uidArray.clear();
                        }

                        pos = position;
                        uidArray = model.bookMarks;
                        Bundle b = new Bundle();
                        b.putStringArrayList("uids", (ArrayList<String>) uidArray);
                        b.putString("uid", model.getUid());
                        b.putString("post_key", postKey);
                        Log.d("WKKN", model.userChoice + "");
                        b.putInt("user_choice", model.userChoice);
                        Toast.makeText(getContext(), userChoice, Toast.LENGTH_SHORT).show();
                        b.putString("category", model.getCategory());
                        Intent postIntent = new Intent(getContext(), ActivityFeedDetail.class);
                        postIntent.putExtras(b);
                        startActivity(postIntent);
                    }
                });

                holder.ivBookMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        DatabaseReference globalPostRef = dbRef.child("posts").child(postKey);
                        DatabaseReference userPostRef = dbRef.child("user-poem").child(getUid()).child(postKey);
//                        onBookMarkClicked(globalPostRef);
                        onBookMarkClicked(postRef);


                    }
                });
                holder.userInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), ActivityProfile.class);
                        i.putExtra("user_id", model.getUid());
                        startActivity(i);
                    }
                });
//                holder.tvPostCategory.setText(model.getCategory());
                Glide.with(getContext()).load(model.getUserLogoUri()).apply(RequestOptions.circleCropTransform()).into(holder.ivUserLogo);


                holder.tvContent.setText(model.getContent());

                holder.tvDate.setText(model.getTime());
                holder.tvUserName.setText(model.getName());

                holder.tvViewer.setText(model.getCountViewer() > 1 ? model.getCountViewer() + " views" : model.getCountViewer() + " view");

            }

        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rvPosts.scrollToPosition(adapter.getItemCount());
            }
        });
        rvPosts.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
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

    @Override
    public void onResume() {
        super.onResume();
        i = 2;

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        i = 2;
        pos = readFragChangePosition();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        savePosition(fragChange);
    }

    public void savePosition(int fragChange) {
        SharedPreferences pref = getContext().getSharedPreferences("positions", Context.MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("fragChange", fragChange);
        editor.putInt("currentPos", pos);
        editor.commit();
    }

    public int readFragChangePosition() {
        SharedPreferences pref = getContext().getSharedPreferences("positions", Context.MODE_PRIVATE);
        return pref.getInt("fragChange", 0);
    }

    public int readCurrentChangePosition() {
        SharedPreferences pref = getContext().getSharedPreferences("positions", Context.MODE_PRIVATE);
        return pref.getInt("currentPos", 0);
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

    public abstract Query getQuery(DatabaseReference databaseReference);

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }
}
