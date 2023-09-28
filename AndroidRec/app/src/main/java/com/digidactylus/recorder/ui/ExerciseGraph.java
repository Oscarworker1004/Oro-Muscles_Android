package com.digidactylus.recorder.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class ExerciseGraph {
    private String [] m_names;
    private int m_count;
    private int m_selected = -1;

    private float m_w = 150;
    private float m_h = 100;

    private float m_off = 0;
    private float m_lastOff = 0;
    private float m_start = 0;

    private ExerciseDB m_db;

    public ExerciseGraph() {
        //m_names = new String[128];
        //m_count = 0;
        m_db = new ExerciseDB();
        m_names = m_db.getList();
        m_count = m_names.length;
        fill();
    }

    public void update() {
        m_db.update();
    }
    private void fill() {
        /*m_count = 0;
        m_off = 0;
        m_lastOff = 0;
        m_start = 0;
        addExe("Bar Dip");
        addExe("Bench Press");
        addExe("Cable Chest Press");
        addExe("Close-Grip Bench Press");
        addExe("Close-Grip Feet-Up Bench Press");
        addExe("Decline Bench Press");
        addExe("Dumbbell Chest Fly");
        addExe("Dumbbell Chest Press");
        addExe("Dumbbell Decline Chest Press");
        addExe("Dumbbell Floor Press");
        addExe("Dumbbell Pullover");
        addExe("Feet-Up Bench Press");
        addExe("Floor Press");
        addExe("Incline Bench Press");
        addExe("Incline Dumbbell Press");
        addExe("Incline Push-Up");
        addExe("Kneeling Incline Push-Up");
        addExe("Kneeling Push-Up");
        addExe("Machine Chest Fly");
        addExe("Machine Chest Press");
        addExe("Pec Deck");
        addExe("Push-Up");
        addExe("Push-Up Against Wall");
        addExe("Push-Ups With Feet in Rings");
        addExe("Resistance Band Chest Fly");
        addExe("Smith Machine Bench Press");
        addExe("Smith Machine Incline Bench Press");
        addExe("Standing Cable Chest Fly");
        addExe("Standing Resistance Band Chest Fly");
*/
    }
/*
    public void addExe(String name) {
        m_names[m_count] = name;
        m_count++;
    }
*/
    public void setSelected(float x, float y) {
        m_selected = -1;
        m_selected = (int)((y-m_off)/m_h+0.5)-1; //***********************

        if (m_selected >= 0 && m_db.getExpand() == -1) {
            m_db.expand(m_selected);
            m_names = m_db.getList();
            m_count = m_names.length;
            m_selected = -1;
            m_off = 0;
        }
        if (m_selected == 0 && m_db.getExpand() >= 0) {
            m_db.expand(-1);
            m_names = m_db.getList();
            m_count = m_names.length;
            m_selected = -1;
            m_off = 0;
        }
    }

    public String getSelected() {
        if (m_selected >= 0 && m_selected < m_count)
            return m_names[m_selected];
        return null;
    }

    public void startMove(float y) {
        if (m_db.getExpand() == -1)
            return;
        m_start = y;
        m_lastOff = m_off;
    }

    public void addMove(float y) {
        if (m_db.getExpand() == -1)
            return;
        m_off = m_lastOff + y-m_start;
        if (m_off > 0)
            m_off = 0;
    }

    public void setMove(float y) {
        if (m_db.getExpand() == -1)
            return;
        m_off = m_lastOff + y-m_start;
        if (m_off > 0)
            m_off = 0;
    }

    public void doDraw(Canvas canvas) {
        Paint bmpPaint = new Paint();
        bmpPaint.setColor(Color.rgb(220, 220, 220));
        //canvas.drawBitmap(m_bmp, m_x, m_y, bmpPaint);

        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.rgb(255, 255, 255));
        whitePaint.setTextSize(70);
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.rgb(9, 9, 9));
        blackPaint.setTextSize(70);

        Paint whitePaintB = new Paint();
        whitePaintB.setColor(Color.rgb(255, 255, 255));
        whitePaintB.setTextSize(80);
        Paint blackPaintB = new Paint();
        blackPaintB.setColor(Color.rgb(9, 9, 9));
        blackPaintB.setTextSize(80);

        whitePaintB.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        blackPaintB.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


        for (int i = 0; i < m_count; i++) {
            boolean bold = false;
            if (i == 0)
                bold = true;
            if (m_db.getExpand() == -1)
                bold = true;
            if (m_selected == i) {
                canvas.drawRect(0, m_off+m_h/4+(i) * m_h, 1500, m_off+m_h/4+(i+1) * m_h, blackPaint);
                if (bold)
                    canvas.drawText(m_names[i], 15, m_off+(i+1) * m_h, whitePaintB);
                else
                    canvas.drawText(m_names[i], 15, m_off+(i+1) * m_h, whitePaint);
            } else {
                if (bold)
                    canvas.drawText(m_names[i], 15, m_off+(i+1) * m_h, blackPaintB);
                else
                    canvas.drawText(m_names[i], 15, m_off+(i+1) * m_h, blackPaint);
            }
        }
    }
}
