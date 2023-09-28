package com.digidactylus.recorder.ui;

import static com.digidactylus.recorder.ui.DashboardView.getDashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.digidactylus.recorder.Fragments.DataFragment;
import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.models.LabelingModel;
import com.digidactylus.recorder.utils.Constants;
import com.digidactylus.recorder.utils.TinyDB;

import java.util.ArrayList;
import java.util.List;
//import android.view.ImageView;

public class FeedView extends ImageView {

    private int m_x = 0;
    private int m_y = 0;
    private boolean m_run = false;
    private static DataBuffer m_buf;
    private boolean m_newData = false;

    private static FeedView g_feed;
    private RMSAnalyze m_rms;

    private boolean m_isSilence = true;
    private boolean m_newPeak = false;
    private MainActivity m_main = null;

    private double m_AUC = 0.;

    private double [] m_peaks;
    private double [] m_rmsPeaks;

    private double [] m_peaksSet;
    private double [] m_rmsPeaksSet;


    private double m_sumRMS = 0.;
    private double m_sumAUC = 0.;

    private boolean m_notifyMove = false;
    private boolean m_notifyStop = false;
    private boolean m_notifyStart = false;

    private int m_peakCount = 0;

    // Elements here
    OneBarGraph m_intens;
    OneBarGraph m_capac;
    OneWheel m_intensWheel;
    OneWheel m_capacWheel;
    OneTriangle m_triangle;
    OneEMG m_emg;
    OneCrossCorr m_cross;
    boolean m_dispToggle = true;
    DataFragment dataFragment;

    boolean m_useSets = true;
    int m_skipFrames = 1;

    public void setMain(MainActivity m) {
        m_main = m;
    }

