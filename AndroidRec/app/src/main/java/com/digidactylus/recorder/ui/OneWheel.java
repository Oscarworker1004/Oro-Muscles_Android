package com.digidactylus.recorder.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;

public class OneWheel extends OneBase{

    private double m_val = 0;

    public OneWheel() {

    }

    void setValue(double v) {
        m_val = v;
    }

    public void doDraw(Canvas canvas) {
        super.doDrawBase(canvas);

        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.rgb(255, 255, 255));
        whitePaint.setTextSize(50);

        Paint orangePaint = new Paint();
        orangePaint.setColor(Color.rgb(200, 255, 0));

        Paint tempPaint = new Paint();

        float x = m_x+m_w/2;
        float y = m_y+m_h/2 + 40;
        float out = m_w/2-60;
        float in = out - 36;
        float inin = in - 36;

        float min = 150;
        float max = 240;

        double val = m_val;

        float angle = (float)((double)(max)/2*val);
        float tmp = min;
        float inc = max/5;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Draw template
            tempPaint.setColor(Color.rgb(255,100, 0));
            canvas.drawArc(x-out,y-out,x+out,y+out,150,inc,true, tempPaint);
            tmp += inc;

            tempPaint.setColor(Color.rgb(255,190, 50));
            canvas.drawArc(x-out,y-out,x+out,y+out,tmp,inc,true, tempPaint);
            tmp += inc;

            tempPaint.setColor(Color.rgb(250,250, 250));
            canvas.drawArc(x-out,y-out,x+out,y+out,tmp,inc,true, tempPaint);
            tmp += inc;

            tempPaint.setColor(Color.rgb(80,120, 255));
            canvas.drawArc(x-out,y-out,x+out,y+out,tmp,inc,true, tempPaint);
            tmp += inc;

            tempPaint.setColor(Color.rgb(20,50, 255));
            canvas.drawArc(x-out,y-out,x+out,y+out,tmp,inc,true, tempPaint);


            float marg = 5;
            canvas.drawArc(x-in-marg,y-in-marg,x+in+marg,y+in+marg,min,max,true, m_fg);



            // Draw value
            canvas.drawArc(x-in,y-in,x+in,y+in,min,angle,true, orangePaint);
            canvas.drawArc(x-inin,y-inin,x+inin,y+inin,min,angle,true, m_fg);

            String ss = new String();
            ss += (int)(100*val);
            canvas.drawText(ss, x-35, m_y+3*m_h/4, whitePaint);
        }
    }

}
