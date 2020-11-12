package com.pecake.paper.categoryFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pecake.paper.Fragments.Fragment_CategoryLists;
import com.pecake.paper.Fragments.PostListFragment;

public class HealthFragment extends Fragment_CategoryLists {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query healthRef = databaseReference.child("posts").orderByChild("category").equalTo("က်န္းမာေရး");
        return healthRef;
    }
}
