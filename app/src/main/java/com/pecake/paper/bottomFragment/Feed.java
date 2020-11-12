package com.pecake.paper.bottomFragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pecake.paper.R;
import com.pecake.paper.adapters.FeedAdapter;

public class Feed extends Fragment {
    ViewPager vpReadPager;
    TabLayout tabReadLayout;
    FeedAdapter adapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vpReadPager = view.findViewById(R.id.vpReadPager);
        tabReadLayout = view.findViewById(R.id.tabs);
        adapter = new FeedAdapter(getChildFragmentManager());
        vpReadPager.setAdapter(adapter);
        tabReadLayout.setupWithViewPager(vpReadPager);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }


}
