package com.digidactylus.recorder.Fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;
import com.digidactylus.recorder.adapters.MuscleMapAdapter;
import com.digidactylus.recorder.models.ExerciseModel;
import com.digidactylus.recorder.models.MuscleMapModel;
import com.digidactylus.recorder.models.MuscleModel;
import com.digidactylus.recorder.models.TrainingModel;
import com.digidactylus.recorder.utils.Constants;
import com.digidactylus.recorder.utils.GridSpacingItemDecoration;
import com.digidactylus.recorder.utils.TinyDB;
import com.digidactylus.recorder.utils.Tools;
import com.digidactylus.recorder.viewmodels.MuscleMapViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MuscleMapFragment extends Fragment {

    private static final String TAG = "MuscleMapFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_muscle_map, container, false);
    }

    MuscleMapAdapter adapter;
    EditText searchEditText;
    HashMap<Constants.MuscleBody, View> bodyParts = new HashMap<>();
    MaterialButton leftSide, rightSide;
    ImageView upperLeg, lowerLeg, upperBody, arms, backNeck, head;
    MuscleMapViewModel muscleMapViewModel;
    Constants.MuscleSide currentSide = Constants.MuscleSide.RIGHT;
    Constants.MuscleBody currentBodyPart = Constants.MuscleBody.UPPER_LEG;

    List<MuscleModel> gridItems = new ArrayList<>();

    Map<String, List<MuscleModel>> muscleMap = new HashMap<>();


    int selectedPosition = -1;
    TinyDB tinyDB;

    MuscleMapModel selectedMuscle;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leftSide = view.findViewById(R.id.leftSideButton);
        rightSide = view.findViewById(R.id.rightSideButton);
        upperLeg = view.findViewById(R.id.upperLeg);
        lowerLeg = view.findViewById(R.id.lowerLeg);
        upperBody = view.findViewById(R.id.upperBody);
        arms = view.findViewById(R.id.arms);
        backNeck = view.findViewById(R.id.backNeck);
        head = view.findViewById(R.id.head);

        tinyDB = new TinyDB(requireContext());

        selectedMuscle = tinyDB.getObject(Constants.PREF_SELECTED_MUSCLE, MuscleMapModel.class);


        rightSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muscleMapViewModel.setCurrentSide(0);
            }
        });
        leftSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muscleMapViewModel.setCurrentSide(1);
            }
        });

        // Body Parts
        bodyParts.put(Constants.MuscleBody.UPPER_LEG, upperLeg);
        bodyParts.put(Constants.MuscleBody.LOWER_LEG, lowerLeg);
        bodyParts.put(Constants.MuscleBody.UPPER_BODY, upperBody);
        bodyParts.put(Constants.MuscleBody.ARMS, arms);
        bodyParts.put(Constants.MuscleBody.BACK, backNeck);
        bodyParts.put(Constants.MuscleBody.HEAD, head);

        upperLeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muscleMapViewModel.setCurrentBodyPart(0);
            }
        });
        lowerLeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muscleMapViewModel.setCurrentBodyPart(1);
            }
        });
        upperBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muscleMapViewModel.setCurrentBodyPart(2);
            }
        });
        arms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muscleMapViewModel.setCurrentBodyPart(3);
            }
        });
        backNeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muscleMapViewModel.setCurrentBodyPart(4);
            }
        });
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muscleMapViewModel.setCurrentBodyPart(5);
            }
        });

        muscleMap = Tools.readMusclesJSON(requireContext());

        gridItems.addAll(muscleMap.get(currentBodyPart.name()));

        RecyclerView recyclerView = view.findViewById(R.id.gridView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 20, false));


        adapter = new MuscleMapAdapter(requireContext(), gridItems, new MuscleMapAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                if (position != selectedPosition) {
                    if (selectedPosition != -1) {
                        adapter.getMuscles().get(selectedPosition).setSelected(false);
                    }
                    selectedPosition = position;
                    adapter.getMuscles().get(position).setSelected(true);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        recyclerView.setAdapter(adapter);


        muscleMapViewModel = new ViewModelProvider(requireActivity()).get(MuscleMapViewModel.class);
        muscleMapViewModel.getLiveCurrentSide().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.workoutSelectionColorOrange));
                    ColorStateList colorStateListPrev = ColorStateList.valueOf(getResources().getColor(R.color.black));
                    rightSide.setBackgroundTintList(colorStateList);
                    rightSide.setStrokeWidth(0);
                    MaterialButton otherBtn = (MaterialButton) leftSide;
                    otherBtn.setStrokeWidth(4);
                    otherBtn.setBackgroundTintList(colorStateListPrev);
                    currentSide = Constants.MuscleSide.RIGHT;

                } else if (integer == 1) {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.workoutSelectionColorOrange));
                    ColorStateList colorStateListPrev = ColorStateList.valueOf(getResources().getColor(R.color.black));
                    leftSide.setBackgroundTintList(colorStateList);
                    leftSide.setStrokeWidth(0);
                    MaterialButton otherBtn = (MaterialButton) rightSide;
                    otherBtn.setStrokeWidth(4);
                    otherBtn.setBackgroundTintList(colorStateListPrev);
                    currentSide = Constants.MuscleSide.LEFT;
                }
            }
        });

        muscleMapViewModel.getLiveCurrentBodyPart().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.workoutSelectionColorOrange));
                ColorStateList colorStateListPrev = ColorStateList.valueOf(getResources().getColor(R.color.mucleMapUnselectedGrey));
                ImageView currentPart = (ImageView) bodyParts.get(currentBodyPart);

                switch (integer) {
                    case 0:
                    default:
                        upperLeg.setImageTintList(colorStateList);
                        if (currentPart != null && currentPart != upperLeg)
                            currentPart.setImageTintList(colorStateListPrev);
                        currentBodyPart = Constants.MuscleBody.UPPER_LEG;
                        changeGridView();
                        break;
                    case 1:
                        lowerLeg.setImageTintList(colorStateList);
                        if (currentPart != null && currentPart != lowerLeg)
                            currentPart.setImageTintList(colorStateListPrev);
                        currentBodyPart = Constants.MuscleBody.LOWER_LEG;
                        changeGridView();
                        break;
                    case 2:
                        upperBody.setImageTintList(colorStateList);
                        if (currentPart != null && currentPart != upperBody)
                            currentPart.setImageTintList(colorStateListPrev);
                        currentBodyPart = Constants.MuscleBody.UPPER_BODY;
                        changeGridView();
                        break;
                    case 3:
                        arms.setImageTintList(colorStateList);
                        if (currentPart != null && currentPart != arms)
                            currentPart.setImageTintList(colorStateListPrev);
                        currentBodyPart = Constants.MuscleBody.ARMS;
                        changeGridView();
                        break;
                    case 4:
                        backNeck.setImageTintList(colorStateList);
                        if (currentPart != null && currentPart != backNeck)
                            currentPart.setImageTintList(colorStateListPrev);
                        currentBodyPart = Constants.MuscleBody.BACK;
                        changeGridView();
                        break;
                    case 5:
                        head.setImageTintList(colorStateList);
                        if (currentPart != null && currentPart != head)
                            currentPart.setImageTintList(colorStateListPrev);
                        currentBodyPart = Constants.MuscleBody.HEAD;
                        changeGridView();
                        break;
                }

            }
        });

        if (selectedMuscle != null) {
            if (selectedMuscle.getMuscleSide() == Constants.MuscleSide.LEFT) {
                muscleMapViewModel.setCurrentSide(1);
            } else {
                muscleMapViewModel.setCurrentSide(0);
            }

            switch (selectedMuscle.getMuscleBody()) {
                case ARMS:
                    muscleMapViewModel.setCurrentBodyPart(3);
                    break;
                case BACK:
                    muscleMapViewModel.setCurrentBodyPart(4);
                    break;
                case HEAD:
                    muscleMapViewModel.setCurrentBodyPart(5);
                    break;
                case LOWER_LEG:
                    muscleMapViewModel.setCurrentBodyPart(1);
                    break;
                case UPPER_LEG:
                    muscleMapViewModel.setCurrentBodyPart(0);
                    break;
                case UPPER_BODY:
                    muscleMapViewModel.setCurrentBodyPart(2);
                    break;
                default:
                    break;
            }

            List<MuscleModel> muscleMapModels = muscleMap.get(currentBodyPart.name());
            if(muscleMapModels != null && muscleMapModels.size() > 0) {
                List<MuscleModel> items =  new ArrayList<>();
                int i = 0;
                for (MuscleModel muscleModel : muscleMapModels) {
                    if(selectedMuscle.getMuscleModel().getItemTitle().equalsIgnoreCase(muscleModel.getItemTitle())) {
                        selectedPosition = i;
                        muscleModel.setSelected(true);
                        MainActivity.getMain().labelingModel.setMuscleMapModel(selectedMuscle);
                    }
                    i++;
                    items.add(muscleModel);
                }
                adapter.setMuscles(items);
                adapter.notifyDataSetChanged();
            }
        }

        searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable charSequence) {
                String searchQuery = charSequence.toString().toLowerCase();

                ArrayList<MuscleModel> filteredList = new ArrayList<>();

                for (MuscleModel item : gridItems) {
                    item.setSelected(false);
                    selectedPosition = -1;
                    if (item.getItemTitle().toLowerCase().contains(searchQuery)) {
                        filteredList.add(item);
                    }
                }
                adapter.setMuscles(filteredList);
                adapter.notifyDataSetChanged();
            }
        });


        MaterialButton confirmMuscleButton = view.findViewById(R.id.confirmMuscleButton);
        confirmMuscleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedAthleteIndex = Tools.getPrefValue(requireContext(), Constants.PREF_SELECTED_ATHLETE);
                if (selectedAthleteIndex > -1) {
                    ArrayList<Object> selectedExercises = Tools.getSelectedExercises(requireContext());
                    if (selectedExercises.size() > 0) {
                        if (selectedPosition > -1) {
                            MuscleMapModel muscleMapModel = new MuscleMapModel();
                            muscleMapModel.setMuscleBody(currentBodyPart);
                            muscleMapModel.setMuscleSide(currentSide);
                            MuscleModel muscleModel = adapter.getMuscles().get(selectedPosition);
                            muscleMapModel.setMuscleModel(muscleModel);
                            tinyDB.putObject(Constants.PREF_SELECTED_MUSCLE, muscleMapModel);

                            MainActivity.getMain().labelingModel.setMuscleMapModel(muscleMapModel);

                            String workoutname = tinyDB.getString(Constants.PREF_WORKOUT_NAME);
                            Toast.makeText(requireContext(), String.format(Locale.getDefault(), "%s setup finished", workoutname), Toast.LENGTH_SHORT).show();
                            MainActivity.getMain().changeViewPage(1);
                        } else {
                            Toast.makeText(requireContext(), "Please select muscle", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Please select exercises", Toast.LENGTH_SHORT).show();
                        changeViewPage(1);
                    }
                } else {
                    Toast.makeText(requireContext(), "Please select athlete", Toast.LENGTH_SHORT).show();
                    changeViewPage(0);
                }

            }
        });
    }

    private void changeViewPage(int position) {
        ViewPager2 viewPager = requireActivity().findViewById(R.id.setupPager);
        viewPager.setCurrentItem(position);
    }

    private void changeGridView() {
        gridItems = new ArrayList<>();
        gridItems.addAll(muscleMap.get(currentBodyPart.name()));
        gridItems.addAll(gridItems);
        adapter.setMuscles(gridItems);
        adapter.notifyDataSetChanged();
    }
}