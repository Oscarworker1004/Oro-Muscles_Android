package com.digidactylus.recorder.Fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;
import com.digidactylus.recorder.adapters.viewPagerAdapter;
import com.digidactylus.recorder.models.athleteModel;
import com.digidactylus.recorder.utils.Constants;
import com.digidactylus.recorder.utils.TinyDB;
import com.digidactylus.recorder.utils.Tools;
import com.digidactylus.recorder.viewmodels.DataFragmentTitleViewModel;
import com.digidactylus.recorder.viewmodels.DataFragmentViewModel;
import com.digidactylus.recorder.viewmodels.recordTimerViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DataFragment extends Fragment  implements AdapterView.OnItemSelectedListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static DataFragment dataFrag = null;

    private static final String TAG = "DataFragment";


    public DataFragment() {
        dataFrag = this;
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
        return inflater.inflate(R.layout.fragment_data, container, false);
    }
    Spinner spinnerDropdown;
    ViewPager2 viewPager2;
    private int currentTab = 0;
    DataFragmentViewModel dataFragmentViewModel;
    CardView analyticsTab, graphsTab, zonesTab;
    HashMap<Integer, View> tabsList = new HashMap<>();
    DataFragmentTitleViewModel dataFragmentTitleViewModel;
    recordTimerViewModel recordTimerViewModel;
    ImageButton recordButton;
    TextView timingText, setsText;

    public LineChart lineChartDownFill;
    MaterialButton submit_workout;
    TinyDB tinyDB;
    ImageView swipeRight, swipeLeft;

    viewPagerAdapter viPagerAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2 = view.findViewById(R.id.dataPager);
        analyticsTab = view.findViewById(R.id.analyticsTab);
        graphsTab = view.findViewById(R.id.graphsTab);
        zonesTab = view.findViewById(R.id.zonesTab);
        recordButton = view.findViewById(R.id.recordButton);
        timingText = view.findViewById(R.id.timingText);
        setsText = view.findViewById(R.id.setsText);
        swipeRight = view.findViewById(R.id.swipeRight);
        swipeLeft = view.findViewById(R.id.swipeLeft);
        submit_workout = view.findViewById(R.id.submit_workout);
        tinyDB = new TinyDB(getActivity());
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.getMain().m_isRecording){
                    recordButton.setBackground(getResources().getDrawable(R.drawable.img_button_radius));
                    recordButton.setImageResource(R.drawable.ic_dot);
                    MainActivity.getMain().stopRecord();
                }
                else{
                    recordButton.setBackground(getResources().getDrawable(R.drawable.img_button_radius_stop));
                    recordButton.setImageResource(R.drawable.stopsquare);
                    MainActivity.getMain().startRecord();
                }
            }
        });
        tabsList.put(0,analyticsTab);
        tabsList.put(1,graphsTab);
        tabsList.put(2,zonesTab);
        dataFragmentTitleViewModel = new ViewModelProvider(getActivity()).get(DataFragmentTitleViewModel.class);

        viPagerAdapter = new viewPagerAdapter(getActivity());
        viewPager2.setAdapter(viPagerAdapter);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setUserInputEnabled(false);
        dataFragmentViewModel = new ViewModelProvider(getActivity()).get(DataFragmentViewModel.class);
        dataFragmentViewModel.getLiveCurrentTab().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                ColorStateList colorStateListLight = ColorStateList.valueOf(getResources().getColor(R.color.orange));
                CardView pastTab = (CardView) tabsList.get(currentTab);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    pastTab.setBackgroundTintList(colorStateListLight);
                }
                if(integer == 0){
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.darkOrange));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        analyticsTab.setBackgroundTintList(colorStateList);
                        currentTab = 0;
                        viewPager2.setCurrentItem(0);

                    }


                } else if (integer == 1) {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.darkOrange));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        graphsTab.setBackgroundTintList(colorStateList);
                        currentTab = 1;
                        viewPager2.setCurrentItem(1);

                    }

                } else if (integer == 2) {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.darkOrange));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        zonesTab.setBackgroundTintList(colorStateList);
                        currentTab = 2;
                        viewPager2.setCurrentItem(2);
                    }
                }
            }
        });

        dataFragmentTitleViewModel.getLivecurrentTime().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                timingText.setText(s);
            }
        });



        analyticsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataFragmentTitleViewModel.setCurrentTabTitle("Data - Analytics");
                viewPager2.setCurrentItem(0, false);
                dataFragmentViewModel.setCurrentTab(0);
                currentTab = 0;
                swipeLeft.setImageTintList(ColorStateList.valueOf(Color.parseColor("#784016")));
                swipeRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F1A250")));


            }
        });
        graphsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataFragmentTitleViewModel.setCurrentTabTitle("Data - Graphs");
                viewPager2.setCurrentItem(1,false);
                dataFragmentViewModel.setCurrentTab(1);
                currentTab = 1;
                swipeLeft.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F1A250")));
                swipeRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F1A250")));


            }
        });
        zonesTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataFragmentTitleViewModel.setCurrentTabTitle("Data - Zones");
                viewPager2.setCurrentItem(2,false);
                dataFragmentViewModel.setCurrentTab(2);
                currentTab = 2;
                swipeLeft.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F1A250")));
                swipeRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#784016")));

            }
        });

        submit_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Object> athleteList = Tools.getPrefValue(requireContext(), Constants.PREF_ATHLETE_LIST, athleteModel.class);
                int selectedAthleteIndex = Tools.getPrefValue(requireContext(), Constants.PREF_SELECTED_ATHLETE);

                if(selectedAthleteIndex >= 0){
                    athleteModel aInfo = (athleteModel) athleteList.get(selectedAthleteIndex);
                    MainActivity.getMain().submitWorkout(aInfo.getId());
                }
                else{
                    Toast.makeText(getActivity(), "You need to select an athlete to submit workouts", Toast.LENGTH_LONG).show();
                }

            }
        });


        swipeLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentTab == 1) {
                    dataFragmentTitleViewModel.setCurrentTabTitle("Data - Analytics");
                    viewPager2.setCurrentItem(0,false);
                    dataFragmentViewModel.setCurrentTab(0);
                    currentTab = 0;
                    swipeLeft.setImageTintList(ColorStateList.valueOf(Color.parseColor("#784016")));
                    swipeRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F1A250")));

                } else if (currentTab == 2) {
                    dataFragmentTitleViewModel.setCurrentTabTitle("Data - Graphs");
                    viewPager2.setCurrentItem(1,false);
                    dataFragmentViewModel.setCurrentTab(1);
                    currentTab = 1;
                    swipeRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F1A250")));

                }
            }
        });

        swipeRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentTab == 0) {
                    dataFragmentTitleViewModel.setCurrentTabTitle("Data - Graphs");
                    viewPager2.setCurrentItem(1,false);
                    dataFragmentViewModel.setCurrentTab(1);
                    currentTab = 1;
                    swipeLeft.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F1A250")));
                } else if (currentTab == 1) {
                    dataFragmentTitleViewModel.setCurrentTabTitle("Data - Zones");
                    viewPager2.setCurrentItem(2,false);
                    dataFragmentViewModel.setCurrentTab(2);
                    currentTab = 2;
                    swipeRight.setImageTintList(ColorStateList.valueOf(Color.parseColor("#784016")));

                }
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setTimingText(String value){
        timingText.setText(value);
    }

    public void setSetsText(String text) {
        setsText.setText(text);
    }

    public static DataFragment getdataFrag() {
        return dataFrag;
    }

    public void noSensor(){
        recordButton.setBackground(getResources().getDrawable(R.drawable.img_button_radius));
        recordButton.setImageResource(R.drawable.ic_dot);
        Toast.makeText(getActivity(), "No sensor found!", Toast.LENGTH_SHORT).show();
    }

    public void doWork() {
        int currentPos = viewPager2.getCurrentItem();
        Fragment fragment = viPagerAdapter.getInstantiatedFragment(currentPos);
        if(fragment instanceof AnalyticsFragment) {
            AnalyticsFragment connectionFragment = (AnalyticsFragment) fragment;
            connectionFragment.changeAthlete();
        }
    }

}