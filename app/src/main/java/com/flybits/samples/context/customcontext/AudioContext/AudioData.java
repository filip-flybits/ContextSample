package com.flybits.samples.context.customcontext.AudioContext;

import android.os.Parcel;
import android.os.Parcelable;

import com.flybits.core.api.context.v2.ContextData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Filip on 6/20/2016.
 */
public class AudioData extends ContextData implements Parcelable {

    public Boolean isHeadsetPluggedIn = false;
    public Boolean isBluetoothA2dpOn = false;
    public Boolean isBluetoothScoOn = false;
    public Double ringerVolume = 0.0, mediaVolume = 0.0, alarmVolume = 0.0;

    public AudioData()
    {
    }

    protected AudioData(Parcel in) {
        this.isHeadsetPluggedIn = in.readInt() == 1;
        this.isBluetoothA2dpOn = in.readInt() == 1;
        this.isBluetoothScoOn = in.readInt() == 1;
        this.ringerVolume = in.readDouble();
        this.mediaVolume = in.readDouble();
        this.alarmVolume = in.readDouble();
    }

    public static final Creator<AudioData> CREATOR = new Creator<AudioData>() {
        @Override
        public AudioData createFromParcel(Parcel in) {
            return new AudioData(in);
        }

        @Override
        public AudioData[] newArray(int size) {
            return new AudioData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isHeadsetPluggedIn ? 1 : 0);
        dest.writeInt(isBluetoothA2dpOn ? 1 : 0);
        dest.writeInt(isBluetoothScoOn ? 1 : 0);
        dest.writeDouble(ringerVolume);
        dest.writeDouble(mediaVolume);
        dest.writeDouble(alarmVolume);
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof AudioData) {
            AudioData that = (AudioData) o;

            return isBluetoothA2dpOn.equals(that.isBluetoothA2dpOn)
                    && isBluetoothScoOn.equals(that.isBluetoothScoOn)
                    && ringerVolume.equals(that.ringerVolume)
                    && mediaVolume.equals(that.mediaVolume)
                    && alarmVolume.equals(that.alarmVolume);
        }
        else
            return false;
    }

    @Override
    public void fromJson(String json) {
        try {
            JSONObject jsonObj  = new JSONObject(json);
            isHeadsetPluggedIn       = jsonObj.getBoolean("isHeadsetPluggedIn");
            isBluetoothA2dpOn        = jsonObj.getBoolean("isBluetoothA2dpOn");
            isBluetoothScoOn         = jsonObj.getBoolean("isBluetoothScoOn");

            ringerVolume             = jsonObj.getDouble("ringerVolume");
            mediaVolume              = jsonObj.getDouble("mediaVolume");
            alarmVolume              = jsonObj.getDouble("alarmVolume");

        }catch (JSONException exception){}
    }

    @Override
    public String toJson() {
        JSONObject object=new JSONObject();
        try {
            object.put("isHeadsetPluggedIn", isHeadsetPluggedIn);
            object.put("isBluetoothA2dpOn", isBluetoothA2dpOn);
            object.put("isBluetoothScoOn", isBluetoothScoOn);

            object.put("ringerVolume", ringerVolume);
            object.put("mediaVolume", mediaVolume);
            object.put("alarmVolume", alarmVolume);
        }catch (JSONException exception){}

        return object.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
