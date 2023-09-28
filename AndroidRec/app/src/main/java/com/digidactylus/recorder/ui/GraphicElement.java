package com.digidactylus.recorder.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class GraphicElement {
    private float m_x = 0;
    private float m_y = 0;
    private float m_w = 0;
    private float m_h = 0;
    private Bitmap m_bmp;
    private boolean m_isClicked = false;

    public GraphicElement() {

    }

    public void set(float x, float y, Bitmap bmp) {
        m_x = x;
        m_y = y;
        m_bmp = bmp;
        m_w = bmp.getWidth();
        m_h = bmp.getHeight();
    }

    public boolean isClicked(float x, float y) {
        if (x >= m_x && x <= m_x + m_w &&
                y >= m_y && y < m_y + m_h)
            return true;
        return false;
    }

    public void toggle() {
        m_isClicked = !m_isClicked;
    }

    public void setClicked(boolean b) {
        m_isClicked = b;
    }

    public boolean isClicked() {
        return m_isClicked;
    }


    public void doDraw(Canvas canvas) {
        Paint bmpPaint = new Paint();
        bmpPaint.setColor(Color.rgb(100, 100, 100));
        canvas.drawBitmap(m_bmp, m_x, m_y, bmpPaint);
    }
}
