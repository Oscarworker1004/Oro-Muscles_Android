package com.digidactylus.recorder.ui;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MLog {

    private static String m_fileName;

    public static void init(String fileName) {
        m_fileName = fileName;
    }

    public static void log(String msg) {
        long time= System.currentTimeMillis();
        String stat = new String();
        //stat += time/1000;
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        stat = formatter.format(date);

        try {
            FileWriter fw = new FileWriter(m_fileName, true);
            fw.write(stat + " - " + msg + "\n");
            fw.close();
        } catch (Exception e) {

        }

    }
}
