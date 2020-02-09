package it.fitnesschallenge;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import it.fitnesschallenge.model.room.ExerciseTable;
import it.fitnesschallenge.model.view.HomeViewModel;

import static it.fitnesschallenge.model.Fragment.HOME_FRAGMENT;
import static it.fitnesschallenge.model.Fragment.LAST_FRAGMENT;
import static it.fitnesschallenge.model.Fragment.PROFILE_FRAGMENT;
import static it.fitnesschallenge.model.Fragment.SETTING_FRAGMENT;
import static it.fitnesschallenge.model.Fragment.TIMER_FRAGMENT;


public class HomeActivity extends AppCompatActivity implements Login.OnChangeFragment, SignUpFragment.OnChangeFragment {

    private static final String TAG = "HomeActivity";
    private Home mHomeFragment;
    private BottomNavigationView mBottomNavigation;
    private ImageButton mBackButton;
    private HomeViewModel homeViewModel;
    private static Context mContext;
    private boolean isHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.getAllExercise().observe(this, new Observer<List<ExerciseTable>>() {
            @Override
            public void onChanged(List<ExerciseTable> exerciseTables) {
                //necessario per il ViewModel
            }
        });
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBackButton = findViewById(R.id.btn_back);
        mBackButton.setVisibility(View.GONE);
        mContext = this;

        if(savedInstanceState == null){
            mBottomNavigation.setSelectedItemId(R.id.navigation_home);
            mHomeFragment = new Home();
            isHome = true;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, mHomeFragment, HOME_FRAGMENT)
                    .addToBackStack(HOME_FRAGMENT)
                    .commit();
        }else{
            String lastFragment = savedInstanceState.getString(LAST_FRAGMENT);
            setCurrentFragment(lastFragment);
        }

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStackImmediate();
                try {
                    setCurrentFragment(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer).getTag());
                }catch (NullPointerException ex){
                    Toast.makeText(mContext, mContext.getString(R.string.shit_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setCurrentFragment(String lastFragment){
        switch (lastFragment){
            case PROFILE_FRAGMENT:
                break;
            case TIMER_FRAGMENT:
                break;
            case SETTING_FRAGMENT:
                break;
            case HOME_FRAGMENT:
                mBackButton.setVisibility(View.GONE);
                break;
        }
    }

    public static Context getHomeActivityContext(){
        return mContext;
    }

    @Override
    public void onChangeFragment() {
        mBackButton.setVisibility(View.VISIBLE);
    }
}
