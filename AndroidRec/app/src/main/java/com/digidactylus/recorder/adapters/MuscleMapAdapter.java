package com.digidactylus.recorder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.MuscleModel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MuscleMapAdapter extends RecyclerView.Adapter<MuscleMapAdapter.MuscleViewHolder> {

    private List<MuscleModel> muscles;

    public interface OnItemListener {
        void onItemClick(int position);
    }

    OnItemListener onItemListener;

    private final Context context;

    public MuscleMapAdapter(Context context, List<MuscleModel> muscles, OnItemListener onItemListener) {
        this.muscles = muscles;
        this.onItemListener = onItemListener;
        this.context = context;
    }

    public void setMuscles(List<MuscleModel> muscles) {
        this.muscles = muscles;
    }

    public List<MuscleModel> getMuscles() {
        return muscles;
    }

    @NonNull
    @Override
    public MuscleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.muscle_map_grid_layout, parent, false);
        return new MuscleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MuscleViewHolder holder, int position) {
        MuscleModel muscleName = muscles.get(position);
        holder.muscleTitle.setText(muscleName.getItemTitle());
        holder.muscleImage.setImageResource(muscleName.getImageResourceId());
        holder.mapCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemListener.onItemClick(position);
            }
        });

        if(muscleName.isSelected()) {
            holder.muscleTitle.setTextColor(ContextCompat.getColor(context, R.color.workoutSelectionColorOrange));
            holder.mapCard.setStrokeColor(ContextCompat.getColor(context, R.color.workoutSelectionColorOrange));
        }
        else {
            holder.muscleTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.mapCard.setStrokeColor(ContextCompat.getColor(context, R.color.muscleCardStrokeColor));
        }
    }

    @Override
    public int getItemCount() {
        return muscles.size();
    }

    static class MuscleViewHolder extends RecyclerView.ViewHolder {
        TextView muscleTitle;
        ImageView muscleImage;

        MaterialCardView mapCard;

        public MuscleViewHolder(@NonNull View itemView) {
            super(itemView);
            mapCard = itemView.findViewById(R.id.mapCard);
            muscleTitle = itemView.findViewById(R.id.muscleTitle);
            muscleImage = itemView.findViewById(R.id.muscleImage);
        }
    }
}