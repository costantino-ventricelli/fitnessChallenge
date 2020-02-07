package it.fitnesschallenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;


public class Home extends Fragment {

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        MaterialButton entreGymButton = view.findViewById(R.id.enterGym);
        MaterialButton startOutdoor = view.findViewById(R.id.startOutdoorWorkout);
        MaterialButton login = view.findViewById(R.id.trainerLogin);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
