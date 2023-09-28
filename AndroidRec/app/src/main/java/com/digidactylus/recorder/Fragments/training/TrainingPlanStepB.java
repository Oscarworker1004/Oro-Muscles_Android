package com.digidactylus.recorder.Fragments.training;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.adapters.ExpandableListAdapter;
import com.digidactylus.recorder.adapters.TrainingPlanAdapter;
import com.digidactylus.recorder.models.ExerciseModel;
import com.digidactylus.recorder.models.TrainingModel;
import com.digidactylus.recorder.utils.Constants;
import com.digidactylus.recorder.utils.CustomExerciseDialog;
import com.digidactylus.recorder.utils.TinyDB;
import com.digidactylus.recorder.utils.Tools;
import com.digidactylus.recorder.viewmodels.TrainingViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TrainingPlanStepB extends Fragment {

    ExpandableListView expandableListView;
    EditText searchEditText;

    LinearLayout createNew;
    CustomExerciseDialog exerciseDialog;

    ArrayList<Object> exerciseModels = new ArrayList<>();

    ExpandableListAdapter expandableListAdapter;

    private static final String TAG = "TrainingPlanStepB";

    public TrainingPlanStepB() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_plan_step_b, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expandableListView = view.findViewById(R.id.expandableListView);
        searchEditText = view.findViewById(R.id.searchEditText);
        createNew = view.findViewById(R.id.createNew);
        createNew.setVisibility(View.GONE);

        /*exerciseDialog = new CustomExerciseDialog(requireActivity(), true);

        createNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseDialog.show();
            }
        });

        exerciseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                notifyData();
            }
        });*/

        exerciseModels = Tools.getSelectedExercises(requireContext());


      /*  Comparator<Object> idComparator = new Comparator<Object>() {
            @Override
            public int compare(Object exercise1, Object exercise2) {
                return Integer.compare(((ExerciseModel) exercise1).getId(), ((ExerciseModel) exercise2).getId());
            }
        };

        Collections.sort(exerciseModels, idComparator);
*/

        expandableListAdapter = new ExpandableListAdapter(requireContext(), exerciseModels);
        expandableListView.setAdapter(expandableListAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                //   expandableListAdapter.filterItems(s.toString());
                String searchQuery = charSequence.toString().toLowerCase();
                ArrayList<Object> filteredList = new ArrayList<>();

                for (Object item : exerciseModels) {
                    ExerciseModel exerciseModel = (ExerciseModel) item;
                    if (exerciseModel.getName().toLowerCase().contains(searchQuery)) {
                        filteredList.add(item);
                    }
                }

                expandableListAdapter.setParentItems(filteredList);
                expandableListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void notifyData() {
        ArrayList<Object> objects;
        objects = Tools.getSelectedExercises(requireContext());

        expandableListAdapter.setParentItems(objects);
        expandableListAdapter.notifyDataSetChanged();
    }

    public void saveData() {
        expandableListAdapter.notifyDataSetChanged();
        expandableListAdapter.makeChanges();

        ArrayList<Object> list = expandableListAdapter.getParentItems();
        TinyDB tinyDB = new TinyDB(requireContext());
        tinyDB.putListObject(Constants.PREF_SELECTED_EXERCIESES, list);
        Toast.makeText(requireContext(), "Workout saved", Toast.LENGTH_SHORT).show();
    }
}