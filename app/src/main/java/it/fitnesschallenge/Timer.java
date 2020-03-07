package it.fitnesschallenge;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.lang.ref.WeakReference;
import java.util.Locale;

import it.fitnesschallenge.model.view.PlayingWorkoutModelView;

import static it.fitnesschallenge.model.SharedConstance.CONVERSION_SEC_IN_MILLIS;
import static it.fitnesschallenge.model.SharedConstance.TIME_FOR_TIMER;

public class Timer extends Fragment {

    private static final String TAG = "Timer";
    private final static int MSG_UPDATE_TIME = 0;

    private long mTimeOfTimerInMillis;
    private PlayingWorkoutModelView mViewModel;
    private TextView mRemainingTime;
    private TextInputLayout mNewTimeTimer;
    private TimerService mTimerService;
    private boolean mServiceBound;
    private Handler mUpdateTimeHandler = new UIUpdateHandler(this);

    public Timer() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Avvio fragment e servizio connesso");
        Intent serviceIntent = new Intent(getContext(), TimerService.class);
        serviceIntent.putExtra(TIME_FOR_TIMER, mTimeOfTimerInMillis);
        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, mConnection, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
         * TODO: Il timer non si ferma mai, bisogna suddividere i metodi di avvio, stop e creazione
         * notifica @link = "https://gist.github.com/mjohnsullivan/403149218ecb480e7759"
         */
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        mViewModel = ViewModelProviders.of(getActivity()).get(PlayingWorkoutModelView.class);
        mRemainingTime = view.findViewById(R.id.timer_fragment_remaining_time);
        mNewTimeTimer = view.findViewById(R.id.timer_fragment_new_timer);
        mTimeOfTimerInMillis = mViewModel.getCurrentExercise().getCoolDown() * CONVERSION_SEC_IN_MILLIS;
        Log.d(TAG, "Tempo per il timer ricevuto: " + mTimeOfTimerInMillis);
        if (mTimeOfTimerInMillis > 0)
            setRemainingTime();
        else
            mNewTimeTimer.setVisibility(View.VISIBLE);
        mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
        return view;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Servizio connesso");
            TimerService.RunServiceBinder binder = (TimerService.RunServiceBinder) service;
            mTimerService = binder.getService();
            if (mTimeOfTimerInMillis > 0) {
                mTimerService.startTimer();
                mTimerService.createNotify();
            }
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Servizio scollegato");
            //TODO: dopo aver scollegato il servizio richiamo handler per permettere di zittire il timer
            mServiceBound = false;
        }
    };

    private void updateTimerUi() {
        if (mTimerService != null)
            mRemainingTime.setText(mTimerService.getRemainingTimeInString());
    }

    /**
     * Studiare con cura Handler
     */
    private static class UIUpdateHandler extends Handler {

        private final static int UPDATE_RATE_MS = 1000;
        private final WeakReference<Timer> reference;

        UIUpdateHandler(Timer reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(@NonNull Message message) {
            if (MSG_UPDATE_TIME == message.what) {
                reference.get().updateTimerUi();
                sendEmptyMessageDelayed(MSG_UPDATE_TIME, UPDATE_RATE_MS);
            }
        }
    }

    private void setRemainingTime() {
        int minutes = (int) (mTimeOfTimerInMillis / CONVERSION_SEC_IN_MILLIS) / 60;
        int seconds = (int) (mTimeOfTimerInMillis / CONVERSION_SEC_IN_MILLIS) % 60;
        Log.d(TAG, "Minutes: " + minutes);
        Log.d(TAG, "Seconds: " + seconds);
        mRemainingTime.setText(String.format(
                Locale.getDefault(), "%02d:%02d", minutes, seconds
        ));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mConnection);
    }
}
