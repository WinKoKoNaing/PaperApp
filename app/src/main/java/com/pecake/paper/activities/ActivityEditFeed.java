package com.pecake.paper.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.pecake.paper.R;
import com.pecake.paper.Util.LoadingFragment;
import com.pecake.paper.helpers.InitFireBase;
import com.pecake.paper.models.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.myatminsoe.mdetect.MDetect;


public class ActivityEditFeed extends AppCompatActivity implements View.OnClickListener {
    LoadingFragment loadingFragment = new LoadingFragment(this);
    ArrayList<Post> globalPost = new ArrayList<>();
    public final int PICK_IMAGE = 1;
    RelativeLayout bottomLayout;
    int radio = 7;
    String categories[] = {"ဘာသာေရးစာေပ", "ရသစာေပ", "သုုတစာေပ", "က်န္းမာေရး", "သပၺံႏွင့္နည္းပညာ", "ဟာသစာေပ", "အားကစား", "အေထြေထြ"};
    ImageView ivCategory, ivImage, ivCross, ivMain, ivDeleteImage;
    TextView tvPost, tvChooseCategory;
    EditText etTitle, etContent;
    ScrollView scrollView;
    int index;
    Post post;
    DatabaseReference dbRef;
    StorageReference storageReference;
    FirebaseUser currentUser;
    Uri selectedImage = null;
    String postKey;
    ProgressBar pbBar;
    boolean deletePhoto = false;
    String userChoice;
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



        setContentView(R.layout.activity_edit_feed);
        InitFireBase.init();
        dbRef = InitFireBase.getDbRef();
        storageReference = InitFireBase.getStorageRef();
        currentUser = InitFireBase.getCurrentUser();


        MDetect.INSTANCE.init(this);
        initUI();


        if(switchPref)
            nightModeOn();

        else
            nightModeOff();


        pbBar = findViewById(R.id.pbBar);
        ivCategory.setOnClickListener(this);
        ivImage.setOnClickListener(this);
        ivCross.setOnClickListener(this);
        tvPost.setOnClickListener(this);
        ivDeleteImage.setOnClickListener(this);
        etContent.setOnClickListener(this);
        userChoice = getIntent().getExtras().getString("user_choice");
        postKey = getIntent().getExtras().getString("post_key");
        index = getIntent().getExtras().getInt("index");
        if (index==1){
            etTitle.setVisibility(View.INVISIBLE);
        }
        dbRef.child(userChoice).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.child(postKey).getValue(Post.class);
                globalPost.add(post);
                try {
                    getPostData(post);
                } catch (Exception e) {
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getPostData(Post post) {
        etTitle.setText(post.getTitle());
        etContent.setText(post.getContent());
        tvChooseCategory.setText(post.getCategory());
//        if (post.getPostPhotoUri() != null || !post.getPostPhotoUri().equals("")) {
//            ivDeleteImage.setVisibility(View.VISIBLE);
//            try {
//                currentPhotoUri = Uri.parse(post.getPostPhotoUri());
//                Glide.with(ActivityEditFeed.this).load(post.getPostPhotoUri()).into(ivMain);
//            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "please wait updating.."+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case PICK_IMAGE:
                    deletePhoto = false;
                    selectedImage = data.getData();
                    Glide.with(ActivityEditFeed.this).load(selectedImage).into(ivMain);
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
                break;

            case R.id.tvPost:
                loadingFragment.loading("posting....");
                loadingFragment.show();
                uploadPost();
                break;

            case R.id.ivCross:
                finish();
                break;

            case R.id.ivDeleteImage:
                deletePhoto = true;
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
            return "https://firebasestorage.googleapis.com/v0/b/paper-204f1.appspot.com/o/user_post_photos%2Fmatt-icons_preferences-desktop-personal.png?alt=media&token=4a53cd23-03ce-478e-afd5-de83c1d5901a";
        } else {
            return currentUser.getPhotoUrl().toString();
        }
    }

    private void uploadPost() {
        pbBar.setVisibility(View.VISIBLE);
        final String uid = currentUser.getUid();
        final String name = currentUser.getDisplayName();


        final String currentTime = getCurrentTime();
        final String title = etTitle.getText().toString().trim();
        final String content = etContent.getText().toString().trim();
        final Post pdetail = globalPost.get(0);
        post = new Post(index, title, content, getCurrentDate(), currentTime, uid, name, getUserLogoUri(), pdetail.countViewer, pdetail.viewers, pdetail.bookMarks, pdetail.starCount, pdetail.stars);
        Map<String, Object> postValues = post.toNoTitleMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + userChoice + "/" + postKey, postValues);
//        childUpdates.put("/user-posts/" + uid + "/" + postKey, postValues);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEditFeed.this);
        builder.setView(layout);

        RadioGroup radioGroup = layout.findViewById(R.id.radioGroup);
        RadioButton[] rbArray = {takeRButton(0, radioGroup), takeRButton(1, radioGroup),
                takeRButton(2, radioGroup), takeRButton(3, radioGroup), takeRButton(4, radioGroup),
                takeRButton(5, radioGroup), takeRButton(6, radioGroup), takeRButton(7, radioGroup)};
        rbArray[radio].setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbOne:
                        radio = 0;
                        break;
                    case R.id.rbTwo:
                        radio = 1;
                        break;
                    case R.id.rbThree:
                        radio = 2;
                        break;
                    case R.id.rbFour:
                        radio = 3;
                        break;
                    case R.id.rbFive:
                        radio = 4;
                        break;
                    case R.id.rbSix:
                        radio = 5;
                        break;
                    case R.id.rbSeven:
                        radio = 6;
                        break;
                    case R.id.rbEight:
                        radio = 7;
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
        ivCross = findViewById(R.id.ivCross);
        tvPost = findViewById(R.id.tvPost);
        ivMain = findViewById(R.id.ivMain);
        ivDeleteImage = findViewById(R.id.ivDeleteImage);
        etTitle = findViewById(R.id.etTitle);
        scrollView = findViewById(R.id.svPostCreate);
        etContent = findViewById(R.id.etContent);
        bottomLayout = findViewById(R.id.relativeThree);
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

    public void nightModeOn(){

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
