package com.pecake.paper.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pecake.paper.DrawerFragment.FragmentMyPost;
import com.pecake.paper.R;
import com.pecake.paper.adapters.CategoryAdapter;
import com.pecake.paper.models.Category;

import java.util.ArrayList;
import java.util.List;

public class UserCategories extends AppCompatActivity {
    Toolbar toolbar;
    String UID;
    RecyclerView rvCategoryList;
    DatabaseReference categoryRef;
    List<Category> categoryList = new ArrayList<>();
    CategoryAdapter categoryAdapter;
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


        setContentView(R.layout.activity_user_categories);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentMyPost myPost = new FragmentMyPost();
        UID = getIntent().getExtras().getString("uid");
        FragmentMyPost.addId(UID);
        getSupportFragmentManager().beginTransaction().add(R.id.post_container,myPost).commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void readCategory() {
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!categoryList.isEmpty()) {
                    categoryList.clear();
                }
                for (DataSnapshot s : dataSnapshot.getChildren()) {

                    com.pecake.paper.models.Category category = s.getValue(com.pecake.paper.models.Category.class);
                    categoryList.add(category);
                    categoryAdapter = new CategoryAdapter(UserCategories.this, categoryList);
                    rvCategoryList.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
