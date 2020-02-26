package it.fitnesschallenge;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.view.HomeViewModel;

import static it.fitnesschallenge.model.SharedConstance.HOME_FRAGMENT;
import static it.fitnesschallenge.model.SharedConstance.LAST_FRAGMENT;


public class HomeActivity extends AppCompatActivity{

    private static final String TAG = "HomeActivity";
    private Home mHomeFragment;
    private BottomNavigationView mBottomNavigation;
    private static ImageButton mBackButton;
    private HomeViewModel homeViewModel;
    private static Context mContext;
    private boolean isHome;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.getExerciseList().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                Log.d(TAG, "Esercizi prelevati dal DB: " + exercises.size());
            }
        });
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBackButton = findViewById(R.id.btn_back);
        mBackButton.setVisibility(View.GONE);
        mContext = this;

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null)
            mPendingIntent = PendingIntent.getActivity(mContext,
                    0, new Intent(this, HomeActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if (savedInstanceState == null) {
            mBottomNavigation.setSelectedItemId(R.id.navigation_home);
            mHomeFragment = new Home();
            isHome = true;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, mHomeFragment, HOME_FRAGMENT)
                    .addToBackStack(HOME_FRAGMENT)
                    .commit();
        } else {
            String lastFragment = savedInstanceState.getString(LAST_FRAGMENT);
            setCurrentFragment(lastFragment);
        }

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStackImmediate();
                try {
                    Log.d(TAG, "back button pressed");
                    setCurrentFragment(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer).getTag());
                } catch (NullPointerException ex) {
                    Toast.makeText(mContext, mContext.getString(R.string.shit_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void setCurrentFragment(String currentFragment) {
        Log.d(TAG, "SetCurrentFragment on: " + currentFragment);
        if (HOME_FRAGMENT.equals(currentFragment)) {
            mBackButton.setVisibility(View.GONE);
        } else {
            mBackButton.setVisibility(View.VISIBLE);
        }
    }

    public static Context getHomeActivityContext() {
        return mContext;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled())
                turnOnNFC();
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    private void turnOnNFC() {
        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && mNfcAdapter != null) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                Parcelable[] rawMessage = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMessage != null) {
                    NdefMessage[] messages = new NdefMessage[rawMessage.length];
                    for (int i = 0; i < rawMessage.length; i++)
                        messages[i] = (NdefMessage) rawMessage[i];
                    Log.d(TAG, "Messagio ricevuto NFC: " + messages[1]);
                    //TODO: gli intent sono parcelizabili quindi si possono passare al fragment
                }
            }
        }
    }
}
