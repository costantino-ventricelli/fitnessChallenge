package it.fitnesschallenge;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import it.fitnesschallenge.adapter.AddAdapter;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.entity.Exercise;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingListHome extends Fragment {


    public TrainingListHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_list_home, container, false);
        ArrayList<Exercise> exerciseArrayList = new ArrayList<>();
        AddAdapter addAdapter = new AddAdapter(exerciseArrayList);
        addAdapter.setOnClickListener(new AddAdapter.OnClickListener() {
            @Override
            public void onClickListener(int finalHeight, int startHeight, View itemView, boolean expanded) {

            }
        });
        FitnessChallengeRepository repository = new FitnessChallengeRepository(getActivity().getApplication());
        return view;
    }

}
