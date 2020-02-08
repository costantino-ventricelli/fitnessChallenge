package it.fitnesschallenge.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private FitnessChallengeRepository repository;
    private LiveData<List<ExerciseTable>> allExercise;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new FitnessChallengeRepository(application);
        allExercise = repository.selectAllExercise();
    }

    public LiveData<List<ExerciseTable>> getAllExercise(){
        return allExercise;
    }
}
