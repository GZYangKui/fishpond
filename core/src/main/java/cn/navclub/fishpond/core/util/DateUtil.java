package cn.navclub.fishpond.core.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class DateUtil {
    public static String formatDateTime(Long timestamp, String format) {
        if (timestamp == null) {
            return "";
        }
        var date = Date.from(Instant.ofEpochMilli(timestamp));
        var sf = new SimpleDateFormat(format);
        return sf.format(date);
    }
}
