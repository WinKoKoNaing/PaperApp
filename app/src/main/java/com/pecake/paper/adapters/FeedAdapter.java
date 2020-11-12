package com.pecake.paper.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pecake.paper.FeedFragment.Poem;
import com.pecake.paper.FeedFragment.Proverb;

public class FeedAdapter extends FragmentPagerAdapter {
    public FeedAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment currentFragment = null;
        switch (position) {
            case 0:
                currentFragment = new Poem();
                break;
            case 1:
                currentFragment = new Proverb();
                break;
        }
        return currentFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = "ကဗ်ာ";
                break;
            case 1:
                title = "အဆိုအမိန္႔";
                break;
        }
        return title;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
