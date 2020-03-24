package it.fitnesschallenge;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.fitnesschallenge.adapter.HomeAdapter;
import it.fitnesschallenge.adapter.HomeAdapterDrag;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.view.CreationViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingListHome extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<PersonalExercise> mList;
    private CreationViewModel mCreationViewModel;
    private Context mContext;

    public TrainingListHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_list_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_training_list_home);

        mCreationViewModel = ViewModelProviders.of(getActivity()).get(CreationViewModel.class);
        mCreationViewModel.getPersonalExerciseList().observe(getViewLifecycleOwner(), new Observer<List<PersonalExercise>>() {
            @Override
            public void onChanged(List<PersonalExercise> personalExercises) {

                layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);

                mAdapter = new HomeAdapter(mList, getActivity().getApplication(), getViewLifecycleOwner());
                recyclerView.setAdapter(mAdapter);

                mList = personalExercises;

                ItemTouchHelper.Callback callback = new HomeAdapterDrag(mAdapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(recyclerView);

                mAdapter.setOnClickListener(new HomeAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(View view, int position) {
                        ImageButton removeButton = view.findViewById(R.id.exercise_item_action);
                        if (!mList.get(position).isDeleted()) {
                            removeButton.setImageResource(R.drawable.ic_undo);
                            mList.get(position).setDeleted(true);
                        } else {
                            removeButton.setImageResource(R.drawable.ic_remove_circle);
                            mList.get(position).setDeleted(false);
                        }
                        mCreationViewModel.setPersonalExerciseList(mList);
                    }
                });
            }
        });

        mCreationViewModel.getIsError().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    Snackbar.make(getView(), getContext()
                            .getResources()
                            .getString(R.string.add_exercise_to_list), Snackbar.LENGTH_LONG).show();
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
