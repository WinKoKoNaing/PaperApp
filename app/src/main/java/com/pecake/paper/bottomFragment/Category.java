package com.pecake.paper.bottomFragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pecake.paper.R;
import com.pecake.paper.activities.ActivityCategoryDetail;
import com.pecake.paper.adapters.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class Category extends Fragment implements View.OnClickListener {
    LinearLayout FictionCard, FlavorCard, FunnyCard, GeneralCard, HealthCard, ReligiousCard, SportCard, TechnologyCard;
    RecyclerView rvCategoryList;
    DatabaseReference categoryRef;
    View rootView;
    List<com.pecake.paper.models.Category> categoryList = new ArrayList<>();
    CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_category, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryRef = FirebaseDatabase.getInstance().getReference().child("categories");
        rvCategoryList = rootView.findViewById(R.id.rvCategoryList);
        rvCategoryList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategoryList.setNestedScrollingEnabled(false);
        GeneralCard = view.findViewById(R.id.generalCard);
        FictionCard = view.findViewById(R.id.FictionCard);
        FlavorCard = view.findViewById(R.id.FlavorCard);
        FunnyCard = view.findViewById(R.id.funnyCard);
        HealthCard = view.findViewById(R.id.healthCard);
        ReligiousCard = view.findViewById(R.id.ReligiousCard);
        SportCard = view.findViewById(R.id.sportCard);
        TechnologyCard = view.findViewById(R.id.technologyCard);
        FictionCard.setOnClickListener(this);
        FlavorCard.setOnClickListener(this);
        FunnyCard.setOnClickListener(this);
        HealthCard.setOnClickListener(this);
        ReligiousCard.setOnClickListener(this);
        SportCard.setOnClickListener(this);
        TechnologyCard.setOnClickListener(this);
        GeneralCard.setOnClickListener(this);
        readCategory();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), ActivityCategoryDetail.class);
        switch (v.getId()) {
            case R.id.generalCard:
                intent.putExtra("category", "general");
                break;
            case R.id.FictionCard:
                intent.putExtra("category", "fiction");
                break;
            case R.id.FlavorCard:
                intent.putExtra("category", "flavor");
                break;
            case R.id.healthCard:
                intent.putExtra("category", "health");
                break;
            case R.id.ReligiousCard:
                intent.putExtra("category", "religious");
                break;
            case R.id.sportCard:
                intent.putExtra("category", "sport");
                break;
            case R.id.technologyCard:
                intent.putExtra("category", "technology");
                break;
            case R.id.funnyCard:
                intent.putExtra("category", "funny");
                break;
        }
        startActivity(intent);
    }

    private void readCategory() {
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!categoryList.isEmpty()) {
                    categoryList.clear();
                }
                for (DataSnapshot s : dataSnapshot.getChildren()) {

                    com.pecake.paper.models.Category category = s.getValue(com.pecake.paper.models.Category.class);
                    categoryList.add(category);
                    categoryAdapter = new CategoryAdapter(getContext(), categoryList);
                    rvCategoryList.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
