package it.fitnesschallenge;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UploadWorkoutOnFireBase extends Fragment {

    public UploadWorkoutOnFireBase() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upload_workout_on_fire_base, container, false);
        ProgressBar progressBar = view.findViewById(R.id.upload_new_work_out_progressbar);
        TextView percent = view.findViewById(R.id.upload_workout_percent);

        //TODO: capire come fare l'uplaod su firebase
        return view;
    }
}
