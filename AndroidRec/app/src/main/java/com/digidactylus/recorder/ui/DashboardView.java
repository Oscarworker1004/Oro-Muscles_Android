package com.digidactylus.recorder.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.digidactylus.recorder.MemReadBuffer;
import com.digidactylus.recorder.MemWriteBuffer;
import com.digidactylus.recorder.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DashboardView extends ImageView {

    private float m_x = 10;
    private float m_y = 10;

    private float m_xM = 0;
    private float m_yM = 0;

    private OneSet [] m_sets;
    private OneMove [] m_move;

    private int m_count = 0;

    private Bitmap m_trash;
    private Bitmap m_muscle;

    private boolean trash_busy = false;

    private int m_selected = -1;
    private int m_lastSelected = -1;
    private int m_element = -1;
    private int m_toggle = -1;

    private String m_fileName;

    private static DashboardView g_dashboard = null;

    private float m_gx = 780;
    private float m_gy = 980;

    private GraphicElement m_muscleButton;
    private GraphicElement m_exeButton;

    private ExerciseGraph m_exeGraph;
    private MuscleGraph m_muscleGraph;

    private boolean m_ai = true;

    private String m_sessionFile;

    private boolean m_useMoves = false;

    private double m_ratioRMS = 0.;
    private double m_ratioAUC = 0.;
    String m_timeCreate;

    public DashboardView(Context context, AttributeSet attr) {
        super(context, attr);

        g_dashboard = this;
        m_sessionFile = new String();

        m_timeCreate = new String();

        m_trash = BitmapFactory.decodeResource(getResources(), R.drawable.trash);
        if (!m_ai)
            m_muscle = BitmapFactory.decodeResource(getResources(), R.drawable.musclemap);
        else
            m_muscle = BitmapFactory.decodeResource(getResources(), R.drawable.musclemap2);

        m_muscleButton = new GraphicElement();
        m_exeButton = new GraphicElement();

        m_muscleGraph = new MuscleGraph();

        Bitmap tmp = BitmapFactory.decodeResource(getResources(), R.drawable.muscle);
        m_muscleButton.set(m_gx, 1260, tmp);
        tmp = BitmapFactory.decodeResource(getResources(), R.drawable.exercise);
        m_exeButton.set(m_gx-180, 1260, tmp);

        m_exeGraph = new ExerciseGraph();

        m_sets = new OneSet[64];
        //initSets();

        //==================== MOVES ============================
        m_move = new OneMove[5];
        for (int i=0; i<m_move.length; i++) {
            m_move[i] = new OneMove();
            m_move[i].setText("<empty>", "", "", "");
            //m_move[1].setText("biceps left", "bodyweight curl", "120kg", "10 reps");
            float a = 50 + 2 * m_move[0].width();
            float b = 10 + 10*i + i*m_move[0].height();
            m_move[i].setCoords(a, b);

        }

        //===========================================

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_y = view.getX();
                m_x = view.getY();
                invalidate();
            }
        });

        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    m_x = (int) event.getX();
                    m_y = (int) event.getY();
                    m_xM = m_x;
                    m_yM = m_y;

                    m_selected = -1;

                    boolean b = checkElement(m_x, m_y);

                    if (b || m_exeButton.isClicked() || m_muscleButton.isClicked()) {
                        if (!b && m_exeButton.isClicked()) {
                            m_exeGraph.startMove(m_y);
                        }
                        m_selected = -1;
                        for (int i = 0; i < m_count; i++) {
                            m_sets[i].setSelected(-1, -1);
                        }

                    } else {
                        // De-select first
                        for (int i = 0; i < m_count; i++) {
                            m_sets[i].setSelected(-1, -1);
                        }
                        boolean no = false;
                        for (int i = 0; i < m_count; i++) {
                            if (m_sets[i].setSelected(m_x, m_y)) {
                                m_selected = i;
                                m_lastSelected = m_selected;
                                no = true;
                                break;
                            }
                        }

                        if (m_useMoves) {
                            for (int i = 0; i < m_move.length; i++) {
                                if (no)
                                    m_move[i].unselect();
                                else
                                    m_move[i].select(m_x, m_y);
                            }
                        }

                    }



                    invalidate();
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    int endX = (int) event.getX();
                    int endY = (int) event.getY();
                    int dX = (int) (endX - m_xM);
                    int dY = (int) (endY - m_yM);
                    m_xM = endX;
                    m_yM = endY;
                    if (m_exeButton.isClicked()) {
                        m_exeGraph.addMove(endY);
                    } else {
                        for (int i = 0; i < m_count; i++) {
                            m_sets[i].move(endX, endY);
                        }
                        if (m_selected == -1) {
                            for (int i = 0; i < m_count; i++) {
                                m_sets[i].moveCoords(dX, dY);
                            }
                        }
                    }
                    if (m_useMoves) {
                        for (int j = 0; j < m_move.length; j++) {
                            m_move[j].highlight(endX, endY);
                        }
                    }
                    invalidate();
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int endX = (int) event.getX();
                    int endY = (int) event.getY();

                    if (m_selected != -1) {
                        if (endX > m_gx && endY > m_gy &&
                                endX < m_gx + m_trash.getWidth() && endY < m_gy + m_trash.getHeight()) {
                            removeSet(m_selected);
                            reArrange();
                            invalidate();
                        }

                        if (m_useMoves) {
                            for (int j=0; j<m_move.length; j++) {
                                if (m_move[j].contains(endX, endY)) {
                                    m_sets[m_selected].setCoords(m_move[j].x(), m_move[j].y());
                                    m_sets[m_selected].setExercise(m_move[j].exe());
                                    m_sets[m_selected].setMuscle(m_move[j].muscle());
                                    break;
                                }
                            }
                        }
                    }
                    //int dX = Math.abs(endX - startX);
                    //int dY = Math.abs(endY - startY);
                    if (m_exeButton.isClicked()) {
                        m_exeGraph.setMove(endY);
                    }

                }
                return true;
            }
        });


    }

    public double lastRMSRatio() {
        return m_ratioRMS;
    }
    public double lastAUCRatio() {
        return m_ratioAUC;
    }

    public void setSessionFile(String s) {
        m_sessionFile = s;
        startSession();
    }


    public void writeSetsBin(String fileName) {
        int n = 0;
        for (int i=0; i<m_count; i++) {
            if (m_sets[i].getSessionFile().equals(fileName))
                n++;
        }
        if (n == 0)
            return;

        MemWriteBuffer mw = new MemWriteBuffer();
        // The global header should be written somewhere else!!!!!!
        int glob = 0x68746D01; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        mw.writeInt(glob);

        int sec = 0x0F0F0F0F;
        mw.writeInt(sec);

        int id = 8*256 + 1; // Type & version
        mw.writeInt(id);

        mw.writeInt(n);

        for (int i=0; i<m_count; i++) {
            if (m_sets[i].getSessionFile().equals(fileName))
                m_sets[i].write(mw);
        }

            try {
            mw.writeToFile(fileName + ".sets");
        } catch(Exception e) {

        }
    }

    public void updateExercises() {
        m_exeGraph.update();
    }

    public static DashboardView getDashboard() {
        return g_dashboard;
    }

    private boolean checkElement(float x, float y) {
        /*if (x > m_gx && y > m_gy) {
            m_toggle *= -1;
            return 1;
        }*/
        //m_toggle = -1;

        if (m_muscleButton.isClicked(x, y)) {
            m_muscleButton.toggle();
            m_selected = -1;
            return true;
        }



        if (m_exeButton.isClicked(x, y)) {
            m_selected = -1;
            if (m_exeButton.isClicked()) {
                String exe = m_exeGraph.getSelected();
                if (exe != null && m_lastSelected != -1) {
                    m_sets[m_lastSelected].setExercise(exe);
                    int i;
                    for (i=m_lastSelected+1; i<m_count; i++)
                        m_sets[i].setExerciseDefault(exe);

                    if (m_useMoves) {
                        for (i = 0; i < m_move.length; i++) {
                            if (m_move[i].isSelected())
                                m_move[i].setExe(exe);
                        }
                    }
                }
            }
            m_exeButton.toggle();
            return true;
        }

        if (m_exeButton.isClicked()) {
            m_exeGraph.setSelected(x, y);
        }

        return false;
    }

    private void initSets() {
        // Keep this for now... remove later!!
        //if (true)
          //  return;
        m_count = 4;
        m_sets[0] = new OneSet();
        m_sets[1] = new OneSet();
        m_sets[2] = new OneSet();
        m_sets[3] = new OneSet();

        m_sets[0].add(25, 0, 0);
        m_sets[0].add(28, 0, 0);
        m_sets[0].add(23, 0, 0);
        m_sets[0].add(30, 0, 0);
        m_sets[0].add(33, 0, 0);
        m_sets[0].done();

        m_sets[1].add(28, 0, 0);
        m_sets[1].add(27, 0, 0);
        m_sets[1].add(35, 0, 0);
        m_sets[1].add(31, 0, 0);
        m_sets[1].add(22, 0, 0);
        m_sets[1].add(18, 0, 0);
        m_sets[1].done();

        m_sets[2].add(15, 0, 0);
        m_sets[2].add(16, 0, 0);
        m_sets[2].add(12, 0, 0);
        m_sets[2].add(18, 0, 0);
        m_sets[2].done();

        m_sets[3].add(12, 0, 0);
        m_sets[3].add(16, 0, 0);
        m_sets[3].add(17, 0, 0);
        m_sets[3].add(19, 0, 0);
        m_sets[3].add(16, 0, 0);
        m_sets[3].add(19, 0, 0);
        m_sets[3].add(19, 0, 0);
        m_sets[3].add(22, 0, 0);
        m_sets[3].add(20, 0, 0);
        m_sets[3].add(12, 0, 0);
        m_sets[3].add(8, 0, 0);
        m_sets[3].add(24, 0, 0);
        m_sets[3].add(29, 0, 0);
        m_sets[3].add(22, 0, 0);
        m_sets[3].add(21, 0, 0);
        m_sets[3].add(33, 0, 0);
        m_sets[3].done();

        //m_sets[0].setCoords(10,10);
        //m_sets[1].setCoords(10*2+m_sets[0].width(),10);

        m_sets[0].setInfo("14:38.17", "21", "glut max left", "squat");
        m_sets[1].setInfo("14:39.44", "29", "glut max left", "press");
        m_sets[2].setInfo("14:42.08", "33", "glut max right", "squat");
        m_sets[3].setInfo("14:45.27", "19", "glut max right", "bench");

        for (int i=0; i<m_count; i++) {
            int a = i % 3;
            int b = i/3;
            m_sets[i].setCoords(10+10*a+a*m_sets[0].width(),10+10*b+b*m_sets[0].height());
        }

    }


    private void reArrange() {
        for (int i = 0; i < m_count; i++) {
            int a = i % 3;
            int b = i / 3;
            m_sets[i].setCoords(10 + 10 * a + a * m_sets[0].width(), 10 + 10 * b + b * m_sets[0].height());

        }
    }


    // Public functions for setup/update
    private void removeSet(int idx) {
        for (int i=idx; i<m_count-1; i++)
            m_sets[i] = m_sets[i+1];
        m_count--;
    }

    //================================================================
    public void read(MemReadBuffer buf) {
        int version = buf.readInt();
        m_count = buf.readInt();
        m_sets = new OneSet[m_count];
        int i;
        for (i=0; i<m_count; i++) {
            m_sets[i] = new OneSet();
            m_sets[i].read(buf, version);
        }
        if (version > 2) {
            int movCnt = buf.readInt();
            m_move = new OneMove[movCnt];
            for (i=0; i<movCnt; i++) {
                m_move[i] = new OneMove();
                m_move[i].read(buf, version);
            }
        }
    }
    public void read(String filename) {
        MemReadBuffer buf = new MemReadBuffer();
        m_fileName = filename;
        try {
            buf.readFromFile(filename);
            read(buf);
        } catch(Exception e) {
            System.out.println("File doesn't exist: " + filename);
        }

    }

    public void write(MemWriteBuffer buf) {
        int version = 5;
        buf.writeInt(version);
        buf.writeInt(m_count);
        for (int i=0; i<m_count; i++) {
            m_sets[i].write(buf);
        }
        buf.writeInt(m_move.length);
        for (int i=0; i<m_move.length; i++) {
            m_move[i].write(buf);
        }


    }

    public void write(String filename) {
        MemWriteBuffer buf = new MemWriteBuffer();
        write(buf);
        try {
            buf.writeToFile(filename);
        } catch(Exception e) {
        }
    }
    public void write() {
        if (m_fileName != null)
            write(m_fileName);
    }

    public void startSet() {
        if (m_count > 0 && m_sets[m_count-1].count() == 0)
            m_count--;

        if (m_count == m_sets.length) {
            OneSet [] tmp = new OneSet[m_count + 64];
            for (int i=0; i<m_count; i++)
                tmp[i] = m_sets[i];
            m_sets = tmp;
        }
        m_sets[m_count] = new OneSet();
        m_sets[m_count].setSessionFile(m_sessionFile);
        m_sets[m_count].setTimeCreate(m_timeCreate);

        int a = m_count % 3;
        int b = m_count/3;
        m_sets[m_count].setCoords(10+10*a+a*m_sets[0].width(),10+10*b+b*m_sets[0].height());

        long time= System.currentTimeMillis();
        String stat = new String();
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        stat = formatter.format(date);
        m_sets[m_count].setTime(stat);

        m_count++;
    }

    public void startSession() {
        long time= System.currentTimeMillis();
        String stat = new String();
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        stat = formatter.format(date);
        m_timeCreate = stat;

    }


    public void endSet() {
        if (m_sets != null && m_count > 0 && m_sets[m_count-1].count() > 0) {
            m_sets[m_count-1].done();

            long time= System.currentTimeMillis();
            String stat = new String();
            Date date = new Date(time);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            stat = formatter.format(date);
            m_sets[m_count-1].setTimeEnd(stat);
        }
    }


    // Add more info if needed
    public void addPeak(double rms, double auc, double time, double duration) {
        if (m_count == m_sets.length)
            startSet();
        m_sets[m_count-1].add(rms, rms, auc); // Feed more info HERE

        if (m_count > 2) {
            double lastRMS = 0.;
            double lastAUC = 0.;
            boolean found = false;
            for (int i=m_count-2; i>=0; i--) {
                if (m_sets[i].count() > 4) {
                    lastRMS = m_sets[i].meanRMS();
                    lastAUC = m_sets[i].totalAUC();
                    found = true;
                    break;
                }
            }
            m_ratioRMS = m_sets[m_count-1].meanRMS()/lastRMS;
            m_ratioAUC = m_sets[m_count-1].totalAUC()/lastAUC;
        } else {
            m_ratioRMS = 0.;
            m_ratioAUC = 0.;
        }

        invalidate();
    }

    public double getLastCorrAUC() {
        if (m_count == 0)
            return 0.;
        return m_sets[m_count-1].corr();
    }

    public double getLastCorrRMS() {
        if (m_count == 0)
            return 0.;
        return m_sets[m_count-1].corrRMS();
    }
    public double getLastCorrRMSAUC() {
        if (m_count == 0)
            return 0.;
        return m_sets[m_count-1].corrRMSAUC();
    }

    //================================================================
    @Override
    public void onDraw(Canvas canvas) {
        if (m_exeButton.isClicked()) {
            canvas.drawColor(Color.rgb(232, 232, 232));
        } else {
            if (m_muscleButton.isClicked())
                canvas.drawColor(Color.rgb(0, 0, 0));
            else
                canvas.drawColor(Color.rgb(132, 132, 132));
        }
        Paint greenPaint = new Paint();
        greenPaint.setColor(Color.rgb(30, 255, 42));

        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.rgb(0, 0, 0));

        Paint bmpPaint = new Paint();
        bmpPaint.setColor(Color.rgb(100, 100, 100));

        // Show muscle map if 1
        m_toggle = -1;
        if (m_muscleButton.isClicked() || m_exeButton.isClicked())
            m_toggle = 1;

        if (m_toggle == 1) {
            if (m_muscleButton.isClicked()) {
                canvas.drawBitmap(m_muscle, 0, 0, bmpPaint);
                canvas.drawCircle(m_x, m_y, 20, greenPaint);

                String mus = m_muscleGraph.getClicked(m_x, m_y);

                boolean update = true;
                if (mus == null) {
                    mus = "<click muscle>";
                    update = false;
                }
                Paint whitePaint = new Paint();
                if (!m_ai)
                    whitePaint.setColor(Color.rgb(255, 255, 255));
                else
                    whitePaint.setColor(Color.rgb(0, 0, 0));
                whitePaint.setTextSize(50);
                String cc = new String();
                cc += m_x;
                cc += " ";
                cc += m_y;
                canvas.drawText(mus, 2, 1200, whitePaint);
                canvas.drawText(cc, 2, 1260, whitePaint);

                if (update) {
                    if (m_lastSelected != -1) {
                        m_sets[m_lastSelected].setMuscle(mus);

                        for (int i=m_lastSelected+1; i<m_count; i++)
                            m_sets[i].setMuscleDefault(mus);

                    }

                    if (m_useMoves) {
                        for (int i=0; i<m_move.length; i++) {
                            if (m_move[i].isSelected())
                                m_move[i].setMuscle(mus);
                        }
                    }

                }
            }
            if (m_exeButton.isClicked()) {
                //canvas.drawBitmap(m_exec, 0, 0, bmpPaint);
                //canvas.drawCircle(m_x, m_y, 20, greenPaint);
                m_exeGraph.doDraw(canvas);
            }

        } else {
            // For text!


            float textBase = 1230;
            float textHeight = 50;
            canvas.drawRect(0, textBase - 50, 1200, 1600, blackPaint);

            Paint whitePaint = new Paint();
            whitePaint.setColor(Color.rgb(255, 255, 255));
            whitePaint.setTextSize(40);

            if (m_selected != -1) {
                canvas.drawText(m_sets[m_selected].exercise(), 2, textBase + 0 * textHeight, whitePaint);
                canvas.drawText(m_sets[m_selected].muscle(), 2, textBase + 1 * textHeight, whitePaint);
                canvas.drawText(m_sets[m_selected].reps(), 2, textBase + 2 * textHeight, whitePaint);
                canvas.drawText(m_sets[m_selected].time(), 2, textBase + 3 * textHeight, whitePaint);

                canvas.drawText(m_sets[m_selected].mean(), 2, textBase + 5 * textHeight, whitePaint);
                canvas.drawText(m_sets[m_selected].total(), 2, textBase +4 * textHeight, whitePaint);

            }

            int i;

            canvas.drawBitmap(m_trash, m_gx, m_gy, bmpPaint);

            if (m_useMoves) {
                for (i = 0; i < m_move.length; i++) {
                    m_move[i].doDraw(canvas);
                }
            }

            for (i = 0; i < m_count; i++) {
                m_sets[i].doDraw(canvas);
            }

        }

        // Move garbage can somewhere further up!!!
        // Garbage can
        m_muscleButton.doDraw(canvas);
        m_exeButton.doDraw(canvas);

    }

}
