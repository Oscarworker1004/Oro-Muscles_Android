package com.digidactylus.recorder.ui.graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.digidactylus.recorder.ui.DataBuffer;
import com.digidactylus.recorder.ui.FeedView;
import com.digidactylus.recorder.ui.OneEMG;

public class MuscleGraph extends ImageView {

    OneEMG m_emg;
    private DataBuffer m_buf;
    private int mWidth;
    private int mHeight;
    static MuscleGraph muscleGraph = null;

    public MuscleGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_emg = new OneEMG();
        muscleGraph = this;
    }
    public static MuscleGraph getmuscleGraph(){
        return muscleGraph;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //m_emg.doDraw(canvas, m_buf);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        m_emg.setCoords(0, 0, mWidth, mHeight);

    }

    public void refreshView(){
        invalidate();
    }

}
