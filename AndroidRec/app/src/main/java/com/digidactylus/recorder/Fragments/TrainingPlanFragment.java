package com.digidactylus.recorder.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.digidactylus.recorder.Fragments.training.TrainingPlanStepA;
import com.digidactylus.recorder.Fragments.training.TrainingPlanStepB;
import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;
import com.digidactylus.recorder.adapters.TrainingPageAdapter;
import com.digidactylus.recorder.databinding.FragmentTrainingPlanBinding;
import com.digidactylus.recorder.utils.Tools;
import com.digidactylus.recorder.viewmodels.TrainingViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TrainingPlanFragment extends Fragment {


    private static final String TAG = "TrainingPlanFragment";

    public TrainingPlanFragment() {
        // Required empty public constructor
    }

    ViewPager2 viewPager2;
    TrainingPageAdapter trainingPageAdapter;

    MaterialButton confirmMuscleButton;

    int currentPage = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTrainingPlanBinding binding = FragmentTrainingPlanBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager2 = view.findViewById(R.id.trainingPager);
        confirmMuscleButton = view.findViewById(R.id.confirmMuscleButton);


        trainingPageAdapter =  new TrainingPageAdapter(requireActivity());

        viewPager2.setAdapter(trainingPageAdapter);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setUserInputEnabled(false);
        viewPager2.setCurrentItem(currentPage);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {

                Fragment fragment = trainingPageAdapter.getInstantiatedFragment(position);
                if(fragment instanceof TrainingPlanStepB) {
                    TrainingPlanStepB trainingPlanStepB = (TrainingPlanStepB) fragment;
                    trainingPlanStepB.notifyData();
                }
                else if(fragment instanceof TrainingPlanStepA) {
                    TrainingPlanStepA trainingPlanStepA = (TrainingPlanStepA) fragment;
                    trainingPlanStepA.notifyData();
                }
            }
        });


        MainActivity.getMain().trainingViewModel.setTrainingModified(false);

        MainActivity.getMain().trainingViewModel.getTrainingModifiedLiveData().observe(requireActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean newStatus) {
                if(currentPage == 1) {
                    if(!newStatus) {
                        confirmMuscleButton.setText("Edit Workout Selection");
                    }
                    else {
                        confirmMuscleButton.setText("Finish Workout Selection");
                    }
                }
                else {
                    confirmMuscleButton.setText("Finish Workout Selection");
                }
            }
        });

        confirmMuscleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Object> selectedExercises = Tools.getSelectedExercises(requireContext());
                if(selectedExercises.size() > 0) {
                    if(currentPage == 0) {
                        confirmMuscleButton.setText("Edit Workout Selection");
                        currentPage = 1;
                    }
                    else if(currentPage == 1) {
                        if(Boolean.FALSE.equals(MainActivity.getMain().trainingViewModel.getTrainingModifiedLiveData().getValue())) {
                            confirmMuscleButton.setText("Finish Workout Selection");
                            currentPage = 0;
                        }
                        else {
                            Fragment fragment = trainingPageAdapter.getInstantiatedFragment(currentPage);
                            if(fragment instanceof TrainingPlanStepB) {
                                TrainingPlanStepB trainingPlanStepB = (TrainingPlanStepB) fragment;
                                trainingPlanStepB.saveData();
                            }
                            MainActivity.getMain().trainingViewModel.setTrainingModified(false);
                            currentPage = 1;
                        }

                    }
                    viewPager2.setCurrentItem(currentPage);
                }
                else {
                    Toast.makeText(requireContext(), "Please select at least one exercise to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}