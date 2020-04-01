/**
 * Questo fragment permette di prelevare l'ultimo workout attivo disponibile, verificando se su
 * FireStore è presente un nuovo workout, e permette di visualizzare la sequenza di esecuzione prima
 * di avviare l'allenamento tramite il FAB.
 */
package it.fitnesschallenge;


import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.fitnesschallenge.adapter.ShowAdapter;
import it.fitnesschallenge.adapter.ShowAdapterDrag;
import it.fitnesschallenge.model.User;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.entity.reference.WorkoutWithExercise;
import it.fitnesschallenge.model.view.PlayingWorkoutModelView;

import static it.fitnesschallenge.model.SharedConstance.EDIT_LIST_FRAGMENT;
import static it.fitnesschallenge.model.SharedConstance.PLAYING_WORKOUT;
import static it.fitnesschallenge.model.SharedConstance.SIGN_UP_FRAGMENT;

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
        View tempView = null; //variabile to hold il valore di view nei due rami if else da restiruire alla fine del metodo onCreateView
        if (mUser != null) {
            View view = inflater.inflate(R.layout.fragment_workout_list_fabplay, container, false);
            mRecyclerView = view.findViewById(R.id.workout_list_recycler_view);
            Log.d(TAG, "RecyclerView: " + mRecyclerView);

            FloatingActionButton floatingActionButton = view.findViewById(R.id.start_workout_FAB);
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

            tempView = view;
        } else {
            View view = inflater.inflate(R.layout.fragment_workout_list_fabweight, container, false);
            mRecyclerView = view.findViewById(R.id.workout_list_recycler_view);

            FloatingActionMenu fabMenu = view.findViewById(R.id.fabMenu); //fab principale (menu)

            com.github.clans.fab.FloatingActionButton fab1 = view.findViewById(R.id.fab1);
            fab1.setOnClickListener(new View.OnClickListener() {
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

            com.github.clans.fab.FloatingActionButton fab2 = view.findViewById(R.id.fab2);
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
            tempView = view;
        }

        mViewModel = ViewModelProviders.of(getActivity()).get(PlayingWorkoutModelView.class);
        /*
         * Verifico se le informazioni sono già state prelevate dal DB, se sono già disponibili nel
         * ViewModel, gli Observer non verranno mai notificati, quindi si aggiunge un controllo
         * manuale.
         */
        if (mViewModel.getPersonalExerciseList() != null)
            setRecyclerView();
        else {
            checkConnection();
        }
        /*
         * Setto i parametri che definiscono l'utente nel ViewModel, in modo da renderli reperibili
         * mentre l'utente effettua il workout
         */
        if (mFireStoreUser != null)
            setUserConsistence();

        mViewModel.getWorkoutId().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                if (aLong != -1) {
                    Log.d(TAG, "Se sono qui ho trovato un workout utilizzabile.");
                    setUpUI();
                }
            }
        });
       return tempView;
    }

    /**
     * Questo metodo controlla se il dispositivo è connesso prima di richiamare il metodo setObserver()
     */
    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Set observer crea gli observer al ViewModel nel caso in cui il fragment venga chiamato per la
     * prima volta, in quanto è necessario prelevare i dati dal DB essendo che il ViewModel è legato
     * alla MainActivity.
     */

    private void setObserver() {
        mViewModel.getWorkout().observe(getViewLifecycleOwner(), new Observer<List<Workout>>() {
            @Override
            public void onChanged(List<Workout> workoutList) {
                try {
                    boolean find = false;
                    Calendar calendar = Calendar.getInstance();
                    for (Workout workout : workoutList) {
                        if (workout.getEndDate().before(calendar.getTime()) && workout.isActive()) {
                            workout.setActive(false);
                            mViewModel.updateWorkout(workout);
                        } else {
                            mViewModel.setWorkoutId(workout.getWorkOutId());
                            find = true;
                        }
                    }
                    if (!find) {
                        if (mFireStoreUser != null) {
                            if (!checkConnection())
                                showErrorMaterial(getString(R.string.connection_error_message));
                            else
                                getWorkoutFromFirebase();
                        } else
                            showErrorMaterial(getString(R.string.registration_error));

                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    showErrorMaterial(getString(R.string.registration_error));
                }
            }
        });
    }

    private void showErrorMaterial(String messageError) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.ops)
                .setMessage(messageError);
        if (messageError.equals(getString(R.string.registration_error))) {
            builder.setPositiveButton("Sign up", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SignUp signUp = new SignUp();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                            R.anim.enter_from_rigth, R.anim.exit_from_left)
                            .replace(R.id.fragmentContainer, signUp, SIGN_UP_FRAGMENT)
                            .addToBackStack(SIGN_UP_FRAGMENT)
                            .commit();
                }
            });
            builder.setNegativeButton("New workout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO: aggiungere possibilità di preare nuovo workuot out door.
                }
            });
        }
        builder.show();
    }

    private void getWorkoutFromFirebase() {
        final ArrayList<WorkoutWithExercise> workoutWithExerciseList = new ArrayList<>();
        mDatabase.collection("user/" + mUser.getUsername() + "/workout")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "Ho letto da Firebase nuovi workout");
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            workoutWithExerciseList.add(queryDocumentSnapshot.toObject(WorkoutWithExercise.class));
                            checkWorkoutList(workoutWithExerciseList);
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
        setUpUI();
    }

    private void setUpUI() {
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
