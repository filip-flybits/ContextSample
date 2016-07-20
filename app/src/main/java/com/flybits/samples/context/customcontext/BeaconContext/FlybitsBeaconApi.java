package com.flybits.samples.context.customcontext.BeaconContext;

import android.content.Context;
import android.util.Log;

import com.flybits.core.api.Flybits;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Filip on 7/19/2016.
 */
public class FlybitsBeaconApi {

    public static List<MonitoredBeacon> getBeaconsToMonitor(Context context) throws IOException {
        OkHttpClient client = Flybits.include(context).getFlybitsSession();

        Request request = new Request.Builder()
                .url("https://gateway.flybits.com/context/beacons/monitoring")
                .addHeader("X-Authorization", Flybits.include(context).getSavedJWTToken())
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

        String resBody = response.body().string();

        Gson gson = new Gson();

        Type type = new TypeToken<List<MonitoredBeacon>>() {}.getType();
        List<MonitoredBeacon> beaconsToMonitor = gson.fromJson(resBody, type);

        Log.i("TEST", "Body is: " + resBody);

        return beaconsToMonitor;
    }

    public static void getBeacons(Context context) throws IOException {
        OkHttpClient client = Flybits.include(context).getFlybitsSession();

        Request request = new Request.Builder()
                .url("https://gateway.flybits.com/context/beacons")
                .addHeader("X-Authorization", Flybits.include(context).getSavedJWTToken())
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

        String resBody = response.body().string();
        Log.i("TEST", "Body is: " + resBody);

    }

    public static void checkHealth(Context context) throws IOException {
        OkHttpClient client = Flybits.include(context).getFlybitsSession();

        Request request = new Request.Builder()
                .url("https://gateway.flybits.com/context/beacons/health")
                .addHeader("X-Authorization", Flybits.include(context).getSavedJWTToken())
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

        String resBody = response.body().string();
        Log.i("TEST", "Body is: " + resBody);

    }

}
