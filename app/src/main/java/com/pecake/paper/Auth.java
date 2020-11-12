package com.pecake.paper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pecake.paper.Util.LoadingFragment;
import com.pecake.paper.models.User;
import com.pecake.paper.userCategoryFragment.PhoneDialogFragment;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Auth extends AppCompatActivity implements View.OnClickListener {
    LoadingFragment loadingFragment = new LoadingFragment(this);
    DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("users");
    Toolbar toolbar;
    TextView tvVerifyCount;
    TextView tvNameValidation, tvPhoneValidation, tvAddressValidation;
    LinearLayout svSignIn, svSignUp, svVerify;
    Button btnSignUp, btnVerify, btnFb;
    LoginButton loginButton;
    EditText etName, etEmail, etPhone, etVerifyCode, etAddress;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;
    CallbackManager callbackManager;
    String verificationId;
    boolean verificationProcess;
    String name, phone, address, email;
    boolean registeredAccount = false;
    boolean isRegisteredAccount = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login Auth");
        // initialize app
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        callbackManager = CallbackManager.Factory.create();
        //et sign in
        // et sign up
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etVerifyCode = findViewById(R.id.etVerifyPass);
        etAddress = findViewById(R.id.etAddress);
        // tvcount
        tvVerifyCount = findViewById(R.id.tvVerifyCount);
        // fb button
        loginButton = findViewById(R.id.login_button);
        // btn sign in , sign out
        btnSignUp = findViewById(R.id.btnSignUp);
        btnVerify = findViewById(R.id.btnVerify);
        btnFb = findViewById(R.id.fb);
        // sign in layout and sign out layout
        svSignUp = findViewById(R.id.svSignUp);
        svSignIn = findViewById(R.id.svSignIn);
        svVerify = findViewById(R.id.svVerify);
        // validation
        tvNameValidation = findViewById(R.id.tvNameValidation);
        tvPhoneValidation = findViewById(R.id.tvPhoneValidation);
        tvAddressValidation = findViewById(R.id.tvAddressValidation);
        //event listener
        btnSignUp.setOnClickListener(this);
        btnVerify.setOnClickListener(this);



        // Tesst
//        startActivity(new Intent(Auth.this, Main.class));
//        finish();




        btnVerify.setEnabled(false);

        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!s.toString().isEmpty()) {
                    tvAddressValidation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!s.toString().isEmpty()) {
                    tvNameValidation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 6) {
                    btnVerify.setEnabled(true);
                } else {
                    btnVerify.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 6) {
                    btnVerify.setEnabled(true);
                } else {
                    btnVerify.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // etTextInput Check
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 5) {
                    tvPhoneValidation.setText("Enter Correct Phone Number");
                    tvPhoneValidation.setVisibility(View.VISIBLE);
                } else if (s.length() > 5) {
                    tvPhoneValidation.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                String st;
                if (str.startsWith("09") && str.length() > 5) {
                    st = "+959" + str.substring(2, str.length());
                    checkLoginedUser(st.trim(),false);
                }else if(str.startsWith("+959")){
                    checkLoginedUser(str,false);
                }

            }
        });


        // facebook
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
            }
        });
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loadingFragment.loading("loading...");
                loadingFragment.show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
        btnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Auth.this, Arrays.asList("email", "public_profile"));
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(Auth.this, "Success",
                                    Toast.LENGTH_SHORT).show();
                            user = task.getResult().getUser();
