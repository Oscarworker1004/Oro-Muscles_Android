package com.digidactylus.recorder;

import static android.hardware.usb.UsbConstants.USB_DIR_OUT;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import static com.example.sensorscanner.utils.UUIDS.ACTION_DATA_AVAILABLE;
import static com.example.sensorscanner.utils.UUIDS.BATTERY_LEVEL_CHAR_UUID;
import static com.example.sensorscanner.utils.UUIDS.BATTERY_SERVICE_UUID;
import static com.example.sensorscanner.utils.UUIDS.SERVICE_UUID;
import static com.fondesa.kpermissions.extension.PermissionsBuilderKt.permissionsBuilder;

import android.Manifest;
//import android.app.AlertDialog;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.digidactylus.recorder.Fragments.DataFragment;
import com.digidactylus.recorder.Fragments.GraphsFragment;
import com.digidactylus.recorder.Fragments.LabelingFragment;
import com.digidactylus.recorder.Fragments.SetupFragment;
import com.digidactylus.recorder.adapters.menuOptionsPagerAdapter;
import com.digidactylus.recorder.models.LabelingModel;
import com.digidactylus.recorder.models.graphItemsList;
import com.digidactylus.recorder.ui.DashboardView;
import com.digidactylus.recorder.ui.DataBuffer;
import com.digidactylus.recorder.ui.DataThread;
import com.digidactylus.recorder.ui.FeedView;
import com.digidactylus.recorder.ui.FileUpload;
import com.digidactylus.recorder.ui.MLog;
import com.digidactylus.recorder.utils.BleDialog;
import com.digidactylus.recorder.utils.Constants;
import com.digidactylus.recorder.utils.CustomDialog;
import com.digidactylus.recorder.utils.TinyDB;
import com.digidactylus.recorder.utils.Tools;
import com.digidactylus.recorder.viewmodels.DataFragmentTitleViewModel;
import com.digidactylus.recorder.viewmodels.DataFragmentViewModel;
import com.digidactylus.recorder.viewmodels.SetupFragmentViewModel;
import com.digidactylus.recorder.viewmodels.TrainingViewModel;
import com.digidactylus.recorder.viewmodels.recordTimerViewModel;
import com.example.sensorscanner.BluetoothDetailsFragment;
import com.example.sensorscanner.ScanningFragment;
import com.example.sensorscanner.utils.UUIDS;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.digidactylus.recorder.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;



import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;


public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TextView screenTitle;
    ImageButton menuButton;
    menuOptionsPagerAdapter menuAdapter;

    RelativeLayout MainScreenContainer;
    LinearLayout optionsmenu;
    DataFragmentTitleViewModel dataFragmentTitleViewModel;
    DataFragmentViewModel dataFragmentViewModel;
    SetupFragmentViewModel setupFragmentViewModel;

    private ActivityMainBinding binding;
    private DataThread m_dt = null;

    List<Double> intensityList = new ArrayList<>();
    List<Double> capacityList = new ArrayList<>();
    public LabelingModel labelingModel = new LabelingModel();
    private boolean added = false;

    TinyDB tinyDB;
