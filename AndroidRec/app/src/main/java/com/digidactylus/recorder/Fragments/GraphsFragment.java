package com.digidactylus.recorder.Fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.adapters.IntensityBarViewRVAdapter;
import com.digidactylus.recorder.adapters.capacityBarViewRVAdapter;
import com.digidactylus.recorder.models.graphItemsList;
import com.digidactylus.recorder.utils.Constants;
import com.digidactylus.recorder.utils.Tools;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<graphItemsList> intensityList = new ArrayList<>();
    List<graphItemsList> capacityList = new ArrayList<>();


    public static GraphsFragment graphsFrag = null;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GraphsFragment() {
        graphsFrag = this;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GraphsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphsFragment newInstance(String param1, String param2) {
        GraphsFragment fragment = new GraphsFragment();
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
        return inflater.inflate(R.layout.fragment_graphs, container, false);
    }
    LineChart bar_chart;
    ImageView needle;
    RecyclerView intensityRv, workCapacityRv;
    LinearLayout intensityBarContainer, workCapacityBarContainer;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intensityRv = view.findViewById(R.id.IntensityBarView);
        workCapacityRv = view.findViewById(R.id.WorkCapacityBarView);

        intensityRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        workCapacityRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }


    public static GraphsFragment getGraphsFrag() {
        return graphsFrag;
    }
    int inPosition = 1;
    int caPosition = 1;
    public void updateGraphs(List<Double> list, int type){
        if(type == 0){
            int height = getBarHeight(list.get(list.size()-1)/5,0);
            String bodyColor;
            if(list.size()>1){
                bodyColor = Tools.barColorString(list.get(list.size()-1),list.get(list.size()-2),0);
                if(bodyColor.equalsIgnoreCase("#FFFFFF")){
                    ZonesFragment.getZonesFrag().rotateIntensity(-90f);
                } else if (bodyColor.equalsIgnoreCase("#EDCFB6")) {
                    ZonesFragment.getZonesFrag().rotateIntensity(-50f);
                } else if (bodyColor.equalsIgnoreCase("#EFB37F")) {
                    ZonesFragment.getZonesFrag().rotateIntensity(-15f);
                } else if (bodyColor.equalsIgnoreCase("#FA8C2B")) {
                    ZonesFragment.getZonesFrag().rotateIntensity(25f);

                } else if (bodyColor.equalsIgnoreCase("#D9DEE4")) {
                    ZonesFragment.getZonesFrag().rotateIntensity(-130f);

                } else if (bodyColor.equalsIgnoreCase("#9EAFBF")) {
                    ZonesFragment.getZonesFrag().rotateIntensity(-170f);

                } else if (bodyColor.equalsIgnoreCase("#406E9F")) {
                    ZonesFragment.getZonesFrag().rotateIntensity(-200f);

                }
            }
            else{
                bodyColor = "#FFFFFF";
            }
            String label = String.valueOf(inPosition); // Not important. can be any value
            double actualValue = list.get(list.size()-1);
            if(intensityList.size()>20){
                intensityList.remove(0);
            }
            Log.d("kfjldsfjlkdsfsd","actual value is:"+actualValue+" resized value is:"+height);
            intensityList.add(new graphItemsList(height,bodyColor,label,actualValue));
            inPosition++;
            intensityRv.setAdapter(new IntensityBarViewRVAdapter(getActivity(),intensityList));
            //intensityRv.getAdapter().notifyDataSetChanged();
            intensityRv.getAdapter().notifyItemInserted(intensityRv.getAdapter().getItemCount() - 1);
            int lastItemPosition = intensityRv.getAdapter().getItemCount() - 1;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    intensityRv.scrollToPosition(lastItemPosition);
                }
            });
        } else if (type == 1) {
            int height = getBarHeight(list.get(list.size()-1),1);
            String bodyColor;
            if(list.size()>1){
                bodyColor = Tools.barColorString(list.get(list.size()-1),list.get(list.size()-2),1);
                if(bodyColor.equalsIgnoreCase("#FFFFFF")){
                    ZonesFragment.getZonesFrag().rotateCapacity(-90f);
                } else if (bodyColor.equalsIgnoreCase("#EDCFB6")) {
                    ZonesFragment.getZonesFrag().rotateCapacity(-50f);
                } else if (bodyColor.equalsIgnoreCase("#EFB37F")) {
                    ZonesFragment.getZonesFrag().rotateCapacity(-15f);
                } else if (bodyColor.equalsIgnoreCase("#FA8C2B")) {
                    ZonesFragment.getZonesFrag().rotateCapacity(25f);

                } else if (bodyColor.equalsIgnoreCase("#D9DEE4")) {
                    ZonesFragment.getZonesFrag().rotateCapacity(-130f);

                } else if (bodyColor.equalsIgnoreCase("#9EAFBF")) {
                    ZonesFragment.getZonesFrag().rotateCapacity(-170f);

                } else if (bodyColor.equalsIgnoreCase("#406E9F")) {
                    ZonesFragment.getZonesFrag().rotateCapacity(-200f);
                }
            }
            else{
                bodyColor = "#FFFFFF";
            }
            String label = String.valueOf(caPosition);
            double actualValue = list.get(list.size()-1);
            if(capacityList.size()>20){
                capacityList.remove(0);
            }
            capacityList.add(new graphItemsList(height,bodyColor,label,actualValue));
            caPosition++;
            workCapacityRv.setAdapter(new capacityBarViewRVAdapter(getActivity(),capacityList));
            workCapacityRv.getAdapter().notifyItemInserted(workCapacityRv.getAdapter().getItemCount() - 1);
            int lastItemPosition = workCapacityRv.getAdapter().getItemCount() - 1;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    workCapacityRv.scrollToPosition(lastItemPosition);
                }
            });
        }
        else{
            return;
        }

    }

    private int getBarHeight(double x, int type){
        if(type == 0){
            return (int) (x * Constants.AUTO_SCALE_INTENSITY);
        }
        else{
            return (int) (x * Constants.AUTO_SCALE_WORK_CAPACITY);
        }
    }

}