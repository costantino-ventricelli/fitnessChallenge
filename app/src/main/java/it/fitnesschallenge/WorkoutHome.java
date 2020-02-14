package it.fitnesschallenge;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

import static it.fitnesschallenge.model.SharedConstance.TRAINING_LIST_HOME;
import static it.fitnesschallenge.model.SharedConstance.WORKOUT_FRAGMENT;
import static it.fitnesschallenge.model.SharedConstance.WORKOUT_STATISTICS_FRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutHome extends Fragment {


    private MaterialButton openStatistics;
    private MaterialButton openTrainingList;
    private MaterialButton openCreateNewList;

    public WorkoutHome() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.setCurrentFragment(WORKOUT_FRAGMENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workout_home, container, false);

        openStatistics = view.findViewById(R.id.openStatistics);
        openTrainingList = view.findViewById(R.id.openTrainingList);

        openStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutStatistics workoutStatistics = new WorkoutStatistics();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                        R.anim.enter_from_rigth, R.anim.exit_from_left);
                transaction.replace(R.id.fragmentContainer, workoutStatistics, WORKOUT_STATISTICS_FRAGMENT)
                        .addToBackStack(WORKOUT_STATISTICS_FRAGMENT)
                        .commit();
            }
        });

        openTrainingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainingListHome trainingListHome = new TrainingListHome();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                        R.anim.enter_from_rigth, R.anim.exit_from_left);
                transaction.replace(R.id.fragmentContainer, trainingListHome, TRAINING_LIST_HOME)
                        .addToBackStack(TRAINING_LIST_HOME)
                        .commit();
            }
        });

        return view;
    }
}
