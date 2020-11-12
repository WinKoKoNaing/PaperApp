package com.pecake.paper.DrawerFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pecake.paper.Fragments.Fragment_CategoryLists;
import com.pecake.paper.Fragments.PostListFragment;

public class TopPostFragment extends Fragment_CategoryLists {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query topPost = databaseReference.child("posts").orderByChild("starCount");
        return topPost;
    }
}
