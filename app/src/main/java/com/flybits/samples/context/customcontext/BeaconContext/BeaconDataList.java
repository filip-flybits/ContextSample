package com.flybits.samples.context.customcontext.BeaconContext;

import android.os.Parcel;
import android.os.Parcelable;

import com.flybits.core.api.context.v2.ContextData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.altbeacon.beacon.Beacon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Filip on 6/20/2016.
 */
public class BeaconDataList extends ContextData implements Parcelable {

    public BeaconData[] data;

    public BeaconDataList(Set<BeaconData> list)
    {
        data = new BeaconData[list.size()];
        this.data = list.toArray(data);
    }

    protected BeaconDataList(Parcel in) {
        Parcelable[] parcelableArray =
                in.readParcelableArray(BeaconDataList.class.getClassLoader());

        if (parcelableArray != null) {
            data = Arrays.copyOf(parcelableArray, parcelableArray.length, BeaconData[].class);
        }
    }

    public static final Creator<BeaconDataList> CREATOR = new Creator<BeaconDataList>() {
        @Override
        public BeaconDataList createFromParcel(Parcel in) {
            return new BeaconDataList(in);
        }

        @Override
        public BeaconDataList[] newArray(int size) {
            return new BeaconDataList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

       dest.writeParcelableArray(data, 0);
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof BeaconDataList) {
            BeaconDataList that = (BeaconDataList) o;
            return data.equals(that.data);
        }
        else
            return false;
    }

    @Override
    public void fromJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<BeaconData>>() {}.getType();
        List<BeaconData> beaconData = gson.fromJson(json, type);

        data = new BeaconData[beaconData.size()];
        data = beaconData.toArray(data);
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<BeaconData>>() {}.getType();
        String json = gson.toJson(data);

        return json;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
