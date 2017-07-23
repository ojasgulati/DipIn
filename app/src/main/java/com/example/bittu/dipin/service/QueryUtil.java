package com.example.bittu.dipin.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.bittu.dipin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class QueryUtil {
    private static final String TAG = "RemoteEndpointUtil";

    private QueryUtil() {
    }

    public static JSONArray fetchJsonArray(Context context) {
        String itemsJson = null;
        try {
            SharedPreferences prefs = context.getSharedPreferences("sharedPlatform",0);
            URL baseUrl = new URL(prefs.getString(context.getString(R.string.pref_shared_platform), "https://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=839b127083e848e188382abfc1e8ee16")); // GET YOUR OWN API at https://newsapi.org/
            itemsJson = fetchPlainText(baseUrl);
        } catch (IOException e) {
            Log.e(TAG, "Error fetching items JSON", e);
            return null;
        }

        // Parse JSON
        try {
            JSONTokener tokener = new JSONTokener(itemsJson);
            JSONObject object = (JSONObject) tokener.nextValue();
            JSONArray val = object.getJSONArray("articles");
            if (!(val instanceof JSONArray)) {
                throw new JSONException("Expected JSONArray");
            }
            return val;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing items JSON", e);
        }

        return null;
    }

    static String fetchPlainText(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}