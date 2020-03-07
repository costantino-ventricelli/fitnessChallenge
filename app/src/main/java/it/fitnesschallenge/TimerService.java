package it.fitnesschallenge;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Locale;

import it.fitnesschallenge.model.room.entity.PersonalExercise;

import static it.fitnesschallenge.App.CHANNEL_ID;
import static it.fitnesschallenge.model.SharedConstance.CONVERSION_SEC_IN_MILLIS;
import static it.fitnesschallenge.model.SharedConstance.TIME_FOR_TIMER;

public class TimerService extends Service {

    private static final String TAG = "TimerService";
    private static final int NOTIFICATION_ID = 1;

    private long mTimeLeftInMillis;
    private boolean isTimerRunning;
    private final IBinder binder = new RunServiceBinder();
    private NotificationManagerCompat mNotificationCompactManager;
    private NotificationCompat.Builder mNotificationCompactBuilder;
    private Notification mNotification;
    private PendingIntent mPendingIntent;
    private CountDownTimer mCountDownTimer;
    private Ringtone mRingtone;

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
        mPendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        mTimeLeftInMillis = intent.getLongExtra(TIME_FOR_TIMER, 0);
        return START_STICKY;
    }

    public void startTimer() {
        isTimerRunning = true;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "Tempo rimanente: " + mTimeLeftInMillis);
                mTimeLeftInMillis = millisUntilFinished;
                mNotificationCompactBuilder.setContentText(PersonalExercise.getCoolDownString(mTimeLeftInMillis));
                mNotification = mNotificationCompactBuilder.build();
                mNotificationCompactManager.notify(NOTIFICATION_ID, mNotification);
            }

            @Override
            public void onFinish() {
                stopSelf();
                Log.d(TAG, "Start ringtone");
                Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                mRingtone = RingtoneManager.getRingtone(getApplicationContext(),
                        defaultUri);
                mRingtone.play();
                cancelNotify();
            }
        }.start();
    }

    public void pauseTimer() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
    }

    public void stopRingtone() {
        if (mRingtone != null)
            mRingtone.stop();
    }

    public void createNotify() {
        mNotificationCompactManager = NotificationManagerCompat.from(this);

        mNotificationCompactBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle("Remaining time")
                .setContentText(PersonalExercise.getCoolDownString(mTimeLeftInMillis))
                .setContentIntent(mPendingIntent);
        mNotification = mNotificationCompactBuilder.build();

        mNotificationCompactManager.notify(NOTIFICATION_ID, mNotification);

        startForeground(NOTIFICATION_ID, mNotification);
    }

    private void cancelNotify() {
        mNotificationCompactManager.cancel(NOTIFICATION_ID);
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public long getRemainingTime() {
        return mTimeLeftInMillis;
    }

    class RunServiceBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }
}
