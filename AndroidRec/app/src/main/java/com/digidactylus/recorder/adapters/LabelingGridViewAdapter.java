package com.digidactylus.recorder.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.LabelingModel;
import com.digidactylus.recorder.ui.LablingGraph;
import com.digidactylus.recorder.utils.TinyDB;
import com.digidactylus.recorder.utils.Tools;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class LabelingGridViewAdapter extends RecyclerView.Adapter<LabelingGridViewAdapter.ViewHolder> {
    private final Context context;
    private final List<Object> labelingModels;

    private boolean isSelected = false;

    public interface ItemListener {
        void onItemClick(int position);
    }

    private ItemListener itemListener;

    public LabelingGridViewAdapter(Context context, List<Object> labelingModels, ItemListener itemListener) {
        this.context = context;
        this.labelingModels = labelingModels;
        this.itemListener = itemListener;
    }


    @NonNull
    @Override
    public LabelingGridViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.holder_labeling_item,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LabelingGridViewAdapter.ViewHolder holder, int position) {
        LabelingModel labelingModel = (LabelingModel) labelingModels.get(position);
        holder.datasetNumber.setText(String.valueOf(position+1));
        holder.datasumValue.setText(labelingModel.getExerciaseString());
        String medianValue = String.valueOf(labelingModel.getMedianValue());
        if(labelingModel.getMedianValue() >= 0) {
            medianValue = "+" + labelingModel.getMedianValue();
        }

        holder.intesityValue.setText(medianValue);

        if(isSelected && labelingModel.isSelected()) {
            holder.mainCardView.setStrokeWidth(8);
            holder.mainCardView.setStrokeColor(ContextCompat.getColor(context, R.color.orange));
        }
        else {
            holder.mainCardView.setStrokeWidth(0);
            labelingModel.setSelected(false);
        }


        if(!labelingModel.getBarList().isEmpty()) {
            List<String> data = labelingModel.getBarList();
            holder.thumbnail.setLabelingBarModelList(data);
        }

        if(labelingModel.getExerciaseString() != null && !labelingModel.getExerciaseString().isEmpty() && !labelingModel.getExerciaseString().equalsIgnoreCase("-")) {
            holder.datasumValue.setText(labelingModel.getExerciaseString());
        }
        else {
            holder.datasumValue.setText("-");
        }


        holder.mainCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelected = true;
                itemListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return labelingModels.size();
    }





    public void updateData(int position) {
        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView datasetNumber;
        TextView intesityValue;
        TextView datasumValue;

        MaterialCardView mainCardView;
        LablingGraph thumbnail;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            datasetNumber = itemView.findViewById(R.id.datasetNumber);
            intesityValue = itemView.findViewById(R.id.intesityValue);
            datasumValue = itemView.findViewById(R.id.datasumValue);
            mainCardView= itemView.findViewById(R.id.mainCardView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}
