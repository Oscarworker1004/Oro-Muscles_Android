package com.digidactylus.recorder.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.models.athleteModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiManager {

    private final String BASE_URL = "https://oro-muscles-webportal.ew.r.appspot.com";

    private final String USER_ATHLETE_URL = "https://oro-muscles-webportal.ew.r.appspot.com/api/current_user";

    private final Context mContext;
    private final RequestQueue mRequestQueue;

    private static final String TAG = "ApiManager";

    public interface OnApiResult {
        void OnSuccess(JSONObject jsonObject);
        void OnFailed(String error);
    }

    public ApiManager(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    public void retrieveAthletes(OnApiResult onApiResult) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, USER_ATHLETE_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onApiResult.OnSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onApiResult.OnFailed(error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + MainActivity.getMain().getPref("auth_token"));
                return headers;
            }
        };

        request.setShouldCache(false);
        mRequestQueue
                .add(request);
    }

    public void makeAuthorizedGetRequest(String endpoint, String token, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener) {
        String url = BASE_URL + endpoint;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                successListener,
                errorListener
        ) {
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        request.setShouldCache(false);
        mRequestQueue
                .add(request);
    }


}
