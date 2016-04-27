package com.flybits.samples.context.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.flybits.samples.context.adapters.ContextAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContextFragment  extends Fragment {

    private TextView mTxtLastUpdated;
    private SwipeRefreshLayout mSwipeContainer;
    private String mCtxData;

    private RecyclerView mRecyclerView;
    private ContextAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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

        View view       = inflater.inflate(R.layout.content_context, container, false);
        mTxtLastUpdated = (TextView) view.findViewById(R.id.txtLastUpdated);
        mRecyclerView   = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);


        mListOfDataData = new ArrayList<>();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
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
                Log.d("Testing", "onRefresh");
                mSwipeContainer.setRefreshing(true);
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

            String refreshTime = (data != null && data.timestamp > 0)
                    ? refreshTime((data.timestamp * 1000)) : refreshTime(System.currentTimeMillis());

            if (data != null){
                mListOfDataData.add(data);
                mAdapter.notifyDataSetChanged();
            }

            mTxtLastUpdated.setText(getString(R.string.txtLastUpdated, refreshTime));
        }
        mSwipeContainer.setRefreshing(false);
    }

    private String refreshTime(long timeInMilli){
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss - yyyy-MM-dd ");
        Date gmt = new Date(timeInMilli);
        return formatter.format(gmt);
    }
}