package com.digidactylus.recorder.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.digidactylus.recorder.Fragments.DataFragment;
import com.digidactylus.recorder.Fragments.LabelingFragment;
import com.digidactylus.recorder.Fragments.SetupFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class menuOptionsPagerAdapter extends FragmentStateAdapter {

    private final Map<Integer, Fragment> fragments = new HashMap<>();

    public menuOptionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            SetupFragment fragment = new SetupFragment();
            fragments.put(0, fragment);
            return fragment;
        } else if (position == 1) {
            DataFragment fragment2 = new DataFragment();
            fragments.put(1, fragment2);
            return fragment2;
        } else {
            LabelingFragment fragment3 = new LabelingFragment();
            fragments.put(2, fragment3);
            return fragment3;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public Fragment getInstantiatedFragment(int position)
    {
        return fragments.get(position);
    }
}
