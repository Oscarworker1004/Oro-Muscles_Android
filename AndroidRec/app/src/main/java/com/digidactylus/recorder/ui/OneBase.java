package com.digidactylus.recorder.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class OneBase {
    protected float m_x = 0;
    protected float m_y = 0;
    protected float m_w = 0;
    protected float m_h = 0;
    protected float m_frame = 10;

    protected Paint m_fg;
    protected Paint m_bg;
    protected Paint m_text;

    private String m_caption;

    public OneBase() {
        m_fg = new Paint();
        m_bg = new Paint();
        m_text = new Paint();

        m_caption = "";

        m_fg.setColor(Color.rgb(0, 0, 0));
        m_bg.setColor(Color.rgb(80, 80, 80));
        m_text.setColor(Color.rgb(255, 255, 255));
        m_text.setTextSize(50);
    }

    public void setCoords(float x, float y, float w, float h) {
        m_x = x;
        m_y = y;
        m_w = w;
        m_h = h;
    }

    public void setCaption(String text) {
        m_caption = text;
    }

    public float x() {return m_x;}
    public float y() {return m_y;}
    public float w() {return m_w;}
    public float h() {return m_h;}

    public void doDrawBase(Canvas canvas) {

        //canvas.drawRect(m_x, m_y, m_x+m_w, m_y+m_h, m_bg);
        //canvas.drawRect(m_x+m_frame, m_y+m_frame, m_x+m_w-m_frame, m_y+m_h-m_frame, m_fg);
        //canvas.drawText(m_caption,m_x+m_frame+20, m_y+m_frame+60, m_text);

    }
}
