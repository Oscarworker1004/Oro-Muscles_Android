package com.digidactylus.recorder.ui;

public class MuscleGraph {
    private float [] m_x;
    private float [] m_y;
    private String [] m_tag;
    private int m_count = 0;
    private float m_middle = 493;

    private boolean m_ai = true;

    public MuscleGraph() {
        int n = 7;
        if (m_ai)
            n = 17;
        m_x = new float[n];
        m_y = new float[n];
        m_tag = new String[n];

        if (m_ai) {
            add("gluteus maximus", 774, 633);
            add("gluteus medus", 808, 590);
            add("erector spinae", 751, 475);
            add("gastrocnemius", 816, 1034);
            add("biceps brachii", 389, 395);
            add("rectus abdominus", 288, 486);
            add("vastus lateralis", 366, 797);
            add("externsor digitorum", 898, 536);
            add("latissimus dorsi", 786, 420);
            add("biceps femoris", 793, 781);
            add("trapecius", 677, 288);
            add("deltoid", 867, 293);
            add("sartorius", 355, 648);
            add("adductor longus",308, 776);
            add("tibiales anterior", 390, 1088);
            add("quadriceps femoris", 337, 808);
            add("pectoralis major", 311, 328);

        } else {
            add("gluteus maximus", 770, 529);
            add("gluteus medus", 815, 506);
            add("erector spinae", 757, 400);
            add("gastrocnemius", 796, 874);
            add("biceps brachii", 397, 323);
            add("rectus abdominus", 288, 377);
            add("vastus lateralis", 365, 664);
        }
        doDouble();
    }

    public void add(String name, float x, float y) {
        m_tag[m_count] = name;
        m_x[m_count] = x;
        m_y[m_count] = y;
        m_count++;
    }

    private float mirror(float x) {
        float mid = m_middle;
        float left = 258;
        float right = 727;
        if (x < mid) {
            x = left - (x - left);
        } else {
            x = right - (x - right);
        }
        return x;
    }

    public void doDouble() {

        String [] tmpS = new String[2*m_count];
        float [] x = new float[2*m_count];
        float [] y = new float[2*m_count];

        for (int i=0; i<m_count; i++) {
            x[2*i] = m_x[i];
            x[2*i+1] = mirror(m_x[i]);

            y[2*i] = m_y[i];
            y[2*i+1] = m_y[i];

            if (m_x[i] > m_middle) {
                tmpS[2 * i] = m_tag[i] + " - right";
                tmpS[2 * i + 1] = m_tag[i] + " - left";
            } else {
                tmpS[2 * i + 1] = m_tag[i] + " - right";
                tmpS[2 * i] = m_tag[i] + " - left";
            }
        }
        m_count = 2*m_count;
        m_x = x;
        m_y = y;
        m_tag = tmpS;
    }

    private float dist(float x, float y, float x1, float y1) {
        float a = x1-x;
        if (a < 0)
            a = -a;
        float b = y1-y;
        if (b < 0)
            b = -b;
        return a + b;
    }

    public String getClicked(float x, float y) {
        float min = dist(m_x[0], m_y[0], x, y);
        int idx = 0;
        for (int i=0; i<m_count; i++) {
            float d = dist(m_x[i], m_y[i], x, y);
            if (d < min) {
                min = d;
                idx = i;
            }
        }
        if (min < 70)
            return m_tag[idx];
        else
            return null;
        //return "Da Mussel";
    }
}
