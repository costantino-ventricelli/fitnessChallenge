package it.fitnesschallenge;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Locale;

import it.fitnesschallenge.model.User;
import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.view.PlayingWorkoutModelView;


public class PlayingWorkout extends Fragment {

    private static final String TAG = "PlayingWorkout";
    private static final short PREVIOUSLY = 1;
    private static final short NEXT = 2;

    private ImageButton mNext;
    private ImageButton mPrev;
    private TextView mNextText;
    private TextView mPrevText;
    private TextView mExerciseTitle;
    private ImageView mExerciseImage;
    private TextView mTimeTimer;
    private TextView mCurrentRepetition;
    private PersonalExercise mCurrentExercise;
    private PlayingWorkoutModelView mViewModel;
    private ProgressBar mProgressBar;
    private TextView mProgressValue;
    private User mUser;
    private FirebaseUser mFireStoreUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    public PlayingWorkout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //FIXME: ogni volta che navigo tra i fragment viene richiesto l'esercizio successivo
        View view = inflater.inflate(R.layout.fragment_playing_workout, container, false);

        MaterialButton stopButton = view.findViewById(R.id.playing_workout_stop_workout);
        mExerciseTitle = view.findViewById(R.id.playing_workout_title);
        mExerciseImage = view.findViewById(R.id.playing_workout_image);
        mTimeTimer = view.findViewById(R.id.playing_workout_timer);
        ImageButton startTimer = view.findViewById(R.id.playing_workout_start_timer);
        mCurrentRepetition = view.findViewById(R.id.playing_workout_repetition);
        ImageButton infoButton = view.findViewById(R.id.playing_workout_info);
        Button addWeight = view.findViewById(R.id.playing_exercise_add_weigth);
        mNext = view.findViewById(R.id.next_exercise);
        mNextText = view.findViewById(R.id.next_exercise_label);
        mPrev = view.findViewById(R.id.prev_exercise);
        mPrevText = view.findViewById(R.id.prev_exercise_label);
        mProgressBar = view.findViewById(R.id.playing_workout_progress_bar);
        mProgressValue = view.findViewById(R.id.progress_value);

        mViewModel = ViewModelProviders.of(getActivity()).get(PlayingWorkoutModelView.class);
        /*
         * Setto l'utente nel Fragment in modo da poterlo utilizzare successivamente, probablimente
         * da accorpare nel metodo che farà il Sync tra locale e remoto(FireBase)
         */
        setUserConsistence();
        // Prelevo il primo esercizio da eseguire nel ViewModel
        getCurrentExercise(NEXT);
        return view;
    }

    private void setUserConsistence() {
        mAuth = mViewModel.getAuth();
        mFireStoreUser = mViewModel.getFireStoreUser();
        mUser = mViewModel.getUser();
        mDatabase = mViewModel.getDatabase();
    }

    private void getCurrentExercise(short witch) {
        setNavigationButton();
        if (witch == PREVIOUSLY)
            mCurrentExercise = mViewModel.getPrevExercise();
        else if (witch == NEXT)
            mCurrentExercise = mViewModel.getNextExercise();
        else
            Toast.makeText(getContext(), "WTF?", Toast.LENGTH_LONG).show();
        setUI(witch);
    }

    private void setUI(final short witch) {
        mViewModel.getExerciseInformation(mCurrentExercise).observe(getViewLifecycleOwner(), new Observer<Exercise>() {
            @Override
            public void onChanged(Exercise exercise) {
                mExerciseTitle.setText(exercise.getExerciseName());
                mExerciseImage.setImageResource(exercise.getImageReference());
                mTimeTimer.setText(new StringBuilder().append(NumberFormat
                        .getInstance(Locale.getDefault())
                        .format(mCurrentExercise.getCoolDown())).append("''").toString());
                /*
                 * TODO: bisogna capire a che serie dell'esercizio ci troviamo, probabilmente dovremo
                 *       creare una classe che tenga in considerazione lo stato di svolgimento attuale
                 */
                if (witch == NEXT) {
                    int currentProgress = mProgressBar.getProgress();
                    currentProgress += (100 /
                            mViewModel.getPersonalExerciseList().size());
                    mProgressValue.setText(NumberFormat.getInstance(Locale.getDefault()).format(currentProgress));
                    mProgressBar.setProgress(currentProgress);
                } else if (witch == PREVIOUSLY) {
                    int currentProgress = mProgressBar.getProgress();
                    currentProgress -= (100 /
                            mViewModel.getPersonalExerciseList().size());
                    mProgressValue.setText(NumberFormat.getInstance(Locale.getDefault()).format(currentProgress));
                    mProgressBar.setProgress(currentProgress - (100 /
                            mViewModel.getPersonalExerciseList().size()));
                } else
                    Toast.makeText(getContext(), "WTF?", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setNavigationButton() {
        if (mViewModel.thereIsPrev()) {
            Log.d(TAG, "C'è un precedente");
            mPrev.setVisibility(View.VISIBLE);
            mPrevText.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Non c'è un precedente");
            mPrev.setVisibility(View.GONE);
            mPrevText.setVisibility(View.GONE);
        }
        if (mViewModel.thereIsNext()) {
            Log.d(TAG, "C'è un successivo");
            mNext.setVisibility(View.VISIBLE);
            mNextText.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Non c'è un successivo");
            mNext.setVisibility(View.GONE);
            mNextText.setVisibility(View.GONE);
        }
    }
}
