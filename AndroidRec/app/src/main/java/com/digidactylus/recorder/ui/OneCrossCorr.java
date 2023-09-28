package com.digidactylus.recorder.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class OneCrossCorr extends OneBase {


    public void doDraw(Canvas canvas, double val) {
        Paint greyPaint = new Paint();
        greyPaint.setColor(Color.rgb(100, 100, 100));
        greyPaint.setStrokeWidth(4);

        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.rgb(255, 255, 255));
        whitePaint.setTextSize(60);

        float x1 = m_x;
        float x2 = m_x + m_w;
        float y1 = m_y;
        float y2 = m_y+m_h;

        float space = 90;
        float ym1 = m_y + (m_h - space)/2;
        float ym2 = m_y + m_h - (m_h - space)/2;

        canvas.drawLine(x1, y1, x2, y1, greyPaint);
        canvas.drawLine(x1, y2, x2, y2, greyPaint);

        canvas.drawLine(x2, y1, x2, ym1, greyPaint);
        canvas.drawLine(x2, y2, x2, ym2, greyPaint);

        String s = new String();
        s += val;
        canvas.drawText(s, x2-90, m_y+m_h/2+15, whitePaint);
    }
}
