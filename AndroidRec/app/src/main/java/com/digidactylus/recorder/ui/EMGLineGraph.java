package com.digidactylus.recorder.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class EMGLineGraph extends ImageView {
    OneEMG m_emg;
    public static EMGLineGraph instance = null;

    public EMGLineGraph(Context context, AttributeSet attr){
        super(context, attr);
        instance = this;
        m_emg = new OneEMG();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        m_emg.doDraw(canvas, FeedView.getM_buf());
    }


    public static EMGLineGraph getInstance(){
        return instance;
    }


}
