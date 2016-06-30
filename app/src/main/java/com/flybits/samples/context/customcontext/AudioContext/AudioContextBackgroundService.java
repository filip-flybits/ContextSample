package com.flybits.samples.context.customcontext.AudioContext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.flybits.core.api.context.v2.FlybitsBackgroundService;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by Filip on 6/20/2016.
 */
public class AudioContextBackgroundService extends FlybitsBackgroundService{

    private String mPluginName;
    private boolean mShouldUpdateServer;

    private final static String _TAG = "Audio";

    @Override
    public int onRunTask(TaskParams taskParams) {

        Log.i(_TAG, "onRunTask");

        Bundle bundle = taskParams.getExtras();
        mShouldUpdateServer = bundle.getBoolean("shouldUpdateServer", false);
        mPluginName = bundle.getString("pluginName", "");

        AudioManager audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        AudioData data = new AudioData();

        data.isHeadsetPluggedIn = audioManager.isWiredHeadsetOn();
        data.isBluetoothA2dpOn = audioManager.isBluetoothA2dpOn();
        data.isBluetoothScoOn = audioManager.isBluetoothScoOn();
        data.ringerVolume = (double)audioManager.getStreamVolume(AudioManager.STREAM_RING)/(double)audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        data.mediaVolume = (double)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)/(double)audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        data.alarmVolume = (double)audioManager.getStreamVolume(AudioManager.STREAM_ALARM)/(double)audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

        refreshData(mPluginName, mShouldUpdateServer, data);

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    @Override
    public void onDestroy() {
        Log.i(_TAG, "...Destroyed");
        super.onDestroy();
    }

}