package com.digidactylus.recorder.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.ExerciseModel;
import com.digidactylus.recorder.models.TrainingModel;
import com.digidactylus.recorder.utils.Constants;
import com.digidactylus.recorder.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TrainingPlanAdapter extends RecyclerView.Adapter<TrainingPlanAdapter.ViewHolder> {

    private static final String TAG = "TrainingPlanAdapter";
    private List<TrainingModel> mData;
    private final Context context;

    interface DataChanged {
        void onDataChanged();
    }


    public TrainingPlanAdapter(Context context, List<TrainingModel> mData) {
        this.context = context;
        this.mData = mData;
    }


    public void setmData(List<TrainingModel> data) {
        this.mData = data;
    }

    public List<TrainingModel> getmData() {
        return mData;
    }

    @NonNull
    @Override
    public ViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_training_parent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder  holder, int position) {
        String category = mData.get(position).getCategory();
        List<ExerciseModel> children = mData.get(position).getExerciseModelList();

        holder.mCategoryTextView.setText(category);
        ChildAdapter mChildAdapter = new ChildAdapter(context, mData.get(position), children, new DataChanged() {
            @Override
            public void onDataChanged() {
                changeCustomTraining();
            }
        });
        holder.mChildrenRecyclerView.setAdapter(mChildAdapter);

    }

    public void changeCustomTraining() {
        if(mData.get(0).isCustom()) {
            mData.remove(0);
            mData.add(0, Tools.getCustomExercises(context));
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mCategoryTextView;
        private RecyclerView mChildrenRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCategoryTextView = itemView.findViewById(R.id.category_text_view);
            mChildrenRecyclerView = itemView.findViewById(R.id.children_recycler_view);
            mChildrenRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }

    }

    public static class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {
        private List<ExerciseModel> mData;
        private final TrainingModel trainingModel;

        private final Context context;

        private final DataChanged dataChanged;

        public ChildAdapter(Context context, TrainingModel trainingModel, List<ExerciseModel> data, DataChanged dataChanged) {
            this.context = context;
            mData = data;
            this.dataChanged = dataChanged;
            this.trainingModel = trainingModel;
        }

        public void setmData(List<ExerciseModel> mData) {
            this.mData = mData;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_training_child, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ExerciseModel child = mData.get(position);
            holder.bind(child);
            holder.groupTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  onItemClick.onChildSelected(parentPos, position);
                    boolean isSelected = !child.isSelected();
                    if(!isSelected) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("Unselect this exercise");
                        builder1.setMessage("All data of this exercise will be removed, are you sure you want to continue?");
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Tools.changeExerciseState(context, child, false);
                                child.setSelected(false);
                                child.setSets(1);
                                child.setReps(1);
                                child.setWeight(0);
                                child.setSetsModels(new ArrayList<>());
                                child.setTonnage(0);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        builder1.setNegativeButton("Cancel", null);
                        builder1.show();
                    }
                    else {
                        Tools.changeExerciseState(context, child, true);
                        child.setSelected(true);
                        notifyDataSetChanged();
                    }

                }
            });
            holder.groupTitleTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(trainingModel.isCustom()) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("Delete This Exercise?");
                        builder1.setMessage("Are you sure you want to delete this Exercise?");
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Tools.deleteFromCustomTraining(context, child);
                                TrainingModel list = Tools.getCustomExercises(context);
                                if(list != null) {
                                    setmData(list.getExerciseModelList());
                                }
                                else {
                                    setmData(new ArrayList<>());
                                }
                                notifyDataSetChanged();
                                dataChanged.onDataChanged();
                                dialog.dismiss();
                            }
                        });
                        builder1.setNegativeButton("Cancel", null);
                        builder1.show();

                        return true;
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView exerciseTitle;
            private LinearLayout groupTitleTextView;
            private TextView exerciseTonnage;
            private TextView exerciseSet;
            private ImageView checkMark;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                exerciseTitle = itemView.findViewById(R.id.exerciseTitle);
                groupTitleTextView = itemView.findViewById(R.id.groupTitleTextView);
                checkMark = itemView.findViewById(R.id.checkMark);
                exerciseTonnage = itemView.findViewById(R.id.exerciseTonnage);
                exerciseSet = itemView.findViewById(R.id.setTraining);
            }

            public void bind(ExerciseModel child) {
                exerciseTitle.setText(child.getName());
                exerciseTonnage.setText(String.format(Locale.getDefault(), "Tonnage: %d", child.getTonnage()));
                exerciseSet.setText(String.format(Locale.getDefault(), "Sets: %d", child.getSets()));
                if(child.isSelected()) {
                    checkMark.setVisibility(View.VISIBLE);
                }
                else {
                    checkMark.setVisibility(View.GONE);
                }
            }
        }
    }
}
