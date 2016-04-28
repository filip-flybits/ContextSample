package com.flybits.samples.context;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.flybits.core.api.context.plugins.AvailablePlugins;
import com.flybits.core.api.events.context.EventContextSensorValuesUpdated;
import com.flybits.samples.context.fragments.ContextFragment;
import com.flybits.samples.context.fragments.HomeFragment;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String CONTEXT_FRAGMENT_TAG   = "tagContext";
    private final String HOME_FRAGMENT_TAG      = "tagHome";

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
        } else if (id == R.id.item_beacon) {
            fragment = ContextFragment.newInstance(AvailablePlugins.BEACON);
        } else if (id == R.id.item_carrier) {
            fragment = ContextFragment.newInstance(AvailablePlugins.CARRIER);
        } else if (id == R.id.item_fitness) {
            fragment = ContextFragment.newInstance(AvailablePlugins.FITNESS);
        } else if (id == R.id.item_language) {
            fragment = ContextFragment.newInstance(AvailablePlugins.LANGUAGE);
        }else if (id == R.id.item_location) {
            fragment = ContextFragment.newInstance(AvailablePlugins.LOCATION);
        }else if (id == R.id.item_network) {
            fragment = ContextFragment.newInstance(AvailablePlugins.NETWORK_CONNECTIVITY);
        }

        fragmentTransaction.replace(R.id.content_frame, fragment, CONTEXT_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
        This is how you would register to events triggered by the Flybits SDK
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /*
        Subscription to the EventContextSensorValuesUpdated event which listens to changes in
          Context plugin values regardless of which Context plugin it is for. Therefore, it is
           important to check to make sure that the context plugin is the one you are interested in.
    */
    public void onEventMainThread(EventContextSensorValuesUpdated event){
        FragmentManager manager = getFragmentManager();
        ContextFragment fragment = (ContextFragment) manager.findFragmentByTag(CONTEXT_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.onNewData(event);
        }
    }
}
