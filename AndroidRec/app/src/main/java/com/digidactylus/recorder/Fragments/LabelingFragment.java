package com.digidactylus.recorder.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;
import com.digidactylus.recorder.adapters.LabelingExerciseAdapter;
import com.digidactylus.recorder.adapters.LabelingGridViewAdapter;
import com.digidactylus.recorder.models.ExerciseModel;
import com.digidactylus.recorder.models.LabelingModel;
import com.digidactylus.recorder.models.MuscleMapModel;
import com.digidactylus.recorder.utils.Constants;
import com.digidactylus.recorder.utils.TinyDB;
import com.digidactylus.recorder.utils.Tools;
import com.google.android.material.button.MaterialButton;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class LabelingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters


    public LabelingFragment() {
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
        return inflater.inflate(R.layout.fragment_labeling, container, false);
    }

    RecyclerView recyclerView;
    CardView dataCard;

    LabelingGridViewAdapter adapter;

    ArrayList<Object> labelingModels = new ArrayList<>();

    int lastItemClicked = -1;

    TinyDB tinyDB;
    TextView startView, repsText, duration_text, selected_muscle, intensity_text, capacity_text;
    MaterialButton deleteDataSet;

    PowerSpinnerView exerciseSpinner;
    List<String> exerciseModels;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Sample data for the GridView

        tinyDB = new TinyDB(requireContext());

        recyclerView = view.findViewById(R.id.dataList);
        dataCard = view.findViewById(R.id.dataCard);
        startView = view.findViewById(R.id.start_time_text);
        repsText = view.findViewById(R.id.repsText);
        duration_text = view.findViewById(R.id.duration_text);
        selected_muscle = view.findViewById(R.id.selected_muscle);
        intensity_text = view.findViewById(R.id.intensity_text);
        capacity_text = view.findViewById(R.id.capacity_text);
        deleteDataSet = view.findViewById(R.id.deleteDataSet);
        exerciseSpinner = view.findViewById(R.id.exerciseSpinner);


         exerciseModels = Tools.getSelectedExercisesString(requireContext());


        exerciseSpinner.setItems(exerciseModels);
        exerciseSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(int i, @Nullable Object o, int i1, Object t1) {
                ExerciseModel exerciseModel = Tools.getSelectedExerciseByIndex(requireContext(), (String)t1);
                if(exerciseModel != null) {
                    repsText.setText(String.valueOf(exerciseModel.getReps()));
                    String shortWord = Tools.shortExercise(exerciseModel.getName());

                    String shortExercise = String.format(Locale.getDefault(), "%s - %d kg - %d reps", shortWord, exerciseModel.getWeight(), exerciseModel.getReps());
                    ((LabelingModel) labelingModels.get(lastItemClicked)).setExerciaseString(shortExercise);
                }
                ((LabelingModel) labelingModels.get(lastItemClicked)).setExerciseModel(exerciseModel);

                adapter.notifyItemChanged(lastItemClicked);
                tinyDB.putListObject(Constants.LABELING_LIST, labelingModels);
            }
        });




        labelingModels.addAll(tinyDB.getListObject(Constants.LABELING_LIST, LabelingModel.class));

        adapter = new LabelingGridViewAdapter(requireActivity(), labelingModels, new LabelingGridViewAdapter.ItemListener() {
            @Override
            public void onItemClick(int position) {

                if(lastItemClicked != position) {
                    if(lastItemClicked != -1) {
                        ((LabelingModel) labelingModels.get(lastItemClicked)).setSelected(false);
                        adapter.notifyItemChanged(lastItemClicked);
                    }
                    dataCard.setVisibility(View.VISIBLE);
                    lastItemClicked = position;
                    changeBottomInfo(position);
                    ((LabelingModel) labelingModels.get(position)).setSelected(true);
                }
                else {
                    dataCard.setVisibility(View.GONE);
                    lastItemClicked = -1;
                    ((LabelingModel) labelingModels.get(position)).setSelected(false);
                }
                adapter.notifyItemChanged(position);
            }
        });

        MainActivity.getMain().labelingModel.setOnDataChanged(new LabelingModel.OnDataChanged() {
            @Override
            public void onData() {
                if(MainActivity.getMain().labelingShowing) {
                    adapter.notifyItemChanged(labelingModels.size() - 1);
                }
            }

            @Override
            public void onAllChanged() {
                tinyDB.putListObject(Constants.LABELING_LIST, labelingModels);
            }

            @Override
            public void onStartRecord() {
                labelingModels.add(MainActivity.getMain().labelingModel);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onBarChanged() {
                adapter.notifyDataSetChanged();

            }
        });

        deleteDataSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastItemClicked > -1) {
                    labelingModels.remove(lastItemClicked);
                    adapter.notifyDataSetChanged();
                    tinyDB.putListObject(Constants.LABELING_LIST, labelingModels);
                    lastItemClicked = -1;
                    dataCard.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(requireContext(), "Please select data set first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.setAdapter(adapter);
        int itemCount = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();

        if (itemCount > 0) {
            recyclerView.scrollToPosition(itemCount - 1);
        }
    }

    private void changeBottomInfo(int position) {
        LabelingModel labelingModel = (LabelingModel) labelingModels.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
        if(labelingModel.getStartTime() > 0) {
            Date currentDate = new Date(labelingModel.getStartTime());
            String formattedTime = sdf.format(currentDate);
            startView.setText(formattedTime);
        }

        if(labelingModel.getMuscleMapModel() != null) {
            selected_muscle.setText(labelingModel.getMuscleMapModel().getMuscleModel().getItemTitle());
        }

        if(labelingModel.getDuration() != null && !labelingModel.getDuration().isEmpty()) {
            duration_text.setText(labelingModel.getDuration().substring(0, labelingModel.getDuration().lastIndexOf(".")));
        }

        int index = -1;
        if(labelingModel.getExerciseModel() != null) {
            for(int i = 0; i < exerciseModels.size(); i++) {
                String item = exerciseModels.get(i);
                if(item.equalsIgnoreCase(labelingModel.getExerciseModel().getName())) {
                    index = i;
                }
            }
            repsText.setText(String.valueOf(labelingModel.getExerciseModel().getReps()));
        }

        if(index > -1) {
            exerciseSpinner.getSpinnerAdapter().setIndex(index);
            exerciseSpinner.getSpinnerAdapter().notifyItemSelected(index);
        }
        else {
            exerciseSpinner.clearSelectedItem();
            repsText.setText("-");
        }



        intensity_text.setText(String.valueOf((int) labelingModel.getIntensity()));
        capacity_text.setText(String.valueOf((int) labelingModel.getWorkCapacity()));

    }

}