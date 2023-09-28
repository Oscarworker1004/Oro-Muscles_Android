package com.digidactylus.recorder.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TriangleGraph extends ImageView {
    OneTriangle oneTriangle;
    public static TriangleGraph instance = null;

    public TriangleGraph(Context context, AttributeSet attr){
        super(context, attr);
        instance = this;
        oneTriangle = new OneTriangle(context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        oneTriangle.doDraw(canvas,FeedView.getM_buf());
    }

    public static TriangleGraph getInstance(){
        return instance;
    }

}
