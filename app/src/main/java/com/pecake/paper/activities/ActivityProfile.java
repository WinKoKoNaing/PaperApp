package com.pecake.paper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pecake.paper.R;
import com.pecake.paper.adapters.CategoryAdapter;
import com.pecake.paper.helpers.PeCake;
import com.pecake.paper.models.Category;
import com.pecake.paper.models.User;
import com.pecake.paper.userCategoryFragment.CategoryDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ActivityProfile extends AppCompatActivity {
    // Views
    private LinearLayout ivAdd;
    private RecyclerView rvCategoryList;
    private LinearLayout linearPhone, linearAddress, mainCard, shortBioLayout;
    private ImageView ivProPic;
    private TextView tvUserName, tvShortBio, tvPhoneNumber, tvAddress, tvBio, tvPostCount, tvCategoryCount;
    private Toolbar toolbar;

    // Adapters
    CategoryAdapter adapter;

    // Arrays
    List<Category> categoryList = new ArrayList<>();

    // FireBase
    DatabaseReference profileRef, categoryRef;
    DatabaseReference postProfileRef;

    // Others
    String USER_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Night mode
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref
                .getBoolean("night_mode", false);

        if (switchPref) {

            setTheme(R.style.ActivityTheme_Primary_Base_Dark);

        } else
            setTheme(R.style.AppThemeSecondary);


        setContentView(R.layout.activity_profile);


        initUI();

        if (switchPref)
            nightModeOn();
        else
            nightModeOff();

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Profile");

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // FireBase
        USER_ID = getIntent().getExtras().getString(PeCake.USER_ID);
        profileRef = FirebaseDatabase.getInstance().getReference("users").child(USER_ID);
        categoryRef = FirebaseDatabase.getInstance().getReference("categories");
        postProfileRef = FirebaseDatabase.getInstance().getReference("posts");

        checkUser();

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryDialogFragment cdf;
                FragmentManager fm;
                fm = getSupportFragmentManager();
                cdf = new CategoryDialogFragment();
                cdf.show(fm, "CategoryFragment");
            }
        });

        rvCategoryList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategoryList.setNestedScrollingEnabled(false);

        readProfileData();
        readCategory();
        readPost();
        tvPostCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), ActivityUserPosts.class);
                i.putExtra(PeCake.USER_ID, USER_ID);
                startActivity(i);
            }
        });
        tvCategoryCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tvBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityProfile.this, ProfileEditActivity.class);
                i.putExtra(PeCake.USER_ID, USER_ID);
                startActivity(i);
            }
        });
    }


    // Override Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        MenuItem edit = menu.findItem(R.id.action_profile_edit);
        if (!USER_ID.equals(getUid())) {
            edit.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile_edit:
                Intent i = new Intent(ActivityProfile.this, ProfileEditActivity.class);
                i.putExtra(PeCake.USER_ID, USER_ID);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // Theme NightMode
    public void nightModeOn() {

        shortBioLayout.setBackgroundColor(getResources().getColor(R.color.backCardColor));
        tvPhoneNumber.setTextColor(getResources().getColor(R.color.white));
        tvAddress.setTextColor(getResources().getColor(R.color.white));
        tvShortBio.setTextColor(getResources().getColor(R.color.white));
        mainCard.setBackgroundColor(getResources().getColor(R.color.backCardColor));

    }

    public void nightModeOff() {

        shortBioLayout.setBackgroundColor(getResources().getColor(R.color.white));
        tvPhoneNumber.setTextColor(getResources().getColor(R.color.black));
        tvAddress.setTextColor(getResources().getColor(R.color.black));
        tvShortBio.setTextColor(getResources().getColor(R.color.black));
        mainCard.setBackgroundColor(getResources().getColor(R.color.white));

    }


    // FireBase Read Data Methods
    private void readCategory() {
        categoryRef.orderByChild("userId").equalTo(USER_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!categoryList.isEmpty()) {
                    categoryList.clear();
                }
                tvCategoryCount.setText(String.valueOf(dataSnapshot.getChildrenCount()) + " Categories");
                for (DataSnapshot s : dataSnapshot.getChildren()) {

                    Category category = s.getValue(Category.class);
                    categoryList.add(category);
                }
                adapter = new CategoryAdapter(getBaseContext(), categoryList);
                rvCategoryList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readProfileData() {
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (!user.getName().isEmpty()) {
                        tvUserName.setText(user.getName());
                    } else {
                        tvUserName.setVisibility(View.GONE);
                    }
                    if (!user.getAddress().isEmpty()) {
                        tvAddress.setText(user.getAddress());
                    } else {
                        linearAddress.setVisibility(View.GONE);
                    }
                    if (!user.getPhone().isEmpty()) {
                        tvPhoneNumber.setText(user.getPhone());
                    } else {
                        linearPhone.setVisibility(View.GONE);
                    }
                    if (!user.getPhotoUri().isEmpty()) {
                        Glide.with(ActivityProfile.this).load(user.getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(ivProPic);
//                        Picasso.get().load(user.getPhotoUri()).transform(new CircleTransform()).into(ivProPic);
                    }
                    if (!user.getBio().isEmpty()) {
                        tvShortBio.setText(user.getBio());
                    } else {
                        tvBio.setText("add short bio");
                        tvShortBio.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void readPost() {
        postProfileRef.orderByChild("uid").equalTo(USER_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvPostCount.setText(String.valueOf(dataSnapshot.getChildrenCount()) + " Posts");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // Other Methods
    public void initUI() {

        toolbar = findViewById(R.id.toolbar);
        ivProPic = findViewById(R.id.ivProfilePic);
        tvUserName = findViewById(R.id.tvUserName);
        shortBioLayout = findViewById(R.id.shortBioLayout);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvShortBio = findViewById(R.id.tvShortBio);
        linearAddress = findViewById(R.id.linearAddress);
        linearPhone = findViewById(R.id.linearPhone);
        tvBio = findViewById(R.id.tvBio);
        tvPostCount = findViewById(R.id.tvPostText);
        tvCategoryCount = findViewById(R.id.tvCategoryText);
        rvCategoryList = findViewById(R.id.rvCategoryList);
        ivAdd = findViewById(R.id.ivAdd);
        mainCard = findViewById(R.id.mainCard);

    }

    public void checkUser() {
        if (!USER_ID.equals(getUid())) {
            tvBio.setVisibility(View.GONE);
            ivAdd.setVisibility(View.GONE);
        } else {
            ivAdd.setVisibility(View.VISIBLE);
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
