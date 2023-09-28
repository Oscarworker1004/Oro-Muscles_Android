package com.digidactylus.recorder.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.util.List;

public class LablingGraph extends View {

    private List<String> labelingBarModelList;
    public LablingGraph(Context context) {
        super(context);
    }

    public LablingGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LablingGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LablingGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setLabelingBarModelList(List<String> labelingBarModelList) {
        this.labelingBarModelList = labelingBarModelList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(labelingBarModelList != null && !labelingBarModelList.isEmpty()) {
            for (String labelingBarModel : labelingBarModelList) {
                String[] data = labelingBarModel.split(";");
                int color = Color.parseColor(data[4]);

                Paint paint = new Paint();
                paint.setColor(color);
                canvas.drawRect(Float.parseFloat(data[0]), Float.parseFloat(data[1]),
                        Float.parseFloat(data[2]), Float.parseFloat(data[3]),paint);
            }
        }
    }
}
