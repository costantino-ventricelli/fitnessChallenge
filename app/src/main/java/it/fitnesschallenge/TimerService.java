package it.fitnesschallenge;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Locale;

import static it.fitnesschallenge.App.CHANNEL_ID;

public class TimerService extends Service {

    private static final String TAG = "TimerService";
    private static final int NOTIFICATION_ID = 1;

    private long mTimeLeftInMillis;
    private boolean isTimerRunning;
    private final IBinder binder = new RunServiceBinder();
    private NotificationManagerCompat mNotificationCompactManager;
    private NotificationCompat.Builder mNotificationCompactBuilder;
    private Notification mNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Creo il service");
        isTimerRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Bind tra activity e service");
        return binder;
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        mTimeLeftInMillis = 5000;
        isTimerRunning = true;
        mNotificationCompactManager = NotificationManagerCompat.from(this);

        mNotificationCompactBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle("Remaining time")
                .setContentText("1:20");
        mNotification = mNotificationCompactBuilder.build();

        mNotificationCompactManager.notify(NOTIFICATION_ID, mNotification);

        startForeground(NOTIFICATION_ID, mNotification);

        new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                mNotificationCompactBuilder.setContentText(getRemainingTimeInString());
                mNotification = mNotificationCompactBuilder.build();
                mNotificationCompactManager.notify(NOTIFICATION_ID, mNotification);
            }

            @Override
            public void onFinish() {
                stopService(intent);
            }
        }.start();
        return START_STICKY;
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public String getRemainingTimeInString() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        return String.format(
                Locale.getDefault(), "%02d:%02d", minutes, seconds
        );
    }

    public class RunServiceBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }
}
