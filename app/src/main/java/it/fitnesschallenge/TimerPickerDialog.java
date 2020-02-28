package it.fitnesschallenge;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class TimerPickerDialog extends AlertDialog implements OnTimerSetListener {

    private OnTimerSetListener mOnTimerSetListener;
    private int hours;
    private int minutes;
    private int seconds;

    protected TimerPickerDialog(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.timer_picker_dialog, null);
        setView(view);

    }

    @Override
    public void onTimerSetListener(View view, int hours, int minutes, int seconds) {

    }
}
