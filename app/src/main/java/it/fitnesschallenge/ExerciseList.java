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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.fitnesschallenge.adapter.ShowAdapter;
import it.fitnesschallenge.model.room.PersonalExercise;
import it.fitnesschallenge.model.view.CreationViewModel;


public class ExerciseList extends Fragment {

    private CreationViewModel mCreationViewModel;
    private ShowAdapter mShowAdapter;
    private RecyclerView mRecyclerList;
    private TextView mMessageView;
    private static final String TAG = "ExerciseList";


    public ExerciseList() {
        // Required empty public constructor
    }

    //TODO: handle click su rimozione e sopostamento elementi nella view

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_execise_list, container, false);

        mRecyclerList = view.findViewById(R.id.show_exercise_list);
        mRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageView = view.findViewById(R.id.exercise_list_message);

        mCreationViewModel = ViewModelProviders.of(getActivity()).get(CreationViewModel.class);
        mCreationViewModel.getPersonalExerciseList().observe(getViewLifecycleOwner(), new Observer<List<PersonalExercise>>() {
            @Override
            public void onChanged(List<PersonalExercise> personalExerciseList) {
                Log.d(TAG, "Ottenuta lista esercizi personale: " + personalExerciseList.toString());
                mShowAdapter = new ShowAdapter(personalExerciseList);
                mRecyclerList.setAdapter(mShowAdapter);
            }
        });
        return view;
    }

}
