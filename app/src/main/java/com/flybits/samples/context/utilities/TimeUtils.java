package com.flybits.samples.context.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static String getTimeAsString(long timeInMilli){
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss - yyyy-MM-dd ");
        Date gmt = new Date(timeInMilli);
        return formatter.format(gmt);
    }
}
