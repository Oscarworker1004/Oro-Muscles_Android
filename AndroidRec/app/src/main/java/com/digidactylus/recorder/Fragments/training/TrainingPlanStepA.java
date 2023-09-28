package com.digidactylus.recorder.Fragments.training;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.adapters.TrainingPlanAdapter;
import com.digidactylus.recorder.models.ExerciseModel;
import com.digidactylus.recorder.models.TrainingModel;
import com.digidactylus.recorder.utils.CustomExerciseDialog;
import com.digidactylus.recorder.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TrainingPlanStepA extends Fragment {

    private static final String TAG = "TrainingPlanStepA";
    RecyclerView expandableListView;
    EditText searchEditText;

    private TrainingPlanAdapter expandableListAdapter;
    LinearLayout createNew;
    CustomExerciseDialog exerciseDialog;

    List<TrainingModel> trainingModels = new ArrayList<>();


    public TrainingPlanStepA() {
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
        return inflater.inflate(R.layout.fragment_training_plan_step_a, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expandableListView = view.findViewById(R.id.expandableListView);
        searchEditText = view.findViewById(R.id.searchEditText);
        createNew = view.findViewById(R.id.createNew);


        exerciseDialog = new CustomExerciseDialog(requireActivity(), false);
        createNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseDialog.show();
            }
        });

        exerciseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TrainingModel trainingModel = Tools.getCustomExercises(requireActivity());
                if(trainingModel != null) {
                    trainingModels.clear();
                    trainingModels.add(trainingModel);
                    trainingModels.addAll(Tools.readExercisesJSON(requireContext()));
                    expandableListAdapter.notifyDataSetChanged();
                }
            }
        });

        TrainingModel trainingModel = Tools.getCustomExercises(requireActivity());
        if(trainingModel != null) {
            trainingModels.add(trainingModel);
        }

        trainingModels.addAll(Tools.readExercisesJSON(requireContext()));


        expandableListAdapter = new TrainingPlanAdapter(requireContext(), trainingModels);
        expandableListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        expandableListView.setAdapter(expandableListAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                //   expandableListAdapter.filterItems(s.toString());
                String searchQuery = charSequence.toString().toLowerCase();
                ArrayList<TrainingModel> filteredList = new ArrayList<>();

                for(TrainingModel item : trainingModels) {
                    TrainingModel trainingModel1 = new TrainingModel();
                    trainingModel1.setCustom(item.isCustom());
                    trainingModel1.setCategory(item.getCategory());
                    ArrayList<ExerciseModel> exerciseModels = new ArrayList<>();
                    for(ExerciseModel exerciseModel : item.getExerciseModelList()) {
                        if(exerciseModel.getName().toLowerCase().contains(searchQuery)) {
                            exerciseModels.add(exerciseModel);
                        }
                    }
                    trainingModel1.setExerciseModelList(exerciseModels);
                    if(!exerciseModels.isEmpty()) {
                        filteredList.add(trainingModel1);
                    }
                }

                expandableListAdapter.setmData(filteredList);
                expandableListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void notifyData() {
        TrainingModel trainingModel = Tools.getCustomExercises(requireActivity());
        List<TrainingModel> list = new ArrayList<>();
        if(trainingModel != null) {
            list.add(trainingModel);
        }

        list.addAll(Tools.readExercisesJSON(requireContext()));

        expandableListAdapter.setmData(list);
        expandableListAdapter.notifyDataSetChanged();
    }
}