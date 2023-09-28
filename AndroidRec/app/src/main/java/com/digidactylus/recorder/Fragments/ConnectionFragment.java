package com.digidactylus.recorder.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.athleteModel;
import com.digidactylus.recorder.ui.FeedView;
import com.digidactylus.recorder.ui.MLog;
import com.digidactylus.recorder.utils.ApiManager;
import com.digidactylus.recorder.utils.BleDialog;
import com.digidactylus.recorder.utils.Constants;
import com.digidactylus.recorder.utils.TinyDB;
import com.digidactylus.recorder.utils.Tools;
import com.google.android.material.button.MaterialButton;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ConnectionFragment extends Fragment {


    public ConnectionFragment() {
        // Required empty public constructor
    }


    private static final String TAG = "ConnectionFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection, container, false);
    }
    SeekBar s1, s2;
    TextView todaysDate, sensoronestatus;

    TextView connectionAthleteText;

    EditText workoutEditText;

    TinyDB tinyDB;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todaysDate = view.findViewById(R.id.todaysDate);
        sensoronestatus = view.findViewById(R.id.sensoronestatus);
        connectionAthleteText = view.findViewById(R.id.connectionAthleteText);
        workoutEditText = view.findViewById(R.id.workoutEditText);

        tinyDB = new TinyDB(requireContext());

        workoutEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    tinyDB.putString(Constants.PREF_WORKOUT_NAME, s.toString());
                }
            }
        });

        sensoronestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new BleDialog(requireActivity());
                dialog.show();
            }
        });
        todaysDate.setText(getCurrentDateFormatted());

        List<Object> athleteModels = Tools.getPrefValue(requireContext(), Constants.PREF_ATHLETE_LIST, athleteModel.class);
        PowerSpinnerView connectionAthleteSpinner  = view.findViewById(R.id.connectionAthleteSpinner);

        ArrayList<Object> athleteList = new ArrayList<>();
        if(athleteModels.size() > 0) {
            for (int i = 0; i < athleteModels.size(); i++) {
                athleteModel athleteMod = (athleteModel) athleteModels.get(i);
                athleteList.add(athleteMod.getName());
            }
            connectionAthleteSpinner.setItems(athleteList);
        }
        else {
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
                        connectionAthleteSpinner.setItems(spinnerList);

                        Tools.savePrefValue(requireContext(), Constants.PREF_ATHLETE_LIST, athleteList);


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


        connectionAthleteSpinner.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
                connectionAthleteSpinner.dismiss();
            }
        });
        connectionAthleteSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(int i, @Nullable Object o, int i1, Object t1) {
                connectionAthleteSpinner.clearSelectedItem();
                connectionAthleteText.setText(t1.toString());
                Tools.savePrefValue(requireContext(), Constants.PREF_SELECTED_ATHLETE, i1);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(MainActivity.getMain().m_isConnectBLE){
                    sensoronestatus.setText("Connected");
                    sensoronestatus.setTextColor(Color.parseColor("#3DD598"));
                }
                else{
                    if(sensoronestatus.getText() == "Connected"){
                        sensoronestatus.setText("Not found");
                        sensoronestatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.redTextColor));
                    }
                    handler.postDelayed(this, 500);
                }
            }
        },500);

        s1 = view.findViewById(R.id.signalBar);
        s2 = view.findViewById(R.id.lengthBar);

        s1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                saveApplySensSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        s2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                saveApplySensSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        loadSensSettings();

    }

    private void loadSensSettings() {


        int min1 = 1;
        int min2 = 1;
        int max1 = 500;
        int max2 = 200;
        int val1 = 200;
        int val2 = 50;

        if (!MainActivity.getMain().getPref("sensitivity1_val").equals("")) {
            min1 = Integer.parseInt(MainActivity.getMain().getPref("sensitivity1_min"));
            max1 = Integer.parseInt(MainActivity.getMain().getPref("sensitivity1_max"));
            val1 = Integer.parseInt(MainActivity.getMain().getPref("sensitivity1_val"));
            min2 = Integer.parseInt(MainActivity.getMain().getPref("sensitivity2_min"));
            max2 = Integer.parseInt(MainActivity.getMain().getPref("sensitivity2_max"));
            val2 = Integer.parseInt(MainActivity.getMain().getPref("sensitivity2_val"));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            s1.setMin(min1);
            s2.setMin(min2);
        }
        s1.setMax(max1);
        s1.setProgress(val1);

        s2.setMax(max2);
        s2.setProgress(val2);

        if (FeedView.getFeed() != null) {
            FeedView.getFeed().getRMSAnalayze().setFloorOff(s1.getProgress());
            FeedView.getFeed().getRMSAnalayze().setPeakMinDist(s2.getProgress());
            MLog.log("loadUpdate ANALYSIS: set Floor Offset: " + s1.getProgress());
            MLog.log("loadUpdate ANALYSIS: set Peak Min: " + s2.getProgress());
        }

    }

    private void saveApplySensSettings() {

        String s = new String();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            s += s1.getMin();
            MainActivity.getMain().setPref("sensitivity1_min", s);
            s = "";
            s += s2.getMin();
            MainActivity.getMain().setPref("sensitivity2_min", s);
        } else {
            MainActivity.getMain().setPref("sensitivity1_min", "1");
            MainActivity.getMain().setPref("sensitivity1_min", "1");
        }
        s = "";
        s += s1.getMax();
        MainActivity.getMain().setPref("sensitivity1_max", s);
        s = "";
        s += s2.getMax();
        MainActivity.getMain().setPref("sensitivity2_max", s);

        s = "";
        s += s1.getProgress();
        MainActivity.getMain().setPref("sensitivity1_val", s);
        s = "";
        s += s2.getProgress();
        MainActivity.getMain().setPref("sensitivity2_val", s);

        if (FeedView.getFeed() != null) {
            FeedView.getFeed().getRMSAnalayze().setFloorOff(s1.getProgress());
            FeedView.getFeed().getRMSAnalayze().setPeakMinDist(s2.getProgress());
            MLog.log("saveUpdate ANALYSIS: set Floor Offset: " + s1.getProgress());
            MLog.log("saveUpdate ANALYSIS: set Peak Min: " + s2.getProgress());
        }
    }


    private String getCurrentDateFormatted() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        return dateFormat.format(currentDate);
    }


    public void changeAthlete() {
        int selectedAthleteIndex = Tools.getPrefValue(requireContext(), Constants.PREF_SELECTED_ATHLETE);
        if(selectedAthleteIndex > -1) {
            List<Object> athleteList = Tools.getPrefValue(requireContext(), Constants.PREF_ATHLETE_LIST, athleteModel.class);
            if(athleteList.size() > selectedAthleteIndex) {
                connectionAthleteText.setText(((athleteModel) athleteList.get(selectedAthleteIndex)).getName());
            }
        }
    }
}