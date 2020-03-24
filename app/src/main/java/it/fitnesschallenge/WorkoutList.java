/**
 * Questo fragment permette di prelevare l'ultimo workout attivo disponibile, verificando se su
 * FireStore è presente un nuovo workout, e permette di visualizzare la sequenza di esecuzione prima
 * di avviare l'allenamento tramite il FAB.
 */
package it.fitnesschallenge;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.fitnesschallenge.adapter.ShowAdapter;
import it.fitnesschallenge.adapter.ShowAdapterDrag;
import it.fitnesschallenge.model.User;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.entity.reference.WorkoutWithExercise;
import it.fitnesschallenge.model.view.PlayingWorkoutModelView;

import static it.fitnesschallenge.model.SharedConstance.PLAYING_WORKOUT;

public class WorkoutList extends Fragment {

    private static final String TAG = "WorkoutList";
    private static final String FIREBASE_USER = "firebaseUser";

    private PlayingWorkoutModelView mViewModel;
    private User mUser;
    private FirebaseUser mFireStoreUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private RecyclerView mRecyclerView;
    private ShowAdapter mShowAdapter;

    public WorkoutList() {
        // Required empty public constructor
    }

    static WorkoutList newInstance(User user) {
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
        mFireStoreUser = mAuth.getCurrentUser();
        if (mFireStoreUser != null)
            mDatabase = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workout_list, container, false);
        mRecyclerView = view.findViewById(R.id.workout_list_recycler_view);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.start_workout_FAB);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayingWorkout playingWorkout = new PlayingWorkout();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                        R.anim.enter_from_rigth, R.anim.exit_from_left);
                transaction.replace(R.id.fragmentContainer, playingWorkout, PLAYING_WORKOUT)
                        .addToBackStack(PLAYING_WORKOUT)
                        .commit();
            }
        });

        mViewModel = ViewModelProviders.of(getActivity()).get(PlayingWorkoutModelView.class);
        /*
         * Verifico se le informazioni sono già state prelevate dal DB, se sono già disponibili nel
         * ViewModel, gli Observer non verranno mai notificati, quindi si aggiunge un controllo
         * manuale.
         */
        if (mViewModel.getPersonalExerciseList() != null)
            setRecyclerView();
        else {
            setObserver();
        }
        /*
         * Setto i parametri che definiscono l'utente nel ViewModel, in modo da renderli reperibili
         * mentre l'utente effettua il workout
         */
        setUserConsistence();
        return view;
    }

    /**
     * Set observer crea gli observer al ViewModel nel caso in cui il fragment venga chiamato per la
     * prima volta, in quanto è necessario prelevare i dati dal DB essendo che il ViewModel è legato
     * alla MainActivity.
     */
    private void setObserver() {
        mViewModel.getWorkoutList().observe(getViewLifecycleOwner(), new Observer<List<Workout>>() {
            @Override
            public void onChanged(List<Workout> workoutList) {
                mViewModel.setActiveWorkoutFromLocal(workoutList).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
            }
        });
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
                mViewModel.setWorkoutList(getViewLifecycleOwner());
            }
        });
        mViewModel.getWorkoutWithExercise().observe(getViewLifecycleOwner(), new Observer<WorkoutWithExercise>() {
            @Override
            public void onChanged(WorkoutWithExercise workoutWithExercise) {
                Log.d(TAG, "Workout con esercizi prelevato dal DB");
                setRecyclerView();
            }
        });
    }

    /**
     * Quseto metodo imposta la recyclerView e il TouchHelper che permette il DragAndDrop degli
     * elementi nella View.
     */
    private void setRecyclerView() {
        mShowAdapter = new ShowAdapter(mViewModel.getPersonalExerciseList(),
                getActivity().getApplication(), getViewLifecycleOwner());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mShowAdapter);
        ItemTouchHelper.Callback callback = new ShowAdapterDrag(mShowAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * In questo metodo vengono salvati nel ViewModel i dati appartenenti all'utente e i dati che
     * permettono la connessione remota a FireBase, per sincronizzare il Workout al termine dello stesso.
     */
    private void setUserConsistence() {
        mViewModel.setAuth(mAuth);
        mViewModel.setFireStoreUser(mFireStoreUser);
        mViewModel.setDatabase(mDatabase);
        mViewModel.setUser(mUser);
    }
}
