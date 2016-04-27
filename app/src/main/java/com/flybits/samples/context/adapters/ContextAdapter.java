package com.flybits.samples.context.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flybits.core.api.context.BasicData;
import com.flybits.core.api.context.plugins.activity.ActivityData;
import com.flybits.samples.context.R;

import java.util.ArrayList;

public class ContextAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ACTIVITY  = 0;
    private static final int TYPE_BATTERY   = 1;
    private static final int TYPE_BEACON    = 2;
    private static final int TYPE_CARRIER   = 3;
    private static final int TYPE_FITNESS   = 4;
    private static final int TYPE_LANGUAGE  = 5;
    private static final int TYPE_LOCATION  = 6;
    private static final int TYPE_NETWORK   = 7;

    private Context mContext;
    private ArrayList<BasicData> mListOfContextData;

    public ContextAdapter(Context context, ArrayList<BasicData> listOfContextData) {
        mListOfContextData  = listOfContextData;
        mContext            = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int code) {

        View v = null;
        if (code == TYPE_ACTIVITY) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_BATTERY) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_BEACON) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_CARRIER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_FITNESS) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_LANGUAGE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_LOCATION) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_NETWORK) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }

        return new ViewContextActivity(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewContextActivity) {

            BasicData<ActivityData> data    = mListOfContextData.get(position);

            ViewContextActivity holderActivity  = (ViewContextActivity) holder;
            holderActivity.txtStationary.setText(mContext.getString(R.string.txtActivityStationary, String.valueOf(data.value.stationary)));
            holderActivity.txtWalking.setText(mContext.getString(R.string.txtActivityWalking, String.valueOf(data.value.walking)));
            holderActivity.txtRunning.setText(mContext.getString(R.string.txtActivityRunning, String.valueOf(data.value.running)));
            holderActivity.txtRidingBike.setText(mContext.getString(R.string.txtActivityOnBike, String.valueOf(data.value.cycling)));
            holderActivity.txtDriving.setText(mContext.getString(R.string.txtActivityDriving, String.valueOf(data.value.driving)));
            holderActivity.txtUnknown.setText(mContext.getString(R.string.txtActivityUnknown, String.valueOf(data.value.unknown)));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mListOfContextData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public static class ViewContextActivity extends RecyclerView.ViewHolder {

        public TextView txtStationary;
        public TextView txtWalking;
        public TextView txtRunning;
        public TextView txtRidingBike;
        public TextView txtDriving;
        public TextView txtUnknown;

        public ViewContextActivity(View v) {
            super(v);

            txtStationary       = (TextView) v.findViewById(R.id.activityStationary);
            txtWalking          = (TextView) v.findViewById(R.id.activityWalking);
            txtRunning          = (TextView) v.findViewById(R.id.activityRunning);
            txtRidingBike       = (TextView) v.findViewById(R.id.activityRidingBike);
            txtDriving          = (TextView) v.findViewById(R.id.activityDriving);
            txtUnknown          = (TextView) v.findViewById(R.id.activityUnknown);
        }
    }
}