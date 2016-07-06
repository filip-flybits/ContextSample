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
import com.flybits.core.api.context.plugins.AvailablePlugins;
import com.flybits.core.api.context.v2.ContextData;
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
import com.flybits.samples.context.MainActivity;
import com.flybits.samples.context.R;
import com.flybits.samples.context.adapters.ContextAdapter;
import com.flybits.samples.context.customcontext.AudioContext.AudioContextBackgroundService;
import com.flybits.samples.context.customcontext.AudioContext.AudioContextForegroundService;
import com.flybits.samples.context.customcontext.AudioContext.AudioData;
import com.flybits.samples.context.utilities.TimeUtils;

import java.util.ArrayList;

public class ContextFragment  extends Fragment {

    private TextView mTxtLastUpdated;
    private SwipeRefreshLayout mSwipeContainer;
    private String mCtxData;
    private AvailablePlugins mCurrentPlugin;
    private ContextAdapter mAdapter;

    private ArrayList<ContextData> mListOfDataData;

    public static Fragment newInstance(String pluginName) {
        ContextFragment newFragment = new ContextFragment();
        Bundle bundle = new Bundle();
        bundle.putString("context", pluginName);
        newFragment.setArguments(bundle);
        return newFragment;
    }

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
           // fetchItems();
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

        if (mCtxData.equals("ctx.sdk.device.audio"))
        {
            CustomContextPlugin customPluginAudio = new CustomContextPlugin.Builder()
                    .setBackgroundService(AudioContextBackgroundService.class)
                    .setForgroundService(AudioContextForegroundService.class)
                    .setInForegroundMode(getActivity())
                    .setPlugin("ctx.sdk.device.audio")
                    .setRefreshTime(60)
                    .setRefreshTimeFlex(60)
                    .build();

            if (customPluginAudio != null) {
                ContextManager.include(getActivity()).register(customPluginAudio);
            }
        }
        else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("", true);
            FlybitsContextPlugin.Builder plugin = new FlybitsContextPlugin.Builder()
                    .setExtras(bundle);

            if (mCtxData.equals(AvailablePlugins.ACTIVITY.getKey())) {
                plugin.setPlugin(AvailablePlugins.ACTIVITY);
            } else if (mCtxData.equals(AvailablePlugins.BATTERY.getKey())) {
                plugin.setPlugin(AvailablePlugins.BATTERY);
            } else if (mCtxData.equals(AvailablePlugins.CARRIER.getKey())) {
                plugin.setPlugin(AvailablePlugins.CARRIER);
            } else if (mCtxData.equals(AvailablePlugins.FITNESS.getKey())) {
                plugin.setPlugin(AvailablePlugins.FITNESS);
            } else if (mCtxData.equals(AvailablePlugins.LANGUAGE.getKey())) {
                plugin.setPlugin(AvailablePlugins.LANGUAGE);
            } else if (mCtxData.equals(AvailablePlugins.LOCATION.getKey())) {
                plugin.setPlugin(AvailablePlugins.LOCATION);
            } else if (mCtxData.equals(AvailablePlugins.NETWORK_CONNECTIVITY.getKey())) {
                plugin.setPlugin(AvailablePlugins.NETWORK_CONNECTIVITY);
            }

            plugin.setInForegroundMode(getActivity());

            if (plugin != null) {
                ContextManager.include(getActivity()).register(plugin.build());
            }
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
        com.flybits.core.api.context.v2.BasicData data = Flybits.include(getActivity()).getDataForContext(mCurrentPlugin);
        setView(data.value);
        mSwipeContainer.setRefreshing(false);
    }

    /*
        Set the Views of the UI including the items in the RecyclerView and the Last Updated time.
     */
    private void setView(ContextData data){
        String refreshTime = TimeUtils.getTimeAsString(System.currentTimeMillis());

        if (data != null){

            if (data instanceof AudioData && !mCtxData.equals("ctx.sdk.device.audio"))
                return;

            if (!(data instanceof AudioData) && !mCtxData.equals("ctx.sdk.device.audio")) {
                if (data instanceof ActivityData && !mCurrentPlugin.getKey().equals(AvailablePlugins.ACTIVITY.getKey())) {
                    return;
                } else if (data instanceof BatteryData && !mCurrentPlugin.getKey().equals(AvailablePlugins.BATTERY.getKey())) {
                    return;
                } else if (data instanceof FitnessContextData && !mCurrentPlugin.getKey().equals(AvailablePlugins.FITNESS.getKey())) {
                    return;
                } else if (data instanceof LanguageContextData && !mCurrentPlugin.getKey().equals(AvailablePlugins.LANGUAGE.getKey())) {
                    return;
                } else if (data instanceof LocationData && !mCurrentPlugin.getKey().equals(AvailablePlugins.LOCATION.getKey())) {
                    return;
                } else if (data instanceof NetworkData && !mCurrentPlugin.getKey().equals(AvailablePlugins.NETWORK_CONNECTIVITY.getKey())) {
                    return;
                } else if (data instanceof CarrierData && !mCurrentPlugin.getKey().equals(AvailablePlugins.CARRIER.getKey())) {
                    return;
                }
            }

            mListOfDataData.clear();

            if (mListOfDataData.size() > 0){
                if (!mListOfDataData.get(0).equals(data)){
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
    public void onNewData(ContextData event) {
        setView(event);

        /*
        if (mCtxData.equals(AvailablePlugins.ACTIVITY.getKey()) && event.dataTypeID.equals(AvailablePlugins.ACTIVITY.getKey())){
            com.flybits.core.api.context.v2.BasicData<ActivityData> data = event;

        }else if (mCtxData.equals(AvailablePlugins.BATTERY.getKey()) && event.dataTypeID.equals(AvailablePlugins.BATTERY.getKey())){
            BasicData<BatteryLifeData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.CARRIER.getKey()) && event.dataTypeID.equals(AvailablePlugins.CARRIER.getKey())){
            BasicData<CarrierData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.FITNESS.getKey()) && event.dataTypeID.equals(AvailablePlugins.FITNESS.getKey())){
            BasicData<FitnessData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.LANGUAGE.getKey()) && event.dataTypeID.equals(AvailablePlugins.LANGUAGE.getKey())){
            BasicData<LanguageData> data = event;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.LOCATION.getKey()) && event.dataTypeID.equals(AvailablePlugins.LOCATION.getKey())){
            BasicData<LocationData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.NETWORK_CONNECTIVITY.getKey()) && event.dataTypeID.equals(AvailablePlugins.NETWORK_CONNECTIVITY.getKey())){
            BasicData<NetworkData> data = event.contextSensor;
            setView(data);
        }
        */
    }
}