
package it.fitnesschallenge;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import it.fitnesschallenge.adapter.ShowAdapter;
import it.fitnesschallenge.adapter.ShowAdapterDrag;
import it.fitnesschallenge.model.User;
import it.fitnesschallenge.model.room.WorkoutType;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.entity.reference.WorkoutWithExercise;
import it.fitnesschallenge.model.view.EditListViewModel;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static it.fitnesschallenge.model.SharedConstance.ADD_EXERCISE_TO_LIST;
import static it.fitnesschallenge.model.SharedConstance.EDIT_LIST_FRAGMENT;

public class EditList extends Fragment {

    private static final String TAG = "EditList";

    private static final String FIREBASE_USER = "firebaseUser";
    private static final String SEARCH_IN_DB_FIRST = "searchInDBFirst";
    private User mUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser mFireStoreUser;
    private Context mContext;
    private EditListViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private List<PersonalExercise> mActualList;
    private ShowAdapter mShowAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = getArguments().getParcelable(FIREBASE_USER);
            mAuth = FirebaseAuth.getInstance();
            mFireStoreUser = mAuth.getCurrentUser();
            mDatabase = FirebaseFirestore.getInstance();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_list, container, false);
        FloatingActionButton addFAB = view.findViewById(R.id.edit_list_fab_add);
        FloatingActionButton saveFAB = view.findViewById(R.id.edit_list_fab_save);
        mRecyclerView = view.findViewById(R.id.edit_list_recycler_view);
        mViewModel = ViewModelProviders.of(getActivity()).get(EditListViewModel.class);

        if (mViewModel.isFirstExecution()) {
            mViewModel.setFirstExecution(false);
            mViewModel.getWorkoutList().observe(getViewLifecycleOwner(), new Observer<List<Workout>>() {
                @Override
                public void onChanged(List<Workout> workoutList) {
                    selectOutdoorWorkout(workoutList);
                }
            });
        }

        mViewModel.getPersonalExerciseList().observe(getViewLifecycleOwner(), new Observer<List<PersonalExercise>>() {
            @Override
            public void onChanged(final List<PersonalExercise> personalExerciseList) {
                Log.d(TAG, "Esercizi inseriti.");
                Log.d(TAG, "Ottenuta lista esercizi personale: " + personalExerciseList.toString());
                mShowAdapter = new ShowAdapter(personalExerciseList, getActivity().getApplication(), getViewLifecycleOwner());
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                mRecyclerView.setAdapter(mShowAdapter);
                ItemTouchHelper.Callback callback = new ShowAdapterDrag(mShowAdapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(mRecyclerView);
                mShowAdapter.setOnClickListener(new ShowAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(View view, int position) {
                        ImageButton removeButton = view.findViewById(R.id.exercise_item_action);
                        Log.d(TAG, "E' rimosso: " + mActualList.get(position).isDeleted());
                        if (!mActualList.get(position).isDeleted()) {
                            removeButton.setImageResource(R.drawable.ic_undo);
                            Log.d(TAG, "Rimuovo esercizio");
                            personalExerciseList.get(position).setDeleted(true);
                        } else {
                            removeButton.setImageResource(R.drawable.ic_remove_circle);
                            Log.d(TAG, "Riaggiungo esercizio");
                            personalExerciseList.get(position).setDeleted(false);
                        }
                        mViewModel.setPersonalExerciseList(personalExerciseList);
                    }
                });
            }
        });

        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddExerciseToList addExerciseToList = AddExerciseToList.newInstance(EDIT_LIST_FRAGMENT);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                        R.anim.enter_from_rigth, R.anim.exit_from_left)
                        .replace(R.id.fragmentContainer, addExerciseToList, ADD_EXERCISE_TO_LIST)
                        .addToBackStack(ADD_EXERCISE_TO_LIST)
                        .commit();
            }
        });

        saveFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewModel.getWorkout() != null) {
                    mViewModel.addNewExerciseToWorkout().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {

                        }
                    });
                } else {
                    mViewModel.addWorkoutWithExercise().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {

                        }
                    });
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void selectOutdoorWorkout(List<Workout> workoutList) {
        for (Workout workout : workoutList) {
            if (workout.getWorkoutType().getValue().equals(WorkoutType.OUTDOOR.getValue())) {
                Log.d(TAG, "Outdoor workout trovato");
                mViewModel.setWorkout(workout);
                getWorkoutWithExercise(workout.getWorkOutId());
            } else {
                Log.d(TAG, "Trovato workout di tipo indoor");
            }
        }
    }

    private void getWorkoutWithExercise(final long workoutId) {
        mViewModel.getSavedExerciseList(workoutId).observe(getViewLifecycleOwner(), new Observer<WorkoutWithExercise>() {
            @Override
            public void onChanged(WorkoutWithExercise workoutWithExercise) {
                Log.d(TAG, "Prelevato workout con esercizi");
                mViewModel.setPersonalExerciseList(workoutWithExercise.getPersonalExerciseList());
            }
        });
    }
}
