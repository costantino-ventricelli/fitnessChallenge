package it.fitnesschallenge;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import it.fitnesschallenge.timepicker.OnTimerSetListener;
import it.fitnesschallenge.timepicker.TimerPickerDialog;

import static android.app.Activity.RESULT_OK;
import static it.fitnesschallenge.model.SharedConstance.POSITION_IN_ADAPTER;
import static it.fitnesschallenge.model.SharedConstance.SELECTED_TIMER;

public class TimerPickerFragment extends AppCompatDialogFragment {

    private int mPosition;

    public TimerPickerFragment(int position) {
        this.mPosition = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new TimerPickerDialog(getActivity(), new OnTimerSetListener() {
            @Override
            public void onTimerSetListener(View view, int hours, int minutes, int seconds) {
                Intent intent = new Intent();
                intent.putExtra(SELECTED_TIMER, getTimeInSeconds(hours, minutes, seconds));
                intent.putExtra(POSITION_IN_ADAPTER, mPosition);
                getTargetFragment().onActivityResult(getTargetRequestCode(),
                        RESULT_OK,
                        intent);
            }
        });
    }

    private Long getTimeInSeconds(int hours, int minutes, int seconds) {
        return (long) (hours * 3600 + minutes * 60 + seconds);
    }
}
