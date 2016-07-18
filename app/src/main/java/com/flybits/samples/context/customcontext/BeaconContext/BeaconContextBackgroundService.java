package com.flybits.samples.context.customcontext.BeaconContext;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.flybits.core.api.context.v2.ContextUtils;
import com.flybits.core.api.context.v2.FlybitsBackgroundService;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;

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

/**
 * Created by Filip on 6/20/2016.
 */
public class BeaconContextBackgroundService extends FlybitsBackgroundService implements BeaconConsumer, RangeNotifier {

    private final static int SERVICE_ID_EDDYSTONE = 0xFEAA;
    private final static int SERVICE_ID_IBEACON = 0x4C000215;

    private final static String FRAME_IBEACON = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";

    private final static String _TAG = "Beacon";

    private String mPluginName;
    private boolean mShouldUpdateServer;

    private static BackgroundPowerSaver mBackgroundPowerSaver; //Bad??
    private BeaconManager mBeaconManager;

    @Override
    public int onRunTask(TaskParams taskParams) {

        Log.i(_TAG, "onRunTask");

        Bundle bundle = taskParams.getExtras();
        mShouldUpdateServer = bundle.getBoolean("shouldUpdateServer", false);
        mPluginName = bundle.getString("pluginName", "");

        mBackgroundPowerSaver = new BackgroundPowerSaver(this);

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

        return GcmNetworkManager.RESULT_SUCCESS;
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

        ArrayList<BeaconData> foundBeacons = new ArrayList();

        for (Beacon b : beacons) {
            BeaconData data = null;

            switch (b.getServiceUuid()) {
                case SERVICE_ID_EDDYSTONE:
                    data = handleEddystoneBeacon(b);
                    if (data != null) {
                        foundBeacons.add(data);
                    }
                    break;
                case SERVICE_ID_IBEACON:
                    data = handleIBeacon(b);
                    if (data != null) {
                        foundBeacons.add(data);
                    }
                    break;
            }
        }

        ContextUtils.refreshData(getBaseContext(), mPluginName, mShouldUpdateServer, new BeaconDataList(foundBeacons));

    }

    private BeaconData handleEddystoneBeacon(Beacon beacon)
    {
        switch (beacon.getBeaconTypeCode())
        {
            case 0x00:
                Identifier namespaceId = beacon.getId1();
                Identifier instanceId = beacon.getId2();

                BeaconData eddystoneUIDData = new BeaconData(namespaceId.toString(), namespaceId.toString());

                Log.d(_TAG, "Eddystone beacon; namespace id: "+namespaceId+
                        ", instance id: "+instanceId+
                        ", approximately "+beacon.getDistance()+"m away.");

                return eddystoneUIDData;
            case 0x10:

                if (beacon.getExtraDataFields().size() > 0) {
                    long telemetryVersion = beacon.getExtraDataFields().get(0);
                    long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                    long pduCount = beacon.getExtraDataFields().get(3);
                    long uptime = beacon.getExtraDataFields().get(4);


                }

                break;
            case 0x20:
                break;
        }

        return null;
    }

    private BeaconData handleIBeacon(Beacon beacon)
    {
        Identifier namespaceId = beacon.getId1();
        Identifier instanceId = beacon.getId2();

        BeaconData iBeaconData = new BeaconData(namespaceId.toString(), namespaceId.toString());

        Log.d(_TAG, "iBeacon; namespace id: "+namespaceId+
                ", instance id: "+instanceId+
                ", approximately "+beacon.getDistance()+"m away.");

        return iBeaconData;
    }

}