package it.fitnesschallenge;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

import static it.fitnesschallenge.model.Fragment.HOME_FRAGMENT;
import static it.fitnesschallenge.model.Fragment.LOGIN_FRAGMENT;


public class Home extends Fragment{

    private MaterialButton enterGymButton;
    private MaterialButton startOutdoor;
    private MaterialButton login;
    private static final String TAG = "Home";

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Home set button GONE");
        HomeActivity.setCurrentFragment(HOME_FRAGMENT);
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        enterGymButton = view.findViewById(R.id.enterGym);
        startOutdoor = view.findViewById(R.id.startOutdoorWorkout);
        login = view.findViewById(R.id.trainerLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login login = new Login();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                        R.anim.enter_from_rigth, R.anim.exit_from_left);
                transaction.replace(R.id.fragmentContainer, login, LOGIN_FRAGMENT)
                        .addToBackStack(LOGIN_FRAGMENT)
                        .commit();
            }
        });
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
