/**
 * Questo fragment permette di prelevare l'ultimo workout attivo disponibile, verificando se su
 * FireStore è presente un nuovo workout, e permette di visualizzare la sequenza di esecuzione prima
 * di avviare l'allenamento tramite il FAB.
 */
package it.fitnesschallenge;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import it.fitnesschallenge.adapter.ShowAdapter;
import it.fitnesschallenge.model.User;
import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.entity.reference.WorkoutWithExercise;
import it.fitnesschallenge.model.view.PlayingWorkoutModelView;

import static it.fitnesschallenge.model.SharedConstance.EDIT_LIST_FRAGMENT;
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
    private Context mContext;

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
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
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
        mViewModel = ViewModelProviders.of(getActivity()).get(PlayingWorkoutModelView.class);

        mViewModel.getWorkoutId().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long workoutId) {
                setCurrentWorkout(workoutId);
            }
        });

        Log.d(TAG, "RecyclerView: " + mRecyclerView.toString());

        if (mUser != null) {
            FloatingActionButton floatingActionButton = view.findViewById(R.id.start_workout_FAB);
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow));
            floatingActionButton.setContentDescription(getString(R.string.start_workout_fab));
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

        } else {
            com.github.clans.fab.FloatingActionMenu menuFab = view.findViewById(R.id.outdoor_workout_list_fab_menu);
            menuFab.setVisibility(View.VISIBLE);
            com.github.clans.fab.FloatingActionButton editFab = view.findViewById(R.id.outdoor_workout_list_fab_edit);
            editFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditList editList = new EditList();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                            R.anim.enter_from_rigth, R.anim.exit_from_left);
                    transaction.replace(R.id.fragmentContainer, editList, EDIT_LIST_FRAGMENT)
                            .addToBackStack(EDIT_LIST_FRAGMENT)
                            .commit();
                }
            });

            com.github.clans.fab.FloatingActionButton fab2 = view.findViewById(R.id.outdoor_workout_list_fab_play);
            fab2.setOnClickListener(new View.OnClickListener() {
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
        }

        mViewModel.getWorkout().observe(getViewLifecycleOwner(), new Observer<List<Workout>>() {
            @Override
            public void onChanged(List<Workout> workoutList) {
                Log.d(TAG, "Avvio verifica su DB, size: " + workoutList.size());
                if (workoutList.size() > 0)
                    checkWorkoutInLocalDB(workoutList);
                else
                    getLastWorkoutOnFireBase();
            }
        });

        return view;
    }

    private void setCurrentWorkout(Long workoutId) {
        if (workoutId != -1) {
            Log.d(TAG, "Workout settatto.");
            mViewModel.getExerciseListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
                @Override
                public void onChanged(List<Exercise> exercises) {
                    Log.d(TAG, "Settata lista esercizi.");
                    mViewModel.setExerciseList(exercises);
                }
            });
            mViewModel.getWorkoutWithExercise().observe(getViewLifecycleOwner(), new Observer<WorkoutWithExercise>() {
                @Override
                public void onChanged(WorkoutWithExercise workoutWithExercise) {
                    setUI(workoutWithExercise);
                }
            });
        }
    }

    private void setUI(WorkoutWithExercise workoutWithExercise) {
        Log.d(TAG, "Observer di WorkoutWithExercise");
        mViewModel.setWorkoutWithExercise(workoutWithExercise);
        Log.d(TAG, "WorkoutWithExercise: " + workoutWithExercise.getPersonalExerciseList().toString());
        mViewModel.setPersonalExerciseList((ArrayList<PersonalExercise>) workoutWithExercise.getPersonalExerciseList());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mShowAdapter = new ShowAdapter(mViewModel.getPersonalExerciseList(), getActivity().getApplication(), getViewLifecycleOwner());
        mRecyclerView.setAdapter(mShowAdapter);
    }

    private void checkWorkoutInLocalDB(List<Workout> workoutList) {
        boolean found = false;
        Workout activeWorkout = null;
        for (int i = 0; i < workoutList.size() && !found; i++) {
            Workout workout = workoutList.get(i);
            Log.d(TAG, "workout[" + i + "]: " + workout.getStartDate());
            if (workout.getEndDate().before(Calendar.getInstance().getTime())) {
                workout.setActive(false);
                mViewModel.updateWorkout(workout);
                Log.d(TAG, "Trovato workout da disattivare");
            } else {
                Log.d(TAG, "Trovato workout attivo");
                found = true;
                activeWorkout = workout;
            }
            if (found) {
                Log.d(TAG, "Setto l'id del workout nel ViewModel");
                mViewModel.setWorkoutId(activeWorkout.getWorkOutId());
            } else {
                if (checkConnection() && mUser != null) {
                    Log.d(TAG, "Apro connessione con firebase, poichè non ho trovato workout attivi");
                    getLastWorkoutOnFireBase();
                } else {
                    errorDialog(R.string.connection_error_message);
                }
            }
        }
    }

    private void errorDialog(int p) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.ops)
                .setMessage(p);
        builder.show();
    }

    private void getLastWorkoutOnFireBase() {
        mDatabase.collection("user").document(mUser.getUsername())
                .collection("workout").orderBy("workout", Query.Direction.DESCENDING).limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }
                        Log.d(TAG, "Dimensione result set firebase: " + queryDocumentSnapshots.getDocuments().size());
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d(TAG, "Ultimo workout inserito prelevato");
                            final WorkoutWithExercise workoutWithExercise = documentSnapshot.toObject(WorkoutWithExercise.class);
                            Log.d(TAG, "Workout: " + workoutWithExercise.getWorkout().getEndDate());
                            if (workoutWithExercise.getWorkout().getEndDate().before(Calendar.getInstance().getTime())) {
                                Log.d(TAG, "Il workout su fire base è scaduto");
                                errorDialog(R.string.no_active_workout);
                            } else {
                                Log.d(TAG, "Avvio la scrittura sul database locale");
                                writeNewWorkoutInLocalDB(workoutWithExercise);
                            }
                        }
                    }
                });
    }

    private void writeNewWorkoutInLocalDB(final WorkoutWithExercise workoutWithExercise) {
        mViewModel.writeWorkout(workoutWithExercise.getWorkout()).observe(
                getViewLifecycleOwner(), new Observer<Long>() {
                    @Override
                    public void onChanged(final Long workoutId) {
                        Log.d(TAG, "Ho scritto il workout e ricevuto il suo id locale: " + workoutId);
                        writePersonaExercise(workoutId, workoutWithExercise);
                    }
                }
        );
    }

    private void writePersonaExercise(final Long workoutId, WorkoutWithExercise workoutWithExercise) {
        mViewModel.writePersonaExercise(workoutWithExercise.getPersonalExerciseList()).observe(
                getViewLifecycleOwner(), new Observer<long[]>() {
                    @Override
                    public void onChanged(long[] longs) {
                        Log.d(TAG, "Ho scritto la lista degli esercizi nel DB locale: " + Arrays.toString(longs));
                        writePersonalExerciseWorkoutReference(longs, workoutId);
                    }
                }
        );
    }

    private void writePersonalExerciseWorkoutReference(long[] longs, final Long workoutId) {
        mViewModel.insertWorkoutExerciseReference(workoutId, longs).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == true) {
                    Log.d(TAG, "Ho messo in relazione il workout con gli esercizi, adesso notifico l'id del workout");
                    mViewModel.setWorkoutId(workoutId);
                }
            }
        });
    }

    /**
     * Questo metodo controlla se il dispositivo è connesso prima di richiamare il metodo setObserver()
     */
    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();
    }
}
