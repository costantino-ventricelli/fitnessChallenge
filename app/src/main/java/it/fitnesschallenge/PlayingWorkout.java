/**
 * Questo Fragment contiente l'intelligenza e le informazioni necessarie all'utente per eseguire il
 * workout step-by-step, una volta terminato il workout questo fragment eseguirà le istruzioni necessarie
 * ad effettuare la sincronizzazione con il FireBase.
 */
package it.fitnesschallenge;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import static it.fitnesschallenge.model.SharedConstance.CONVERSION_SEC_IN_MILLIS;
import static it.fitnesschallenge.model.SharedConstance.TIMER_FRAGMENT;


public class PlayingWorkout extends Fragment {

    private static final String TAG = "PlayingWorkout";
    private static final short PREVIOUSLY = 1;
    private static final short NEXT = 2;
    private static final short INIT = 3;

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
        getCurrentExercise(INIT);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentExercise(NEXT);
            }
        });
        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentExercise(PREVIOUSLY);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new Timer();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                        R.anim.enter_from_rigth, R.anim.exit_from_left)
                        .replace(R.id.fragmentContainer, timer, TIMER_FRAGMENT)
                        .addToBackStack(TIMER_FRAGMENT)
                        .commit();
            }
        });
        return view;
    }

    /**
     * Questo metodo preleva dal ViewModel le informazioni relative all'utente.
     */
    private void setUserConsistence() {
        mAuth = mViewModel.getAuth();
        mFireStoreUser = mViewModel.getFireStoreUser();
        mUser = mViewModel.getUser();
        mDatabase = mViewModel.getDatabase();
    }

    /**
     * Questo metodo permette di ottenere l'esercizio corrente, in base al tipo di operazione richiesta
     *
     * @param witch contiene il tipo di operazione da esegure:
     *              PREVIOUSLY: indica che si sta selezionando l'esercio precedente
     *              NEXT: indica che si sta selezionando l'esercizio successivo
     *              INIT: indica che l'operazione è quella di inizializzazione iniziale
     */
    private void getCurrentExercise(short witch) {
        if (!mViewModel.isIteratorNull())
            Log.d(TAG, "Indice esercizio successivo prima dell'aggiornamento: " + mViewModel.getNextIndex());
        else
            Log.d(TAG, "Inizializzazione esercizio successivo prima dell'aggiornamento: 0");
        if (witch == PREVIOUSLY) {
            mCurrentExercise = mViewModel.getPrevExercise();
        } else if (witch == NEXT) {
            mCurrentExercise = mViewModel.getNextExercise();
        } else if (witch == INIT) {
            if (mViewModel.isIteratorNull()) {
                Log.d(TAG, "Prelievo il primo esercizio");
                mViewModel.setListIterator();
                mCurrentExercise = mViewModel.getNextExercise();
            } else {
                Log.d(TAG, "Ripristino l'esercizio");
                mCurrentExercise = mViewModel.getCurrentExercise();
            }
        }

        Log.d(TAG, "Indice esercizio successivo dopo l'aggiornamento: " + mViewModel.getNextIndex());
        setNavigationButton();
        mViewModel.setCurrentExercise(mCurrentExercise);
        setUI(witch);
    }

    /**
     * SetUi contiene tutte le istruzioni necessarie a mostrare l'esercizio corrente all'utente
     *
     * @param witch indica che tipo di operazione eseguire
     */
    private void setUI(final short witch) {
        final int progress = 100 / mViewModel.getPersonalExerciseList().size();
        mViewModel.getExerciseInformation(mCurrentExercise).observe(getViewLifecycleOwner(), new Observer<Exercise>() {
            @Override
            public void onChanged(Exercise exercise) {
                mExerciseTitle.setText(exercise.getExerciseName());
                mExerciseImage.setImageResource(exercise.getImageReference());
                mTimeTimer.setText(PersonalExercise.getCoolDownString(mCurrentExercise.getCoolDown() * CONVERSION_SEC_IN_MILLIS));
                /*
                 * TODO: bisogna capire a che serie dell'esercizio ci troviamo, probabilmente dovremo
                 *       creare una classe che tenga in considerazione lo stato di svolgimento attuale
                 */
                if (witch == NEXT || witch == INIT) {
                    mProgressValue.setText(NumberFormat.getInstance(Locale.getDefault()).format(progress * mViewModel.getNextIndex()));
                    mProgressBar.setProgress(progress * mViewModel.getNextIndex());
                } else if (witch == PREVIOUSLY) {
                    mProgressValue.setText(NumberFormat.getInstance(Locale.getDefault()).format(progress * mViewModel.getPrevIndex()));
                    mProgressBar.setProgress(progress * mViewModel.getPrevIndex());
                }
            }
        });
    }

    /**
     * Questo metodo consente di verificare quali tasti di navigazione mostrare all'utente
     * se l'esercizio è il primo allora mostrerà solo il pusante di successivo, e così via.
     */
    private void setNavigationButton() {
        if (mViewModel.thereIsPrev()) {
            mPrev.setVisibility(View.VISIBLE);
            mPrevText.setVisibility(View.VISIBLE);
        } else {
            mPrev.setVisibility(View.GONE);
            mPrevText.setVisibility(View.GONE);
        }
        if (mViewModel.thereIsNext()) {
            mNext.setVisibility(View.VISIBLE);
            mNextText.setVisibility(View.VISIBLE);
        } else {
            mNext.setVisibility(View.GONE);
            mNextText.setVisibility(View.GONE);
        }
    }
}
