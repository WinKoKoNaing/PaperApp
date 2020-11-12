package com.pecake.paper.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InitFireBase {
    private static FirebaseAuth firebaseAuth;
    private static FirebaseStorage firebaseStorage;
    private static FirebaseDatabase firebaseDb;
    private static DatabaseReference dbRef;
    private static StorageReference storageRef;
    private static FirebaseUser currentUser;
    public static void init(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDb =    FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        dbRef = firebaseDb.getReference();
        storageRef = firebaseStorage.getReference();
        currentUser = firebaseAuth.getCurrentUser();
    }
    public static FirebaseDatabase getDatabase() {
        if (firebaseDb == null) {
            firebaseDb = FirebaseDatabase.getInstance();
        }
        return firebaseDb;
    }
    public static FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public static FirebaseStorage getFirebaseStorage() {
        return firebaseStorage;
    }

    public static FirebaseDatabase getFirebaseDb() {
        return firebaseDb;
    }

    public static DatabaseReference getDbRef() {
        return dbRef;
    }

    public static StorageReference getStorageRef() {
        return storageRef;
    }
}
