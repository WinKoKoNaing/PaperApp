package com.pecake.paper.userCategoryFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pecake.paper.Fragments.Fragment_CategoryLists;

public class UserCategory extends Fragment_CategoryLists {
    public static String categoryName;
    public static String getCategoryId;

    public static void addCategoryKey(String name) {
        categoryName = name;
    }

    public static void addCategoryId(String id) {
        getCategoryId = id;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query userRef = null;
        if (getCategoryId != null) {
            userRef = databaseReference.child("posts").orderByChild("categoryId").equalTo(getCategoryId);
        } else {
            userRef = databaseReference.child("posts").orderByChild("category").equalTo(categoryName);
        }

        return userRef;
    }
}
