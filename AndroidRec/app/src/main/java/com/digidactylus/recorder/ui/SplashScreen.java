package com.digidactylus.recorder.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.result.Credentials;
import com.digidactylus.recorder.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashScreen extends AppCompatActivity {
    MaterialButton tomainbtn;
    private Lock lock;
    private static final String API_URL = "https://oro-muscles-webportal.ew.r.appspot.com/api/current_user";
    private Auth0 account;

    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);

        account = new Auth0(this);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // if(getPref("auth_token").isEmpty() || getPref("refresh_token").isEmpty()){
                if(getPref("auth_token").isEmpty() || getPref("refresh_token").isEmpty()){
                    startActivity(lock.newIntent(SplashScreen.this));
                }
                else {
                    getNewToken();
                }
            }
        },2000);

        // Instantiate Lock once
        lock = Lock.newBuilder(account, callback)
                // Customize Lock
                .withAudience("https://oro-muscles-webportal.ew.r.appspot.com")
                .withScope("offline_access read:current_user create:athlete red:athlete share:athlete update:athlete read:all_users update:roles invite:users create:organizations delete:organizations: read:organizations update:users delete:users upload:recordings download:recordings upload:training_plans download:training_plans upload:analysis update:analysis get:analysis read:all_athletes delete:recordings delete:analysis")
                .build(this);
    }


    private void getNewToken() {
        AuthenticationAPIClient client = new AuthenticationAPIClient(account);
        client.renewAuth(getPref("refresh_token"))
                .start(new Callback<Credentials, AuthenticationException>() {
                    @Override
                    public void onFailure(AuthenticationException e) {
                        e.printStackTrace();
                        startActivity(lock.newIntent(SplashScreen.this));
                    }

                    @Override
                    public void onSuccess(Credentials credentials) {
                        setPref("auth_token",credentials.getAccessToken());
                        setPref("refresh_token",credentials.getRefreshToken());
                        startActivity(new Intent(SplashScreen.this, WorkoutSelection.class));
                        finish();
                    }
                });
    }

    private AuthenticationCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            setPref("auth_token",credentials.getAccessToken());
            setPref("refresh_token",credentials.getRefreshToken());
            startActivity(new Intent(SplashScreen.this, WorkoutSelection.class));
            finish();
        }

        @Override
        public void onError(AuthenticationException error) {
            Toast.makeText(SplashScreen.this, "Connection Error. Try again later.", Toast.LENGTH_LONG).show();
            // An exception occurred
        }

    };

    public void setPref(String key, String value) {
        SharedPreferences prefs = getSharedPreferences("oropref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
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





}