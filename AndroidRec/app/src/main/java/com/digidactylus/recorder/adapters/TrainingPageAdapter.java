package com.digidactylus.recorder.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.digidactylus.recorder.Fragments.training.TrainingPlanStepA;
import com.digidactylus.recorder.Fragments.training.TrainingPlanStepB;

import java.util.HashMap;
import java.util.Map;

public class TrainingPageAdapter extends FragmentStateAdapter {


    public TrainingPageAdapter(@NonNull FragmentActivity fragmentActivity) {
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
                TrainingPlanStepA trainingPlanStepA = new TrainingPlanStepA();
                fragments.put(0, trainingPlanStepA);
                return trainingPlanStepA;
            case 1:
            default:
                TrainingPlanStepB trainingPlanStepB = new TrainingPlanStepB();
                fragments.put(1, trainingPlanStepB);
                return trainingPlanStepB;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }



}
