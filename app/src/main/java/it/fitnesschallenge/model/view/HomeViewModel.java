package it.fitnesschallenge.model.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;

public class HomeViewModel extends AndroidViewModel {

    public HomeViewModel(@NonNull Application application) {
        super(application);
        new FitnessChallengeRepository(application);
    }
}
