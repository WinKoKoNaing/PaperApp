package com.pecake.paper.DrawerFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pecake.paper.Fragments.Fragment_CategoryLists;
import com.pecake.paper.Fragments.PostListFragment;

public class FragmentMyPost extends Fragment_CategoryLists {
    public static String id;
    public static void addId(String uid){
        id = uid;
    }
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query mypost = databaseReference.child("posts").orderByChild("uid").equalTo(id);
        return mypost;
    }
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
