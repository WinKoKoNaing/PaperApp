package com.pecake.paper.Util;

import android.net.Uri;
import androidx.annotation.NonNull;

import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UserUpdateInformation {
    String userId;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference postRef;

    public UserUpdateInformation(String userId) {
        this.userId = userId;
    }

    public void updateName(final String name) {
        final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "update photo", Toast.LENGTH_SHORT).show();
            }
        });
        postRef = FirebaseDatabase.getInstance().getReference().child("posts");
        postRef.orderByChild("uid").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot db : dataSnapshot.getChildren()) {
                    postRef.child(db.getKey()).child("name").setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateLogo(final String logo) {
        final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(logo))
                .build();
        user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "update photo", Toast.LENGTH_SHORT).show();
            }
        });
        postRef = FirebaseDatabase.getInstance().getReference().child("posts");
        postRef.orderByChild("uid").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot db : dataSnapshot.getChildren()) {
                    postRef.child(db.getKey()).child("userLogoUri").setValue(logo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void updateUserLogo() {

    }
    public void deleteOldProfile(String oldrUri){
        try{
            if (!oldrUri.equals("https://firebasestorage.googleapis.com/v0/b/paper-204f1.appspot.com/o/profile%2Fprofile.png?alt=media&token=e3e968c1-fc69-4a4a-b69c-84f2f1c7043c")){
                StorageReference postPhotoRef;
                postPhotoRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldrUri);
                postPhotoRef.delete();
            }
        }catch (NullPointerException e){
//            Toast.makeText(getApplicationContext(),"Are you A" , Toast.LENGTH_SHORT).show();
        }catch (Exception e){

        }

    }
}
