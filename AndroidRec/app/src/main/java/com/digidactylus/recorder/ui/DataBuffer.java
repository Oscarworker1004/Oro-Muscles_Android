package com.digidactylus.recorder.ui;

// Ring buffer to store "live" data
public class DataBuffer {


    // Kinda stupid...
    private double[] m_frame;
    private double[] m_emg;
    private double[] m_rms;

    private double[] m_aX;
    private double[] m_aY;
    private double[] m_aZ;

    private double[] m_gX;
    private double[] m_gY;
    private double[] m_gZ;

    private int m_size = 0;
    private int m_ptr = 0;

    private double m_scaleEMG = 0.1;
    private double m_scaleAcc = 10.;
    private double m_scaleGyr = 0.8;

    private Triangle [] m_tria;

    public DataBuffer() {

    }

    public void setSize(int n) {
        if (n == m_size)
            return;
        m_size = n;
        m_frame = new double[n];
        m_emg = new double[n];
        m_rms = new double[n];

        m_aX = new double[n];
        m_aY = new double[n];
        m_aZ = new double[n];

        m_gX = new double[n];
        m_gY = new double[n];
        m_gZ = new double[n];

        m_tria = new Triangle[n];

        for (int i=0; i<m_size; i++) {
            m_tria[i] = new Triangle();

            m_frame[i] = 0.;
            m_emg[i] = 0.;
            m_rms[i] = 0.;

            m_aX[i] = 0;
            m_aY[i] = 0;
            m_aZ[i] = 0;

            m_gX[i] = 0;
            m_gY[i] = 0;
            m_gZ[i] = 0;
        }
    }

    public int size() {
        return m_size;
    }

    public void addData(double[] d, int n) {
        int i;
        for (i=0; i<d.length/n; i++) {
            int j = (m_ptr + i) % m_size;
            m_frame[j] = d[i*n +  0];
            m_emg[j] = d[i*n +  1];
            m_rms[j] = d[i*n +  2];

            m_aX[j] = d[i*n +  3];
            m_aY[j] = d[i*n +  4];
            m_aZ[j] = d[i*n +  5];

            m_gX[j] = d[i*n +  6];
            m_gY[j] = d[i*n +  7];
            m_gZ[j] = d[i*n +  8];

            if (n >= 15) {
                m_tria[j].set(d[i*n +  9],
                              d[i*n + 10],
                              d[i*n + 11],
                              d[i*n + 12],
                              d[i*n + 13],
                              d[i*n + 14]);

                m_tria[j].setLine(m_rms[j]);
            }
        }

        m_ptr = (m_ptr + d.length/n) % m_size;
    }

    //======================================================
    private int get(int i) {
        int j = i+m_ptr;
        if (j >= m_size)
            j -= m_size;
        return j;
    }
    //======================================================

    public float getFrame(int i) {
        return (float)m_frame[get(i)];
    }
    public float getEMG(int i) {
        return (float)(m_scaleEMG*m_emg[get(i)]);
    }
    public float getRMS(int i) {
        return (float)(m_scaleEMG*m_rms[get(i)]);
    }

    public float getAccX(int i) {
        return (float)(m_scaleAcc*m_aX[get(i)]);
    }
    public float getAccY(int i) {
        return (float)(m_scaleAcc*m_aY[get(i)]);
    }
    public float getAccZ(int i) {
        return (float)(m_scaleAcc*m_aZ[get(i)]);
    }

    public float getGyrX(int i) {
        return (float)(m_scaleGyr*m_gX[get(i)]);
    }
    public float getGyrY(int i) {
        return (float)(m_scaleGyr*m_gY[get(i)]);
    }
    public float getGyrZ(int i) {
        return (float)(m_scaleGyr*m_gZ[get(i)]);
    }

    public Triangle getTriangle(int i) {
        return m_tria[get(i)];
    }
    public Triangle getTriangleLast() {
        return getTriangle(m_size-1);
    }

}
