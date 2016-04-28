package com.flybits.samples.context;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.flybits.core.api.context.plugins.AvailablePlugins;
import com.flybits.core.api.events.context.EventContextSensorValuesUpdated;
import com.flybits.samples.context.fragments.ContextFragment;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String CONTEXT_FRAGMENT_TAG = "tagContext";

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        android.app.FragmentManager manager = getFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
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

    public void onEventMainThread(EventContextSensorValuesUpdated event){
        android.app.FragmentManager manager = getFragmentManager();
        ContextFragment fragment = (ContextFragment) manager.findFragmentByTag(CONTEXT_FRAGMENT_TAG);
        if (fragment != null) {

            fragment.onNewData(event);
        }
    }
}
