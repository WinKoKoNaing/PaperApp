package com.pecake.paper.userCategoryFragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pecake.paper.R;
import com.pecake.paper.Util.LoadingFragment;
import com.pecake.paper.models.Category;

import static android.app.Activity.RESULT_OK;

public class CategoryDialogFragment extends DialogFragment {
    LoadingFragment loadingFragment;
    View rootView;
    Uri selectedImage;
    EditText etTitle;
    Button btnCreateCategory;
    ImageView ivCategoryImage;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("categories");

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.create_category, container, false);
        ivCategoryImage = rootView.findViewById(R.id.ivCategoryImage);
        etTitle = rootView.findViewById(R.id.etCategoryTitle);
        btnCreateCategory = rootView.findViewById(R.id.btnCreateCategory);
        btnCreateCategory.setEnabled(false);
        etTitle.setFocusable(true);
        loadingFragment = new LoadingFragment(getContext());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    loadingFragment.loading("creating category...");
                    loadingFragment.show();
                    createdCategory();
                } else {
                    Toast.makeText(getActivity(), "Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageChooseIntent = new Intent();
                imageChooseIntent.setType("image/*");
                imageChooseIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(imageChooseIntent, "Select Photo"), 1);
            }
        });
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (TextUtils.isEmpty(s)) {
                    btnCreateCategory.setEnabled(false);
                } else {
                    btnCreateCategory.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case 1:
                    selectedImage = data.getData();
                    Glide.with(getActivity()).load(selectedImage).apply(RequestOptions.circleCropTransform()).into(ivCategoryImage);

                    break;
            }
        }
    }

    public void createdCategory() {


        final String title = etTitle.getText().toString().trim();

        final String categoryKey = categoryRef.child(getUid()).push().getKey();

        final StorageReference photoRel = storageReference.child("category-photo").child(getUid()).child(categoryKey).child(selectedImage.getLastPathSegment());
        final UploadTask uploadTask = photoRel.putFile(selectedImage);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                            final Category category = new Category(categoryKey, getUid(), title, postUri.toString());
                            categoryRef.child(categoryKey).setValue(category).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loadingFragment.hide();
                                    getDialog().dismiss();

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

    public boolean validate() {
        if (selectedImage == null) {
            return false;
        }
        return true;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
