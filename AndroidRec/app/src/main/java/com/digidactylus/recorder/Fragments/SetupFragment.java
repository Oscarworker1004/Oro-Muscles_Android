package com.digidactylus.recorder.Fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.adapters.SetupPagerAdapter;
import com.digidactylus.recorder.viewmodels.DataFragmentTitleViewModel;
import com.digidactylus.recorder.viewmodels.SetupFragmentViewModel;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SetupFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "SetupFragment";

    public SetupFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setup, container, false);
    }
    ViewPager2 viewPager2;
    private int currentTab = 0;
    CardView connectionTab, trainingPlanTab, muscleMapTab;
    HashMap<Integer, View> tabsList = new HashMap<>();
    DataFragmentTitleViewModel dataFragmentTitleViewModel;

    SetupPagerAdapter setupPagerAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2 = view.findViewById(R.id.setupPager);
        connectionTab = view.findViewById(R.id.connectionTab);
        trainingPlanTab = view.findViewById(R.id.trainingPlanTab);
        muscleMapTab = view.findViewById(R.id.muscleMapTab);
        tabsList.put(0,connectionTab);
        tabsList.put(1,trainingPlanTab);
        tabsList.put(2,muscleMapTab);
        dataFragmentTitleViewModel = new ViewModelProvider(getActivity()).get(DataFragmentTitleViewModel.class);

        setupPagerAdapter = new SetupPagerAdapter(getActivity());
        viewPager2.setAdapter(setupPagerAdapter);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setUserInputEnabled(false);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int integer) {
                ColorStateList colorStateListLight = ColorStateList.valueOf(getResources().getColor(R.color.orange));
                CardView pastTab = (CardView) tabsList.get(currentTab);
                pastTab.setBackgroundTintList(colorStateListLight);

                if(integer == 0){
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.darkOrange));
                    connectionTab.setBackgroundTintList(colorStateList);
                    currentTab = 0;
                    viewPager2.setCurrentItem(0);


                } else if (integer == 1) {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.darkOrange));
                    trainingPlanTab.setBackgroundTintList(colorStateList);
                    currentTab = 1;
                    viewPager2.setCurrentItem(1);



                } else if (integer == 2) {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.darkOrange));
                    muscleMapTab.setBackgroundTintList(colorStateList);
                    currentTab = 2;
                    viewPager2.setCurrentItem(2);


                }
            }
        });

        connectionTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataFragmentTitleViewModel.setCurrentTabTitle("Set-up Connection");
                viewPager2.setCurrentItem(0, false);
                currentTab = 0;
            }
        });
        trainingPlanTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataFragmentTitleViewModel.setCurrentTabTitle("Set-up Training Plan");
                viewPager2.setCurrentItem(1,false);
                currentTab = 1;
            }
        });
        muscleMapTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataFragmentTitleViewModel.setCurrentTabTitle("Set-up Muscle Map");
                viewPager2.setCurrentItem(2,false);
                currentTab = 2;
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void doWork() {
        int currentPos = viewPager2.getCurrentItem();
        Fragment fragment = setupPagerAdapter.getInstantiatedFragment(currentPos);
        if(fragment instanceof ConnectionFragment) {
            ConnectionFragment connectionFragment = (ConnectionFragment) fragment;
            connectionFragment.changeAthlete();
        }
    }

    public void seletectTraining() {
        viewPager2.setCurrentItem(1);
    }

}