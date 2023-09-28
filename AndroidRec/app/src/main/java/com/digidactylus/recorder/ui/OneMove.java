package com.digidactylus.recorder.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.digidactylus.recorder.MemReadBuffer;
import com.digidactylus.recorder.MemWriteBuffer;

public class OneMove {
    private float m_w = 10;
    private float m_h = 10;
    private float m_x = 0;
    private float m_y = 0;
    private String m_exe;
    private String m_muscle;
    private String m_extra;
    private String m_reps;
    private boolean m_high = false;

    private boolean m_active = false;

    public OneMove() {
        m_exe = "move = ?";
        m_muscle = "muscle = ?";
        m_extra = "?";
        m_reps = "? reps";

        m_w = 290;
        m_h = 200;
    }

    public boolean write(MemWriteBuffer buf) {
        buf.writeString(m_exe);
        buf.writeString(m_muscle);
        buf.writeString(m_extra);
        buf.writeString(m_reps);

        buf.writeInt((int)m_x);
        buf.writeInt((int)m_y);
        buf.writeInt((int)m_w);
        buf.writeInt((int)m_h);
        return true;
    }

    public boolean read(MemReadBuffer buf, int version) {
        m_exe = buf.readString();
        m_muscle = buf.readString();
        m_extra = buf.readString();
        m_reps = buf.readString();

        m_x = buf.readInt();
        m_y = buf.readInt();
        m_w = buf.readInt();
        m_h = buf.readInt();
        return true;
    }

        public void setCoords(float x, float y) {
        m_x = x;
        m_y = y;
    }

    public float x() {
        return m_x;
    }
    public float y() {
        return m_y;
    }
    public float width() {
        return m_w;
    }
    public float height() {
        return m_h;
    }

    public boolean select(float x, float y) {
        if (contains(x, y)) {
            m_active = true;
            return true;
        }
        return false;
    }
    public void unselect() {
        m_active = false;
    }

    public boolean isSelected() {
        return m_active;
    }

    public void highlight(float x, float y) {
        m_high = false;
        if (contains(x, y))
            m_high = true;
    }

    public boolean contains(float x, float y) {
        if (x >= m_x && x < m_x + m_w && y >= m_y && y < m_y + m_h)
            return true;
        return false;
    }

    public void setExe(String s) {
        m_exe = s;
    }
    public void setMuscle(String s) {
        m_muscle = s;
    }
    public void setExtra(String s) {
        m_extra = s;
    }
    public void setReps(String s) {
        m_extra = s;
    }

    public String exe() {
        return m_exe;
    }
    public String muscle() {
        return m_muscle;
    }
    public String extra() {
        return m_extra;
    }
    public String reps() {
        return m_reps;
    }

    public void setText(String musc, String exe, String extra, String reps) {
        m_muscle = musc;
        m_exe = exe;
        m_extra = extra;
        m_reps = reps;
    }

    public void doDraw(Canvas canvas) {
        Paint greyPaint = new Paint();
        greyPaint.setColor(Color.rgb(20, 20, 30));
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.rgb(0, 0, 0));


        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.rgb(255, 255, 255));
        whitePaint.setTextSize(40);

        if (m_active)
            whitePaint.setColor(Color.rgb(230, 130, 0));

        if (m_high)
            whitePaint.setColor(Color.rgb(230, 230, 0));

        float w = m_w;
        float h = m_h;
        float bb = 4;
        canvas.drawRect(m_x - bb, m_y - bb, m_x + w + bb, m_y + h + bb, blackPaint);
        canvas.drawRect(m_x, m_y, m_x + w, m_y + h, whitePaint);

        blackPaint.setTextSize(36);
        float off = 10;
        float dist = 50;
        canvas.drawText(m_muscle, m_x + off, m_y + 1*dist, blackPaint);
        canvas.drawText(m_exe, m_x + off, m_y + 2*dist, blackPaint);
        canvas.drawText(m_extra, m_x + off, m_y + 3*dist, blackPaint);

    }
}