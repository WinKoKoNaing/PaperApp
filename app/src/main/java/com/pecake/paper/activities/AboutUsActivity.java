package com.pecake.paper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pecake.paper.R;


public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);

        LinearLayout layout = findViewById(R.id.aboutLayout);
        TextView textOne = findViewById(R.id.textOne);
        TextView textTwo = findViewById(R.id.textTwo);

        //Night mode
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref
                .getBoolean("night_mode", false);

        if(switchPref){

            layout.setBackgroundColor(getResources().getColor(R.color.windowBack));
            textOne.setTextColor(getResources().getColor(R.color.white));
            textTwo.setTextColor(getResources().getColor(R.color.white));

        }
        else{

            layout.setBackgroundColor(getResources().getColor(R.color.white));
            textOne.setTextColor(getResources().getColor(R.color.black));
            textTwo.setTextColor(getResources().getColor(R.color.black));

        }

        Toolbar toolbar = findViewById(R.id.aboutToolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AboutUsActivity.this, ActivitySetting.class);
                startActivity(intent);
                finish();

            }
        });

        getSupportActionBar().setTitle("About");

    }



}
