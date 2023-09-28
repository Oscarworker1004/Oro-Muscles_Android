package com.digidactylus.recorder.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.digidactylus.recorder.MainActivity;

public class OneBarGraph extends OneBase{

    private float m_bar = 22;
    private float m_space = 14;



    public void doDraw(Canvas canvas, double [] bars, double scale, double num, int type) {
        super.doDrawBase(canvas);
        int r = 255;
        int g = 255;
        if (num < 0) {
            g += (int)(num*255);
        } else {
            r -= (int)(num*255);
        }
/*        if(type == 0){
            MainActivity.getMain().updateIntensity(bars, 0);
        } else if (type == 1) {
            MainActivity.getMain().updateIntensity(bars, 1);
        }*/
        Paint orangePaint = new Paint();
        orangePaint.setColor(Color.rgb(r, g, 0));

        int i;
        float th = 2;
        float base = m_y + m_h - 4 * m_frame;
        float maxH = m_h - 8 * m_frame - 4;

        double max = 0.;
        for (i=0; i<bars.length; i++) {
            if (bars[i] > max)
                max = bars[i];
        }
        // Draw the bars here
        for (i=0; i<bars.length; i++) {
            float x1 = m_x + 2*m_frame + i*(m_bar+m_space);
            float x2 = x1 + m_bar;
            double d = bars[i];
            float y = base - (float)(bars[i]*maxH/max)-2;// Fill values here
            canvas.drawRect(x1, base, x2, y, orangePaint);
        }


    }
}
