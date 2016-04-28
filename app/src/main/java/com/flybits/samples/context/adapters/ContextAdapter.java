package com.flybits.samples.context.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flybits.core.api.context.BasicData;
import com.flybits.core.api.context.plugins.activity.ActivityData;
import com.flybits.core.api.context.plugins.battery.BatteryLifeData;
import com.flybits.core.api.context.plugins.beacon.EddyStone;
import com.flybits.core.api.context.plugins.beacon.IBeacon;
import com.flybits.core.api.context.plugins.carrier.CarrierData;
import com.flybits.core.api.context.plugins.fitness.FitnessData;
import com.flybits.core.api.context.plugins.language.LanguageData;
import com.flybits.core.api.context.plugins.location.LocationData;
import com.flybits.core.api.context.plugins.network.NetworkData;
import com.flybits.samples.context.R;

import java.util.ArrayList;

public class ContextAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ACTIVITY          = 0;
    private static final int TYPE_BATTERY           = 1;
    private static final int TYPE_BEACON_IBEACON    = 2;
    private static final int TYPE_BEACON_EDDYSTONE  = 3;
    private static final int TYPE_CARRIER           = 4;
    private static final int TYPE_FITNESS           = 5;
    private static final int TYPE_LANGUAGE          = 6;
    private static final int TYPE_LOCATION          = 7;
    private static final int TYPE_NETWORK           = 8;

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
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_battery, parent, false);
            return new ViewContextBattery(v);
        }else if (code == TYPE_BEACON_EDDYSTONE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_BEACON_IBEACON) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_CARRIER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrier, parent, false);
            return new ViewContextCarrier(v);
        }else if (code == TYPE_FITNESS) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_LANGUAGE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
            return new ViewContextLanguage(v);
        }else if (code == TYPE_LOCATION) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ViewContextActivity(v);
        }else if (code == TYPE_NETWORK) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_network, parent, false);
            return new ViewContextNetwork(v);
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
        }else if (holder instanceof ViewContextBattery) {

            BasicData<BatteryLifeData> data    = mListOfContextData.get(position);

            ViewContextBattery holderActivity  = (ViewContextBattery) holder;
            holderActivity.txtIsCharging.setText(mContext.getString(R.string.txtBatteryIsCharging, String.valueOf(data.value.isCharging)));
            holderActivity.txtPercentage.setText(mContext.getString(R.string.txtBatteryPercentage, data.value.percentage));
        }else if (holder instanceof ViewContextCarrier) {

            BasicData<CarrierData> data    = mListOfContextData.get(position);

            ViewContextCarrier holderActivity  = (ViewContextCarrier) holder;
            holderActivity.txtMCC.setText(mContext.getString(R.string.txtCarrierMCC, data.value.mcc));
            holderActivity.txtMNC.setText(mContext.getString(R.string.txtCarrierMNC, data.value.mnc));
        }else if (holder instanceof ViewContextLanguage) {

            BasicData<LanguageData> data    = mListOfContextData.get(position);

            ViewContextLanguage holderActivity  = (ViewContextLanguage) holder;
            holderActivity.txtLanguage.setText(mContext.getString(R.string.txtLanguageCode, data.value.language));
        }else if (holder instanceof ViewContextNetwork) {

            BasicData<NetworkData> data    = mListOfContextData.get(position);

            ViewContextNetwork holderActivity  = (ViewContextNetwork) holder;
            holderActivity.txtNetworkIsConnected.setText(mContext.getString(R.string.txtNetworkIsConnected, String.valueOf(data.value.isConnected)));

            String ssid = (data.value.ssid == null)? "Not Connected To WiFi" : data.value.ssid;
            holderActivity.txtNetworkSSID.setText(mContext.getString(R.string.txtNetworkSSID, ssid));

            String connectionType;
            switch ((int)data.value.connectionType){
                case 1:
                    connectionType = "WiFi";
                    break;
                case 2:
                    connectionType = "2G";
                    break;
                case 3:
                    connectionType = "3G";
                    break;
                case 4:
                    connectionType = "4G";
                    break;
                case -1:
                    connectionType = "Not Connected To Internet";
                    break;
                default:
                    connectionType = "Unknown";
            }
            holderActivity.txtNetworkConnectionType.setText(mContext.getString(R.string.txtNetworkConnectionType, connectionType));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mListOfContextData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mListOfContextData.get(position).value instanceof ActivityData){
            return TYPE_ACTIVITY;
        }else if (mListOfContextData.get(position).value instanceof BatteryLifeData){
            return TYPE_BATTERY;
        }else if (mListOfContextData.get(position).value instanceof IBeacon){
            return TYPE_BEACON_IBEACON;
        }else if (mListOfContextData.get(position).value instanceof EddyStone){
            return TYPE_BEACON_EDDYSTONE;
        }else if (mListOfContextData.get(position).value instanceof FitnessData){
            return TYPE_FITNESS;
        }else if (mListOfContextData.get(position).value instanceof LanguageData){
            return TYPE_LANGUAGE;
        }else if (mListOfContextData.get(position).value instanceof LocationData){
            return TYPE_LOCATION;
        }else if (mListOfContextData.get(position).value instanceof NetworkData){
            return TYPE_NETWORK;
        }else if (mListOfContextData.get(position).value instanceof CarrierData){
            return TYPE_CARRIER;
        }
        return TYPE_ACTIVITY;
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

    public static class ViewContextBattery extends RecyclerView.ViewHolder {

        public TextView txtIsCharging;
        public TextView txtPercentage;

        public ViewContextBattery(View v) {
            super(v);

            txtIsCharging       = (TextView) v.findViewById(R.id.batteryIsCharging);
            txtPercentage       = (TextView) v.findViewById(R.id.batteryPercentage);
        }
    }

    public static class ViewContextCarrier extends RecyclerView.ViewHolder {

        public TextView txtMNC;
        public TextView txtMCC;

        public ViewContextCarrier(View v) {
            super(v);

            txtMNC       = (TextView) v.findViewById(R.id.carrierMNC);
            txtMCC       = (TextView) v.findViewById(R.id.carrierMCC);
        }
    }

    public static class ViewContextLanguage extends RecyclerView.ViewHolder {

        public TextView txtLanguage;

        public ViewContextLanguage(View v) {
            super(v);

            txtLanguage       = (TextView) v.findViewById(R.id.languageCode);
        }
    }

    public static class ViewContextNetwork extends RecyclerView.ViewHolder {

        public TextView txtNetworkIsConnected;
        public TextView txtNetworkSSID;
        public TextView txtNetworkConnectionType;

        public ViewContextNetwork(View v) {
            super(v);

            txtNetworkIsConnected       = (TextView) v.findViewById(R.id.networkIsConnected);
            txtNetworkSSID              = (TextView) v.findViewById(R.id.networkSSID);
            txtNetworkConnectionType    = (TextView) v.findViewById(R.id.networkConnectionType);
        }
    }
}