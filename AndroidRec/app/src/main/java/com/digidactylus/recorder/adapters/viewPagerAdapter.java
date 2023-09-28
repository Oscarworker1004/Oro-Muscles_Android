package com.digidactylus.recorder.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.digidactylus.recorder.Fragments.AnalyticsFragment;
import com.digidactylus.recorder.Fragments.GraphsFragment;
import com.digidactylus.recorder.Fragments.ZonesFragment;

import java.util.HashMap;
import java.util.Map;

public class viewPagerAdapter extends FragmentStateAdapter {

    private final Map<Integer, Fragment> fragments = new HashMap<>();
    public viewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                AnalyticsFragment fragment = new AnalyticsFragment();
                fragments.put(0, fragment);
                return fragment;
            case 1:
                GraphsFragment fragment2 = new GraphsFragment();
                fragments.put(1, fragment2);
                return fragment2;
            case 2:
            default:
                ZonesFragment fragment3 = new ZonesFragment();
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
