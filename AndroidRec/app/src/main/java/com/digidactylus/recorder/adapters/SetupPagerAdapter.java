package com.digidactylus.recorder.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.digidactylus.recorder.Fragments.ConnectionFragment;
import com.digidactylus.recorder.Fragments.MuscleMapFragment;
import com.digidactylus.recorder.Fragments.TrainingPlanFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SetupPagerAdapter extends FragmentStateAdapter {
    public SetupPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    private final Map<Integer, Fragment> fragments = new HashMap<>();

    public Fragment getInstantiatedFragment(int position)
    {
        return fragments.get(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                ConnectionFragment connectionFragment = new ConnectionFragment();
                fragments.put(0, connectionFragment);
                return connectionFragment;
            case 1:
                TrainingPlanFragment trainingPlanFragment = new TrainingPlanFragment();
                fragments.put(1, trainingPlanFragment);
                return trainingPlanFragment;
            case 2:
            default:
                MuscleMapFragment muscleMapFragment = new MuscleMapFragment();
                fragments.put(2, muscleMapFragment);
                return muscleMapFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }



}
