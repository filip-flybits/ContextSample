package com.flybits.samples.context.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.context.BasicData;
import com.flybits.core.api.context.plugins.AvailablePlugins;
import com.flybits.core.api.context.plugins.activity.ActivityData;
import com.flybits.core.api.context.plugins.battery.BatteryLifeData;
import com.flybits.core.api.context.plugins.carrier.CarrierData;
import com.flybits.core.api.context.plugins.fitness.FitnessData;
import com.flybits.core.api.context.plugins.language.LanguageData;
import com.flybits.core.api.context.plugins.location.LocationData;
import com.flybits.core.api.context.plugins.network.NetworkData;
import com.flybits.core.api.events.context.EventContextSensorValuesUpdated;
import com.flybits.samples.context.R;
import com.flybits.samples.context.adapters.ContextAdapter;
import com.flybits.samples.context.utilities.TimeUtils;

import java.util.ArrayList;

public class ContextFragment  extends Fragment {

    private TextView mTxtLastUpdated;
    private SwipeRefreshLayout mSwipeContainer;
    private String mCtxData;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view                   = inflater.inflate(R.layout.content_context, container, false);
        mTxtLastUpdated             = (TextView) view.findViewById(R.id.txtLastUpdated);
        mSwipeContainer             = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        RecyclerView mRecyclerView  = (RecyclerView) view.findViewById(R.id.my_recycler_view);


        mListOfDataData = new ArrayList<>();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ContextAdapter(getActivity(), mListOfDataData);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchItems();
            }
        });
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        Bundle bundle;
        if ((bundle = getArguments()) != null){
            mSwipeContainer.setRefreshing(true);
            mCtxData = bundle.getString("context");
            fetchItems();
        }

        return view;
    }

    private void fetchItems() {
        if (mCtxData.equals(AvailablePlugins.ACTIVITY.getKey())){
            BasicData<ActivityData> data = Flybits.include(getActivity()).getContextData(AvailablePlugins.ACTIVITY);
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.BATTERY.getKey())){
            BasicData<BatteryLifeData> data = Flybits.include(getActivity()).getContextData(AvailablePlugins.BATTERY);
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.BATTERY.getKey())){

        }else if (mCtxData.equals(AvailablePlugins.CARRIER.getKey())){
            BasicData<CarrierData> data = Flybits.include(getActivity()).getContextData(AvailablePlugins.CARRIER);
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.FITNESS.getKey())){
            BasicData<FitnessData> data = Flybits.include(getActivity()).getContextData(AvailablePlugins.FITNESS);
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.LANGUAGE.getKey())){
            BasicData<LanguageData> data = Flybits.include(getActivity()).getContextData(AvailablePlugins.LANGUAGE);
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.LOCATION.getKey())){
            BasicData<LocationData> data = Flybits.include(getActivity()).getContextData(AvailablePlugins.LOCATION);
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.NETWORK_CONNECTIVITY.getKey())){
            BasicData<NetworkData> data = Flybits.include(getActivity()).getContextData(AvailablePlugins.NETWORK_CONNECTIVITY);
            setView(data);
        }

        mSwipeContainer.setRefreshing(false);
    }

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

    public void onEventMainThread(EventContextSensorValuesUpdated event){
        if (mCtxData.equals(AvailablePlugins.ACTIVITY.getKey()) && event.plugin == AvailablePlugins.ACTIVITY){

            BasicData<ActivityData> data = event.contextSensor;
            setView(data);

        }else if (mCtxData.equals(AvailablePlugins.BATTERY.getKey()) && event.plugin == AvailablePlugins.BATTERY){
            BasicData<BatteryLifeData> data = event.contextSensor;
            setView(data);
        }else if (mCtxData.equals(AvailablePlugins.BEACON.getKey()) && event.plugin == AvailablePlugins.BEACON){

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