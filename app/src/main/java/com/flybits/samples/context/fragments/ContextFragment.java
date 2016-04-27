package com.flybits.samples.context.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.context.BasicData;
import com.flybits.core.api.context.plugins.AvailablePlugins;
import com.flybits.core.api.context.plugins.activity.ActivityData;
import com.flybits.samples.context.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContextFragment  extends Fragment {

    private TextView txtLastUpdated;
    private SwipeRefreshLayout swipeContainer;
    private String ctxData;

    public static Fragment newInstance(String plugin) {

        ContextFragment newFragment = new ContextFragment();
        Bundle bundle = new Bundle();
        bundle.putString("context", plugin);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view       = inflater.inflate(R.layout.content_context, container, false);
        txtLastUpdated  = (TextView) view.findViewById(R.id.txtLastUpdated);
        swipeContainer  = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                Log.d("Testing", "onRefresh");
                swipeContainer.setRefreshing(true);
                fetchItems();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        Bundle bundle;
        if ((bundle = getArguments()) != null){
            swipeContainer.setRefreshing(true);
            ctxData = bundle.getString("context");
            fetchItems();
        }

        return view;
    }

    private void fetchItems() {
        if (ctxData.equals(AvailablePlugins.ACTIVITY.getKey())){
            BasicData<ActivityData> data = Flybits.include(getActivity()).getContextData(AvailablePlugins.ACTIVITY);

            String refreshTime = (data != null && data.timestamp > 0)
                    ? refreshTime((data.timestamp * 1000)) : refreshTime(System.currentTimeMillis());

            txtLastUpdated.setText(getString(R.string.txtLastUpdated, refreshTime));
        }
        swipeContainer.setRefreshing(false);
    }

    private String refreshTime(long timeInMilli){
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss - yyyy-MM-dd ");
        Date gmt = new Date(timeInMilli);
        return formatter.format(gmt);
    }
}