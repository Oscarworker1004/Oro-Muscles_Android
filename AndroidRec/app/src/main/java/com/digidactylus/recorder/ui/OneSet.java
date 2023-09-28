package com.digidactylus.recorder.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.MemReadBuffer;
import com.digidactylus.recorder.MemWriteBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OneSet {
    private double m_corr = 0.;
    private double m_corrRMS = 0.;
    private double m_corrRMSAUC = 0.;

    private double [] m_data = null;
    private double [] m_rms = null;
    private double [] m_auc = null;
    private int m_count = 0;
    private String m_time;
    private String m_timeEnd;
    private String m_timeCreate;
    private String m_duration;
    private String m_exercise;
    private String m_muscle;

    private float m_w;
    private float m_h;

    private float x = 0;
    private float y = 0;

    private float x_sel = 0;
    private float y_sel = 0;

    private boolean m_selected = false;

    private String m_timeF;
    private String m_exerciseF;
    private String m_muscleF;

    private boolean m_autoMuscle = true;
    private boolean m_autoExe = true;

    private String m_sessionFile;

    private int m_frameStart = 0;
    private int m_frameStop = 0;

    OneSet() {
        m_w = 290;
        m_h = 200;
        m_timeF = new String();
        m_muscleF = new String();
        m_exerciseF = new String();
        m_time = new String();
        m_timeEnd = new String();
        m_timeCreate = new String();
        m_duration = new String();
        m_muscle = new String();
        m_exercise = new String();
        m_sessionFile = new String();
    }

    public void setSessionFile(String sess) {
        m_sessionFile = sess;
    }
    public String getSessionFile() {
        return m_sessionFile;
    }

    public boolean write(MemWriteBuffer buf) {
        buf.writeString(m_time);
        buf.writeString(m_duration);
        buf.writeString(m_muscle);
        buf.writeString(m_exercise);
        buf.writeString(m_sessionFile);

        buf.writeString(m_timeEnd);
        buf.writeString(m_timeCreate);
        buf.writeInt(m_frameStart);
        buf.writeInt(m_frameStop);


        buf.writeDouble(x);
        buf.writeDouble(y);

        if (m_autoMuscle)
            buf.writeInt(1);
        else
            buf.writeInt(0);
        if (m_autoExe)
            buf.writeInt(1);
        else
            buf.writeInt(0);



        buf.writeInt((int)((m_corr+1)*100));
        buf.writeInt(m_count);
        int i;
        for (i=0; i<m_count; i++) {
            buf.writeDouble(m_data[i]);
            buf.writeDouble(m_rms[i]);
            buf.writeDouble(m_auc[i]);
        }
        /*
        for (i=0; i<m_count; i++) {
            buf.writeDouble(m_rms[i]);
        }
        for (i=0; i<m_count; i++) {
            buf.writeDouble(m_rms[i]);
        }*/


        return true;
    }
    public boolean read(MemReadBuffer buf, int version) {
        m_time = buf.readString();
        m_duration = buf.readString();
        m_muscle = buf.readString();
        m_exercise = buf.readString();

        if (version >= 4)
            m_sessionFile = buf.readString();

        if (version >= 5) {
            m_timeEnd = buf.readString();
            m_timeCreate = buf.readString();
            m_frameStart = buf.readInt();
            m_frameStop = buf.readInt();
        }
        //m_exercise = "";

        // IMPORTANT: read/write the assignment status!!
        /*m_autoMuscle = true;
        if (m_muscle != null && !m_muscle.equals(""))
            m_autoMuscle = false;
        m_autoExe = true;
        if (m_exercise != null && !m_exercise.equals(""))
            m_autoExe = false;
*/

        x = (float)buf.readDouble();
        y = (float)buf.readDouble();
        //x = 0;

        int b = buf.readInt();
        if (b == 1)
            m_autoMuscle = true;
        else
            m_autoMuscle = false;

        b = buf.readInt();
        if (b == 1)
            m_autoExe = true;
        else
            m_autoExe = false;


        //setInfo(m_time, m_duration, m_muscle, m_exercise);

        m_corr = -1+0.01*(double)buf.readInt();
        m_corr = Math.round(m_corr * 100.0) / 100.0;

        m_count = buf.readInt();
        int i;
        m_data = new double[m_count];
        m_rms = new double[m_count];
        m_auc = new double[m_count];
        for (i=0; i<m_count; i++) {
            m_data[i] = buf.readDouble();
            m_rms[i] = buf.readDouble();
            m_auc[i] = buf.readDouble();
        }

        done();

        // Enable when available
        /*
        m_rms = new double[m_count];
        for (i=0; i<m_count; i++) {
            m_rms[i] = buf.readDouble();
        }
        m_auc = new double[m_count];
        for (i=0; i<m_count; i++) {
            m_rms[i] = buf.readDouble();
        }
        */

        return true;
    }

    public void setInfo(String time, String duration, String muscle, String exercise) {
        m_time = time;
        m_muscle = muscle;
        m_exercise = exercise;
        m_duration = duration;

        m_timeF = "Time: ";
        m_timeF += time;
        m_timeF += "  Duration: ";
        m_timeF += duration;
        m_timeF += "s";

        m_muscleF = "Muscle: ";
        m_muscleF += muscle;

        m_exerciseF = "Exercise: ";
        m_exerciseF += exercise;
        m_exerciseF += "  Reps: ";
        m_exerciseF += m_count;
    }

    public void setTime(String time) {
        m_time = time;
    }

    public void setTimeCreate(String time) {
        m_timeCreate = time;
    }
    public void setTimeEnd(String time) {
        m_timeEnd = time;
    }

    public String reps() {
        String r = "Reps: ";
        r += m_count;
        return r;
    }

    public String time() {
        m_timeF = "Start: ";
        m_timeF += m_time;
        m_timeF += "  End: ";
        m_timeF += m_timeEnd;
        //m_timeF += "s";
        return m_timeF;
    }
    public String muscle() {
        m_muscleF = "Muscle: ";
        m_muscleF += m_muscle;
        return m_muscleF;
    }
    public String exercise() {
        m_exerciseF = "Exercise: ";
        m_exerciseF += m_exercise;
        return m_exerciseF;
    }

    public void setExercise(String ss) {
        m_exercise = ss;
        m_autoExe = false;
    }
    public void setMuscle(String ss) {
        m_muscle = ss;
        m_autoMuscle = false;
    }

    public void setExerciseDefault(String ss) {
        if (m_autoExe)
            m_exercise = ss;
    }
    public void setMuscleDefault(String ss) {
        if (m_autoMuscle)
            m_muscle = ss;
    }


    public boolean setSelected(float xx, float yy) {
        m_selected = false;
        if (xx >= x && xx <= x+m_w && yy>=y && yy <= y+m_h) {
            m_selected = true;
            x_sel = xx-x;
            y_sel = yy-y;
            return true;
        }
        return false;
    }
    public void move(float xx, float yy) {
        if (!m_selected)
            return;
        x = xx - x_sel;
        y = yy - y_sel;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float width() {
        return m_w;
    }
    public float height() {
        return m_h;
    }

    public double [] data() {
        return m_data;
    }
    public double [] rms() {
        return m_rms;
    }
    public double [] auc() {
        return m_auc;
    }
    public double corr() {
        return m_corr;
    }
    public double corrRMS() {
        return m_corrRMS;
    }
    public double corrRMSAUC() {return m_corrRMSAUC;}

    public int count() {
        return m_count;
    }

    private double round(double d, int n) {
        double div = 1;
        for (int i=0; i<n; i++) {
            d *= 10;
            div *= 10;
        }
        d = (double)((int)d)/div;
        return d;
    }

    public String mean() {
        String s = "Mean AUC: ";
        s += round(meanAUC(), 1);
        s += " RMS: ";
        s += round(meanRMS(), 1);
        return s;
    }
    public String total() {
        String s = "Tot. AUC: ";
        s += round(totalAUC(), 1);
        s += " RMS: ";
        s += round(totalRMS(), 1);
        return s;
    }
    public double meanAUC() {
        if (m_count == 0)
            return 0;
        double s = 0.;
        for (int i=0; i<m_count; i++)
            s += m_auc[i];
        return s /(double)m_count;
    }
    public double meanRMS() {
        if (m_count == 0)
            return 0;
        double s = 0.;
        for (int i=0; i<m_count; i++)
            s += m_rms[i];
        return s /(double)m_count;
    }
    public double totalAUC() {
        if (m_count == 0)
            return 0;
        double s = 0.;
        for (int i=0; i<m_count; i++)
            s += m_auc[i];
        return s;
    }
    public double totalRMS() {
        if (m_count == 0)
            return 0;
        double s = 0.;
        for (int i=0; i<m_count; i++)
            s += m_rms[i];
        return s;
    }

    public void add(double d, double rms, double auc) {
        if (m_data == null || m_data.length <= m_count) {
            double [] tmp = new double[m_count + 128];
            for (int i=0; i<m_count; i++)
                tmp[i] = m_data[i];
            m_data = tmp;

            tmp = new double[m_count + 128];
            for (int i=0; i<m_count; i++)
                tmp[i] = m_rms[i];
            m_rms = tmp;

            tmp = new double[m_count + 128];
            for (int i=0; i<m_count; i++)
                tmp[i] = m_auc[i];
            m_auc = tmp;
        }
        m_data[m_count] = d;
        m_auc[m_count] = auc;
        m_rms[m_count] = rms;
        if (m_count > 3) {
            compCorr();
        }
        m_count++;
    }

    public void done() {
        if (m_data == null)
            return;
        // Compact data
        double [] tmp = new double[m_count];
        for (int i=0; i<m_count; i++)
            tmp[i] = m_data[i];
        m_data = tmp;
        m_corr = 0.;
        if (m_count > 3)
            compCorr();
    }

    public void compCorr() {
        if (m_count < 3) {
            m_corr = 0.;
            return;
        }
        double [] x = new double[m_count];
        double [] y = new double[m_count];
        double [] z = new double[m_count];
        for (int i=0; i<x.length; i++) {
            x[i] = (double) i;
            y[i] = m_auc[i];
            z[i] = m_rms[i];
        }
        m_corr = Spearmans.spearman(x, y);
        m_corr = (double)((int)(100*m_corr))/100;

        m_corrRMS = Spearmans.spearman(x, z);
        m_corrRMS = (double)((int)(100*m_corrRMS))/100;

        m_corrRMSAUC = Spearmans.spearman(y, z);
        m_corrRMSAUC = (double)((int)(100*m_corrRMSAUC))/100;
    }

    public void setCoords(float xx, float yy) {
        x = xx;
        y = yy;
    }

    public void moveCoords(float diffX, float diffY) {
        x += diffX;
        y += diffY;
    }

    public void doDraw(Canvas canvas) {
        Paint greyPaint = new Paint();
        greyPaint.setColor(Color.rgb(20, 20, 30));


        Paint redPaint = new Paint();
        redPaint.setColor(Color.rgb(200, 40, 0));

        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.rgb(255, 255, 255));
        whitePaint.setTextSize(60);

        if (m_selected) {
            greyPaint.setColor(Color.rgb(210, 210, 210));
            whitePaint.setColor(Color.rgb(0, 0, 0));
        }


            //if (true)
          //  return;

        float w = m_w;
        float h = m_h;
        canvas.drawRect(x,y, x+w, y+h, greyPaint);

        if (m_data == null)
            return;
        int i;

        float off = 20;

        float max = 0;
        for (i=0; i<m_count; i++) {
            if (m_data[i] > max)
                max = (float)m_data[i];
        }

        Paint barPaint = new Paint();
        int g = 255;
        if (m_corr < 0.)
            g += (int)255*m_corr;
        int r = 255;
        if (m_corr > 0.)
            r -= (int)255*m_corr;

        barPaint.setColor(Color.rgb(r, g, 0));
        float bar = 6;
        if (m_count > 22)
           bar = 3;
        if (m_count > 44)
            bar = 1;
        List<String> list = new ArrayList<>();

        for (i=0; i<m_count; i++) {
            float xx1 = x + off + 2*bar*i;
            float xx2 = x + off + 2*bar*i+bar;

            float yy1 = y + h - off;
            float yy2 = yy1 - (float)m_data[i]/max*(h-2*off);
            canvas.drawRect(xx1, yy1, xx2, yy2, barPaint);
            int  color = Color.rgb(r, g, 0);

            String dstr = String.format(Locale.getDefault(), "%s;%s;%s;%s;#%06X", xx1, yy1, xx2, yy2, (0xFFFFFF & color));
            list.add(dstr);
        }
        MainActivity.getMain().labelingModel.setBarList(list);
        double cc = 0;
        cc += m_corr;

        //Log.e("App", "doDraw: " + cc );
        MainActivity.getMain().labelingModel.setMedianValue(cc);
        MainActivity.getMain().labelingModel.setIntensity(totalRMS());
        MainActivity.getMain().labelingModel.setWorkCapacity(totalAUC());

        canvas.drawText(String.valueOf(cc),x+w/3, y+h/2+8, whitePaint);

        // Exercise HERE
        whitePaint.setTextSize(36);
        canvas.drawText(m_exercise,x+5, y+30, whitePaint);




        Paint exPaint = new Paint();
        Paint msPaint = new Paint();
        exPaint.setColor(Color.rgb(255, 0, 0));
        msPaint.setColor(Color.rgb(255, 0, 0));
        exPaint.setTextSize(30);
        msPaint.setTextSize(30);
        float xA = x + w - 25;
        float yAe = y + 24;
        float yAm = y + 50;

        if (m_muscle.equals("")) {
            canvas.drawText("?",xA, yAm, msPaint);
        } else {
            if (m_autoMuscle) {
                msPaint.setColor(Color.rgb(200, 130, 0));
            } else {
                msPaint.setColor(Color.rgb(0, 180, 90));
            }
            canvas.drawText("m",xA-4, yAm, msPaint);
        }
        if (m_exercise.equals("")) {
            canvas.drawText("?",xA, yAe, exPaint);
        } else {
            if (m_autoExe) {
                exPaint.setColor(Color.rgb(200, 130, 0));
            } else {
                exPaint.setColor(Color.rgb(0, 180, 90));
            }
            canvas.drawText("e",xA, yAe, exPaint);

        }

    }


}
