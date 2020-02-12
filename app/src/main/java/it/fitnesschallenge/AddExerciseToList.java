package it.fitnesschallenge;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.fitnesschallenge.adapter.AddAdapter;
import it.fitnesschallenge.model.room.Exercise;
import it.fitnesschallenge.model.room.PersonalExercise;
import it.fitnesschallenge.model.view.AddExerciseToListModel;


public class AddExerciseToList extends Fragment {

    private static final String ARG_PARAM1 = "personalExerciseList";
    private static final String TAG = "AddExerciseToList";

    private List<PersonalExercise> mPersonalExerciseList;
    private List<Exercise> mExerciseList;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private AddExerciseToListModel mViewModel;
    private AddAdapter mAddAdapter;

    public AddExerciseToList() {
        // Required empty public constructor
    }

    public static AddExerciseToList newInstance(ArrayList<PersonalExercise> personalExerciseList) {
        AddExerciseToList fragment = new AddExerciseToList();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, personalExerciseList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPersonalExerciseList =  getArguments().getParcelableArrayList(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_add_exercise_to_list, container, false);
        mRecyclerView = view.findViewById(R.id.adding_exercise_list);
        mViewModel = ViewModelProviders.of(getActivity()).get(AddExerciseToListModel.class);
        mViewModel.getExerciseList().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                Log.d(TAG, "Exercise getted: " + exercises.size());
                mExerciseList = exercises;
                mAddAdapter = new AddAdapter(mContext, mExerciseList);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                if(mAddAdapter != null){
                    mAddAdapter.setOnClickListener(new AddAdapter.OnClickListener() {
                        @Override
                        public void onClickListener(View view) {
                            Log.d(TAG, "Expand button click detected");
                        }
                    });
                    mAddAdapter.setOnSelectedItemListener(new AddAdapter.OnSelectItemListener() {
                        @Override
                        public void onSelectItemListener(View view) {
                            Log.d(TAG, "Check button click detected");
                        }
                    });
                }
                mRecyclerView.setAdapter(mAddAdapter);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }
}
