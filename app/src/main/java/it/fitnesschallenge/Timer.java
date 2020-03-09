/**
 * Questo Fragment implementa la funzionalità del timer che viene settato o in base all'esercizio
 * da cui è richiamato oppure è possibile settare un nuovo timer in base ad un valore inserito in
 * secondi, l'implementazione richiama un Service, quindi è possibile passare il fragment in background
 * e comunque continuerà a scandire il tempo rimanente.
 * Utilizza inoltre un Handler che permette la comunicazione tra il service e l'UI thread.
 */
package it.fitnesschallenge;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.ref.WeakReference;

import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.view.PlayingWorkoutModelView;
import it.fitnesschallenge.model.view.TimerViewModel;

import static it.fitnesschallenge.model.SharedConstance.CONVERSION_SEC_IN_MILLIS;
import static it.fitnesschallenge.model.SharedConstance.TIME_FOR_TIMER;

public class Timer extends Fragment {

    private static final String TAG = "Timer";
    private static final int MSG_UPDATE_TIME = 0;
    private static final int MSG_TIMER_FINISH = 1;

    private long mTimeOfTimerInMillis;
    private TextView mRemainingTime;
    private TextInputLayout mNewTimeTimer;
    private ImageButton mStopPlayButton;
    private TimerService mTimerService;
    private boolean mServiceBound;
    private CircularImageView mImageView;
    private TimerViewModel mTimerViewModel;
    private ServiceConnection mConnection;
    private Handler mUpdateTimeHandler = new UIUpdateHandler(this);

    public Timer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
         * {@link = https://gist.github.com/mjohnsullivan/403149218ecb480e7759}
         */
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        ImageButton deleteTimer = view.findViewById(R.id.timer_delete);
        mServiceBound = false;
        PlayingWorkoutModelView mViewModel = ViewModelProviders.of(getActivity()).get(PlayingWorkoutModelView.class);
        mTimerViewModel = ViewModelProviders.of(getActivity()).get(TimerViewModel.class);
        mRemainingTime = view.findViewById(R.id.timer_fragment_remaining_time);
        mNewTimeTimer = view.findViewById(R.id.timer_fragment_new_timer);
        mStopPlayButton = view.findViewById(R.id.timer_play_pause);
        mTimeOfTimerInMillis = mViewModel.getCurrentExercise().getCoolDown() * CONVERSION_SEC_IN_MILLIS;
        Log.d(TAG, "Tempo per il timer ricevuto: " + mTimeOfTimerInMillis);

        if (mTimeOfTimerInMillis > 0)
            setRemainingTime();
        else
            mNewTimeTimer.setVisibility(View.VISIBLE);
        mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);

        mStopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStopPlayButton.getContentDescription().equals(
                        getContext().getString(R.string.stop_timer))) {
                    Log.d(TAG, "Stop ringtone");
                    mTimerService.stopRingtone();
                    mTimerService.cancelTimer();
                    mTimerService = null;
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                } else if (mStopPlayButton.getContentDescription().equals(
                        getContext().getString(R.string.pause_timer))) {
                    mTimeOfTimerInMillis = mTimerService.pauseTimer();
                    mStopPlayButton.setImageResource(R.drawable.ic_play_circle_filled);
                    mStopPlayButton.setContentDescription(getString(R.string.play_timer));
                } else if (mStopPlayButton.getContentDescription().equals(
                        getContext().getString(R.string.play_timer))) {
                    mTimerService.startTimer();
                    mStopPlayButton.setImageResource(R.drawable.ic_pause_circle_filled);
                    mStopPlayButton.setContentDescription(
                            getContext().getString(R.string.pause_timer)
                    );
                }
            }
        });

        deleteTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimerService.cancelTimer();
                mTimerService = null;
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        return view;
    }

    /**
     * Questo override permette di verificare che se il Service è già legato al fragment così da non
     * crearne un altro.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(TAG, "Prelevo il precendente stato");
            mServiceBound = savedInstanceState.getBoolean("IS_SERVICE_BOUND");
        }
    }

    /**
     * onStart ha il compito di creare il Service traminte un Intent, se non è già stato creato, e di
     * collegarlo al Fragment.
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Avvio fragment e servizio connesso");
        mConnection = createConnection();
        if (!mTimerViewModel.isServiceBound()) {
            Log.d(TAG, "Creo nuovo intent");
            Intent serviceIntent = new Intent(getContext(), TimerService.class);
            serviceIntent.putExtra(TIME_FOR_TIMER, mTimeOfTimerInMillis);
            getActivity().startService(serviceIntent);
            getActivity().bindService(serviceIntent, mConnection, 0);
        }
    }

    /**
     * Create connection serve a creare la connessione con i vari metodi di callback tra il fragment
     * e il servizio di Timer.
     *
     * @return ritorna l'istanza della connessione
     */
    private ServiceConnection createConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "Servizio connesso");
                /*
                 * In questo punto avviene la verifica di precedenti servizi creati, se il ViewModel
                 * contiene Memoria di un precedente Servizio allora non ne verrà instanziato uno
                 * nuovo
                 */
                if (mTimerViewModel.getTimerService() == null) {
                    Log.d(TAG, "Timer precendete inesistente");
                    TimerService.RunServiceBinder binder = (TimerService.RunServiceBinder) service;
                    mTimerService = binder.getService();
                    /*
                     * Qui verifichiamo che il tempo passato al timer sia effettivamente un tempo
                     * corretto in millisecondi.
                     */
                    if (mTimeOfTimerInMillis > 0) {
                        mTimerService.startTimer();
                        mTimerService.createNotify();
                        mStopPlayButton.setImageDrawable(
                                getContext()
                                        .getResources()
                                        .getDrawable(R.drawable.ic_pause_circle_filled));
                        mStopPlayButton.setContentDescription(getString(R.string.pause_timer));
                    }
                    mServiceBound = true;
                } else
                    mTimerService = mTimerViewModel.getTimerService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "Servizio scollegato");
                /*
                 * Questo messaggio è inviato all'UIHandler che setta l'ui in modo da fornire un feedback
                 * sulla fine del timer.
                 */
                mUpdateTimeHandler.sendEmptyMessage(MSG_TIMER_FINISH);
                mServiceBound = false;
            }
        };
    }

    /**
     * Questo metodo viene richiamato ogni secondo dall handler, il quale viene richiamato dal servizio
     * e serve per mostrare il count down all'utente
     */
    private void updateTimerUi() {
        if (mTimerService != null)
            mRemainingTime.setText(PersonalExercise
                    .getCoolDownString(mTimerService.getRemainingTime()));
    }

    /**
     * Questo metodo imposta l'Ui in modo da mostrare che il timer può essere stoppato, viene richiamato
     * quando il count down è terminato.
     */
    private void stopTimerUi() {
        mStopPlayButton.setImageDrawable(getContext()
                .getResources()
                .getDrawable(R.drawable.ic_stop_48dp));
        mStopPlayButton.setContentDescription(getString(R.string.stop_timer));
    }

    /**
     * Questa classe interana estende la classe del SO Handler che permette di mettere in comunicazione
     * thread differenti tramite dei processi interni che coinvolgono la classe del SO Looper, la
     * comunicazione tra porcessi avviene tramite l'invio di messaggi inter-processo.
     */
    public static class UIUpdateHandler extends Handler {

        private final static int UPDATE_RATE_MS = 1000;
        /*
         * Questa classe WeakReference permette appunto di creare un riferimento tra processi senza
         * generare memory leaks, in quanto se il processo a cui si riferisce muore, allora il
         * collegamento viene interrotto e il Garbage Collector può recuperare la memoria precedentemente
         * occupata dal thred killato
         */
        private final WeakReference<Timer> reference;

        UIUpdateHandler(Timer reference) {
            this.reference = new WeakReference<>(reference);
        }

        /**
         * Questo metodo gestisce i messaggi che vengono inviati dai thread collegati, viene fatta
         * una selezione in base al messaggio ricevuto, che in questo caso sono:
         * MSG_UPDATE_TIME: viene richiamato ogni tick del timer
         * MSG_TIMER_FINISH: viene richiamato quando il cont down è terminato e bisogna impostare la
         * UI in modo da interromperre la "suoneria" di feedback.
         * @param message
         */
        @Override
        public void handleMessage(@NonNull Message message) {
            if (MSG_UPDATE_TIME == message.what) {
                reference.get().updateTimerUi();
                sendEmptyMessageDelayed(MSG_UPDATE_TIME, UPDATE_RATE_MS);
            } else if (MSG_TIMER_FINISH == message.what) {
                reference.get().stopTimerUi();
                sendEmptyMessage(MSG_TIMER_FINISH);
            }
        }
    }

    private void setRemainingTime() {
        mRemainingTime.setText(PersonalExercise.getCoolDownString(mTimeOfTimerInMillis));
    }

    /**
     * onPause salva il service creato in per quando sarà necessario ripristinarlo senza crearne uno
     * nuovo.
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Chiamato onPause");
        mTimerViewModel.setTimerService(mTimerService);
        getActivity().unbindService(mConnection);
        mServiceBound = false;
    }
}
