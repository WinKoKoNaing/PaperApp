package com.pecake.paper.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.pecake.paper.R;
import com.pecake.paper.categoryFragment.FictionFragment;
import com.pecake.paper.categoryFragment.FlavorFragment;
import com.pecake.paper.categoryFragment.FuncyFragment;
import com.pecake.paper.categoryFragment.GeneralFragment;
import com.pecake.paper.categoryFragment.HealthFragment;
import com.pecake.paper.categoryFragment.ReligiousFragment;
import com.pecake.paper.categoryFragment.SportFragment;
import com.pecake.paper.categoryFragment.TechnologyFragment;

public class ActivityCategoryDetail extends AppCompatActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Night mode
        nightMode();



        setContentView(R.layout.activity_category_detail);
        toolbar = findViewById(R.id.toolbarC);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String category = getIntent().getExtras().getString("category");
        Fragment currentFragment = null;
        switch (category) {
            case "general":
                    currentFragment = new GeneralFragment();
                    getSupportActionBar().setTitle("အေထြေထြ");
                break;
            case "fiction":
                currentFragment = new FictionFragment();
                getSupportActionBar().setTitle("သုုတစာေပ");
                break;
            case "flavor":
                currentFragment = new FlavorFragment();
                getSupportActionBar().setTitle("ရသစာေပ");
                break;
            case "health":
                currentFragment = new HealthFragment();
                getSupportActionBar().setTitle("က်န္းမာေရး");
                break;
            case "religious":
                currentFragment = new ReligiousFragment();
                getSupportActionBar().setTitle("ဘာသာေရးစာေပ");
                break;
            case "sport":
                currentFragment = new SportFragment();
                getSupportActionBar().setTitle("အားကစား");
                break;
            case "technology":
                currentFragment = new TechnologyFragment();
                getSupportActionBar().setTitle("သိပၸံႏွင့္နည္းပညာ");
                break;
            case "funny":
                currentFragment = new FuncyFragment();
                getSupportActionBar().setTitle("ဟာသစာေပ");
                break;
        }
        getSupportFragmentManager().beginTransaction().add(R.id.category_container, currentFragment).commit();
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

    public void nightMode(){
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
    }
}
