package it.fitnesschallenge;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadNewWorkout extends Fragment {


    public UploadNewWorkout() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_new_workout, container, false);
        MaterialButton uploadButton = view.findViewById(R.id.upload_new_work_out_finish_button);
        return view;
    }

}
