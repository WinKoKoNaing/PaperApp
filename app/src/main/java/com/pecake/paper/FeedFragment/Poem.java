package com.pecake.paper.FeedFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pecake.paper.Fragments.PoemProverbListFragment;

public class Poem extends PoemProverbListFragment {


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query poemRef = databaseReference.child("user-poem");
        return poemRef;
    }
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
