package com.digidactylus.recorder.Fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.athleteModel;
import com.digidactylus.recorder.ui.DataBuffer;
import com.digidactylus.recorder.utils.ApiManager;
import com.digidactylus.recorder.utils.Constants;
import com.digidactylus.recorder.utils.TinyDB;
import com.digidactylus.recorder.utils.Tools;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AnalyticsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "AnalyticsFragment";

    boolean atheletFetched = false;

    public AnalyticsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }
    PowerSpinnerView workoutSpinner, athleteSpinner;
    TextView workoutSelection, athleteSelection;
    LineChart lineChartDownFill;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workoutSpinner = view.findViewById(R.id.workoutSpinner);
        athleteSpinner = view.findViewById(R.id.athleteSpinner);
        workoutSelection = view.findViewById(R.id.workoutSelection);
        athleteSelection = view.findViewById(R.id.athleteSelection);


        //lineChartDownFill = view.findViewById(R.id.lineChartDownFill);
        workoutSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(int i, @Nullable Object o, int i1, Object t1) {
                workoutSpinner.clearSelectedItem();
                workoutSelection.setText(t1.toString());
            }
        });
        workoutSpinner.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
                workoutSpinner.dismiss();
            }
        });
        athleteSpinner.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
                athleteSpinner.dismiss();
            }
        });
        athleteSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(int i, @Nullable Object o, int i1, Object t1) {
                athleteSpinner.clearSelectedItem();
                athleteSelection.setText(t1.toString());
                Tools.savePrefValue(requireContext(), Constants.PREF_SELECTED_ATHLETE, i1);
            }
        });

        ApiManager apiManager = new ApiManager(requireContext());
        apiManager.retrieveAthletes(new ApiManager.OnApiResult() {
            @Override
            public void OnSuccess(JSONObject jsonObject) {
                try {
                    JSONObject user_data = jsonObject.getJSONObject("user_data");
                    JSONArray athletes = user_data.getJSONArray("athletes");
                    List<String> spinnerList = new ArrayList<>();
                    ArrayList<Object> athleteList = new ArrayList<>();
                    for(int i = 0; i<athletes.length();i++){
                        JSONObject athlete = athletes.getJSONObject(i);
                        spinnerList.add(athlete.getString("name"));
                        athleteModel athleteModel = new athleteModel(athlete.getString("_id"),athlete.getString("name"));
                        athleteList.add(athleteModel);
                    }
                    athleteSpinner.setItems(spinnerList);

                    Tools.savePrefValue(requireContext(), Constants.PREF_ATHLETE_LIST, athleteList);

                    atheletFetched = true;

                } catch (JSONException e) {
                    Log.e(TAG, "OnSuccess: " + e.getMessage() );
                    Tools.savePrefValue(requireContext(), Constants.PREF_ATHLETE_LIST, new ArrayList<>());
                    Tools.savePrefValue(requireContext(), Constants.PREF_SELECTED_ATHLETE, -1);
                }
            }

            @Override
            public void OnFailed(String error) {
                Log.e(TAG, "OnFailed: " + error );
                Tools.savePrefValue(requireContext(), Constants.PREF_ATHLETE_LIST, new ArrayList<>());
                Tools.savePrefValue(requireContext(), Constants.PREF_SELECTED_ATHLETE, -1);
            }
        });

    }

    public void changeAthlete() {
        if(atheletFetched) {
            int selectedAthleteIndex = Tools.getPrefValue(requireContext(), Constants.PREF_SELECTED_ATHLETE);
            if (selectedAthleteIndex > -1) {
                List<Object> athleteList = Tools.getPrefValue(requireContext(), Constants.PREF_ATHLETE_LIST, athleteModel.class);
                if (athleteList.size() > selectedAthleteIndex) {
                    athleteSelection.setText(((athleteModel) athleteList.get(selectedAthleteIndex)).getName());
                }
            }
        }
    }
}