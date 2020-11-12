package com.pecake.paper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pecake.paper.R;
import com.pecake.paper.Util.LoadingFragment;
import com.pecake.paper.helpers.InitFireBase;
import com.pecake.paper.models.Post;

import java.util.ArrayList;

public class ActivityPostCategoryDetail extends AppCompatActivity {

    ImageView ivAppbarPhoto, ivStar;
    Uri currentUri;
    TextView tvContent, tvShowTitle, tvViewCount, tvDate, tvStartCount;
    Toolbar toolbar;
    String postKey, userChoice;
    DatabaseReference postRef;
    Post post;
    FirebaseUser user;
    String currentUser;
    Menu menu;
    ArrayList<String> currentBookmark = new ArrayList<>();
    CoordinatorLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail_activity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InitFireBase.init();
        user = InitFireBase.getCurrentUser();

        ivAppbarPhoto = findViewById(R.id.ivAppBarPhoto);
        tvContent = findViewById(R.id.tvShowContent);
        myLayout = findViewById(R.id.myLayout);





        //Night mode
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref
                .getBoolean("night_mode", false);

        if(switchPref){

            nightModeOn();
        }
        else{

            nightModeOff();


        }



        tvShowTitle = findViewById(R.id.tvShowTitle);
        tvViewCount = findViewById(R.id.tvViewCount);
        tvDate = findViewById(R.id.tvPostDetailDate);
        tvStartCount = findViewById(R.id.tvStarCount);
        ivStar = findViewById(R.id.ivStartCount);
        Bundle b = getIntent().getExtras();
        postKey = b.getString("post_key");
        currentUser = b.getString("uid");
        userChoice = b.getString("user_choice");
        currentBookmark = b.getStringArrayList("uids");
        getSupportActionBar().setTitle(b.getString("category"));
        postRef = InitFireBase.getDbRef().child(userChoice).child(postKey);
        onStarClicked(postRef);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            readPostDetail(postRef);
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
    }

    public void showPost(Post post) {
        if (post.stars.containsKey(getUid())) {
            ivStar.setImageResource(R.drawable.love_full_24);
        } else {
            ivStar.setImageResource(R.drawable.love_border_24);
        }
        tvViewCount.setText(post.getCountViewer() > 1 ? post.getCountViewer() + " views" : post.getCountViewer() + " view");
        tvDate.setText(post.getTime());
        tvStartCount.setText(String.valueOf(post.starCount));
        tvContent.setText(post.getContent());
        tvShowTitle.setText(post.getTitle());
        try{
            currentUri = Uri.parse(post.getPostPhotoUri());
            Glide.with(ActivityPostCategoryDetail.this).load(post.getPostPhotoUri()).into(ivAppbarPhoto);
        }catch (Exception e){

        }

    }

    private void onStarClicked(DatabaseReference postRef) {

        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);

                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p.viewers.contains(user.getUid())) {
                    // Unstar the post and remove self from stars
//                    p.countViewer = p.countViewer - 1;
//                    p.viewers.remove(user.getUid());
                } else {
                    // Star the post and add self to stars
                    p.countViewer = p.countViewer + 1;

                    p.viewers.add(user.getUid());
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

    public void readPostDetail(DatabaseReference db) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);
                showPost(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        getMenuInflater().inflate(R.menu.post_detail_activity, menu);
        final MenuItem bookMark = menu.findItem(R.id.action_post_bookmark);
        if (currentBookmark.contains(user.getUid())) {
            bookMark.setIcon(R.drawable.ic_white_full_bookmark);
        } else {
            bookMark.setIcon(R.drawable.ic_white_border_bookmark);
        }
        if (!currentUser.equals(user.getUid())) {
            MenuItem none = menu.findItem(R.id.action_none);

            none.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                finish();
                break;
            case R.id.action_post_bookmark:
                onBookMarkClicked(postRef);
                final MenuItem bookMark = menu.findItem(R.id.action_post_bookmark);
                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        post = dataSnapshot.getValue(Post.class);
                        if (post.bookMarks.contains(user.getUid())) {
                            bookMark.setIcon(R.drawable.ic_white_full_bookmark);
                        } else {
                            bookMark.setIcon(R.drawable.ic_white_border_bookmark);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case R.id.action_post_edit:
                Intent i = new Intent(ActivityPostCategoryDetail.this, ActivityEditCategoryPost.class);
                i.putExtra("post_key", postKey);
                startActivity(i);
                break;
            case R.id.action_post_delete:
                final LoadingFragment loadingFragment = new LoadingFragment(this);
                loadingFragment.loading("deleting ....");
                loadingFragment.show();
                postRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (currentUri!=null){
                            StorageReference postPhotoRef;
                            postPhotoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentUri.toString());
                            postPhotoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingFragment.hide();
                                    finish();
                                }
                            });
                        }
                    }
                });


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void onBookMarkClicked(DatabaseReference postRef) {

        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);

                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p.bookMarks.contains(user.getUid())) {
                    // Unstar the post and remove self from stars
                    p.bookMarks.remove(user.getUid());
                } else {
                    // Star the post and add self to stars
                    p.bookMarks.add(user.getUid());
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
    public void nightModeOn(){
        myLayout.setBackgroundColor(getResources().getColor(R.color.windowBack));
        tvContent.setBackgroundColor(getResources().getColor(R.color.windowBack));
        tvContent.setTextColor(getResources().getColor(R.color.white));
    }

    public void nightModeOff(){
        myLayout.setBackgroundColor(getResources().getColor(R.color.white));
        tvContent.setBackgroundColor(getResources().getColor(R.color.white));
        tvContent.setTextColor(getResources().getColor(R.color.black));
    }
}
