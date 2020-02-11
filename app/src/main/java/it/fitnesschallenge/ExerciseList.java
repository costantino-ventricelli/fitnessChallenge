package it.fitnesschallenge;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import it.fitnesschallenge.model.room.ExerciseTable;
import it.fitnesschallenge.model.view.CreationViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExerciseList extends Fragment {

    private CreationViewModel mCreationViewModel;
    private List<ExerciseTable> mExerciseList;


    public ExerciseList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_execise_list, container, false);

        mCreationViewModel = ViewModelProviders.of(getActivity()).get(CreationViewModel.class);
        mCreationViewModel.getAllExercise().observe(getViewLifecycleOwner(), new Observer<List<ExerciseTable>>() {
            @Override
            public void onChanged(List<ExerciseTable> exerciseTables) {
                mExerciseList = exerciseTables;
            }
        });
        return view;
    }

}
