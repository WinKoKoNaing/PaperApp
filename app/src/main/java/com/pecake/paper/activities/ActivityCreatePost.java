package com.pecake.paper.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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


public class ActivityCreatePost extends AppCompatActivity implements View.OnClickListener {
    LoadingFragment loadingFragment = new LoadingFragment(this);
    public final int PICK_IMAGE = 1;
    public final int CUT_IMAGE = 2;
    int postIndex = 0;
    int radio = 9 ;
    Toolbar toolbar;
    String categories[] = {"ကဗ်ာ", "အဆိုအမိန္႔", "ဘာသာေရးစာေပ", "ရသစာေပ", "သုုတစာေပ", "က်န္းမာေရး", "သိပၸံႏွင့္နည္းပညာ", "ဟာသစာေပ", "အားကစား", "အေထြေထြ"};
    ImageView ivCategory, ivImage, ivMain, ivDeleteImage;
    TextView tvChooseCategory, tvTextCount;
    EditText etTitle, etContent;
    ScrollView scrollView;




















    LinearLayout bottomLayout;
    Post post;
    DatabaseReference dbRef;
    StorageReference storageReference;
    FirebaseUser currentUser;
    Uri selectedImage = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

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


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Post");
        InitFireBase.init();
        dbRef = InitFireBase.getDbRef();
        storageReference = InitFireBase.getStorageRef();
        currentUser = InitFireBase.getCurrentUser();


        MDetect.INSTANCE.init(this);
        initUI();


        ivCategory.setOnClickListener(this);
        ivImage.setOnClickListener(this);
        ivDeleteImage.setOnClickListener(this);
        etContent.setOnClickListener(this);


        if (switchPref)
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
                if (str.length() < 50 && postIndex == 1) {
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

    public void choicePost(int position) {
        switch (position) {
            case 0:
                postIndex = 0;
                etTitle.setVisibility(View.VISIBLE);
                ivMain.setVisibility(View.VISIBLE);
                etContent.setGravity(Gravity.LEFT);
                etTitle.setGravity(Gravity.LEFT);
                etTitle.setPadding(4, 0, 0, 0);
                etContent.setPadding(4, 0, 0, 0);
                etContent.setHint("အေၾကာင္းအရာ");
                ivImage.setVisibility(View.VISIBLE);
                etTitle.setHint("ေခါင္းစဥ္");
                break;
            case 1:
                postIndex = 1;
                etTitle.setVisibility(View.VISIBLE);
                etTitle.setGravity(Gravity.CENTER_HORIZONTAL);
                etTitle.setHint("ကဗ်ာ ေခါင္းစဥ္");
                ivMain.setVisibility(View.GONE);
                etContent.setGravity(Gravity.CENTER_HORIZONTAL);
                etContent.setHint("ကဗ်ာ စာသာ");
//                bottomLayout.setVisibility(View.GONE);
                ivImage.setVisibility(View.GONE);
                break;
            case 2:
                postIndex = 2;
                etTitle.setVisibility(View.GONE);
                ivMain.setVisibility(View.GONE);
                etContent.setGravity(Gravity.CENTER);
                etContent.setHint("အဆိုအမိန္႔");
                ivImage.setVisibility(View.GONE);
                break;
        }
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
                    loadingFragment.loading("posting.....");
                    loadingFragment.show();
                    switch (postIndex) {

                        case 0:
                            uploadPost();
                            break;
                        case 1:
                            uploadPoem();
                            break;
                        case 2:
                            uploadProverb();
                            break;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fill. each blank", Toast.LENGTH_SHORT).show();
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

                case PICK_IMAGE:
                    selectedImage = data.getData();
                    Glide.with(ActivityCreatePost.this).load(selectedImage).into(ivMain);
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

            case R.id.ivCategory:
                alertDialog();
                break;

            case R.id.ivImage:
                Intent imageChooseIntent = new Intent();
                imageChooseIntent.setType("image/*");
                imageChooseIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(imageChooseIntent, "Select Photo"), PICK_IMAGE);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ivMain.setImageDrawable(null);

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
        final String title = etTitle.getText().toString().trim();
        final String content = etContent.getText().toString().trim();
        final String category = categories[radio];
        if (selectedImage != null) {
            final StorageReference photoRel = storageReference.child("post-photos").child(getUid()).child(selectedImage.getLastPathSegment());
            final UploadTask uploadTask = photoRel.putFile(selectedImage);
            uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri postUri = taskSnapshot.getUploadSessionUri();
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
                                        post = new Post(title, content, category, postUri.toString(), getCurrentDate(), getCurrentTime(), uid, name, userLogoUri, 0, null, null, 0, null);
                                        Map<String, Object> postValues = post.toPhotoMap();

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
            post = new Post(title, content, category, getCurrentDate(), getCurrentTime(), uid, name, getUserLogoUri(), 0, null, null, 0, null);
            String key = dbRef.child("posts").push().getKey();
            Map<String, Object> postValues = post.toMap();

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

    private void uploadPoem() {
        final String uid = currentUser.getUid();
//        final String userLogoUri = getUserLogoUri();
        final String name = currentUser.getDisplayName();
        final String currentTime = getCurrentTime();
        final String title = etTitle.getText().toString().trim();
        final String content = etContent.getText().toString().trim();
        post = new Post(0, title, content, getCurrentDate(), currentTime, uid, name, getUserLogoUri(), 0, null, null, 0, null);
        String key = dbRef.child("posts").push().getKey();
        Map<String, Object> postValues = post.toNoTitleMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user-poem/" + key, postValues);
//        childUpdates.put("/user-poem/" + uid + "/" + key, postValues);

        Toast.makeText(getApplication(), "posting....", Toast.LENGTH_SHORT).show();
        dbRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadingFragment.hide();
                finish();
            }
        });

    }

