package com.pecake.paper.FeedFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pecake.paper.Fragments.PoemProverbListFragment;

public class Proverb extends PoemProverbListFragment {


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query proverb = databaseReference.child("user-proverb");
        return proverb;
    }
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
