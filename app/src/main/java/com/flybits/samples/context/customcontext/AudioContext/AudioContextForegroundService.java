package com.flybits.samples.context.customcontext.AudioContext;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.context.v2.ContextUtils;
import com.flybits.core.api.context.v2.FlybitsForegroundService;
import com.flybits.core.api.utils.LogType;
import com.google.android.gms.gcm.GcmNetworkManager;

/**
 * Created by Filip on 6/20/2016.
 */
public class AudioContextForegroundService extends FlybitsForegroundService {

    private final static String _TAG = "AudioContext";

    @Override
    public void getData(String pluginName, boolean shouldUpdateServer, boolean shouldCloseAfterFinished){

        AudioManager audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        AudioData data = new AudioData();

        data.isHeadsetPluggedIn = audioManager.isWiredHeadsetOn();
        data.isBluetoothA2dpOn = audioManager.isBluetoothA2dpOn();
        data.isBluetoothScoOn = audioManager.isBluetoothScoOn();
        data.ringerVolume = (double)audioManager.getStreamVolume(AudioManager.STREAM_RING)/(double)audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        data.mediaVolume = (double)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)/(double)audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        data.alarmVolume = (double)audioManager.getStreamVolume(AudioManager.STREAM_ALARM)/(double)audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

        ContextUtils.refreshData(getBaseContext(), pluginName, shouldUpdateServer, data);

        if (shouldCloseAfterFinished){
            stopSelf();
        }

    }

    @Override
    public void onDestroy() {
        Log.i(_TAG, "...Destroyed");


        super.onDestroy();
    }

}