    private void uploadProverb() {
        final String uid = currentUser.getUid();
//        final String userLogoUri = getUserLogoUri();
        final String name = currentUser.getDisplayName();
        final String currentTime = getCurrentTime();
//        final String title = etTitle.getText().toString().trim();
        final String content = etContent.getText().toString().trim();
        post = new Post(1, "", content, getCurrentDate(), currentTime, uid, name, getUserLogoUri(), 0, null, null, 0, null);
        String key = dbRef.child("posts").push().getKey();
        Map<String, Object> postValues = post.toNoTitleMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user-proverb/" + key, postValues);
//        childUpdates.put("/user-proverb/" + uid + "/" + key, postValues);

        Toast.makeText(getApplication(), "posting....", Toast.LENGTH_SHORT).show();
        dbRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadingFragment.hide();
                finish();
            }
        });
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

    public void alertDialog() {
        final AlertDialog alertDialog;
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.category_alert, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCreatePost.this);
        builder.setView(layout);

        RadioGroup radioGroup = layout.findViewById(R.id.radioGroup);
//        RadioButton[] rbArray = {takeRButton(0, radioGroup), takeRButton(1, radioGroup),
//                takeRButton(2, radioGroup), takeRButton(3, radioGroup), takeRButton(4, radioGroup),
//                takeRButton(5, radioGroup), takeRButton(6, radioGroup), takeRButton(7, radioGroup)};
//        rbArray[radio].setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbPoem:
                        radio = 0;
                        choicePost(1);
                        break;
                    case R.id.rbProverb:
                        choicePost(2);
                        radio = 1;
                        break;
                    case R.id.rbOne:
                        choicePost(0);
                        radio = 2;
                        break;
                    case R.id.rbTwo:
                        choicePost(0);
                        radio = 3;
                        break;
                    case R.id.rbThree:
                        choicePost(0);
                        radio = 4;
                        break;
                    case R.id.rbFour:
                        choicePost(0);
                        radio = 5;
                        break;
                    case R.id.rbFive:
                        choicePost(0);
                        radio = 6;
                        break;
                    case R.id.rbSix:
                        choicePost(0);
                        radio = 7;
                        break;
                    case R.id.rbSeven:
                        choicePost(0);
                        radio = 8;
                        break;
                    case R.id.rbEight:
                        choicePost(0);
                        radio = 9;
                        break;
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                tvChooseCategory.setText(categories[radio]);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

    }

    public RadioButton takeRButton(int index, RadioGroup rg) {
        RadioButton rb = (RadioButton) rg.getChildAt(index);
        return rb;
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
            if (postIndex==2){
                return true;
            }
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


    public void nightModeOn() {

        tvChooseCategory.setTextColor(getResources().getColor(R.color.white));
        bottomLayout.setBackgroundColor(getResources().getColor(R.color.black));

        etTitle.setBackgroundColor(getResources().getColor(R.color.windowBack));
        etTitle.setTextColor(getResources().getColor(R.color.white));
        etTitle.setHintTextColor(getResources().getColor(R.color.white));

        etContent.setBackgroundColor(getResources().getColor(R.color.windowBack));
        etContent.setTextColor(getResources().getColor(R.color.white));
        etContent.setHintTextColor(getResources().getColor(R.color.white));


        scrollView.setBackgroundColor(getResources().getColor(R.color.windowBack));

    }

    public void nightModeOff() {

        tvChooseCategory.setTextColor(getResources().getColor(R.color.dark));
        bottomLayout.setBackgroundColor(getResources().getColor(R.color.grey));

        etTitle.setBackgroundColor(getResources().getColor(R.color.white));
        etTitle.setTextColor(getResources().getColor(R.color.black));


        etContent.setBackgroundColor(getResources().getColor(R.color.white));
        etContent.setTextColor(getResources().getColor(R.color.black));

        scrollView.setBackgroundColor(getResources().getColor(R.color.white));

    }
}