/*    private static List<graphItemsList> intensityList;
    private static List<graphItemsList> capacityList;*/


    public static MainActivity g_main = null;

    private String[] m_projID = null;
    private String[] m_projName = null;
    private int m_projCount = 0;

    private boolean m_refreshProj = false;

    private int m_lastIndex = 0;

    private String m_fileBase;

    private boolean m_sensErr = false;
    private boolean m_isInit = false;

    private String m_dashStore;

    public static boolean m_isRecording = false;


    // BLE stuff
    private String deviceName = "";
    private BluetoothGatt bluetoothGatt;
    public BluetoothAdapter mBluetoothAdapter;
    private String uuid, charUuid;

    private ScanningFragment scanningFragment;
    private BluetoothDetailsFragment bluetoothDetailsFragment;
    private BluetoothDevice bluetoothDevice;
    CustomDialog myDialog;

    private static final String TAG = "MainActivity";


    private OutputStream outputStream;

    public ScanResult m_scanRes = null;
    public boolean m_isConnectBLE = false;
    public String bleDeviceName = null;
    public String bleDeviceUuid = null;

    public TrainingViewModel trainingViewModel;

    public MainActivity() {
        m_dt = new DataThread();
        m_dt.setMain(this);
        g_main = this;

        m_projID = new String[256];
        m_projName = new String[256];
        m_projID[0] = "0";
        m_projName[0] = "Default Athlete";
        m_projCount = 1;


    }

    public static MainActivity getMain() {
        return g_main;
    }


    static void loadLib() {
        System.loadLibrary("recorder");
    }

    public void setScanResult(ScanResult s) {
        m_scanRes = s;
    }

    HashMap<String, android.bluetooth.le.ScanResult> scanResultHashMap = new HashMap<>();

    public ScanResult getM_scanRes() {
        return m_scanRes;
    }

    // UI switches
    public void setSwitchDashboard() {
        if (m_isInit) {
            enableHome(false);
            enableNotifications(false);
            enableDashboard(true);
        }
    }

    public void setSwitchHome() {
        if (m_isInit) {
            enableHome(true);
            enableDashboard(false);
            enableNotifications(false);

            // Write dashboard data
            DashboardView dash = (DashboardView) findViewById(R.id.imageView2);
            dash.write(m_dashStore);
        }
    }

    public void setSwitchNotifications() {
        if (m_isInit) {
            enableHome(false);
            enableDashboard(false);
            enableNotifications(true);
        }
    }

    private void enableNotifications(boolean b) {
        int vis = VISIBLE;
        if (!b)
            vis = INVISIBLE;
/*        EditText text = (EditText)findViewById(R.id.notificationTextTextMultiLine);
        text.setVisibility(vis);
        text.setEnabled(false);
        String t = "Oro Recorder version " + getResources().getString(R.string.version);
        t += "\nReleased " + getResources().getString(R.string.release) + "\n";
        t += "\n(c) 2023 Oro Muscles\n";
        t += "For additional information, see oromuscles.com\n";
        t += "\n\nNo pending notifications\n";
        text.setText(t);

        findViewById(R.id.button3).setEnabled(b);
        findViewById(R.id.button3).setVisibility(vis);
        //findViewById(R.id.textView2).setEnabled(b);
        //findViewById(R.id.textView2).setVisibility(vis);
        findViewById(R.id.spinner).setEnabled(b);
        findViewById(R.id.spinner).setVisibility(vis);
        findViewById(R.id.textView).setEnabled(b);
        findViewById(R.id.textView).setVisibility(vis);
        findViewById(R.id.textView4).setEnabled(b);
        findViewById(R.id.textView4).setVisibility(vis);
        findViewById(R.id.editTextTextPersonName).setEnabled(b);
        findViewById(R.id.editTextTextPersonName).setVisibility(vis);*/

/*        findViewById(R.id.seekBar2).setEnabled(b);
        findViewById(R.id.seekBar2).setVisibility(vis);
        findViewById(R.id.slider1).setEnabled(false);
        findViewById(R.id.slider1).setVisibility(vis);*/

/*        findViewById(R.id.seekBar3).setEnabled(b);
        findViewById(R.id.seekBar3).setVisibility(vis);*/
/*
        findViewById(R.id.slider2).setEnabled(false);
        findViewById(R.id.slider2).setVisibility(vis);
*/


        // Write dashboard data
        DashboardView dash = (DashboardView) findViewById(R.id.imageView2);
        dash.write(m_dashStore);

        saveApplySensSettings();
    }

    private void enableDashboard(boolean b) {
        DashboardView dash = (DashboardView) findViewById(R.id.imageView2);

        int vis = VISIBLE;
        if (!b)
            vis = INVISIBLE;
        dash.setVisibility(vis);
    }

    private void enableHome(boolean b) {
/*        Button upload_button = (Button) findViewById(R.id.button3);
        Spinner muscle_spin = (Spinner)findViewById(R.id.spinner);
        EditText txt = (EditText)findViewById(R.id.editTextTextPersonName);*/
        ImageButton stop_button = (ImageButton) findViewById(R.id.imageButton3);
        ImageButton the_button = (ImageButton) findViewById(R.id.imageButton2);
        //TextView tv1 = (TextView) findViewById(R.id.textView);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        //TextView tv4 = (TextView) findViewById(R.id.textView4);
        TextView tv3 = (TextView) findViewById(R.id.textView3);
        FeedView feed = (FeedView) findViewById(R.id.imageView);

        stop_button.setEnabled(b);
        the_button.setEnabled(b);
        //upload_button.setEnabled(b);
        int vis = VISIBLE;
        if (!b)
            vis = INVISIBLE;
        //tv1.setVisibility(vis);
        tv2.setVisibility(vis);
        tv3.setVisibility(vis);
        //tv4.setVisibility(vis);
        //upload_button.setVisibility(vis);
        //muscle_spin.setVisibility(vis);
        //txt.setVisibility(vis);
        stop_button.setVisibility(vis);
        the_button.setVisibility(vis);
        feed.setVisibility(vis);


    }

    //===================================== SETTINGS ===========================
    public void setPref(String key, String value) {
        SharedPreferences prefs = getSharedPreferences("oropref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPref(String key) {
        try {
            SharedPreferences pref = getSharedPreferences("oropref", Context.MODE_PRIVATE);
            String read_data = pref.getString(key, "");
            return read_data;
        } catch (Exception e) {
            //Log.e("Not data shared", e.toString());
            return "";
        }
    }

    private int newRecID() {
        m_lastIndex++;
        String rec_id = new String();
        rec_id += m_lastIndex;
        setPref("rec_id", rec_id);

        updateToUpload();

        return m_lastIndex;
    }

    private void initPref() {
        if (getPref("use_auth").equals("")) {
            // auth0 default stuff
            setPref("auth_athlete", "63e53d0530a2980877402f6a");
            setPref("auth_muscle", "Fake Muscle");
            setPref("use_auth", "true");

            // classic stack
            setPref("user_token", "UT1PH4I1sZ1uJL1Ojup4BR6Z");
            setPref("user_project", "12345");

            setStatus("first launch");
        }

        if (getPref("rec_id").equals("")) {
            String rec_id = new String();
            rec_id += m_lastIndex;
            setPref("rec_id", rec_id);
            if (getPref("rec_id_upload").equals("")) {
                setPref("rec_id_upload", rec_id);
            }

        } else {
            m_lastIndex = Integer.parseInt(getPref("rec_id"));
        }
    }

    private void readProjFromPref() {
        String nc = getPref("proj_count");
        if (!nc.equals("")) {
            int n = Integer.parseInt(nc);
            m_projCount = n;
            for (int i = 0; i < n; i++) {
                String keyID = "proj_id";
                String keyName = "proj_name";
                keyID += i;
                keyName += i;
                m_projID[i] = getPref(keyID);
                m_projName[i] = getPref(keyName);
            }

        } else {
            m_projCount = 0;
        }
        loadSensSettings();
    }

    private void writeProjToPref() {
        String count = new String();
        count += m_projCount;
        setPref("proj_count", count);

        for (int i = 0; i < m_projCount; i++) {
            String keyID = "proj_id";
            String keyName = "proj_name";
            keyID += i;
            keyName += i;
            setPref(keyID, m_projID[i]);
            setPref(keyName, m_projName[i]);
        }
        saveApplySensSettings();
    }

    private void processDirective(String txt) {
        String[] ll = txt.split(" ");
        if (ll.length > 1 && ll[0].equals("exercises")) {
            String cont = new String();
            for (int j = 1; j < ll.length; j++) {
                if (j > 1)
                    cont += " ";
                cont += ll[j];
            }
            setPref(ll[0], cont);
            if (DashboardView.getDashboard() != null)
                DashboardView.getDashboard().updateExercises();
            return;
        }
        if (ll.length % 2 != 0) {
            setStatus("didn't understand");
            return;
        }

        String stack = new String();
        for (int i = 0; i < ll.length; i += 2) {

            setPref(ll[i], ll[i + 1]);
            if (ll[i].equals("auth_token")) {
                setPref("use_auth", "true");
                stack = "stack-auth";
                //======================================================
                // Query the athlete ids and names here, and update the
                // preferences via setPref() by name0, name1, etc. and num_athletes/projects
                // HERE!!!
                //======================================================
                int x = 17;
                new Thread(new Runnable() {
                    public void run() {
                        FileUpload fu = new FileUpload();
                        String token = getPref("auth_token");
                        m_projCount = fu.get_athlete_stack(m_projID, m_projName, token);

                        writeProjToPref();
                        //g_main.updateProjList();
                    }
                }).start();
            }
            if (ll[i].equals("user_token")) {
                setPref("use_auth", "false");
                stack = "stack-custom";
            }
        }


        setStatus("updated, " + stack);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        //onRequestPermissionsResultBLE(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("GRANTED");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    System.out.println("DENIED");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                        }
                    } else {
                        System.out.println("permission denied for device " + device);
                    }
                }
            }
        }
    };

    private UsbDevice m_device = null;

    private byte[] getLineEncoding(int baudRate) {
        final byte[] lineEncodingRequest = {(byte) 0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x08};
        //Get the least significant byte of baudRate,
        //and put it in first byte of the array being sent
        lineEncodingRequest[0] = (byte) (baudRate & 0xFF);

        //Get the 2nd byte of baudRate,
        //and put it in second byte of the array being sent
        lineEncodingRequest[1] = (byte) ((baudRate >> 8) & 0xFF);

        //ibid, for 3rd byte (my guess, because you need at least 3 bytes
        //to encode your 115200+ settings)
        lineEncodingRequest[2] = (byte) ((baudRate >> 16) & 0xFF);

        return lineEncodingRequest;

    }

    public boolean labelingShowing = false;

    private void loadSensSettings() {
        //TODO: Modify these to seekbar viewModel

       /* SeekBar s1 = findViewById(R.id.seekBar2);
        SeekBar s2 = findViewById(R.id.seekBar3);

        int min1 = 1;
        int min2 = 1;
        int max1 = 500;
        int max2 = 200;
        int val1 = 200;
        int val2 = 50;

        if (!getPref("sensitivity1_val").equals("")) {
            min1 = Integer.parseInt(getPref("sensitivity1_min"));
            max1 = Integer.parseInt(getPref("sensitivity1_max"));
            val1 = Integer.parseInt(getPref("sensitivity1_val"));
            min2 = Integer.parseInt(getPref("sensitivity2_min"));
            max2 = Integer.parseInt(getPref("sensitivity2_max"));
            val2 = Integer.parseInt(getPref("sensitivity2_val"));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            s1.setMin(min1);
            s2.setMin(min2);
        }
        s1.setMax(max1);
        s1.setProgress(val1);

        s2.setMax(max2);
        s2.setProgress(val2);

        if (FeedView.getFeed() != null) {
            FeedView.getFeed().getRMSAnalayze().setFloorOff(s1.getProgress());
            FeedView.getFeed().getRMSAnalayze().setPeakMinDist(s2.getProgress());
            MLog.log("loadUpdate ANALYSIS: set Floor Offset: " + s1.getProgress());
            MLog.log("loadUpdate ANALYSIS: set Peak Min: " + s2.getProgress());
        }*/

    }

    private void saveApplySensSettings() {
        //TODO: Modify these to seekbar viewModel

        /*SeekBar s1 = findViewById(R.id.seekBar2);
        SeekBar s2 = findViewById(R.id.seekBar3);

        String s = new String();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            s += s1.getMin();
            setPref("sensitivity1_min", s);
            s = "";
            s += s2.getMin();
            setPref("sensitivity2_min", s);
        } else {
            setPref("sensitivity1_min", "1");
            setPref("sensitivity1_min", "1");
        }
        s = "";
        s += s1.getMax();
        setPref("sensitivity1_max", s);
        s = "";
        s += s2.getMax();
        setPref("sensitivity2_max", s);

        s = "";
        s += s1.getProgress();
        setPref("sensitivity1_val", s);
        s = "";
        s += s2.getProgress();
        setPref("sensitivity2_val", s);

        if (FeedView.getFeed() != null) {
            FeedView.getFeed().getRMSAnalayze().setFloorOff(s1.getProgress());
            FeedView.getFeed().getRMSAnalayze().setPeakMinDist(s2.getProgress());
            MLog.log("saveUpdate ANALYSIS: set Floor Offset: " + s1.getProgress());
            MLog.log("saveUpdate ANALYSIS: set Peak Min: " + s2.getProgress());
        }*/
    }

    View overlay;
    boolean selectTraining;

    com.digidactylus.recorder.viewmodels.recordTimerViewModel recordTimerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FeedView.getFeed().setMain(this);
        tinyDB = new TinyDB(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myDialog = new CustomDialog(this);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        recordTimerViewModel = new ViewModelProvider(this).get(com.digidactylus.recorder.viewmodels.recordTimerViewModel.class);
        menuAdapter = new menuOptionsPagerAdapter(this);
        dataFragmentViewModel = new ViewModelProvider(this).get(DataFragmentViewModel.class);
        setupFragmentViewModel = new ViewModelProvider(this).get(SetupFragmentViewModel.class);

        trainingViewModel = new ViewModelProvider(this).get(TrainingViewModel.class);


        viewPager2 = findViewById(R.id.viewpage);
        menuButton = findViewById(R.id.menuButton);
        optionsmenu = findViewById(R.id.optionsmenu);
        screenTitle = findViewById(R.id.screenTitle);
        overlay = findViewById(R.id.screenOverlay);
        MainScreenContainer = findViewById(R.id.MainScreenContainer);
        MainScreenContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("fkjdsfds", "flkdmsfds");
            }
        });

        dataFragmentTitleViewModel = new ViewModelProvider(MainActivity.this).get(DataFragmentTitleViewModel.class);

        dataFragmentTitleViewModel.getLiveCurrentTabTitle().observe(MainActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                screenTitle.setText(s);
            }
        });

        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setUserInputEnabled(false);
        viewPager2.setAdapter(menuAdapter);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay.setVisibility(VISIBLE);
                int visibility = optionsmenu.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE;
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View dynamicLayout = inflater.inflate(R.layout.menu_layout, optionsmenu, false);
                dynamicLayout.findViewById(R.id.setupmenubtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        screenTitle.setText("Set-up - Connection");
                        setupFragmentViewModel.setCurrentTab(0);
                        viewPager2.setCurrentItem(0, false);
                        optionsmenu.setVisibility(View.INVISIBLE);
                        overlay.setVisibility(INVISIBLE);

                    }
                });

                dynamicLayout.findViewById(R.id.datamenubtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        screenTitle.setText("Data - Analytics");
                        dataFragmentViewModel.setCurrentTab(0);
                        viewPager2.setCurrentItem(1, false);
                        optionsmenu.setVisibility(View.INVISIBLE);
                        overlay.setVisibility(INVISIBLE);

                    }
                });

                dynamicLayout.findViewById(R.id.labelingmenubtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        screenTitle.setText("Labeling");
                        viewPager2.setCurrentItem(2, false);
                        optionsmenu.setVisibility(View.INVISIBLE);
                        overlay.setVisibility(INVISIBLE);
                    }
                });

                optionsmenu.addView(dynamicLayout);
                optionsmenu.setVisibility(visibility);
                overlay.setVisibility(visibility);

            }
        });
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (optionsmenu.getVisibility() == VISIBLE) {
                    optionsmenu.setVisibility(INVISIBLE);
                    overlay.setVisibility(View.GONE);
                }
            }
        });
        viewPager2.setUserInputEnabled(false);
        int position = getIntent().getIntExtra("currentPage", 1);
        selectTraining = getIntent().getBooleanExtra("selectTraining", false);
        viewPager2.setCurrentItem(position, false);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Fragment fragment = menuAdapter.getInstantiatedFragment(position);
                if (fragment instanceof SetupFragment) {
                    SetupFragment setupFragment = (SetupFragment) fragment;
                    setupFragment.doWork();
                    if (selectTraining) {
                        setupFragment.seletectTraining();
                        selectTraining = false;
                    }
                    labelingShowing = false;
                } else if (fragment instanceof DataFragment) {
                    DataFragment dataFragment = (DataFragment) fragment;
                    dataFragment.doWork();
                    labelingShowing = false;
                }
                else if (fragment instanceof LabelingFragment) {
                    LabelingFragment labelingFragment = (LabelingFragment) fragment;
                    labelingShowing = true;
                }
            }
        });




        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //UsbDevice device = usbManager.getDeviceList().get();
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        //UsbDevice device = null;
        while (deviceIterator.hasNext()) {
            m_device = deviceIterator.next();
            usbManager.requestPermission(m_device, permissionIntent);
            //your code
            String devName = m_device.getDeviceName();
        }


        // BLE -----------------------------------------------------------------
        BluetoothManager bluetoothManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        scanningFragment = ScanningFragment.newInstance(this);
        // MGG: needed?
        //transaction.add(R.id.fragment, scanningFragment);
        //transaction.commit();


        // ---------------------------------------------------------------------

        //**********************************************************************
        //**********************************************************************
        //**********************************************************************
        File dir = getFilesDir();
        String path = dir.getAbsolutePath();

        MLog.init(path + "/ororec.log");

        MLog.log("RECORDER START");
        m_fileBase = path + "/recording.";
        //**********************************************************************
        //**********************************************************************
        //**********************************************************************

        try {
            FileWriter fw = new FileWriter(path + "/testing.txt");
            //FileWriter fw = new FileWriter("storage/emulated/0/Android/data/com.digidactylus.recorder/files/testing.txt");
            fw.write("This is a Java test?\n");
            fw.close();

            String devName = m_device.getDeviceName();
            File ff = new File(devName);

            Scanner sc;
            sc = new Scanner(ff);
            String str = sc.nextLine();


            fw = new FileWriter(path + "/jtest2.txt");
            fw.write("This is a Java test 222?\n");
            fw.close();

        } catch (Exception e) {
            System.out.println("WARNING: Could not open local cache file!");
        }


        System.out.println("Load lib");
        //MainActivity.loadLib();
        JNIWrapRec rec = new JNIWrapRec();
        System.out.println("Loaded!");
        //rec.doStartVideo("", "", path + "/ultimate.txt", "");
        //rec.doStopVideo("");

        //rec.doStartRec("/data/data/com.digidactylus.recorder/files/arec.bin");

        System.out.println("Called!");


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // NavigationUI.setupWithNavController(binding.navView, navController);

        //===================================================================

        initPref();
        readProjFromPref();
        // Init dashboard
        DashboardView dash = (DashboardView) findViewById(R.id.imageView2);
        m_dashStore = path + "/orodata1.bin";
        dash.read(m_dashStore);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type) || "oromuscles/qrtext".equals(type)) {
                //handleSendText(intent); // Handle text being sent
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                processDirective(sharedText);
                m_refreshProj = true;
                //setStatus(sharedText);

            }
        }


        //====================================================================

        //TODO: Upload logic is here

        /*Button upload_button = (Button) findViewById(R.id.button3);
        upload_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText txt = (EditText)findViewById(R.id.editTextTextPersonName);
                String stuff = txt.getText().toString();
                System.out.println("UPLOAD Button CLICKED: " + stuff);

                Spinner muscle_spin = (Spinner)findViewById(R.id.spinner);
                int idx = muscle_spin.getSelectedItemPosition();
                String projTmp = null;
                if (idx >= 0 && m_projCount > 0)
                    projTmp = m_projID[idx];

                final String proj = projTmp;

                new Thread(new Runnable() {
                    public void run() {
                        boolean b = false;

                        //==================================================================
                        Intent sendIntent2 = new Intent();
                        String rc2 = Intent.ACTION_SEND;
                        sendIntent2.setAction(rc2);
                        //sendIntent.putExtra(Intent.EXTRA_TEXT, "auth_token " + accessToken);
                        sendIntent2.setType("oromuscles/logintext");

                        startActivity(sendIntent2);
                        //==================================================================

                        boolean failed = false;

                        int from = Integer.parseInt(getPref("rec_id_upload"));
                        from++;
                        int to = m_lastIndex;
                        for (int i=from; i<=m_lastIndex; i++) {

                            String fileName = m_fileBase + i + ".bin";
                            File f = new File(fileName);
                            if (!f.exists()) {
                                setStatus("Skip " + fileName);
                                continue;
                            }

                            DashboardView.getDashboard().writeSetsBin(fileName);


                            setStatus("Upload " + fileName);

                            MLog.log("Try to upload " + fileName);
                            try {
                                FileUpload fu = new FileUpload();
                                fu.setMain(g_main);
                                b = fu.upload(stuff, fileName, proj);

                                if (b) {
                                    String up = new String();
                                    up += i;
                                    setPref("rec_id_upload", up);
                                    updateToUpload();
                                    MLog.log("Upload successful");
                                } else {

                                    MLog.log("Upload failed!");

                                    failed = true;
                                    break;
                                }
                            } catch (Exception ex) {
                                MLog.log("EXCEPTION during upload: " + ex.getMessage());
                                break;
                            }
                        }
                        // Log file
                        try {
                            File dir = getFilesDir();
                            String path = dir.getAbsolutePath();
                            String fileName = path + "/ororec.log";

                            FileUpload fu = new FileUpload();
                            fu.setMain(g_main);
                            b = fu.upload_log(stuff, fileName, proj);
                        } catch (Exception ex) {
                            MLog.log("EXCEPTION during LOG upload: " + ex.getMessage());
                        }
                        // End log file

                        if (failed) {
                            // Start login?
                            Intent sendIntent = new Intent();
                            String rc = Intent.ACTION_SEND;
                            sendIntent.setAction(rc);
                            //sendIntent.putExtra(Intent.EXTRA_TEXT, "auth_token " + accessToken);
                            sendIntent.setType("oromuscles/logintext");

                            setStatus("Starting login");
                            MLog.log("Starting login window");
                            startActivity(sendIntent);
                        }

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        if (b)
                            builder1.setMessage("Uploaded: " + stuff);
                        else
                            builder1.setMessage("ERROR - NOT Uploaded: " + stuff);
                        builder1.setCancelable(true);

                        builder1.setPositiveButton("Sure!",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


                        //builder1.setNegativeButton(
                        //        "No",
                        //        new DialogInterface.OnClickListener() {
                        //            public void onClick(DialogInterface dialog, int id) {
                        //                dialog.cancel();
                        //            }
                        //        });

                        //AlertDialog alert11 = builder1.create();
                        //alert11.show();


                    }
                }).start();


            }
        });*/


