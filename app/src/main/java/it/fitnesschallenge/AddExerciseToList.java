package it.fitnesschallenge;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.fitnesschallenge.model.room.PersonalExercise;


public class AddExerciseToList extends Fragment {

    private static final String ARG_PARAM1 = "personalExerciseList";

    private List<PersonalExercise> mPersonalExerciseList;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private Context mContext;

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
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(List<PersonalExercise> personalExerciseList) {
        if (mListener != null) {
            mListener.onFragmentInteraction(personalExerciseList);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mContext = context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mContext = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(List<PersonalExercise> personalExerciseList);
    }
}
