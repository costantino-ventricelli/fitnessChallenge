package it.fitnesschallenge;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.fitnesschallenge.adapter.ItemTouchHelperCallBack;
import it.fitnesschallenge.adapter.ShowAdapter;
import it.fitnesschallenge.model.room.PersonalExercise;
import it.fitnesschallenge.model.view.CreationViewModel;


public class ExerciseList extends Fragment {

    private CreationViewModel mCreationViewModel;
    private ShowAdapter mShowAdapter;
    private RecyclerView mRecyclerView;
    private TextView mMessageView;
    private Context mContext;
    private static final String TAG = "ExerciseList";


    public ExerciseList() {
        // Required empty public constructor
    }

    /*
     * TODO: trovare un modo di eliminare gli esercizi logicamente all'inizo e poi eliminarli
     * effettivamente allo step successivo della creazione della scheda.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_execise_list, container, false);

        mRecyclerView = view.findViewById(R.id.show_exercise_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageView = view.findViewById(R.id.exercise_list_message);

        mCreationViewModel = ViewModelProviders.of(getActivity()).get(CreationViewModel.class);
        mCreationViewModel.getPersonalExerciseList().observe(getViewLifecycleOwner(), new Observer<List<PersonalExercise>>() {
            @Override
            public void onChanged(List<PersonalExercise> personalExerciseList) {
                Log.d(TAG, "Ottenuta lista esercizi personale: " + personalExerciseList.toString());
                mShowAdapter = new ShowAdapter(personalExerciseList);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                mRecyclerView.setAdapter(mShowAdapter);
                ItemTouchHelper.Callback callback = new ItemTouchHelperCallBack(mShowAdapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(mRecyclerView);
                mShowAdapter.setOnClickListener(new ShowAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(View view, boolean isRemoved) {
                        ImageButton removeButton = view.findViewById(R.id.exercise_item_action);
                        if (!isRemoved) {
                            removeButton.setImageResource(R.drawable.ic_undo);
                            Log.d(TAG, "Rimuovo esercizio");
                        } else {
                            removeButton.setImageResource(R.drawable.ic_remove_circle);
                            Log.d(TAG, "Riaggiungo esercizio");
                        }
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
