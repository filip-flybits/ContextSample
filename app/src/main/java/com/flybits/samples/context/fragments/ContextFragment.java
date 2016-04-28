package com.flybits.samples.context.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.context.BasicData;
import com.flybits.core.api.context.ContextPlugin;
import com.flybits.core.api.context.plugins.AvailablePlugins;
import com.flybits.core.api.context.plugins.activity.ActivityData;
import com.flybits.core.api.context.plugins.activity.ActivityProvider;
import com.flybits.core.api.context.plugins.battery.BatteryLifeData;
import com.flybits.core.api.context.plugins.battery.BatteryLifeProvider;
import com.flybits.core.api.context.plugins.beacon.BeaconData;
import com.flybits.core.api.context.plugins.beacon.BeaconProvider;
import com.flybits.core.api.context.plugins.carrier.CarrierData;
import com.flybits.core.api.context.plugins.carrier.CarrierProvider;
import com.flybits.core.api.context.plugins.fitness.FitnessData;
import com.flybits.core.api.context.plugins.fitness.FitnessProvider;
import com.flybits.core.api.context.plugins.language.LanguageData;
import com.flybits.core.api.context.plugins.language.LanguageProvider;
import com.flybits.core.api.context.plugins.location.LocationData;
import com.flybits.core.api.context.plugins.location.LocationProvider;
import com.flybits.core.api.context.plugins.network.NetworkData;
import com.flybits.core.api.context.plugins.network.NetworkProvider;
import com.flybits.core.api.events.context.EventContextSensorValuesUpdated;
import com.flybits.samples.context.R;
import com.flybits.samples.context.adapters.ContextAdapter;
import com.flybits.samples.context.utilities.TimeUtils;

import java.util.ArrayList;

public class ContextFragment  extends Fragment {

    private TextView mTxtLastUpdated;
    private SwipeRefreshLayout mSwipeContainer;
    private String mCtxData;
    private AvailablePlugins mCurrentPlugin;
    private ContextAdapter mAdapter;

    private ArrayList<BasicData> mListOfDataData;

    public static Fragment newInstance(AvailablePlugins plugin) {
        ContextFragment newFragment = new ContextFragment();
        Bundle bundle = new Bundle();
        bundle.putString("context", plugin.getKey());
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view                   = inflater.inflate(R.layout.content_context, container, false);
        mTxtLastUpdated             = (TextView) view.findViewById(R.id.txtLastUpdated);
        mSwipeContainer             = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        RecyclerView mRecyclerView  = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        mListOfDataData = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ContextAdapter(getActivity(), mListOfDataData);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContext();
            }
        });

        Bundle bundle;

        /*
            Retrieve the context that this fragment to be associated with as this fragment is re-used
            for each context plugin.
         */
        if ((bundle = getArguments()) != null){
            mSwipeContainer.setRefreshing(true);
            mCtxData            = bundle.getString("context");
            mCurrentPlugin      = AvailablePlugins.fromKey(mCtxData);
            fetchItems();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshContext();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        Refresh the context value for the Context Plugin registered for this Fragment.
     */
    private void refreshContext() {
        ContextPlugin plugin = null;

        if (mCtxData.equals(AvailablePlugins.ACTIVITY.getKey())){
            plugin =  new ActivityProvider(getActivity(), 60000);
        }else if (mCtxData.equals(AvailablePlugins.BATTERY.getKey())){
            plugin =  new BatteryLifeProvider(getActivity(), 60000);
        }else if (mCtxData.equals(AvailablePlugins.BEACON.getKey())){
            plugin =  new BeaconProvider(getActivity(), 60000);
        }else if (mCtxData.equals(AvailablePlugins.CARRIER.getKey())){
            plugin =  new CarrierProvider(getActivity(), 60000);
        }else if (mCtxData.equals(AvailablePlugins.FITNESS.getKey())){
            plugin =  new FitnessProvider(getActivity(), 60000);
        }else if (mCtxData.equals(AvailablePlugins.LANGUAGE.getKey())){
            plugin =  new LanguageProvider(getActivity(), 60000);
        }else if (mCtxData.equals(AvailablePlugins.LOCATION.getKey())){
            plugin =  new LocationProvider(getActivity(), 60000);
        }else if (mCtxData.equals(AvailablePlugins.NETWORK_CONNECTIVITY.getKey())){
            plugin =  new NetworkProvider(getActivity(), 60000);
        }

        if (plugin != null) {
            Flybits.include(getActivity()).refreshContext(plugin);
        }
        mSwipeContainer.setRefreshing(false);

        /*
            Update the refresh time of the Context Plugin. Refreshing the context will result in a
            triggered event notification from to the SDK to the subscribed event in MainActivity. This
            event will only be triggered if there is a change in the Context. If the value has not
            changed then no event will be triggered.
         */
        mTxtLastUpdated.setText(getString(R.string.txtLastUpdated, TimeUtils.getTimeAsString(System.currentTimeMillis())));
    }

    /*
        Fetch the last known context value for the registered context plugin.
     */
    private void fetchItems() {
        BasicData data = Flybits.include(getActivity()).getContextData(mCurrentPlugin);
        setView(data);
        mSwipeContainer.setRefreshing(false);
    }

    /*
        Set the Views of the UI including the items in the RecyclerView and the Last Updated time.
     */
    private void setView(BasicData data){
        String refreshTime = (data != null && data.timestamp > 0)
                ? TimeUtils.getTimeAsString(data.timestamp * 1000) : TimeUtils.getTimeAsString(System.currentTimeMillis());

        if (data != null){

            if (mListOfDataData.size() > 0){
                if (!mListOfDataData.get(0).value.equals(data.value)){
                    mListOfDataData.add(0, data);
                }
            }else{
                mListOfDataData.add(0, data);
            }
            mAdapter.notifyDataSetChanged();
        }

        mTxtLastUpdated.setText(getString(R.string.txtLastUpdated, refreshTime));
    }

    /*
        Method that is triggered from the MainActivity's onEventMainThread(EventContextSensorValuesUpdated event)
        method which is triggered by the SDK whenever a Context Plugin's information is updated either
        programmatically or manually.
     */
    public void onNewData(EventContextSensorValuesUpdated event) {
        if (mCtxData.equals(AvailablePlugins.ACTIVITY.getKey()) && event.plugin == AvailablePlugins.ACTIVITY){
            BasicData<ActivityData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.BATTERY.getKey()) && event.plugin == AvailablePlugins.BATTERY){
            BasicData<BatteryLifeData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.BEACON.getKey()) && event.plugin == AvailablePlugins.BEACON){
            BasicData<BeaconData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.CARRIER.getKey()) && event.plugin == AvailablePlugins.CARRIER){
            BasicData<CarrierData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.FITNESS.getKey()) && event.plugin == AvailablePlugins.FITNESS){
            BasicData<FitnessData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.LANGUAGE.getKey()) && event.plugin == AvailablePlugins.LANGUAGE){
            BasicData<LanguageData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.LOCATION.getKey()) && event.plugin == AvailablePlugins.LOCATION){
            BasicData<LocationData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.NETWORK_CONNECTIVITY.getKey()) && event.plugin == AvailablePlugins.NETWORK_CONNECTIVITY){
            BasicData<NetworkData> data = event.contextSensor;
            setView(data);
        }
    }
}