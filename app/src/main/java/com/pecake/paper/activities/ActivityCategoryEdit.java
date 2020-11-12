package com.pecake.paper.activities;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.pecake.paper.models.Category;

public class ActivityCategoryEdit extends AppCompatActivity {
    LoadingFragment loadingFragment = new LoadingFragment(this);
    EditText etTitle;
    ImageView ivCategoryImage;
    Uri selectedImage, currentUri;
    String categoryKey;
    Button btnCreateCategory;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("categories");
    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("posts");
    DatabaseReference userPostRef = FirebaseDatabase.getInstance().getReference().child("user-posts").child(getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);
        ivCategoryImage = findViewById(R.id.ivCategoryImage);
        etTitle = findViewById(R.id.etCategoryTitle);
        btnCreateCategory = findViewById(R.id.btnCreateCategory);
        categoryKey = getIntent().getExtras().getString("category_id");
        btnCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    loadingFragment.loading("updating ...");
                    loadingFragment.show();
                    createdCategory();
                } else {
                    Toast.makeText(getApplicationContext(), "add title", Toast.LENGTH_SHORT).show();
                }
            }
        });
        readCategory();
        ivCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageChooseIntent = new Intent();
                imageChooseIntent.setType("image/*");
                imageChooseIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(imageChooseIntent, "Select Photo"), 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case 1:
                    selectedImage = data.getData();
                    Glide.with(this).load(selectedImage).apply(RequestOptions.circleCropTransform()).into(ivCategoryImage);

                    break;
            }
        }
    }

    public void readCategory() {
        categoryRef.child(categoryKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Category category = dataSnapshot.getValue(Category.class);
                try {
                    etTitle.setText(category.title);
                    currentUri = Uri.parse(category.photoUri);
                    Glide.with(ActivityCategoryEdit.this).load(category.photoUri).apply(RequestOptions.circleCropTransform()).into(ivCategoryImage);
                } catch (Exception e) {
//                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void createdCategory() {

        final String title = etTitle.getText().toString().trim();
        if (selectedImage != null) {
            final StorageReference postRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentUri.toString());
            postRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "delete", Toast.LENGTH_SHORT).show();
                }
            });
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
        } else {
            final Category category = new Category(categoryKey, getUid(), title, currentUri.toString());
            categoryRef.child(categoryKey).setValue(category).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                }
            });
        }


    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void deleteCategory(View view) {
        deleteAllPost();
        final StorageReference postRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentUri.toString());
        postRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                categoryRef.child(categoryKey).removeValue();
                Toast.makeText(getApplicationContext(), "deleted Category", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public boolean validate() {
        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            return false;
        }
        return true;
    }

    public boolean deleteAllPost() {
        postRef.orderByChild("categoryId").equalTo(categoryKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    postRef.child(s.getKey()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userPostRef.orderByChild("categoryId").equalTo(categoryKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    userPostRef.child(s.getKey()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return false;
    }
}
