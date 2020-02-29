package it.fitnesschallenge;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.fitnesschallenge.model.User;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.entity.reference.WorkoutWithExercise;
import it.fitnesschallenge.model.view.PlayingWorkoutModelView;

public class WorkoutList extends Fragment {

    private static final String TAG = "WorkoutList";
    private static final String FIREBASE_USER = "firebaseUser";

    private PlayingWorkoutModelView mViewModel;
    private User mUser;
    private FirebaseUser mFirestoreUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    public WorkoutList() {
        // Required empty public constructor
    }

    public static WorkoutList newInstance(User user) {
        WorkoutList fragment = new WorkoutList();
        Bundle args = new Bundle();
        args.putParcelable(FIREBASE_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mUser = getArguments().getParcelable(FIREBASE_USER);
        mAuth = FirebaseAuth.getInstance();
        mFirestoreUser = mAuth.getCurrentUser();
        if (mFirestoreUser != null)
            mDatabase = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workout_list, container, false);

        mViewModel = ViewModelProviders.of(getActivity()).get(PlayingWorkoutModelView.class);
        mViewModel.setActiveWorkoutFromLocal().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                final List<WorkoutWithExercise> workoutList = new ArrayList<>();
                if (!aBoolean) {
                    Log.d(TAG, "Non sono stati individuati workout nel DB");
                    mDatabase.collection("user/" + mUser.getUsername() + "/workout")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    Log.d(TAG, "Ho letto da Firebase nuovi workout");
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                        workoutList.add(queryDocumentSnapshot.toObject(WorkoutWithExercise.class));
                                        checkWorkoutList(workoutList);
                                        Log.d(TAG, "Individuato workout per l'utente");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Qualcosa è andato storto nella lettura del workout");
                                }
                            });
                }
            }
        });
        return view;
    }

    /**
     * Questo metodo verifica se i workout prelevati da Firestore sono attivi o sono da disattivare
     * se il metdo individua un allenamento da lasciare attivo lo passa a write il localDB che lo
     * scriverà sul DB locale.
     *
     * @param workouts contiene una lista dei workout con i relativi esercizi
     */
    private void checkWorkoutList(List<WorkoutWithExercise> workouts) {
        Log.d(TAG, "Verifico i workout ricevuti da Firebase");
        Calendar calendar = Calendar.getInstance();
        Log.d(TAG, "Data sistema: " + calendar.getTime().toString());
        for (WorkoutWithExercise workoutWithExercise : workouts) {
            Workout workout = workoutWithExercise.getWorkout();
            if (workout.isActive()) {
                Log.d(TAG, "Individuato workout attivo");
                if (workout.getEndDate().before(calendar.getTime())) {
                    //Questi sono i wokout da disattivare
                    workout.setActive(false);
                    Log.d(TAG, "Individuato workout scaduto");
                    Log.d(TAG, "Data scadenza workout: " + workout.getEndDate().toString());
                } else {
                    //Setto l'id del workout selezionato
                    Log.d(TAG, "Individuato workout non scaduto");
                    writeInLocalDB(workoutWithExercise);
                }
            }
        }
    }

    /**
     * Questo metodo viene richiamato quando viene prelevato un workout dal DB Firestore e bisogna
     * inserirlo in locale, quindi con il workoutId del ViewModel
     */
    private void writeInLocalDB(WorkoutWithExercise workoutWithExercise) {
        Log.d(TAG, "Scrivo un nuovo workout nel database locale");
        mViewModel.writeWorkoutWithExercise(workoutWithExercise, getViewLifecycleOwner());
        mViewModel.getWorkoutId().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                mViewModel.setWorkoutList();
            }
        });
        mViewModel.getWorkoutWithExercise().observe(getViewLifecycleOwner(), new Observer<WorkoutWithExercise>() {
            @Override
            public void onChanged(WorkoutWithExercise workoutWithExercise) {
                Log.d(TAG, "Workout con esercizi prelevato dal DB");

            }
        });
    }

}
