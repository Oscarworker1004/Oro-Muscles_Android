package com.digidactylus.recorder.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class OneEMG extends OneBase {

    public OneEMG() {

    }

    public void doDraw(Canvas canvas, DataBuffer buf) {
        super.doDrawBase(canvas);

        // Calculate the center of the canvas
        float canvasCenterX = canvas.getWidth() / 2;
        float canvasCenterY = canvas.getHeight() / 2;

        // Calculate the width and height of the view to match the canvas
        float viewWidth = canvas.getWidth();
        float viewHeight = canvas.getHeight();

        // Update m_x, m_y, m_w, and m_h to center the view and match canvas size
        m_x = canvasCenterX - viewWidth / 2;
        m_y = canvasCenterY - viewHeight / 2;
        m_w = viewWidth;
        m_h = viewHeight;

        Paint redPaint = new Paint();
        redPaint.setColor(Color.rgb(255, 120, 0));
        Paint bluePaint = new Paint();
        bluePaint.setColor(Color.rgb(120, 120, 255));

        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.rgb(255, 255, 255));


        float n = m_w - 2 * m_frame;
        int start = buf.size() - (int) n;
        
        start = Math.max(start, 0);

        float eOff = m_y + m_h / 2;
        canvas.drawLine(m_x + m_frame, eOff, m_x + m_w - m_frame, eOff, redPaint);

        for (int j = start; j < buf.size() - 1; j++) {
            int i = j - start;
            float x1 = i + m_x + m_frame;
            float x2 = i + 1 + m_x + m_frame;
            canvas.drawLine(x2, eOff, x2, eOff - buf.getRMS(j + 1), bluePaint);
            canvas.drawLine(x1, eOff + buf.getEMG(j), x2, eOff + buf.getEMG(j + 1), redPaint);
        }
    }
}
