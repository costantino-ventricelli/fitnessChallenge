package it.fitnesschallenge;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.view.PlayingWorkoutModelView;


public class PlayingWorkout extends Fragment {

    private ImageButton mNext;
    private ImageButton mPrev;
    private TextView mNextText;
    private TextView mPrevText;
    private Exercise mCurrentExercise;
    private PlayingWorkoutModelView mViewModel;
    private ProgressBar mProgressBar;
    private TextView mProgressValue;

    public PlayingWorkout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playing_workout, container, false);

        MaterialButton stopButton = view.findViewById(R.id.playing_workout_stop_workout);
        TextView exerciseTitle = view.findViewById(R.id.playing_workout_title);
        ImageView exerciseImage = view.findViewById(R.id.playing_workout_image);
        TextView timeTimer = view.findViewById(R.id.playing_workout_timer);
        ImageButton startTimer = view.findViewById(R.id.playing_workout_start_timer);
        TextView currentRepetition = view.findViewById(R.id.playing_workout_repetition);
        TextView lastTime = view.findViewById(R.id.playing_workout_last_time);
        ImageButton infoButton = view.findViewById(R.id.playing_workout_info);
        Button addWeight = view.findViewById(R.id.playing_exercise_add_weigth);
        mNext = view.findViewById(R.id.next_exercise);
        mNextText = view.findViewById(R.id.next_exercise_label);
        mPrev = view.findViewById(R.id.prev_exercise);
        mPrevText = view.findViewById(R.id.prev_exercise_label);
        mProgressBar = view.findViewById(R.id.playing_workout_progress_bar);
        mProgressValue = view.findViewById(R.id.progress_value);

        mViewModel = ViewModelProviders.of(getActivity()).get(PlayingWorkoutModelView.class);

        return view;
    }

}