/*        Button conn_button = (Button) findViewById(R.id.connectButton);
        conn_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setStatus("Connecting...");
                connect(m_scanRes);
                conn_button.setEnabled(false);
                conn_button.setVisibility(INVISIBLE);
            }
        });*/


        ImageButton stop_button = (ImageButton) findViewById(R.id.imageButton3);
        stop_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("STOP Button CLICKED!");
                m_isRecording = false;
                Tools.playMediaPlayer(g_main, R.raw.click);



                m_sensErr = false;
                //upload_button.setEnabled(true);

                if (m_scanRes != null) { //BLE

                    if (false) { // Disable
                        //m_isConnectBLE = true; // Move this to the right place!
                        setStatus("Connecting...");
                        connect(m_scanRes);
                    } else {
                        stopDataStream();
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {

                        }
                        setStatus("Stopped BLE"); // Useless, it still gets data...
                        //================================================
                        m_dt.stopBLE();
                        //================================================
                    }
                } else { // USB
                    m_dt.Stop();
                }

            }
        });

        ImageButton the_button = (ImageButton) findViewById(R.id.imageButton2);
        the_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (m_isRecording)
                    return;

                m_sensErr = false;
                // upload_button.setEnabled(false);
                System.out.println("Button CLICKED!");

                // Use BLE if available
                if (m_isConnectBLE) {
                    String fileName = m_fileBase + newRecID() + ".bin";
                    m_isRecording = true;

                    try {
                        if (outputStream != null)
                            outputStream.close();
                        outputStream = new FileOutputStream(fileName);
                        byte[] sig = new byte[4];
                        sig[0] = 127;
                        sig[1] = 127;
                        sig[2] = 127;
                        sig[3] = 127;
                        outputStream.write(sig);
                    } catch (Exception e) {

                    }
                    //================================================
                    m_dt.startBLE();
                    //================================================

                    getDataStream();

                    // Tell the dashboard what to do
                    DashboardView.getDashboard().setSessionFile(fileName);
                    return;
                }


                try {
                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

                    UsbDeviceConnection con = usbManager.openDevice(m_device);

                    con.controlTransfer(0x40, 0, 0, 0, null, 0, 0);// reset
                    con.controlTransfer(0x40, 0, 1, 0, null, 0, 0);// clear Rx
                    con.controlTransfer(0x40, 0, 2, 0, null, 0, 0);// clear Tx
                    con.controlTransfer(0x40, 0x03, 0x000D, 0, null, 0, 0);//Baud 0x000D = 230400


                    UsbInterface ifc = m_device.getInterface(0);

                    UsbEndpoint ep = ifc.getEndpoint(0);


                    new Thread(new Runnable() {
                        public void run() {
                            try {

                                m_isRecording = true;
                                String fileName = m_fileBase + newRecID();

                                // Tell the dashboard what to do
                                DashboardView.getDashboard().setSessionFile(fileName);

                                m_dt.DoRecord(ep, con, fileName);

                                con.close();


                            } catch (Exception ex) {
                                con.close();
                            }
                        }
                    }).start();

                    //Sound


                    Tools.playMediaPlayer(g_main, R.raw.harp);

                } catch (Exception e) {
                    m_isRecording = false;
                    setStatus("No sensor found!");
                    System.out.println("WARNING: Blahhhh!");
                }

            }
        });

        // Selection of project
        //Spinner proj_spin = (Spinner)findViewById(R.id.spinner);
