package com.flybits.samples.context.customcontext.BeaconContext;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.flybits.core.api.context.v2.ContextUtils;
import com.flybits.core.api.context.v2.FlybitsForegroundService;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Filip on 6/20/2016.
 */
public class BeaconContextForegroundService extends FlybitsForegroundService implements BeaconConsumer, RangeNotifier {

    private final static int SERVICE_ID_EDDYSTONE = 0xFEAA;
    private final static int SERVICE_ID_IBEACON = 0x4C000215;

    private final static String FRAME_IBEACON = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";

    private final static String _TAG = "Beacon";

    private static BackgroundPowerSaver mBackgroundPowerSaver; //Bad??
    private BeaconManager mBeaconManager;
    private String pluginName;
    private boolean shouldUpdateServer;
    private long mTimeInSecondsToRefresh;
    private long mLastRefresh = 0;

    private HashMap<BeaconData, Long> mActiveBeacons = new HashMap();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mBackgroundPowerSaver = new BackgroundPowerSaver(this);

        Bundle bundle = intent.getExtras();
        shouldUpdateServer = bundle.getBoolean("shouldUpdateServer", false);
        pluginName = bundle.getString("pluginName", "");
        mTimeInSecondsToRefresh = bundle.getLong("minimumRefreshTime", 60);

        if (mTimeInSecondsToRefresh > 0) {
            getData(pluginName, shouldUpdateServer, false);
        } else {
            getData(pluginName, shouldUpdateServer, true);
        }

        mBeaconManager = BeaconManager.getInstanceForApplication(this);

        //Setup Frames
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(FRAME_IBEACON));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));

        mBeaconManager.bind(this);

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void getData(String pluginName, boolean shouldUpdateServer, boolean shouldCloseAfterFinished) {

        if (shouldCloseAfterFinished) {
            stopSelf();
        }

    }

    @Override
    public void onDestroy() {
        Log.i(_TAG, "...Destroyed");

        mBeaconManager.unbind(this);

        super.onDestroy();
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        ArrayList<BeaconData> beaconsToRemove = new ArrayList();

        for (Map.Entry<BeaconData, Long> b : mActiveBeacons.entrySet())
        {
            if (SystemClock.elapsedRealtime() - b.getValue() > 30000)
                beaconsToRemove.add(b.getKey());
        }

        for (BeaconData b : beaconsToRemove)
            mActiveBeacons.remove(b);

        for (Beacon b : beacons) {
            BeaconData data = null;

            switch (b.getServiceUuid()) {
                case SERVICE_ID_EDDYSTONE:
                    data = handleEddystoneBeacon(b);
                    break;
                case SERVICE_ID_IBEACON:
                    data = handleIBeacon(b);
                    break;
            }

            if (data != null && !mActiveBeacons.containsKey(data))
                mActiveBeacons.put(data, SystemClock.elapsedRealtime());
        }

        if (SystemClock.elapsedRealtime() - mLastRefresh > mTimeInSecondsToRefresh * 1000)
        {
            ContextUtils.refreshData(getBaseContext(), pluginName, shouldUpdateServer, new BeaconDataList(mActiveBeacons.keySet()));
            mLastRefresh = SystemClock.elapsedRealtime();
        }

    }

    private BeaconData handleEddystoneBeacon(Beacon beacon)
    {
        switch (beacon.getBeaconTypeCode())
        {
            case 0x00:
                Identifier namespaceId = beacon.getId1();
                Identifier instanceId = beacon.getId2();

                BeaconData eddystoneUIDData = new BeaconData(namespaceId.toString(), instanceId.toString());

                /*
                Log.d(_TAG, "I see a eddystone beacon transmitting namespace id: "+namespaceId+
                        " and instance id: "+instanceId+
                        " approximately "+beacon.getDistance()+" meters away.");*/

                return eddystoneUIDData;
            case 0x10:

                if (beacon.getExtraDataFields().size() > 0) {
                    long telemetryVersion = beacon.getExtraDataFields().get(0);
                    long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                    long pduCount = beacon.getExtraDataFields().get(3);
                    long uptime = beacon.getExtraDataFields().get(4);

                    /*Log.d(_TAG, "The above beacon is sending telemetry version " + telemetryVersion +
                            ", has been up for : " + uptime + " seconds" +
                            ", has a battery level of " + batteryMilliVolts + " mV" +
                            ", and has transmitted " + pduCount + " advertisements.");*/
                }

                break;
            case 0x20:
                break;
        }

        return null;
    }

    private BeaconData handleIBeacon(Beacon beacon)
    {
        Identifier uuid = beacon.getId1();
        Identifier majorID = beacon.getId2();
        Identifier minorID = beacon.getId3();

        BeaconData iBeaconData = new BeaconData(uuid.toString(), majorID.toString(), minorID.toString());

        //Log.d(_TAG, "I see a iBeacon transmitting uuid id: "+uuid+
        //        " approximately "+beacon.getDistance()+"m away.");

        return iBeaconData;
    }

}
