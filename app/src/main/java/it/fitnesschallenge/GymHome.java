package it.fitnesschallenge;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.fitnesschallenge.model.User;

public class GymHome extends Fragment {

    private static final String USER = "user";

    private User mUser;


    public GymHome() {
        // Required empty public constructor
    }

    public static GymHome newInstance(User user) {
        GymHome fragment = new GymHome();
        Bundle args = new Bundle();
        args.putParcelable(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = getArguments().getParcelable(USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gym_home, container, false);
        return view;
    }

}