/*        proj_spin.setOnItemClickListener(new Spinner.OnItemClickListener() {
            public void onItemClick(AdapterView<?> v,View vv, int a ,long b) {
                updateProjList();
                int x = 15;
            }
        });*/

/*        proj_spin.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //doWhatYouWantHere();
                    int x = 29;
                    updateProjList();
                }
                return false;
            }
        });*/


        /*
        proj_spin.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    //doWhatYouWantHere();
                    int x = 29;
                    return true;
                } else {
                    int x = 29;
                    return false;
                }
            }
        });
*/

        //prepProjList();

        if (m_projCount > 0)
            updateProjList();
        else
            prepProjList();

        updateToUpload();

        m_isInit = true;

        //File dirr = getFilesDir();
        //String path = dir.getAbsolutePath();

        String fileName = path + "/cometaSensor.bin";

        try {
            outputStream = new FileOutputStream(fileName);
            byte[] sig = new byte[4];
            sig[0] = 127;
            sig[1] = 127;
            sig[2] = 127;
            sig[3] = 127;
            outputStream.write(sig);

        } catch (Exception e) {

        }

        doStartScan();
    }

    public void changeViewPage(int position) {
        if(viewPager2 != null) {
            viewPager2.setCurrentItem(position);
        }
    }

    public void prepProjList() {
/*        Spinner proj_spin = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> options = new ArrayList<String>();

        options.add("<select athlete>");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, options);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item,options);
        proj_spin.setAdapter(adapter); // this will set list of values to spinner


        proj_spin.setSelection(options.indexOf(""));*/

    }

    public void updateProjList() {

/*        Spinner proj_spin = (Spinner)findViewById(R.id.spinner);
        ArrayList<String> options=new ArrayList<String>();
        for (int i=0; i<m_projCount; i++) {
            options.add(m_projName[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item,options);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item,options);
        proj_spin.setAdapter(adapter); // this will set list of values to spinner

        if (m_projCount > 0)
            proj_spin.setSelection(options.indexOf(m_projName[0]));

        //==============================================
        FeedView.getFeed().run();
        //==============================================*/

    }

    public void setMove() {
        Tools.playMediaPlayer(this, R.raw.move);
    }

    public void setStart() {
        Tools.playMediaPlayer(this, R.raw.start);
    }

    public void setStop() {
        Tools.playMediaPlayer(this, R.raw.silence);
    }

    public void setUploaded(boolean b) {
        int file;
        if (b) {
            file = R.raw.notification;
        } else {
            file = R.raw.error2;
        }

        Tools.playMediaPlayer(this, file);
    }

    //===================================================================
    public void setStatus(String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView) findViewById(R.id.textView2);
                String d = "Status: " + status;
                tv.setText(d);
            }
        });


    }


    public void updateToUpload() {
        int from = Integer.parseInt(getPref("rec_id_upload"));
        int to = m_lastIndex;

        //TextView tv = (TextView) findViewById(R.id.textView4);
        String d = new String();
        d += to - from;
        //tv.setText(d);
    }


    public void setRecStatus(int status) {
        if (status == 1) {
            if (m_sensErr) {
                TextView tv = (TextView) findViewById(R.id.textView2);
                String d = "Status: possible errors";
                tv.setText(d);
            }
            m_sensErr = false;
            return;
        }

        TextView tv = (TextView) findViewById(R.id.textView2);
        String d = "*** SENSOR ERROR ***";
        tv.setText(d);

        if (m_sensErr)
            return;
        m_sensErr = true;
        //*************************************************************
        new Thread(new Runnable() {
            public void run() {
                while (g_main.isSensError()) {
                    final MediaPlayer mp = MediaPlayer.create(g_main, R.raw.dead);
                    mp.start();
                    while (mp.isPlaying()) {
                        try {
                            Thread.sleep(50);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }).start();


    }

    public boolean isSensError() {
        return m_sensErr;
    }

    public void setTime(String status) {
/*        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataFragment.getdataFrag().setTimingText(status);
            }
        });*/
        labelingModel.setDuration(status);
        /*TextView tv = (TextView) findViewById(R.id.textView3);
        String d = status;
        tv.setText(d);*/
    }

    public void setsCircleText(String text) {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataFragment.getdataFrag().setSetsText(text);
            }
        });
    }

    public void doNotify(boolean b, String stuff) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        if (b)
            builder1.setMessage("Uploaded: " + stuff);
        else
            builder1.setMessage("ERROR - NOT Uploaded: " + stuff);
        builder1.setCancelable(true);

        builder1.setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }

        });

        //builder1.setNegativeButton(
        //        "No",
        //        new DialogInterface.OnClickListener() {
        //            public void onClick(DialogInterface dialog, int id) {
        //                dialog.cancel();
        //            }
        //        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    // BLE ===================================================================
    // BLE ===================================================================
    // BLE ===================================================================

    /*
    @Override
    protected void onResume() {
        super.onResume();
        if (!checkScanPermission()) {
            requestPermission();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                startScan();
            }
        }
    }*/

    private void doStartScan() {
        if (!checkScanPermission()) {
            requestPermission();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                startScan();
            }
        }

    }

    private boolean checkScanPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT}, 100);
    }

    public HashMap<String, android.bluetooth.le.ScanResult> getScanResultHashMap() {
        return scanResultHashMap;
    }


    private BluetoothLeScanner bluetoothLeScanner;
    private android.bluetooth.le.ScanCallback scanCallback;

    @SuppressLint("MissingPermission")
    private void startScan() {
        bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();

        scanCallback = new android.bluetooth.le.ScanCallback() {
            @Override
            public void onScanResult(int callbackType, @NonNull android.bluetooth.le.ScanResult result) {
                String bleAddress = result.getDevice().getAddress();
                String bleName = result.getDevice().getName();
                if (bleName != null && !scanResultHashMap.containsKey(bleName)) {
                    scanResultHashMap.put(bleName, result);
                }
            }

        };

        bluetoothLeScanner.startScan(scanCallback);



      /*  ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, @NonNull ScanResult result) {
                super.onScanResult(callbackType, result);
               *//* BluetoothDevice device = result.getDevice();
                String deviceAddress = device.getAddress();
                Log.e(TAG, "onScanResult: " + device.getName() + " - " + deviceAddress );*//*
                String bleAddress = result.getDevice().getAddress();
                Log.e(TAG, "onScanResult: " + result.getDevice().getName() + " - " + bleAddress );
                if (bleAddress != null && !scanResultHashMap.containsKey(bleAddress)) {
                    scanResultHashMap.put(bleAddress, result);
                }
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onBatchScanResults(@NonNull List<ScanResult> results) {
                super.onBatchScanResults(results);

              *//*  Log.e(TAG, "onBatchScanResults: " + results.size());
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).getScanRecord() != null) {
                        //String deviceName = results.get(i).getScanRecord().getDeviceName();

                        String bleAddress = results.get(i).getDevice().getAddress();

                        if (bleAddress != null && !scanResultHashMap.containsKey(bleAddress)) {
                            scanResultHashMap.put(bleAddress, results.get(i));
                        }

                    }
                }

                //scanningFragment.updateList(list);

                // MGG: Put temporary workaround back in!!
                if (m_scanRes != null)
                    connect(m_scanRes);*//*
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG, "onScanFailed: " + errorCode );
                super.onScanFailed(errorCode);
            }
        };
        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .setReportDelay(5000)
                .setUseHardwareBatchingIfSupported(true)
                .build();

        List<ScanFilter> filters = new ArrayList<>();

        ParcelUuid parcelUuid = new ParcelUuid(UUID.fromString(SERVICE_UUID));

        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(parcelUuid)
                .build();

        //  filters.add(scanFilter)
        //scanner.startScan(filters, settings, scanCallback);
        scanner.startScan(scanCallback);*/
    }

    public void connect(ScanResult scanResult) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            deviceName = scanResult.getScanRecord().getDeviceName();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothDevice = scanResult.getDevice();
        }
        connectToDeviceSelected(bluetoothDevice);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bluetoothLeScanner != null && scanCallback != null)
            bluetoothLeScanner.stopScan(scanCallback);
    }

    public void disconnect() {
        if(getM_scanRes() == null) {
            return;
        }
        BluetoothDevice bluetoothDevice = m_scanRes.getDevice();
        unpairDevice(bluetoothDevice);
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            // Remove the bond
            device.getClass().getMethod("removeBond").invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void onRequestPermissionsResultBLE(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allGranted = false;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                allGranted = true;
            } else {
                Toast.makeText(this, "Grant all permissions in settings.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if (allGranted) {
            if (mBluetoothAdapter.isEnabled())
                startScan();
            else
                scanningFragment.onResume();
        }

    }


    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            //bluetoothDetailsFragment.setDataStream(characteristic.getValue());
            try {
                // ========= DATA COMING IN HERE =============================
                // ========= DATA COMING IN HERE =============================
                // ========= DATA COMING IN HERE =============================
                outputStream.write(characteristic.getValue());
                m_dt.processBLE(characteristic.getValue());

                setStatus("Reading cometa data...");
            } catch (Exception e) {

            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            switch (newState) {
                case 0:
                    //m_isConnectBLE = false;
                    MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "device disconnected", Toast.LENGTH_SHORT).show());
                    break;
                case 2:
                    m_isConnectBLE = true;
                    MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show());
                    bluetoothGatt.discoverServices();
                    setStatus("BLE connected");
                    break;
                default:
                    MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "we encountered an unknown state", Toast.LENGTH_SHORT).show());
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            displayGattServices(gatt, bluetoothGatt.getServices());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            }
        }

        /*
        @Override
        public void onDescriptorRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor, int status, @NonNull byte[] value) {
            super.onDescriptorRead(gatt, descriptor, status, value);
        }*/

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }
    };

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        int batteryNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        double batteryLevel = ((batteryNumber - 182.0) / 27) * 100;

        Bundle bundle = new Bundle();
        bundle.putString(UUIDS.DEVICE_NAME, deviceName);
        bundle.putString(UUIDS._SERVICE_UUID, uuid);
        bundle.putString(UUIDS.CHARACTERISTIC_UUID, charUuid);
        bundle.putString(UUIDS.BATTERY_LEVEL, String.format("%.2f", batteryLevel));

        bluetoothDetailsFragment = BluetoothDetailsFragment.newInstance(this, bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        //transaction.replace(R.id.fragment, bluetoothDetailsFragment);
        //transaction.commit();

    }

    public void connectToDeviceSelected(BluetoothDevice bluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connecting..", Toast.LENGTH_SHORT).show());
            bluetoothGatt = bluetoothDevice.connectGatt(this, false, btleGattCallback);
        }
    }

    @SuppressLint("MissingPermission")
    private void displayGattServices(BluetoothGatt gatt, List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        byte[] enableNotificationsValue = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
        BluetoothGattCharacteristic batteryLevel = gatt.getService(BATTERY_SERVICE_UUID).getCharacteristic(BATTERY_LEVEL_CHAR_UUID);
        gatt.readCharacteristic(batteryLevel);

        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            new ArrayList<HashMap<String, String>>();

            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {
                charUuid = gattCharacteristic.getUuid().toString();
            }
        }

        gatt.writeCharacteristic(batteryLevel);

    }

    public void enableBT() {
        if (!mBluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Intent blueToothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blueToothIntent, 123);
        }
    }

    @SuppressLint("MissingPermission")
    public void getDataStream() {


        BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(UUIDS.NUS_SERVICE_UUID).getCharacteristic(UUIDS.TX_CHARACTERISTIC_UUID);

        bluetoothGatt.setCharacteristicNotification(characteristic, true);

        String CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CCC_DESCRIPTOR_UUID));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);


    }

    @SuppressLint("MissingPermission")
    public void stopDataStream() {
        BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(UUIDS.NUS_SERVICE_UUID).getCharacteristic(UUIDS.TX_CHARACTERISTIC_UUID);

        bluetoothGatt.setCharacteristicNotification(characteristic, true);

        String CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CCC_DESCRIPTOR_UUID));
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
    }


    public void startRecord() {
        if (m_isRecording)
            return;

        labelingModel.setStartTime(System.currentTimeMillis());
        if(!added) {
            labelingModel.startRecord();
            added = true;
        }
        m_sensErr = false;
        // upload_button.setEnabled(false);
        System.out.println("Button CLICKED!");

        // Use BLE if available
        if (m_isConnectBLE) {
            String fileName = m_fileBase + newRecID() + ".bin";
            m_isRecording = true;


            try {
                if (outputStream != null)
                    outputStream.close();
                outputStream = new FileOutputStream(fileName);
                byte[] sig = new byte[4];
                sig[0] = 127;
                sig[1] = 127;
                sig[2] = 127;
                sig[3] = 127;
                outputStream.write(sig);
            } catch (Exception e) {

            }
            //================================================
            m_dt.startBLE();
            //================================================

            getDataStream();

            // Tell the dashboard what to do
            DashboardView.getDashboard().setSessionFile(fileName);
            return;
        }


        try {
            if(m_device == null) {
                m_isRecording = false;
                setStatus("No sensor found!");
                DataFragment.getdataFrag().noSensor();
                System.out.println("WARNING: Blahhhh!");
                return;
            }

            UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection con = usbManager.openDevice(m_device);

            con.controlTransfer(0x40, 0, 0, 0, null, 0, 0);// reset
            con.controlTransfer(0x40, 0, 1, 0, null, 0, 0);// clear Rx
            con.controlTransfer(0x40, 0, 2, 0, null, 0, 0);// clear Tx
            con.controlTransfer(0x40, 0x03, 0x000D, 0, null, 0, 0);//Baud 0x000D = 230400


            UsbInterface ifc = m_device.getInterface(0);

            UsbEndpoint ep = ifc.getEndpoint(0);


            new Thread(new Runnable() {
                public void run() {
                    try {
                        m_isRecording = true;
                        String fileName = m_fileBase + newRecID();

                        // Tell the dashboard what to do
                        DashboardView.getDashboard().setSessionFile(fileName);

                        m_dt.DoRecord(ep, con, fileName);

                        con.close();


                    } catch (Exception ex) {
                        con.close();
                    }
                }
            }).start();


            //Sound
            Tools.playMediaPlayer(this,  R.raw.harp);

        } catch (Exception e) {
            m_isRecording = false;
            setStatus("No sensor found!");
            DataFragment.getdataFrag().noSensor();
            System.out.println("WARNING: Blahhhh!");
        }

    }


    public void stopRecord() {
        System.out.println("STOP Button CLICKED!");
        m_isRecording = false;

        Tools.playMediaPlayer(this,  R.raw.click);
        m_sensErr = false;
        //upload_button.setEnabled(true);

        if (m_scanRes != null) { //BLE

            if (false) { // Disable
                //m_isConnectBLE = true; // Move this to the right place!
                setStatus("Connecting...");
                connect(m_scanRes);
            } else {
                stopDataStream();
                try {
                    Thread.sleep(100);
                } catch (Exception e) {

                }
                setStatus("Stopped BLE"); // Useless, it still gets data...
                //================================================
                m_dt.stopBLE();
                //================================================
            }
        } else { // USB
            m_dt.Stop();
        }
    }


    double zzz = 0;

    public void updateIntensity(double[] values, int type) {
        double lastItem = values[values.length - 1];
        if (type == 0) {

            intensityList.add(lastItem);
        } else if (type == 1) {
            double big = Math.max(zzz, lastItem);
            if (big != zzz) {
                Log.e(TAG, "run: " + Math.max(zzz, lastItem));
                zzz = big;
            }
            capacityList.add(lastItem);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (GraphsFragment.getGraphsFrag() != null) {
                    if (type == 0) {
                        GraphsFragment.getGraphsFrag().updateGraphs(intensityList, type);
                    } else if (type == 1) {
                        GraphsFragment.getGraphsFrag().updateGraphs(capacityList, type);
                    }
                }
            }
        });
    }


    public void submitWorkout(String athleteID) {
        String stuff = "Random comment";

        new Thread(new Runnable() {
            public void run() {
                boolean b = false;
                int successUploads = 0;
                int failedUploads = 0;
                boolean failed = false;

                int from = Integer.parseInt(getPref("rec_id_upload"));
                from++;
                int to = m_lastIndex;
                for (int i = from; i <= m_lastIndex; i++) {

                    String fileName = m_fileBase + i + ".bin";
                    File f = new File(fileName);
                    if (!f.exists()) {
                        setStatus("Skip " + fileName);
                        continue;
                    }

                    DashboardView.getDashboard().writeSetsBin(fileName);


                    setStatus("Upload " + fileName);

                    MLog.log("Try to upload " + fileName);
                    try {
                        FileUpload fu = new FileUpload();
                        fu.setMain(g_main);
                        b = fu.upload(stuff, fileName, athleteID);

                        if (b) {
                            String up = new String();
                            up += i;
                            setPref("rec_id_upload", up);
                            updateToUpload();
                            MLog.log("Upload successful");
                            successUploads++;
                        } else {

                            MLog.log("Upload failed!");
                            failed = true;
                            failedUploads++;
                            break;
                        }
                    } catch (Exception ex) {
                        failedUploads++;
                        MLog.log("EXCEPTION during upload: " + ex.getMessage());
                        break;
                    }
                }
                if (failedUploads > 0) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            myDialog.show("Upload failed");
                        }
                    });
                } else if (failedUploads == 0 && successUploads > 0) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            myDialog.show("Workout uploaded successfully");
                        }
                    });
                }
                // Log file
                try {
                    File dir = getFilesDir();
                    String path = dir.getAbsolutePath();
                    String fileName = path + "/ororec.log";

                    FileUpload fu = new FileUpload();
                    fu.setMain(g_main);
                    b = fu.upload_log(stuff, fileName, athleteID);
                } catch (Exception ex) {
                    MLog.log("EXCEPTION during LOG upload: " + ex.getMessage());
                    Log.d("fjlmdsjflkds", ex.getMessage());
                }
                // End log file

                if (failed) {
                    // Start login?
                    Intent sendIntent = new Intent();
                    String rc = Intent.ACTION_SEND;
                    sendIntent.setAction(rc);
                    //sendIntent.putExtra(Intent.EXTRA_TEXT, "auth_token " + accessToken);
                    sendIntent.setType("oromuscles/logintext");

                    setStatus("Starting login");
                    MLog.log("Starting login window");
                    startActivity(sendIntent);
                }
            }
        }).start();
    }


    public void saveLabelingModel(){
/*        ArrayList<Object> labelingModelsList = tinyDB.getListObject(Constants.LABELING_LIST, LabelingModel.class);
        labelingModelsList.add(labelingModel);
        tinyDB.putListObject(Constants.LABELING_LIST, labelingModelsList);*/


    }

}