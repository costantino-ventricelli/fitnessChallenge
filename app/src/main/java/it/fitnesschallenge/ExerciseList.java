package it.fitnesschallenge;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import it.fitnesschallenge.adapter.ShowAdapter;
import it.fitnesschallenge.model.room.WorkoutWithExercise;
import it.fitnesschallenge.model.view.CreationViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExerciseList extends Fragment {

    private CreationViewModel mCreationViewModel;
    private WorkoutWithExercise mWorkoutWithExercise;
    private ShowAdapter mShowAdapter;
    private RecyclerView mRicyclerView;
    private TextView mMessageView;
    private static final String TAG = "ExerciseList";


    public ExerciseList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_execise_list, container, false);

        mRicyclerView = view.findViewById(R.id.show_exercise_list);
        mMessageView = view.findViewById(R.id.exercise_list_message);

        mCreationViewModel = ViewModelProviders.of(getActivity()).get(CreationViewModel.class);
        mCreationViewModel.getWorkoutId().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(final Integer integer) {
                if(integer != -1){
                    mCreationViewModel.getWorkoutWithExercise(integer).observe(getViewLifecycleOwner(), new Observer<WorkoutWithExercise>() {
                        @Override
                        public void onChanged(WorkoutWithExercise workoutWithExercise) {
                            mWorkoutWithExercise = workoutWithExercise;
                            mRicyclerView.setVisibility(View.VISIBLE);
                            mMessageView.setVisibility(View.GONE);
                            Log.d(TAG, "Lista esercizi ottenuta, workout id: " + integer.toString());
                        }
                    });
                }else {
                    Log.d(TAG, "Lista esercizi non ottenuta, workout id: " + integer.toString());
                    mRicyclerView.setVisibility(View.GONE);
                    mMessageView.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

}
