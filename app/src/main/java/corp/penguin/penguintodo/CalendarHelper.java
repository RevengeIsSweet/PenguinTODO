package corp.penguin.penguintodo;

import java.text.SimpleDateFormat;

/**
 * Created by vladislav on 24.02.16.
 */
public class CalendarHelper {

    public static String getDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);

    }

    public static String getTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(time);
    }

    public static String getStringDate(long date) {
        SimpleDateFormat stringDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return stringDateFormat.format(date);
    }
}
