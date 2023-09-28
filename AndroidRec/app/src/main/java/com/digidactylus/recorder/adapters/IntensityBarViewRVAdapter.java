package com.digidactylus.recorder.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.R;
import com.digidactylus.recorder.models.graphItemsList;

import java.util.List;

public class IntensityBarViewRVAdapter extends RecyclerView.Adapter<IntensityBarViewRVAdapter.ViewHolder> {

    List<graphItemsList> list;
    Context context;
    public IntensityBarViewRVAdapter(Context context, List<graphItemsList> list ){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_bar_layout,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final graphItemsList model = list.get(position);
        holder.barLabel.setText(model.getBarLabel());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.barBody.getLayoutParams();
        layoutParams.width = dpToPx(context, 10);
        layoutParams.height = dpToPx(context, model.getBarBodyHeight());
        holder.barBody.setLayoutParams(layoutParams);
        holder.barBody.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(model.getBarBodyColor())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View barBody;
        TextView barLabel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            barBody = itemView.findViewById(R.id.barBody);
            barLabel = itemView.findViewById(R.id.barLabel);
        }
    }


    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
