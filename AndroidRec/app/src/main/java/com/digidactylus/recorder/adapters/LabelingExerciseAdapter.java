package com.digidactylus.recorder.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.ExerciseModel;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerInterface;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.List;

public class LabelingExerciseAdapter extends RecyclerView.Adapter<LabelingExerciseAdapter.IconSpinnerViewHolder>
        implements PowerSpinnerInterface<Object> {

    private int index;
    private final PowerSpinnerView spinnerView;
    private OnSpinnerItemSelectedListener<Object> onSpinnerItemSelectedListener;
    private final List<Object> spinnerItems = new ArrayList<>();

    private final Context context;

    public LabelingExerciseAdapter(Context context, PowerSpinnerView powerSpinnerView) {
        this.spinnerView = powerSpinnerView;
        this.context = context;
        this.index = powerSpinnerView.getSelectedIndex();
    }

    @Override
    public IconSpinnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new IconSpinnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IconSpinnerViewHolder holder, int position) {
        holder.text1.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        holder.bind(spinnerItems.get(position), spinnerView);
    }

    @Override
    public void setItems(@NonNull List<? extends Object> list) {
        this.spinnerItems.clear();
        this.spinnerItems.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void notifyItemSelected(int index) {
        Log.e("testtt", "notifyItemSelected: " );
        if (index == -1) {
            return;
        }
        int oldIndex = this.index;
        this.index = index;
        this.spinnerView.notifyItemSelected(index, ((ExerciseModel) this.spinnerItems.get(index)).getName());
        if (onSpinnerItemSelectedListener != null) {
            ExerciseModel oldItem = oldIndex != -1 ? ((ExerciseModel)this.spinnerItems.get(oldIndex)) : null;
            ExerciseModel newItem = ((ExerciseModel)this.spinnerItems.get(index));
            onSpinnerItemSelectedListener.onItemSelected(oldIndex, oldItem, index, newItem);
        }
    }

    @Override
    public int getItemCount() {
        return spinnerItems.size();
    }

    public void setOnSpinnerItemSelectedListener(OnSpinnerItemSelectedListener<Object> listener) {
        this.onSpinnerItemSelectedListener = listener;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int i) {
        this.index = i;
    }

    @Nullable
    @Override
    public OnSpinnerItemSelectedListener<Object> getOnSpinnerItemSelectedListener() {
        return onSpinnerItemSelectedListener;
    }

    @NonNull
    @Override
    public PowerSpinnerView getSpinnerView() {
        return spinnerView;
    }



    static class IconSpinnerViewHolder extends RecyclerView.ViewHolder {

        TextView text1;
        IconSpinnerViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);

        }

        void bind(Object item, PowerSpinnerView spinnerView) {
            text1.setText(((ExerciseModel) item).getName());
        }
    }
}

