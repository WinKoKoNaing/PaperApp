package com.pecake.paper.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pecake.paper.R;
import com.pecake.paper.Util.LoadingFragment;
import com.pecake.paper.helpers.InitFireBase;
import com.pecake.paper.models.Post;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.myatminsoe.mdetect.MDetect;


public class CategoryPostCreate extends AppCompatActivity implements View.OnClickListener {
    LoadingFragment loadingFragment = new LoadingFragment(this);
    Toolbar toolbar;
    ImageView ivCategory, ivImage, ivMain, ivDeleteImage;
    TextView tvChooseCategory, tvTextCount;
    EditText etTitle, etContent;
    ScrollView scrollView;
    LinearLayout bottomLayout;
    Post post;
    String categoryTitle, categoryId;
    DatabaseReference dbRef;
    StorageReference storageReference;
    FirebaseUser currentUser;
    Uri selectedImage = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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



        setContentView(R.layout.activity_create_post);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Post");
        InitFireBase.init();
        dbRef = InitFireBase.getDbRef();
        storageReference = InitFireBase.getStorageRef();
        currentUser = InitFireBase.getCurrentUser();
        categoryTitle = getIntent().getExtras().getString("title");
        categoryId = getIntent().getExtras().getString("category_id");
        MDetect.INSTANCE.init(this);
        initUI();
        tvChooseCategory.setText(categoryTitle);

        ivImage.setOnClickListener(this);
        ivDeleteImage.setOnClickListener(this);
        etContent.setOnClickListener(this);


        if(switchPref)
            nightModeOn();

