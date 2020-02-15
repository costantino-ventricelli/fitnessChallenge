package it.fitnesschallenge;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static it.fitnesschallenge.model.SharedConstance.ENTER_FRAGMENT;

public class GymEnter extends Fragment {

    private static final String TAG = "GymEnter";

    private Context mContext;

    public GymEnter() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        HomeActivity.setCurrentFragment(ENTER_FRAGMENT);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gym_enter, container, false);
        return view;
    }

}
