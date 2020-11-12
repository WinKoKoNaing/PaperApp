package com.pecake.paper.DrawerFragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pecake.paper.R;
import com.pecake.paper.adapters.BookMarkPoemProverbAdapter;
import com.pecake.paper.adapters.BookMarkPostAdapter;
import com.pecake.paper.models.Post;

import java.util.ArrayList;
import java.util.List;


public class BookMark extends Fragment {
    Spinner spBookMarkFilter;
    List<Post> bookMarkLists = new ArrayList<>();
    List<String> keyList = new ArrayList<>();
    RecyclerView rvBookMarkList;
    BookMarkPostAdapter adapter;
    BookMarkPoemProverbAdapter poemAdapter;
    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("posts");
    DatabaseReference poemRef = FirebaseDatabase.getInstance().getReference().child("user-poem");
    DatabaseReference proverbRef = FirebaseDatabase.getInstance().getReference().child("user-proverb");

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvBookMarkList = view.findViewById(R.id.rvBookMarkList);
        rvBookMarkList.setHasFixedSize(true);
        rvBookMarkList.setLayoutManager(new LinearLayoutManager(getContext()));

        spBookMarkFilter = view.findViewById(R.id.spFilter);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.PostPormProverbSpinner, R.layout.bookmark_spinner_row);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spBookMarkFilter.setAdapter(adapter);
        spBookMarkFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        readPost(20);
                        break;
                    case 1:
                        readPoem(8);
                        break;
                    case 2:
                        readProverb(9);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_mark, container, false);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void readPost(int rcount) {

        postRef.orderByChild("date").limitToLast(rcount).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!bookMarkLists.isEmpty() && !keyList.isEmpty()) {
                    keyList.clear();
                    bookMarkLists.clear();
                }
                for (DataSnapshot d : dataSnapshot.getChildren()) {

                    Post post = d.getValue(Post.class);
                    if (post != null && post.bookMarks.contains(getUid())) {
                        keyList.add(d.getKey());
                        bookMarkLists.add(post);
                    }
                }

                adapter = new BookMarkPostAdapter(getContext(), bookMarkLists, keyList);
                rvBookMarkList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readPoem(int count) {
        poemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!bookMarkLists.isEmpty() && !keyList.isEmpty()) {
                    keyList.clear();
                    bookMarkLists.clear();
                }
                for (DataSnapshot d : dataSnapshot.getChildren()) {

                    Post post = d.getValue(Post.class);
                    if (post != null && post.bookMarks.contains(getUid())) {
                        keyList.add(d.getKey());
                        bookMarkLists.add(post);
                    }
                }
                poemAdapter = new BookMarkPoemProverbAdapter(getContext(), bookMarkLists, keyList, "user-poem");
                rvBookMarkList.setAdapter(poemAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void readProverb(int count) {
        proverbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!bookMarkLists.isEmpty() && !keyList.isEmpty()) {
                    keyList.clear();
                    bookMarkLists.clear();
                }
                for (DataSnapshot d : dataSnapshot.getChildren()) {

                    Post post = d.getValue(Post.class);
                    if (post != null && post.bookMarks.contains(getUid())) {
                        keyList.add(d.getKey());
                        bookMarkLists.add(post);
                    }
                }
                poemAdapter = new BookMarkPoemProverbAdapter(getContext(), bookMarkLists, keyList, "user-proverb");
                rvBookMarkList.setAdapter(poemAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
