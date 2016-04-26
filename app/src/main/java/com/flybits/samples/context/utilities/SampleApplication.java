package com.flybits.samples.context.utilities;

import android.app.Application;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.FlybitsOptions;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FlybitsOptions options = new FlybitsOptions.Builder(this)
                //Indicate whether or not exceptions/network traffic should be displayed in the logcat
                .setDebug(true)
                .build();


        //Initialize the FlybitsOptions
        Flybits.include(this).initialize(options);
    }
}
