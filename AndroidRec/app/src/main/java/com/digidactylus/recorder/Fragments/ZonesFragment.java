package com.digidactylus.recorder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.utils.Animations;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZonesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZonesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static ZonesFragment zonesFrag = null;


    public ZonesFragment() {
        // Required empty public constructor
        zonesFrag = this;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ZonesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ZonesFragment newInstance(String param1, String param2) {
        ZonesFragment fragment = new ZonesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zones, container, false);
    }
    ImageView intensityNeedle, workCapacityNeedle;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intensityNeedle = view.findViewById(R.id.intensityNeedle);
        workCapacityNeedle = view.findViewById(R.id.workCapacityNeedle);
    }

    public static ZonesFragment getZonesFrag() {
        return zonesFrag;
    }

    public void rotateIntensity(float endRotation){
        Animations.GaugeNeedleAnimation(intensityNeedle,endRotation);
    }
    public void rotateCapacity(float endRotation){
        Animations.GaugeNeedleAnimation(workCapacityNeedle,endRotation);
    }
}