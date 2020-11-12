package com.pecake.paper.bottomFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pecake.paper.Fragments.PostListFragment;


public class HomeFragment extends PostListFragment {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query postRef = databaseReference.child("posts").orderByChild("time");
        return postRef;
    }


}