//                            checkFBLoginedUser(user.getProviderId());
                            if (isRegisteredAccount) {
                                startActivity(new Intent(Auth.this, Main.class));
                                finish();

                            } else {
                                if (user != null) {

                                    if (user.getPhoneNumber() != null || user.getEmail() != null) {
                                        if (user.getPhoneNumber() != null) {
                                            createUser(user.getUid(), "Facebook", user.getDisplayName(), user.getPhoneNumber(),
                                                    user.getPhotoUrl().toString(), user.getEmail(), "");
                                        } else {
                                            createUser(user.getUid(), "Facebook", user.getDisplayName(), "",
                                                    user.getPhotoUrl().toString(), user.getEmail(), "");
                                        }

                                    }
                                    startActivity(new Intent(Auth.this, Main.class));
                                    finish();
                                }
                            }


                        } else {
                            Toast.makeText(Auth.this, "Authentication error",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }


    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        if (user == null) {

        } else {
            startActivity(new Intent(Auth.this, Main.class));
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (verificationProcess) {
            svSignUp.setVisibility(View.GONE);
            svVerify.setVisibility(View.VISIBLE);
            verificationProcess = false;
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(Auth.this, "start Complete", Toast.LENGTH_SHORT).show();
                        etVerifyCode.setText(phoneAuthCredential.getSmsCode());
                        verifyPhoneNumberWithCode(verificationId, phoneAuthCredential.getSmsCode());
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        loadingFragment.hide();
                        Toast.makeText(Auth.this, "start fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        loadingFragment.hide();
                        svSignUp.setVisibility(View.GONE);
                        svVerify.setVisibility(View.VISIBLE);
                        verificationId = s;
                        verificationProcess = true;
                        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                Bundle bundle = msg.getData();
                                int num = bundle.getInt("count");
                                if (num == 60) {
                                    svSignUp.setVisibility(View.VISIBLE);
                                    svVerify.setVisibility(View.GONE);
                                    svSignIn.setVisibility(View.GONE);
                                }
                                tvVerifyCount.setText(num + "");
                            }
                        };
                        final Runnable mMessage = new Runnable() {
                            @Override
                            public void run() {

                                for (int i = 1; i <= 60; i++) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Message m = handler.obtainMessage();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("count", i);
                                    m.setData(bundle);
                                    handler.sendMessage(m);

                                }
                            }
                        };
                        new Thread(mMessage).start();
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }
                });        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        loadingFragment.loading("verify code....");
        loadingFragment.show();
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/paper-204f1.appspot.com/o/profile%2Fprofile.png?alt=media&token=e3e968c1-fc69-4a4a-b69c-84f2f1c7043c"))
                                .build();
                        try {
                            user = task.getResult().getUser();
                            final String userId = user.getUid();
                            if (registeredAccount) {
                                loadingFragment.hide();
                                startActivity(new Intent(Auth.this, Main.class));
                                finish();
                            } else {

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    loadingFragment.hide();
                                                    Toast.makeText(Auth.this, "Complete User", Toast.LENGTH_SHORT).show();
                                                    createUser(userId, "Phone", name, phone, profileUpdates.getPhotoUri().toString(), email, address);
                                                    startActivity(new Intent(Auth.this, Main.class));
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        } catch (Exception e) {
                            loadingFragment.hide();
                             svSignIn.setVisibility(View.VISIBLE);
                            svSignUp.setVisibility(View.GONE);
                            svVerify.setVisibility(View.GONE);
                        }


                    }
                });
    }

    // [END sign_in_with_phone]
    //
    // check input
    public boolean validatePhone() {
        phone = etPhone.getText().toString().trim();
        if (phone.startsWith("09") || phone.startsWith("+959")) {
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "must start 09 or +959", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean validateRandon() {
        if (TextUtils.isEmpty(etAddress.getText().toString()) && TextUtils.isEmpty(etName.getText().toString()) && TextUtils.isEmpty(etPhone.getText().toString())) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etAddress.startAnimation(shake);
            etName.startAnimation(shake);
            etPhone.startAnimation(shake);
//            tvPhoneValidation.setText("Enter your phone number!");
            tvPhoneValidation.setVisibility(View.VISIBLE);
            tvNameValidation.setVisibility(View.VISIBLE);
            tvAddressValidation.setVisibility(View.VISIBLE);
            return false;
        } else if (TextUtils.isEmpty(etName.getText().toString()) && TextUtils.isEmpty(etPhone.getText().toString())) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etName.startAnimation(shake);
            etPhone.startAnimation(shake);
//            tvPhoneValidation.setText("Enter your phone number!");
            tvPhoneValidation.setVisibility(View.VISIBLE);
            tvNameValidation.setVisibility(View.VISIBLE);
            tvAddressValidation.setVisibility(View.INVISIBLE);
            return false;
        } else if (TextUtils.isEmpty(etName.getText().toString()) && TextUtils.isEmpty(etAddress.getText().toString())) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etName.startAnimation(shake);
            etAddress.startAnimation(shake);
//            tvPhoneValidation.setText("Enter your phone number!");
            tvPhoneValidation.setVisibility(View.INVISIBLE);
            tvNameValidation.setVisibility(View.VISIBLE);
            tvAddressValidation.setVisibility(View.VISIBLE);
            return false;
        } else if (TextUtils.isEmpty(etPhone.getText().toString()) && TextUtils.isEmpty(etAddress.getText().toString())) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etPhone.startAnimation(shake);
            etAddress.startAnimation(shake);
//            tvPhoneValidation.setText("Enter your phone number!");
            tvPhoneValidation.setVisibility(View.VISIBLE);
            tvNameValidation.setVisibility(View.INVISIBLE);
            tvAddressValidation.setVisibility(View.VISIBLE);
            return false;
        } else if (TextUtils.isEmpty(etName.getText().toString())) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etName.startAnimation(shake);
