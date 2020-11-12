package com.pecake.paper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.pecake.paper.R;
import com.pecake.paper.userCategoryFragment.UserCategory;

public class ActivityUserCategory extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton fab;
    String title,uid,categoryId;
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



        setContentView(R.layout.activity_category_create);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = getIntent().getExtras().getString("title");
        uid = getIntent().getExtras().getString("user_id");
        categoryId = getIntent().getExtras().getString("category_id");
        getSupportActionBar().setTitle(title);




        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getApplication(),CategoryPostCreate.class);
                i.putExtra("title",title);
                i.putExtra("category_id",categoryId);
                startActivity(i);
            }
        });

        if (!getUid().equals(uid)){
            fab.setVisibility(View.GONE);
        }
        UserCategory category = new UserCategory();
        UserCategory.addCategoryKey(title);
        UserCategory.addCategoryId(categoryId);
        getSupportFragmentManager().beginTransaction().add(R.id.category_container,category).commit();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_category_menu,menu);
        MenuItem setting = menu.findItem(R.id.action_category_setting);
        if (!getUid().equals(uid)){
            setting.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_category_setting:

                Intent i = new Intent(ActivityUserCategory.this,ActivityCategoryEdit.class);
                i.putExtra("category_id",categoryId);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
