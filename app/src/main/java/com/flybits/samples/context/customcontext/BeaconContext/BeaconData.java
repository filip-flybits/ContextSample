package com.flybits.samples.context.customcontext.BeaconContext;

import android.os.Parcel;
import android.os.Parcelable;

import com.flybits.core.api.context.v2.ContextData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Filip on 6/20/2016.
 */
public class BeaconData extends ContextData implements Parcelable {

    public static final int TYPE_IBEACON    = 1;
    public static final int TYPE_EDDYSTONE  = 2;

    public int type;
    public String instance;
    public String namespace;

    public String majorID, minorID;
    public String uuid;

    public BeaconData(String instance, String namespace)
    {
        this.instance = instance;
        this.namespace = namespace;
        this.type = TYPE_EDDYSTONE;
    }

    public BeaconData(String majorID, String minorID, String uuid)
    {
        this.majorID = majorID;
        this.minorID = minorID;
        this.uuid = uuid;
        this.type = TYPE_IBEACON;
    }

    protected BeaconData(Parcel in) {
        type = in.readInt();
        switch (type)
        {
            case TYPE_IBEACON:
                majorID = in.readString();
                minorID = in.readString();
                uuid = in.readString();
                break;
            case TYPE_EDDYSTONE:
                instance = in.readString();
                namespace = in.readString();
                break;
        }
    }

    public static final Creator<BeaconData> CREATOR = new Creator<BeaconData>() {
        @Override
        public BeaconData createFromParcel(Parcel in) {
            return new BeaconData(in);
        }

        @Override
        public BeaconData[] newArray(int size) {
            return new BeaconData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       dest.writeInt(type);
       switch (type)
       {
           case TYPE_IBEACON:
               dest.writeString(majorID);
               dest.writeString(minorID);
               dest.writeString(uuid);
               break;
           case TYPE_EDDYSTONE:
               dest.writeString(instance);
               dest.writeString(namespace);
               break;
       }
    }

    @Override
    public int hashCode() {
        switch (type)
        {
            case TYPE_IBEACON:
                return uuid.hashCode();
            case TYPE_EDDYSTONE:
                return instance.hashCode();
        }

        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof BeaconData) {
            BeaconData that = (BeaconData) o;
            if (type == that.type)
            {
                switch (type)
                {
                    case TYPE_IBEACON:
                        if (majorID.equals(that.majorID) && minorID.equals(that.minorID) && uuid.equals(that.uuid))
                            return true;
                        break;
                    case TYPE_EDDYSTONE:
                        if (instance.equals(that.instance) && namespace.equals(that.namespace))
                            return true;
                        break;
                }
            }
            return false;
        }
        else
            return false;
    }

    @Override
    public void fromJson(String json) {
        try {
            JSONObject jsonObj  = new JSONObject(json);

            type = jsonObj.getInt("type");
            switch (type)
            {
                case TYPE_IBEACON:
                    majorID = jsonObj.getString("majorID");
                    minorID = jsonObj.getString("minorID");
                    uuid = jsonObj.getString("uuid");
                    break;
                case TYPE_EDDYSTONE:
                    instance = jsonObj.getString("instance");
                    namespace = jsonObj.getString("namespace");
                    break;
            }

        }catch (JSONException exception){}
    }

    @Override
    public String toJson() {
        JSONObject object=new JSONObject();
        try {

            switch (type)
            {
                case TYPE_IBEACON:
                    object.put("majorID", majorID);
                    object.put("minorID", minorID);
                    object.put("uuid", uuid);
                    object.put("type", type);
                    break;
                case TYPE_EDDYSTONE:
                    object.put("instance", instance);
                    object.put("namespace", namespace);
                    object.put("type", type);
                    break;
            }

        }catch (JSONException exception){}

        return object.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