    // Constructor
    public FeedView(Context context, AttributeSet attr) {
        super(context, attr);

        m_rms = new RMSAnalyze();
        dataFragment = new DataFragment();

        m_peaks = new double[20];
        m_rmsPeaks = new double[20];

        m_peaksSet = new double[20];
        m_rmsPeaksSet = new double[20];



        clearPeaks();


        m_intens = new OneBarGraph();
        m_capac = new OneBarGraph();

        m_intens.setCaption("Intensity");
        m_capac.setCaption("Work Capacity");

        m_intens.setCoords(10,450, 800, 270);
        m_capac.setCoords(10,750, 800, 270);

        m_cross = new OneCrossCorr();
        float corrOff = m_intens.h()/4;
        m_cross.setCoords(10 + m_intens.x() + m_intens.w(), m_intens.y()+corrOff,
                120, m_capac.y()+m_capac.h()-m_intens.y()-2*corrOff);



        m_intensWheel = new OneWheel();
        m_intensWheel.setCoords(10, 400, 500, 500);
        m_intensWheel.setCaption("Intensity (%)");

        m_capacWheel = new OneWheel();
        m_capacWheel.setCoords(520, 400, 500, 500);
        m_capacWheel.setCaption("Capacity (%)");

        m_triangle = new OneTriangle(context);
        m_emg = new OneEMG();

        m_emg.setCaption("Muscle");
        m_emg.setCoords(10, 10, 600, 350);

        m_triangle.setCaption("Movement");
        m_triangle.setCoords(660, 10, 330, 350);

        m_newData = true;

        //super(context);

        g_feed = this;

        int width = this.getWidth();

        m_buf = new DataBuffer();

        m_buf.setSize((int)m_capac.w());

        m_run = true;


        new Thread(new Runnable() {
            public void run() {

                int count = 0;

                try {
                  while (true) {
                      Thread.sleep(10);
                      if (m_run) {
                          if (m_newData) {
                              if (count >= m_skipFrames) {
                                  invalidate();
                                  count = 0;
                              } else {
                                  count++;
                              }
                              m_newData = false;
                          }
                      }
                  }
                } catch(Exception e) {

                }

            }
        }).start();

        //==========================================
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dispToggle = !m_dispToggle;
                invalidate();
            }
        });

    }

   /* public void toggleDisp() {
        m_dispToggle = !m_dispToggle;
    }*/
   public void setSkipFrames(int n) {
       m_skipFrames = n;
   }

    public RMSAnalyze getRMSAnalayze() {
        return m_rms;
    }
    int setcount = 1;
    private void addPeak(double rms, double auc) {
        // MGG: Sets or not?
        if (!m_useSets) {
            /// HERE
            MainActivity.getMain().updateIntensity(m_peaks, 0);
            MainActivity.getMain().updateIntensity(m_rmsPeaks, 1);
        }
        m_main.setsCircleText(String.valueOf(setcount));
        for (int i=1; i<m_peaks.length; i++) {
            m_peaks[i-1] = m_peaks[i];
            m_rmsPeaks[i-1] = m_rmsPeaks[i];
        }
        m_peaks[m_peaks.length-1] = auc;
        m_rmsPeaks[m_rmsPeaks.length-1] = rms;


        m_peakCount++;
        m_sumRMS += rms;
        m_sumAUC += auc;


        if (getDashboard() != null){
            getDashboard().addPeak(rms, auc,0, 0);
        }

        setcount++;

    }
    private void clearPeaks() {
        for (int i=0; i<m_peaks.length; i++) {
            m_peaks[i] = 0.;
            m_rmsPeaks[i] = 0.;
        }
        for (int i=0; i<m_peaksSet.length; i++) {
            m_peaksSet[i] = 0.;
            m_rmsPeaksSet[i] = 0.;
        }

        /*
        if (DashboardView.getDashboard() != null) {
            DashboardView.getDashboard().endSet();
            DashboardView.getDashboard().startSet();
        }*/
        m_peakCount = 0;
        m_sumAUC = 0.;
        m_sumRMS = 0.;
    }

    public static FeedView getFeed() {
        return g_feed;
    }

    public boolean isMove() {
        boolean b = m_notifyMove;
        m_notifyMove = false;
        return b;
    }
    public boolean isStop() {
        boolean b = m_notifyStop;
        m_notifyStop = false;
        return b;
    }
    public boolean isStart() {
        boolean b = m_notifyStart;
        m_notifyStart = false;
        return b;
    }

    public void FeedData(double [] d, int n) {
        m_buf.addData(d, n);


        for (int i=0; i<d.length; i+=n) {
            if (m_rms.feed(d[i+2], d[i+1])) {
                m_AUC = m_rms.getAUC();
                double rms = m_rms.getRMS();
                addPeak(rms, m_AUC);
                m_newPeak = true;
                m_notifyMove = true;
            }
        }
        boolean b = m_rms.isSilence();

        if (b && !m_isSilence) {
            m_notifyStop = true;
            if (getDashboard() != null) {
                getDashboard().endSet();
            }

            // MGG: Sets
            if (m_useSets && m_peakCount > 3) {

                for (int i = 1; i < m_peaksSet.length; i++) {
                    m_peaksSet[i - 1] = m_peaksSet[i];
                    m_rmsPeaksSet[i - 1] = m_rmsPeaksSet[i];
                }
                m_peaksSet[m_peaksSet.length-1] = m_sumAUC/5;
                m_rmsPeaksSet[m_peaksSet.length-1] = m_sumRMS/(double)m_peakCount;

                MainActivity.getMain().updateIntensity(m_peaksSet, 0);
                MainActivity.getMain().updateIntensity(m_rmsPeaksSet, 1);

            }


        }

        if (!b && m_isSilence) {
            clearPeaks();
            if (getDashboard() != null) {
                getDashboard().startSet();
            }
            m_notifyStart = true;
        }
        m_isSilence = b;
        m_newData = true;
    }



    public void run() {
        m_run = true;
    }
    public void stop() {
        m_run = false;
    }
    /*
    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        //super.onCreateView(savedInstanceState);
    }*/



    @Override
    public void onDraw(Canvas canvas){

        int i;

        canvas.drawColor(Color.rgb(0,0,0));


        Paint redPaint = new Paint();
        redPaint.setColor(Color.rgb(255, 120, 0));

        Paint greenPaint = new Paint();
        greenPaint.setColor(Color.rgb(30, 255, 42));
        greenPaint.setTextSize(40);

        Paint bluePaint = new Paint();
        bluePaint.setColor(Color.rgb(90, 90, 255));

        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.rgb(255, 255, 255));
        whitePaint.setTextSize(60);

        Paint whitePaintS = new Paint();
        whitePaintS.setColor(Color.rgb(255, 255, 255));
        whitePaintS.setTextSize(40);

        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.rgb(0, 0, 0));
        blackPaint.setTextSize(60);

        Paint darkRedPaint = new Paint();
        darkRedPaint.setColor(Color.rgb(200, 0, 0));

        Paint greyPaint = new Paint();
        greyPaint.setColor(Color.rgb(60, 60, 70));

        Paint greyPaint2 = new Paint();
        greyPaint2.setColor(Color.rgb(180, 180, 180));


        Paint redPaintT = new Paint();
        redPaintT.setColor(Color.rgb(255, 120, 0));

        Paint greenPaintT = new Paint();
        greenPaintT.setColor(Color.rgb(30, 255, 42));

        Paint bluePaintT = new Paint();
        bluePaintT.setColor(Color.rgb(90, 90, 255));

        Paint bluePaintL = new Paint();
        bluePaintL.setColor(Color.rgb(150, 150, 255));

        Paint whitePaintT = new Paint();
        whitePaintT.setColor(Color.rgb(255, 255, 255));

        Paint rmsPaint = new Paint();
        rmsPaint.setColor(Color.rgb(255, 255, 60));
        rmsPaint.setTextSize(40);



        float eOff = 120;
        float aOff = 240;
        float gOff = 360;

        float width = this.getWidth();



        if (m_newPeak) {
            //==========================================================
            m_newPeak = false;
        }

        float w = 10;
        float h_off = 410;

        if (m_dispToggle) {
            m_intens.doDraw(canvas, m_peaks, 3. / 100., DashboardView.getDashboard().getLastCorrRMS(),0);
            m_capac.doDraw(canvas, m_rmsPeaks, 30. / 100., DashboardView.getDashboard().getLastCorrAUC(),1);
            m_cross.doDraw(canvas, DashboardView.getDashboard().getLastCorrRMSAUC());
        } else {
            double intens = DashboardView.getDashboard().lastRMSRatio();
            double capac = DashboardView.getDashboard().lastAUCRatio();
            m_intensWheel.setValue(intens);
            m_intensWheel.doDraw(canvas);
            m_capacWheel.setValue(capac);
            m_capacWheel.doDraw(canvas);
        }


        //m_triangle.doDraw(canvas, m_buf);
        TriangleGraph.getInstance().invalidate();

        //m_emg.doDraw(canvas, m_buf);
        EMGLineGraph.getInstance().invalidate();



        //============================== REFACTOR THIS =============================
        // Peaks & silence here!!
        String cc = new String();

        //m_peakCount = 22;
        cc += m_peakCount;

        float corn = 2;

        float cX = 90;
        float cY = 280;

        //canvas.drawRect(500-corn,600-corn, 700+corn, 700+corn, greyPaint2);
        //canvas.drawRect(500,600, 700, 700, greyPaint);
        if (m_isSilence) {
            //m_main.setsCircleText(cc);
            canvas.drawCircle(cX+10, cY-10, 60, whitePaint);
            //canvas.drawText("idle", 50, 360, blackPaint);
            if (m_peakCount < 10)
                canvas.drawText(cc, cX-5, cY+10, blackPaint);
            else
                canvas.drawText(cc, cX-22, cY+10, blackPaint);
        } else {
            //m_main.setsCircleText(cc);

            canvas.drawCircle(cX+10, cY-10, 60, darkRedPaint);
            //canvas.drawText("move", 50, 360, blackPaint);
            if (m_peakCount < 10)
                canvas.drawText(cc, cX-5, cY+10, whitePaint);
            else
                canvas.drawText(cc, cX-22, cY+10, whitePaint);
        }



    }

    public static DataBuffer getM_buf(){
        return m_buf;
    }
}
