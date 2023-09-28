package com.digidactylus.recorder.ui;

public class Triangle {
    private double m_ax = 0.;
    private double m_ay = 0.;
    private double m_bx = 0.;
    private double m_by = 0.;
    private double m_cx = 0.;
    private double m_cy = 0.;
    private double m_line = 0.;

    public double ax() {return m_ax;}
    public double ay() {return m_ay;}
    public double bx() {return m_bx;}
    public double by() {return m_by;}
    public double cx() {return m_cx;}
    public double cy() {return m_cy;}

    public double line() {return m_line;}

    public void set(double ax, double ay, double bx, double by, double cx, double cy) {
        m_ax = ax;
        m_ay = ay;
        m_bx = bx;
        m_by = by;
        m_cx = cx;
        m_cy = cy;
    }

    public void setLine(double line) {
        m_line = line;
    }
}
