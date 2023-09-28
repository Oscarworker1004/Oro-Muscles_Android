package com.digidactylus.recorder.utils;

import android.content.Context;
import android.widget.TextView;

import com.digidactylus.recorder.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;

public class IndexMarkerView extends MarkerView {
    private TextView tvIndex;

    public IndexMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvIndex = findViewById(R.id.index_label);
    }

    // Set the marker view for a specific entry
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        DecimalFormat format = new DecimalFormat("#");
        String index = format.format(e.getX());
        tvIndex.setText(index);
        super.refreshContent(e, highlight);
    }

    // Adjust the marker's position
    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}
