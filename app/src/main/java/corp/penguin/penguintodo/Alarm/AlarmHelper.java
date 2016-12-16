package corp.penguin.penguintodo.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import corp.penguin.penguintodo.model.ModelTask;

/**
 * Created by vladislav on 02.03.16.
 */
public class AlarmHelper {
    private static AlarmHelper instance;
    private Context context;
    private AlarmManager alarmManager;

    public static AlarmHelper getInstance() {
        if (instance == null) {
            instance = new AlarmHelper();
        }

        return instance;
    }

    public void initialize (Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm (ModelTask task) {
        Intent intent  = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", task.getTitle());
        intent.putExtra("time_stamp", task.getTimeStamp());
        intent.putExtra("color", task.getPriorityColor());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                (int) task.getTimeStamp(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDate(), pendingIntent);

    }

    public void removeAlarm (long taskTimeStamp) {
        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) taskTimeStamp,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }
}