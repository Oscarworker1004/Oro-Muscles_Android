package com.digidactylus.recorder.ui;

import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.digidactylus.recorder.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class FileUpload {
    String m_baseURL = "https://68.183.8.165:8000/oro";
    //String m_baseURL = "https://192.168.1.109:8000/oro";
    private MainActivity m_main = null;

    public void setMain(MainActivity m) {
        m_main =m;
    }

    public boolean upload(String text, String binfile, String proj)
    {
        m_main.setStatus("starting upload...");

        if (m_main.getPref("use_auth").equals("true"))
            return upload_auth(text, binfile, proj);
        if (m_main.getPref("use_auth").equals("false"))
            return upload_user(text, binfile, proj);
        m_main.setStatus("please initialize!");
        return false;
    }

    public boolean upload_auth(String text, String binfile, String athlete)
    {
        File file = new File(binfile);
        //String athlete_id = m_main.getPref("auth_athlete");
        String athlete_id = athlete;
        String description = text;
        String muscleGroup = m_main.getPref("auth_muscle");

        String auth_token = m_main.getPref("auth_token");


        try {

            String url = "https://storage-analysis-api-dot-oro-muscles-webportal.ew.r.appspot.com/mobile/recordings";

            if (athlete != null && !athlete.equals("")) {
                url = "https://storage-analysis-api-dot-oro-muscles-webportal.ew.r.appspot.com/recordings";
            }
            System.out.println(url);
            // Upload File location
            HttpsURLConnection connUpload = (HttpsURLConnection) new URL(url).openConnection();
            connUpload.setUseCaches(false);
            connUpload.setDoOutput(true);
            connUpload.setRequestMethod("POST");
            connUpload.setRequestProperty("Connection", "Keep-Alive");
            connUpload.setRequestProperty("Cache-Control", "no-cache");
            connUpload.setRequestProperty("Authorization", "Bearer " + auth_token);
            connUpload.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=*****");

            // File Header
            OutputStream outputStream = connUpload.getOutputStream();
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

            if (athlete != null && !athlete.equals("")) {
                printWriter.append("--*****\r\n");
                printWriter.append("Content-Disposition: form-data; name=\"athlete_id\"\r\n");
                printWriter.append("\r\n").flush();
                printWriter.append(athlete_id);
                outputStream.flush();

                printWriter.append("\r\n").flush();
            }



            printWriter.append("--*****\r\n").flush();

            printWriter.append("Content-Disposition: form-data; name=\"description\"\r\n");
            printWriter.append("\r\n").flush();
            printWriter.append(description);
            outputStream.flush();

            printWriter.append("\r\n").flush();
            printWriter.append("--*****\r\n").flush();

            printWriter.append("Content-Disposition: form-data; name=\"muscleGroup\"\r\n");
            printWriter.append("\r\n").flush();
            printWriter.append(muscleGroup);
            outputStream.flush();

            printWriter.append("\r\n").flush();
            printWriter.append("--*****\r\n").flush();

            printWriter.append("Content-Disposition: form-data; name=\"file\"; filename=\"ctest.bin\"\r\n");
            printWriter.append("\r\n").flush();
            // Write binary data
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                // SEND ALONG THE SET INFO
                File sets = new File(file.toPath() + ".sets");
                if (sets.exists()) {
                    Files.copy(sets.toPath(), outputStream);
                }
                Files.copy(file.toPath(), outputStream);
            }
            outputStream.flush();

            // End of requedst
            printWriter.append("\r\n").flush();
            printWriter.append("--*****--\r\n").flush();


            int responseCode = connUpload.getResponseCode();
            String responseMessage = connUpload.getResponseMessage();
            System.out.println(responseCode);
            System.out.println(responseMessage);

            StringBuffer response = new StringBuffer();

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(connUpload.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Set token and logged in user
                System.out.println(response.toString());
                m_main.setStatus("uploaded!");
                m_main.setUploaded(true);
                return true;
            } else {
                m_main.setStatus("upload failed!");
                m_main.setUploaded(false);

                return false;
            }
        } catch(Exception e) {
            int x = 15;
            m_main.setStatus("upload exception!");
            return false;
        }

    }

    //====================================================================================
    public boolean upload_log(String text, String logfile, String athlete)
    {
        File file = new File(logfile);
        //String athlete_id = m_main.getPref("auth_athlete");
        String athlete_id = athlete;
        String description = text;
        String muscleGroup = m_main.getPref("auth_muscle");

        String auth_token = m_main.getPref("auth_token");


        try {

            String url = "https://storage-analysis-api-dot-oro-muscles-webportal.ew.r.appspot.com/mobile/recordings/log";

            //if (athlete != null && !athlete.equals("")) {
            url = "https://storage-analysis-api-dot-oro-muscles-webportal.ew.r.appspot.com/recordings/log";
            //}
            System.out.println(url);
            // Upload File location
            HttpsURLConnection connUpload = (HttpsURLConnection) new URL(url).openConnection();
            connUpload.setUseCaches(false);
            connUpload.setDoOutput(true);
            connUpload.setRequestMethod("POST");
            connUpload.setRequestProperty("Connection", "Keep-Alive");
            connUpload.setRequestProperty("Cache-Control", "no-cache");
            connUpload.setRequestProperty("Authorization", "Bearer " + auth_token);
            connUpload.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=*****");

            // File Header
            OutputStream outputStream = connUpload.getOutputStream();
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

           /* if (athlete != null && !athlete.equals("")) {
                printWriter.append("--*****\r\n");
                printWriter.append("Content-Disposition: form-data; name=\"athlete_id\"\r\n");
                printWriter.append("\r\n").flush();
                printWriter.append(athlete_id);
                outputStream.flush();

                printWriter.append("\r\n").flush();
            }*/



            //printWriter.append("--*****\r\n").flush();

            /*printWriter.append("Content-Disposition: form-data; name=\"description\"\r\n");
            printWriter.append("\r\n").flush();
            printWriter.append(description);
            outputStream.flush();

            printWriter.append("\r\n").flush();
            printWriter.append("--*****\r\n").flush();

            printWriter.append("Content-Disposition: form-data; name=\"muscleGroup\"\r\n");
            printWriter.append("\r\n").flush();
            printWriter.append(muscleGroup);
            outputStream.flush();

            printWriter.append("\r\n").flush();
            printWriter.append("--*****\r\n").flush();


            printWriter.append("Content-Disposition: form-data; name=\"file\"; filename=\"orolog.txt\"\r\n");
            printWriter.append("\r\n").flush();*/

            // Write binary data
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Files.copy(file.toPath(), outputStream);
            }
            outputStream.flush();

            // End of request
            printWriter.append("\r\n").flush();
            printWriter.append("--*****--\r\n").flush();


            int responseCode = connUpload.getResponseCode();
            String responseMessage = connUpload.getResponseMessage();
            System.out.println(responseCode);
            System.out.println(responseMessage);

            StringBuffer response = new StringBuffer();

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(connUpload.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Set token and logged in user
                System.out.println(response.toString());
                m_main.setStatus("log file uploaded!");
                m_main.setUploaded(true);
                return true;
            } else {
                m_main.setStatus("upload failed (log)!");
                m_main.setUploaded(false);

                return false;
            }
        } catch(Exception e) {
            int x = 15;
            m_main.setStatus("upload exception (log)!");
            return false;
        }

    }



    //====================================================================================


    // TODO: Fix proper handling of project!!
    public boolean upload_user(String text, String binfile, String project)
    {

            //uploading();
        System.out.println("Upload confirmed, sending file");
        String subjectId = text.replaceAll(" ", "_");
        //String muscleG = muscleDropdown.getSelectedItem().toString().equals("Other...") ?

        // GET FILE
        String filename = binfile + "-" + subjectId + "-" + System.currentTimeMillis() + ".bin";
        File currFile = new File(binfile);

        if (!currFile.exists()) {
            m_main.setStatus("Nothing to upload");
            return false;
        }

        m_main.setStatus("Uploading: " +  subjectId);

        File file = new File(filename);
        currFile.renameTo(file);


        //File file = new File("/data/data/com.digidactylus.recorder/files/jrec.txt.bin-Default_training-1674230412594.bin");

        String [] tok = filename.split("/");
        filename = tok[tok.length-1];
        // Get root project
        String session_token = m_main.getPref("user_token");
        String rootProject = m_main.getPref("user_project");

        //String session_token = "UT1PH4I1sZ1uJL1Ojup4BR6Z";
        //String rootProject = "12345";
        //String session_token = "X7fdNaYNRVOdPxs6SJsPUjUW";
        //String rootProject = "30011";
        //String session_token = "Yms2DkEBRJneZNPLBhhCKiLl";
        //String rootProject = "30012";
        //String session_token = "TCicF86bDbyGrHxMLQr1EoqD";
        //String rootProject = "30013";
        //String session_token = "XW0cFtQWB0122jOadRDp2Atk";
        //String rootProject = "30014";
        //String session_token = "v2QTibqv5W3rglkz3XBFZf8h";
        //String rootProject = "30015";



        String request = m_baseURL + "??upload_bin&user=" + session_token + "&project=" + rootProject + "&data="+ filename +"&desc=No_desc" + "&send_notification=yes" + "&metainfo=" + filename;
//        String request = m_baseURL + "??upload_bin&user=" + session_token + "&project=" + rootProject + "&data="+ "testing.bin" +"&desc=No_desc" + "&send_notification=yes" + "&metainfo=testing.bin";

        try {
            boolean yes = uploadFile(file, request);
            m_main.setStatus("Upload complete!");

            return yes;
        } catch(Exception e) {
            m_main.setStatus("Upload failed... network?");
            return false;
        }

        //return true;
    }

    static boolean uploadFile(File file, String serverUrl) {

        System.out.println("Uploading file...");

        if ( !file.exists()) {
            System.out.println("ERROR, file does not exist!");
            return false;
        }

        String charset = "UTF-8";
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\n";

        try {

            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        // Install the all-trusting trust manager
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);


        // Establishing connection with server
        URLConnection connection = new URL(serverUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
            // Opening output stream with server
            OutputStream outputStream = connection.getOutputStream();
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, charset), true) )
            {

                // Writing header data to server
                printWriter.append("--")
                        .append(boundary)
                        .append(CRLF);

                printWriter.append("Content-Disposition: form-data; name=\"fileIndexName\"; filename=\"")
                        .append(file.getName())
                        .append("\"")
                        .append(CRLF);

                printWriter.append("Content-Type: ")
                        .append(URLConnection.guessContentTypeFromName(file.getName()))
                        .append(CRLF);

                printWriter.append("Content-Transfer-Encoding: binary")
                        .append(CRLF);

                printWriter.append(CRLF).flush();

                // Writing binary data to server output stream
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Files.copy(file.toPath(), outputStream);
                }

                outputStream.flush();

                printWriter.append(CRLF).flush();

                printWriter.append("--")
                        .append(boundary)
                        .append("--")
                        .append(CRLF)
                        .flush();

                // Server http response code
                int responseCode = ((HttpURLConnection) connection).getResponseCode();

                // Buffering response body
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader((connection.getInputStream())));
                StringBuilder responseBody = new StringBuilder();
                String responseBodyLine;
                while ((responseBodyLine = bufferedReader.readLine()) != null) {
                    responseBody.append(responseBodyLine);
                }

                System.out.println("Server returned http status "
                        + responseCode
                        + " from url "
                        + serverUrl
                        + " with response body "
                        + responseBody.toString());

            }

        } catch (Exception ex) {
            // Http Status >= 500 got here
            System.out.println("ERROR uploading file");
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // Populate athlete list
    public int get_athlete_stack(String [] athlete_id, String [] athlete_name, String auth_token)
    {

        int n = 0;

        try {
            HttpsURLConnection connection =
                (HttpsURLConnection) new URL("https://oro-muscles-webportal.ew.r.appspot.com/api/current_user").openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + auth_token);
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            System.out.println(responseCode);
            System.out.println(responseMessage);


            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                    n = parseAthletes(athlete_id, athlete_name, inputLine);
                }
                in.close();
                // Set token and logged in user
                String res = response.toString();
                System.out.println(res);
            }
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
        return n;
    }

    private int parseAthletes(String [] athlete_id, String [] athlete_name, String line)
    {
        int j;

        String [] s1 = line.split("\"");

        int n = 0;

        for (j=0; j<s1.length-5; j++) {
            String tok = s1[j];
            if (s1[j].equals("_id") && s1[j+4].equals("name")) {
                String id = s1[j+2];
                String name = s1[j+6];
                athlete_id[n] = id;
                athlete_name[n] = name;
                n++;
            }
        }

        return n;

    }


}
