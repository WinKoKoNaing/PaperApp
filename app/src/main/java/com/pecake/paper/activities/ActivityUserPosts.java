package com.pecake.paper.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.pecake.paper.DrawerFragment.FragmentMyPost;
import com.pecake.paper.R;
import com.pecake.paper.helpers.PeCake;

public class ActivityUserPosts extends AppCompatActivity {
    Toolbar toolbar;
    String UID ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Night mode
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref
                .getBoolean("night_mode", false);

        if(switchPref){

            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        }
        else{

            setTheme(R.style.AppTheme);


        }



        setContentView(R.layout.activity_user_posts);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Post");
        FragmentMyPost myPost = new FragmentMyPost();
        UID = getIntent().getExtras().getString(PeCake.USER_ID);
        FragmentMyPost.addId(UID);
        getSupportFragmentManager().beginTransaction().add(R.id.post_container,myPost).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
