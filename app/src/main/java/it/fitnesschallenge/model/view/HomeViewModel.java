package it.fitnesschallenge.model.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.fitnesschallenge.model.room.ExerciseTable;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;

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