        else
            nightModeOff();



//        tvChooseCategory.setText(categories[radio]);
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() < 50) {
                    tvTextCount.setText(String.valueOf(str.length()));
                } else {
                    int i = 500 - str.length();
                    tvTextCount.setText(String.valueOf(i));
                    tvTextCount.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_postCreatePost:
                if (validateCreatePost()) {
                    loadingFragment.loading("posting...");
                    loadingFragment.show();
                    uploadPost();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case 1:
                    selectedImage = data.getData();
                    ivDeleteImage.setVisibility(View.VISIBLE);      // visible delete image

                    break;
            }

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ivImage:
                Intent imageChooseIntent = new Intent();
                imageChooseIntent.setType("image/*");
                imageChooseIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(imageChooseIntent, "Select Photo"), 1);
                break;


            case R.id.ivDeleteImage:
                selectedImage = null;
                ivMain.setImageDrawable(null);
                ivDeleteImage.setVisibility(View.INVISIBLE);      //invisible delete image
                break;

            case R.id.etContent:
                if (etContent.getText().toString().trim().length() == 0) {
                    scrollUp();
                }
                break;

        }

    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentDate() {

        Date currentDate = Calendar.getInstance().getTime();
        return new SimpleDateFormat("dd.MM.yyyy").format(currentDate);
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentTime() {

        Calendar c = Calendar.getInstance();
        return new SimpleDateFormat("K:mm a").format(c.getTime());
    }

    public String getUserLogoUri() {
        if (currentUser.getPhotoUrl() == null) {
            return "https://firebasestorage.googleapis.com/v0/b/paper-204f1.appspot.com/o/profile%2Fprofile.png?alt=media&token=5a996dc3-654f-48b2-b316-f672a020fad4";
        } else {
            return currentUser.getPhotoUrl().toString();
        }
    }

    private void uploadPost() {
        final String uid = currentUser.getUid();
        final String userLogoUri = getUserLogoUri();
        final String name = currentUser.getDisplayName();
        final String currentTime = getCurrentTime();
        final String title = etTitle.getText().toString().trim();
        final String content = etContent.getText().toString().trim();
        if (selectedImage != null) {
            final StorageReference photoRel = storageReference.child("post-photos").child(getUid()).child(selectedImage.getLastPathSegment());
            final UploadTask uploadTask = photoRel.putFile(selectedImage);
            uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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
                                    return photoRel.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri postUri = task.getResult();
                                        Toast.makeText(getApplication(), "Photo Upload..", Toast.LENGTH_SHORT).show();
                                        String key = dbRef.child("posts").push().getKey();
                                        post = new Post(categoryId,title, content, categoryTitle, postUri.toString(), getCurrentDate(), getCurrentTime(), uid, name, userLogoUri, 0, null, null, 0, null);
                                        Map<String, Object> postValues = post.toPhotoCategoryMap();

                                        Map<String, Object> childUpdates = new HashMap<>();
                                        childUpdates.put("/posts/" + key, postValues);
//                                        childUpdates.put("/user-posts/" + uid + "/" + key, postValues);

                                        Toast.makeText(getApplication(), "posting....", Toast.LENGTH_SHORT).show();
                                        dbRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                loadingFragment.hide();
                                                finish();
                                            }
                                        });

                                    } else {
                                        // Handle failures
                                        // ...
                                    }
                                }
                            });
                        }
                    });
                }
            });
        } else {
            post = new Post("", "",categoryId, title, content, categoryTitle, getCurrentDate(), getCurrentTime(), uid, name, getUserLogoUri(), 0, null, null, 0, null);
            String key = dbRef.child("posts").push().getKey();
            Map<String, Object> postValues = post.toCategoryMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/posts/" + key, postValues);
//            childUpdates.put("/user-posts/" + uid + "/" + key, postValues);

            Toast.makeText(getApplication(), "posting....", Toast.LENGTH_SHORT).show();
            dbRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loadingFragment.hide();
                    finish();
                }
            });
        }
    }


    private void scrollUp() {
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
                int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
                int sy = scrollView.getScrollY();
                int sh = scrollView.getHeight();
                int delta = bottom - (sy + sh + 300);
                scrollView.smoothScrollBy(0, delta);
            }
        }, 200);
    }

    public void initUI() {

        tvChooseCategory = findViewById(R.id.tvChooseCategory);
        ivCategory = findViewById(R.id.ivCategory);
        ivImage = findViewById(R.id.ivImage);
        ivMain = findViewById(R.id.ivMain);
        ivDeleteImage = findViewById(R.id.ivDeleteImage);
        etTitle = findViewById(R.id.etTitle);
        scrollView = findViewById(R.id.svPostCreate);
        etContent = findViewById(R.id.etContent);
        bottomLayout = findViewById(R.id.relativeThree);
        tvTextCount = findViewById(R.id.tvTextCount);
        if (MDetect.INSTANCE.isUnicode()) {
            tvChooseCategory.setText("အမျိုးအစားရွေးပါ");
            etTitle.setHint("ခေါင်းစဉ်");
            etContent.setHint("အကြောင်းအရာ");
        } else {
            tvChooseCategory.setText("အမ်ိဳးအစားေရြးပါ");
            etTitle.setHint("ေခါင္းစဥ္");
            etContent.setHint("အေၾကာင္းအရာ");
        }

    }

    private boolean validateCreatePost() {
        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            etTitle.setHintTextColor(Color.RED);
            return false;
        }
        if (TextUtils.isEmpty(etContent.getText().toString())) {
            etContent.setHintTextColor(Color.RED);
            return false;
        }
        return true;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void nightModeOn(){

        tvChooseCategory.setTextColor(getResources().getColor(R.color.white));
        bottomLayout.setBackgroundColor(getResources().getColor(R.color.black));

        etTitle.setBackgroundColor(getResources().getColor(R.color.windowBack));
        etTitle.setTextColor(getResources().getColor(R.color.white));

        etContent.setBackgroundColor(getResources().getColor(R.color.windowBack));
        etContent.setTextColor(getResources().getColor(R.color.white));


    }

    public void nightModeOff(){

        tvChooseCategory.setTextColor(getResources().getColor(R.color.dark));
        bottomLayout.setBackgroundColor(getResources().getColor(R.color.grey));

        etTitle.setBackgroundColor(getResources().getColor(R.color.white));
        etTitle.setTextColor(getResources().getColor(R.color.black));

        etContent.setBackgroundColor(getResources().getColor(R.color.white));
        etContent.setTextColor(getResources().getColor(R.color.black));

        scrollView.setBackgroundColor(getResources().getColor(R.color.white));

    }
}
