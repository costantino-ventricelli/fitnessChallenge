package it.fitnesschallenge;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

import it.fitnesschallenge.model.User;

import static it.fitnesschallenge.model.SharedConstance.EDIT_LIST_FRAGMENT;
import static it.fitnesschallenge.model.SharedConstance.WORKOUT_FRAGMENT;
import static it.fitnesschallenge.model.SharedConstance.WORKOUT_LIST_FRAGMENT;
import static it.fitnesschallenge.model.SharedConstance.WORKOUT_STATISTICS_FRAGMENT;

public class WorkoutHome extends Fragment {

    private static final String USER = "user";
    private User mUser;

    public WorkoutHome() {
        // Required empty public constructor
    }

    static WorkoutHome newInstance(User user) {
        WorkoutHome fragment = new WorkoutHome();
        Bundle args = new Bundle();
        args.putParcelable(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = getArguments().getParcelable(USER);
        }
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

        MaterialButton openStatistics = view.findViewById(R.id.openStatistics);
        MaterialButton openTrainingList = view.findViewById(R.id.openTrainingList);
        MaterialButton openCreateNewList = view.findViewById(R.id.workout_home_create_new_workout);

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
                WorkoutList workoutList = WorkoutList.newInstance(mUser);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                        R.anim.enter_from_rigth, R.anim.exit_from_left);
                transaction.replace(R.id.fragmentContainer, workoutList, WORKOUT_LIST_FRAGMENT)
                        .addToBackStack(WORKOUT_LIST_FRAGMENT)
                        .commit();
            }
        });

        openCreateNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditList editList = new EditList();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                        R.anim.enter_from_rigth, R.anim.exit_from_left)
                        .replace(R.id.fragmentContainer, editList, EDIT_LIST_FRAGMENT)
                        .addToBackStack(EDIT_LIST_FRAGMENT)
                        .commit();
            }
        });

        return view;
    }
}
