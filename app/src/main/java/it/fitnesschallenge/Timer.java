package it.fitnesschallenge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static it.fitnesschallenge.model.SharedConstance.TIME_FOR_TIMER;

public class Timer extends Fragment {

    private static final String TAG = "Timer";

    public Timer() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(getContext(), TimerService.class);
        serviceIntent.putExtra(TIME_FOR_TIMER, 0);
        getActivity().startService(serviceIntent);
        Log.d(TAG, "Ho avviato il service");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO: bisogna trovare un modo per far comunicare l'UI thread con il service.
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Scollego il service");
    }
}
