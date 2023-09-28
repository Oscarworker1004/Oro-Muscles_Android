package com.digidactylus.recorder.ui.graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.digidactylus.recorder.ui.DataBuffer;
import com.digidactylus.recorder.ui.FeedView;
import com.digidactylus.recorder.ui.OneTriangle;

public class MovementTriangle extends ImageView {

    private DataBuffer m_buf;
    OneTriangle m_Triangle;
    private int mWidth;
    private int mHeight;


    public MovementTriangle(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_Triangle = new OneTriangle(context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //m_Triangle.doDraw(canvas,m_buf);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        m_Triangle.setCoords(0, 0, mWidth, mHeight);

    }
}

