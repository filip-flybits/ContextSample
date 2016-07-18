package com.flybits.samples.context;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.flybits.core.api.context.plugins.AvailablePlugins;
import com.flybits.core.api.context.v2.ContextManager;
import com.flybits.core.api.context.v2.CustomContextPlugin;
import com.flybits.core.api.context.v2.FlybitsContextPlugin;
import com.flybits.core.api.context.v2.plugins.activity.ActivityData;
import com.flybits.core.api.context.v2.plugins.battery.BatteryData;
import com.flybits.core.api.context.v2.plugins.carrier.CarrierData;
import com.flybits.core.api.context.v2.plugins.fitness.FitnessContextData;
import com.flybits.core.api.context.v2.plugins.language.LanguageContextData;
import com.flybits.core.api.context.v2.plugins.location.LocationData;
import com.flybits.core.api.context.v2.plugins.network.NetworkData;
import com.flybits.samples.context.customcontext.AudioContext.AudioContextBackgroundService;
import com.flybits.samples.context.customcontext.AudioContext.AudioContextForegroundService;
import com.flybits.samples.context.customcontext.AudioContext.AudioData;
import com.flybits.samples.context.customcontext.BeaconContext.BeaconContextBackgroundService;
import com.flybits.samples.context.customcontext.BeaconContext.BeaconContextForegroundService;
import com.flybits.samples.context.customcontext.BeaconContext.BeaconData;
import com.flybits.samples.context.fragments.ContextFragment;
import com.flybits.samples.context.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String CONTEXT_FRAGMENT_TAG   = "tagContext";
    private final String HOME_FRAGMENT_TAG      = "tagHome";

    private FlybitsContextPlugin.Builder plugin     = new FlybitsContextPlugin.Builder()
            .setPlugin(AvailablePlugins.BATTERY)
            .setInForegroundMode(MainActivity.this)
            .setRefreshTime(10)
            .setRefreshTimeFlex(10);

    CustomContextPlugin customPluginAudio = new CustomContextPlugin.Builder()
            .setBackgroundService(AudioContextBackgroundService.class)
            .setForgroundService(AudioContextForegroundService.class)
            .setInForegroundMode(MainActivity.this)
            .setPlugin("ctx.sdk.device.audio")
            .setRefreshTime(60)
            .setRefreshTimeFlex(60)
            .build();

    CustomContextPlugin customPluginBeacon = new CustomContextPlugin.Builder()
            .setBackgroundService(BeaconContextBackgroundService.class)
            .setForgroundService(BeaconContextForegroundService.class)
            .setInForegroundMode(MainActivity.this)
            .setPlugin("ctx.sdk.beacon")
            .setRefreshTime(60)
            .setRefreshTimeFlex(60)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        android.app.FragmentManager manager = getFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        Fragment fragment = HomeFragment.newInstance();
        fragmentTransaction.replace(R.id.content_frame, fragment, HOME_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        /**
         * REGISTER FOR CONTEXT CHANGES
         */
        IntentFilter filter = new IntentFilter("CONTEXT_UPDATED");
        registerReceiver(receiver, filter);

        /**
         * Sample Foreground for Context Plugin
         */
        Bundle bundle = new Bundle();
        bundle.putBoolean("shouldUpdateServer", true);

        plugin.setExtras(bundle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;

        int id = item.getItemId();

        if (id == R.id.item_activity) {
            fragment = ContextFragment.newInstance(AvailablePlugins.ACTIVITY);
        } else if (id == R.id.item_battery) {
            fragment = ContextFragment.newInstance(AvailablePlugins.BATTERY);
        } else if (id == R.id.item_carrier) {
            fragment = ContextFragment.newInstance(AvailablePlugins.CARRIER);
        } else if (id == R.id.item_fitness) {
            fragment = ContextFragment.newInstance(AvailablePlugins.FITNESS);
        } else if (id == R.id.item_language) {
            fragment = ContextFragment.newInstance(AvailablePlugins.LANGUAGE);
        } else if (id == R.id.item_location) {
            fragment = ContextFragment.newInstance(AvailablePlugins.LOCATION);
        } else if (id == R.id.item_network) {
            fragment = ContextFragment.newInstance(AvailablePlugins.NETWORK_CONNECTIVITY);
        } else if (id == R.id.item_audio) {
            fragment = ContextFragment.newInstance("ctx.sdk.device.audio");
        }

        fragmentTransaction.replace(R.id.content_frame, fragment, CONTEXT_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * ReceIving Notifications about Context Changes in the background
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            FragmentManager manager = getFragmentManager();
            ContextFragment fragment = (ContextFragment) manager.findFragmentByTag(CONTEXT_FRAGMENT_TAG);

            if (fragment != null) {
                if (bundle.containsKey("CONTEXT_TYPE")) {

                    if (bundle.getString("CONTEXT_TYPE", "").equals(AvailablePlugins.BATTERY.getKey())) {
                        BatteryData data = bundle.getParcelable("CONTEXT_OBJ");
                        if (data != null) {
                            Log.d("Testing", data.toString());
                            fragment.onNewData(data);
                        }
                    }
                    else if (bundle.getString("CONTEXT_TYPE", "").equals(AvailablePlugins.CARRIER.getKey())) {
                        CarrierData data = bundle.getParcelable("CONTEXT_OBJ");
                        if (data != null) {
                            Log.d("Testing", data.toString());
                            fragment.onNewData(data);
                        }
                    }
                    else if (bundle.getString("CONTEXT_TYPE", "").equals(AvailablePlugins.LOCATION.getKey())) {
                        LocationData data = bundle.getParcelable("CONTEXT_OBJ");
                        if (data != null) {
                            Log.d("Testing", data.toString());
                            fragment.onNewData(data);
                        }
                    }
                    else if (bundle.getString("CONTEXT_TYPE", "").equals(AvailablePlugins.LANGUAGE.getKey())) {
                        LanguageContextData data = bundle.getParcelable("CONTEXT_OBJ");
                        if (data != null) {
                            Log.d("Testing", data.toString());
                            fragment.onNewData(data);
                        }
                    }
                    else if (bundle.getString("CONTEXT_TYPE", "").equals(AvailablePlugins.FITNESS.getKey())) {
                        FitnessContextData data = bundle.getParcelable("CONTEXT_OBJ");
                        if (data != null) {
                            Log.d("Testing", data.toString());
                            fragment.onNewData(data);
                        }
                    }
                    else if (bundle.getString("CONTEXT_TYPE", "").equals(AvailablePlugins.ACTIVITY.getKey())) {
                        ActivityData data = bundle.getParcelable("CONTEXT_OBJ");
                        if (data != null) {
                            Log.d("Testing", data.toString());
                            fragment.onNewData(data);
                        }
                    }
                    else if (bundle.getString("CONTEXT_TYPE", "").equals(AvailablePlugins.NETWORK_CONNECTIVITY.getKey())) {
                        NetworkData data = bundle.getParcelable("CONTEXT_OBJ");
                        if (data != null) {
                            Log.d("Testing", data.toString());
                            fragment.onNewData(data);
                        }
                    }
                    else if (bundle.getString("CONTEXT_TYPE").equals("ctx.sdk.device.audio")) {
                        AudioData data = bundle.getParcelable("CONTEXT_OBJ");
                        if (data != null) {
                            Log.d("Testing", data.toString());
                            fragment.onNewData(data);
                        }
                    }
                    else if (bundle.getString("CONTEXT_TYPE").equals("ctx.sdk.beacon")) {
                        BeaconData data = bundle.getParcelable("CONTEXT_OBJ");
                        if (data != null) {
                            Log.d("Testing", data.toString());
                            fragment.onNewData(data);
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onPause() {
        ContextManager.include(MainActivity.this).unregister(plugin.build());
        ContextManager.include(MainActivity.this).unregister(customPluginAudio);
        ContextManager.include(MainActivity.this).unregister(customPluginBeacon);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ContextManager.include(MainActivity.this).register(plugin.build());
        ContextManager.include(MainActivity.this).register(customPluginAudio);
        ContextManager.include(MainActivity.this).register(customPluginBeacon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * UNREGISTER FOR CONTEXT CHANGES
         */
        unregisterReceiver(receiver);

    }
}
