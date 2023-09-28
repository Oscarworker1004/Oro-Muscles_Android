package com.digidactylus.recorder.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.ExerciseModel;
import com.digidactylus.recorder.models.ParentItem;
import com.digidactylus.recorder.models.SetsModel;
import com.digidactylus.recorder.models.TrainingModel;
import com.digidactylus.recorder.utils.AppManager;
import com.digidactylus.recorder.utils.MinValueInputFilter;
import com.digidactylus.recorder.utils.Tools;
import com.digidactylus.recorder.viewmodels.TrainingViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;

    private  ArrayList<Object> parentItems = new ArrayList<>();

    private static final String TAG = "ExpandableListAdapter";

    private boolean setDataChanged = false;

    private SetsAdapter setsAdapter;

    public ExpandableListAdapter(Context context, ArrayList<Object> parentItems) {
        this.context = context;
        this.parentItems = parentItems;
    }

    public void setParentItems(ArrayList<Object> data) {
        this.parentItems = data;
        notifyDataSetChanged();
    }

    public ArrayList<Object> getParentItems() {
        return parentItems;
    }

    @Override
    public int getGroupCount() {
        return parentItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parentItems.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        ExerciseModel parentItem = (ExerciseModel) getGroup(groupPosition);
        parentItem.setExpanded(true);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        ExerciseModel parentItem = (ExerciseModel) getGroup(groupPosition);
        parentItem.setExpanded(false);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_training_last_child, parent, false);
        }


        ExerciseModel parentItem = (ExerciseModel) getGroup(groupPosition);
        TextView exerciseTitle = convertView.findViewById(R.id.exerciseTitle);
        TextView exerciseTonnage = convertView.findViewById(R.id.exerciseTonnage);
        TextView setTraining = convertView.findViewById(R.id.setTraining);
        ImageView checkMark = convertView.findViewById(R.id.checkMark);

        exerciseTitle.setText(parentItem.getName());
        exerciseTonnage.setText(String.format(Locale.getDefault(), "Tonnage: %d", parentItem.getTonnage()));
        setTraining.setText(String.format(Locale.getDefault(), "Sets: %d", parentItem.getSets()));

        if(parentItem.isExpanded()) {
            checkMark.setImageResource(R.drawable.arrow_down);
        }
        else {
            checkMark.setImageResource(R.drawable.arrow_right);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_training_expendable_child, parent, false);
        }

        EditText setlevel = convertView.findViewById(R.id.setlevel);
        setlevel.setTag(groupPosition); // Set the tag to the current group position


        InputFilter minValueFilter = new MinValueInputFilter(1);
        setlevel.setFilters(new InputFilter[] { minValueFilter });

        RecyclerView listView = convertView.findViewById(R.id.setListView);
        listView.setLayoutManager(new LinearLayoutManager(context));

        ExerciseModel parentItem = (ExerciseModel) getGroup(groupPosition);

        if(parentItem.getSets() > 0) {
            setlevel.setText(String.format(Locale.getDefault(),"%d", parentItem.getSets()));
        }


        List<SetsModel> setsList = parentItem.getSetsModels();

        if(setsList.size() == 0) {
            SetsModel setsModel = new SetsModel();
            setsModel.setId(0);
            setsModel.setWeight(0);
            setsModel.setReps(1);
            setsList.add(setsModel);
        }

        setsAdapter = new SetsAdapter(context, parentItem.getName(), setsList, new SetsAdapter.AdapterListener() {
            @Override
            public void onDataChanged(List<SetsModel> setsList) {
                parentItem.setSetsModels(setsList);
                int weight = Tools.calculateDataInSets(setsList, 0);
                int reps = Tools.calculateDataInSets(setsList, 1);
                int tonnage = Tools.calculateTonnage(setsList);
                parentItem.setReps(reps);
                parentItem.setWeight(weight);
                parentItem.setTonnage(tonnage);

                notifyDataSetChanged();
            }
        });

        listView.setAdapter(setsAdapter);

        setlevel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if(!s.toString().isEmpty()) {
                        int val = Integer.parseInt(s.toString());
                        int currentGroupPosition = (int) setlevel.getTag(); // Retrieve the tag
                        ExerciseModel parentItem2 = (ExerciseModel) getGroup(currentGroupPosition);
                        if(val != parentItem2.getSets()) {
                            List<SetsModel> modelList = ((ExerciseModel)getGroup(currentGroupPosition)).getSetsModels();
                            if (val > modelList.size()) {
                                for (int i = modelList.size(); i < val; i++) {
                                    SetsModel setsModel = new SetsModel();
                                    setsModel.setId(i);
                                    setsModel.setWeight(0);
                                    setsModel.setReps(1);
                                    modelList.add(setsModel);
                                }
                                setsAdapter.setList(modelList);
                            } else {
                                List<SetsModel> newList = modelList.subList(0, val);
                                setsAdapter.setList(newList);
                            }
                            int weight = Tools.calculateDataInSets(setsAdapter.getSetsList(), 0);
                            int reps = Tools.calculateDataInSets(setsAdapter.getSetsList(), 1);
                            int tonnage = Tools.calculateTonnage(modelList);
                            parentItem2.setReps(reps);
                            parentItem2.setWeight(weight);
                            parentItem2.setSets(val);
                            parentItem2.setTonnage(tonnage);
                            parentItems.remove(currentGroupPosition);
                            parentItems.add(currentGroupPosition, parentItem2);
                            setDataChanged = true;
                            setsAdapter.notifyDataSetChanged();
                            MainActivity.getMain().trainingViewModel.setTrainingModified(true);

                        }
                    }
                }
                catch (Exception ignored) {
                }
            }
        });

        setlevel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && setDataChanged) {
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                            setDataChanged = false;
                            hideKeyboard(v);
                        }
                    });
                }
            }
        });

        return convertView;
    }

    private void hideKeyboard(android.view.View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void makeChanges() {
        if(setsAdapter != null) {
            setsAdapter.makeChanges();
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }
}
