package com.pecake.paper.DrawerFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.pecake.paper.R;
import com.pecake.paper.activities.AboutUsActivity;

public class SettingFragment extends PreferenceFragmentCompat implements androidx.preference.Preference.OnPreferenceClickListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {


        setPreferencesFromResource(R.xml.preferences, rootKey);


        Preference aboutPref = findPreference("about");
        Preference helpPref = findPreference("help");
        Preference ackPref = findPreference("ack");

        aboutPref.setOnPreferenceClickListener(this);
        helpPref.setOnPreferenceClickListener(this);
        ackPref.setOnPreferenceClickListener(this);



    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        switch (preference.getKey()){

            case "about":

                Toast.makeText(getActivity(), "about", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity(), AboutUsActivity.class);
                getActivity().startActivity(i);

                break;

            case "help":

                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.facebook.com/min.thihaaung.35325")));

                break;

            case "ack":

                alertDialog();

                break;
        }

        return false;
    }

    public void alertDialog() {
        final AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.acknowledgment_alert, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setView(layout);



        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog = builder.create();
        alertDialog.show();

    }
}
