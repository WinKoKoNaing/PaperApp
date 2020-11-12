package com.pecake.paper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pecake.paper.R;
import com.pecake.paper.Util.LoadingFragment;
import com.pecake.paper.Util.UserUpdateInformation;
import com.pecake.paper.models.User;

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener {
    LoadingFragment loadingFragment = new LoadingFragment(this);
    UserUpdateInformation updateInformation;
    private final int PICK_IMAGE = 1;
    private EditText etUserName, etUserPhone, etUserAddress, etBio;
    private ImageView ivNewProfile;
    private Toolbar toolbar;
    String USER_ID;
    DatabaseReference profileRef;
    Uri currentPhotoUri;
    Uri selectedImage;
    String provider;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Night mode
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref
                .getBoolean("night_mode", false);

        if (switchPref) {

            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        } else
            setTheme(R.style.AppThemeNoToolBar);


        setContentView(R.layout.activity_profile_edit);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit");
        Bundle bundle = getIntent().getExtras();
        USER_ID = bundle.getString("user_id");


        updateInformation = new UserUpdateInformation(USER_ID);

        profileRef = FirebaseDatabase.getInstance().getReference().child("users").child(USER_ID);
        initUI();

        //night mode detail
        if (switchPref)
            nightModeOn();
        else
            nightModeOff();
        //night mode detail

        ivNewProfile.setOnClickListener(this);

        readUserData();
    }

    private void readUserData() {
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    inputProfileData(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inputProfileData(User user) {
        if (user.getPhotoUri() != null) {
            currentPhotoUri = Uri.parse(user.getPhotoUri());
            Glide.with(ProfileEditActivity.this).load(user.getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(ivNewProfile);
        }
        provider = user.getProvider();
        etUserName.setText(user.getName());
        etUserPhone.setText(user.getPhone());
        etUserAddress.setText(user.getAddress());
        etBio.setText(user.getBio());
    }


    public void initUI() {
        etUserPhone = findViewById(R.id.etPhone);
        etUserAddress = findViewById(R.id.etProfileAddress);
        etUserName = findViewById(R.id.etName);
        ivNewProfile = findViewById(R.id.ivNewProfile);
        etBio = findViewById(R.id.etShortBio);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            selectedImage = data.getData();
            Glide.with(this).load(selectedImage).apply(RequestOptions.circleCropTransform()).into(ivNewProfile);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "ေရြးခ်ယ္မွဳမျပဳလုပ္ခဲ့ပါ", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.ivNewProfile:
                Intent imageChooseIntent = new Intent();
                imageChooseIntent.setType("image/*");
                imageChooseIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(imageChooseIntent, "Select Photo"), PICK_IMAGE);
                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                loadingFragment.loading("updating.....");
                loadingFragment.show();
                upLoad();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void upLoad() {
        final String bio = etBio.getText().toString().trim();
        final String name = etUserName.getText().toString().trim();
        final String phone = etUserPhone.getText().toString().trim();
        final String address = etUserAddress.getText().toString().trim();

        updateInformation.updateName(name);


        if (selectedImage != null) {
            final StorageReference profileRel = FirebaseStorage.getInstance().getReference().child("profile").child(selectedImage.getLastPathSegment());
            final UploadTask uploadTask = profileRel.putFile(selectedImage);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return profileRel.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                updateInformation.deleteOldProfile(currentPhotoUri.toString());
                                updateInformation.updateLogo(downloadUri.toString());
                                if (!provider.equals("Phone")) {
                                    profileRef.setValue(new User("Facebook", name, phone, downloadUri.toString(), "", address, bio, "", "", "", false)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loadingFragment.hide();
                                            finish();
                                        }
                                    });
                                } else {
                                    profileRef.setValue(new User("Phone", name, phone, downloadUri.toString(), "", address, bio, "", "", "", false)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loadingFragment.hide();
                                            finish();
                                        }
                                    });
                                }
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });
                }
            });
        } else {
            if (!provider.equals("Phone")) {
                profileRef.setValue(new User("Facebook", name, phone, currentPhotoUri.toString(), "", address, bio, "", "", "", false)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingFragment.hide();
                        finish();
                    }
                });
            } else {
                profileRef.setValue(new User("Phone", name, phone, currentPhotoUri.toString(), "", address, bio, "", "", "", false)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingFragment.hide();
                        finish();
                    }
                });
            }
        }
        loadingFragment.hide();
        finish();

    }

    public void nightModeOn() {


    }

    public void nightModeOff() {


    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
