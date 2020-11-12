package com.pecake.paper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.pecake.paper.Main;
import com.pecake.paper.R;
import com.pecake.paper.DrawerFragment.SettingFragment;

public class ActivitySetting extends AppCompatActivity {

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



        setContentView(R.layout.activity_setting);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.setTitle("Settings");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySetting.this, Main.class);
                startActivity(intent);
                finish();
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingFrame, new SettingFragment())
                .commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ActivitySetting.this, Main.class);
        startActivity(intent);
        finish();
    }
}
