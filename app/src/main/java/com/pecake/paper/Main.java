package com.pecake.paper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pecake.paper.DrawerFragment.BookMark;
import com.pecake.paper.DrawerFragment.FaqFragment;
import com.pecake.paper.DrawerFragment.TopPostFragment;
import com.pecake.paper.Util.LoadingFragment;
import com.pecake.paper.activities.ActivityProfile;
import com.pecake.paper.activities.ActivitySetting;
import com.pecake.paper.bottomFragment.Category;
import com.pecake.paper.bottomFragment.Feed;
import com.pecake.paper.bottomFragment.HomeFragment;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    LoadingFragment loadingFragment = new LoadingFragment(this);
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    ImageView ivNavUserLogo;
    TextView tvProfile;
    NavigationView navigationView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    LinearLayout llProfile;
    boolean currentHomeFrag = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Night mode
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref
                .getBoolean("night_mode", false);

        if (switchPref) {

            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        } else {

            setTheme(R.style.AppTheme);


        }


        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        View havHeader = navigationView.getHeaderView(0);
        ivNavUserLogo = havHeader.findViewById(R.id.ivNavUserLogo);
        Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(ivNavUserLogo);
        tvProfile = havHeader.findViewById(R.id.tvProfile);
        tvProfile.setText(user.getDisplayName());
        llProfile = havHeader.findViewById(R.id.llProfile);


        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Main.this, ActivityProfile.class);
                i.putExtra("user_id", getUid());
                startActivity(i);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setTitle("Home");
        //start  bottom navigation
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container, new HomeFragment(), "HomeFragment").commit();
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        if (switchPref) {

            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.black));
        } else {

            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.white));


        }


        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment currentFrag = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        currentFrag = HomeFragment.newInstance();
                        toolbar.setTitle("Home");
                        currentHomeFrag = false;
                        break;
                    case R.id.action_bookmark:
                        currentFrag = new Feed();
                        toolbar.setTitle("Feed");
                        currentHomeFrag = true;
                        break;
                    case R.id.action_category:
                        currentFrag = new Category();
                        toolbar.setTitle("Category");
                        currentHomeFrag = true;
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, currentFrag).commit();

                return true;
            }
        });
    }

    // end bottom navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Fragment selectedFragment = null;
        switch (item.getItemId()) {

//            case R.id.drawer_profile:
//                Intent i = new Intent(Main.this, ActivityProfile.class);
//                i.putExtra("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                startActivity(i);
//                item.setCheckable(false);
//
//                break;
//            case R.id.mypost:
//                selectedFragment = new FragmentMyPost();
//                item.setCheckable(true);
//                toolbar.setTitle("My Post");
//                break;
            case R.id.drawer_home:
                selectedFragment = HomeFragment.newInstance();
                item.setChecked(true);
                toolbar.setTitle("Paper");
                bottomNavigationView.setVisibility(View.VISIBLE);
                bottomNavigationView.setSelectedItemId(R.id.action_home);
                currentHomeFrag = false;
                break;
            case R.id.action_nav_bookmar:
                selectedFragment = new BookMark();
                item.setCheckable(true);
                toolbar.setTitle("BookMark");
                bottomNavigationView.setVisibility(View.GONE);
                currentHomeFrag = true;
                break;
            case R.id.action_myTopPost:
                selectedFragment = new TopPostFragment();
                item.setCheckable(true);
                toolbar.setTitle("Top Posts");
                bottomNavigationView.setVisibility(View.GONE);
                currentHomeFrag = true;
                break;
            case R.id.setting:
                currentHomeFrag = true;
                Intent ii = new Intent(Main.this, ActivitySetting.class);
                startActivity(ii);
                finish();
                break;
            case R.id.faq:
                selectedFragment = FaqFragment.newInstance();
                item.setChecked(true);
                toolbar.setTitle("FAQs");
                bottomNavigationView.setVisibility(View.GONE);
                currentHomeFrag = true;
                break;
            case R.id.logout:
                loadingFragment.loading("logout...");
                loadingFragment.show();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                loadingFragment.hide();
                Intent iii = new Intent(Main.this, Auth.class);
                startActivity(iii);
                finish();
        }

        if (selectedFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container, selectedFragment);
            transaction.commit();

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (currentHomeFrag) {
            currentHomeFrag = false;
            getSupportFragmentManager().popBackStack("HomeFragment", 0);
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFragment()).commit();
            bottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.setSelectedItemId(R.id.action_home);
            toolbar.setTitle("Home");
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}