//            tvPhoneValidation.setText("Enter your phone number!");
            tvPhoneValidation.setVisibility(View.INVISIBLE);
            tvNameValidation.setVisibility(View.VISIBLE);
            tvAddressValidation.setVisibility(View.INVISIBLE);
            return false;
        } else if (TextUtils.isEmpty(etPhone.getText().toString())) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etPhone.startAnimation(shake);
            tvPhoneValidation.setText("Enter your phone number!");
            tvPhoneValidation.setVisibility(View.VISIBLE);
            tvNameValidation.setVisibility(View.INVISIBLE);
            tvAddressValidation.setVisibility(View.INVISIBLE);
            return false;
        } else if (TextUtils.isEmpty(etAddress.getText().toString())) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etAddress.startAnimation(shake);
//            tvPhoneValidation.setText("Enter your phone number!");
            tvPhoneValidation.setVisibility(View.INVISIBLE);
            tvNameValidation.setVisibility(View.INVISIBLE);
            tvAddressValidation.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSignUp:
                if (!validatePhone()) {
                    return;
                }
                if (!validateRandon()) {
                    return;
                }

                loadingFragment.loading("checking user info");
                loadingFragment.show();
                startVerifyPhone();
                break;
            case R.id.btnVerify:
                setVerifyPhoneCode();
                break;
        }
    }

    private void startVerifyPhone() {
        address = etAddress.getText().toString().trim();
        name = etName.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        if (etPhone.getText().toString().startsWith("09")) {
            phone = "+959" + phone.substring(2, phone.length());
        }

        email = etEmail.getText().toString().trim();
        startPhoneNumberVerification(phone);

    }

    private void setVerifyPhoneCode() {
        String verifyCode = etVerifyCode.getText().toString().trim();
        verifyPhoneNumberWithCode(verificationId, verifyCode);

    }

    private void createUser(String userId, String provider, String name, String phone, String photoUri, String email, String address) {
        User user = new User(provider, name, phone, photoUri, email, address, "", "", "", "", false);
        ref.child("users").child(userId).setValue(user);
    }

    //.......................................................................
    public void choiceSignUp(View v) {
        svSignUp.setVisibility(View.GONE);
        svVerify.setVisibility(View.GONE);
        svSignIn.setVisibility(View.VISIBLE);
    }

    public void signWithPhone(View v) {
        svSignUp.setVisibility(View.VISIBLE);
        svVerify.setVisibility(View.GONE);
        svSignIn.setVisibility(View.GONE);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkLoginedUser(final String phoneNo,boolean check) {
        final LoadingFragment userCheckLoading = new LoadingFragment(Auth.this);
        userCheckLoading.loading("Checking Account");
        if (check){
            userCheckLoading.show();
        }

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    try {
                        if (s.child("provider").getValue().equals("Phone")) {
                            if (s.child("phone").getValue().equals(phoneNo)) {
                                tvPhoneValidation.setText("Register Account");
                                Thread.sleep(2000);
                                Snackbar.make(toolbar.getRootView(), "Register Account", Snackbar.LENGTH_SHORT).show();
                                tvPhoneValidation.setTextColor(getResources().getColor(R.color.colorPrimary));
                                tvPhoneValidation.setVisibility(View.VISIBLE);
                                registeredAccount = true;
                                userCheckLoading.hide();
                                svVerify.setVisibility(View.VISIBLE);
                                svSignUp.setVisibility(View.GONE);
                                svSignIn.setVisibility(View.GONE);
                                startPhoneNumberVerification(phoneNo);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.getLocalizedMessage();
                    } finally {
                        userCheckLoading.hide();
                        if (!registeredAccount) {
                            loadingFragment.loading("no register");
                            loadingFragment.show();
                            svVerify.setVisibility(View.GONE);
                            svSignUp.setVisibility(View.VISIBLE);
                            svSignIn.setVisibility(View.GONE);
                            loadingFragment.hide();
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFBLoginedUser(final String userId) {
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    try {
                        if (s.child("provider").getValue().equals("Facebook")) {
                            if (userId.equals(s.getKey())) {
                                isRegisteredAccount = true;
                            }
                        }
                    } catch (Exception e) {
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loginWithPhone(View v) {
        FragmentManager fm = getSupportFragmentManager();
        final PhoneDialogFragment dialogFragment = new PhoneDialogFragment();
        dialogFragment.setLoginClickListener(new PhoneDialogFragment.setPhoneLoginClickListener() {
            @Override
            public void onClick(String phone) {
                dialogFragment.dismiss();
                String st;
                if (phone.startsWith("09")) {
                    st = phone.substring(2, phone.length());
                    phone = "+959" + st.trim();
                } else if (phone.startsWith("+959")) {
                }
                checkLoginedUser(phone,true);
            }
        });
        dialogFragment.show(fm, "PhoneDialogFragment");
    }
}
