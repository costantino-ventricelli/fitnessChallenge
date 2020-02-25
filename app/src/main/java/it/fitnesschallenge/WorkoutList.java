package it.fitnesschallenge;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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
import java.util.List;

import it.fitnesschallenge.model.User;
import it.fitnesschallenge.model.room.Workout;
import it.fitnesschallenge.model.view.PlayingWorkoutModelView;

public class WorkoutList extends Fragment {

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
                final List<Workout> workoutList = new ArrayList<>();
                if (!aBoolean) {
                    mDatabase.collection("user/" + mUser.getUsername() + "/workout")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                        workoutList.add(queryDocumentSnapshot.toObject(Workout.class));
                                        /* TODO: verificare i workout attivi, se ci sono prendere il più recente
                                         * scriverlo nel DB locale e settare gli altri ad inattivi sia
                                         * in locale sia su Firebase
                                         */
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        });
        return view;
    }

}
