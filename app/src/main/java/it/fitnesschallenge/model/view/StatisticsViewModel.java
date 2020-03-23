package it.fitnesschallenge.model.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.entity.ExerciseExecution;

public class StatisticsViewModel extends AndroidViewModel {

    private static final String TAG = "StatisticsViewModel";

    private FitnessChallengeRepository mRepository;

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new FitnessChallengeRepository(application);
    }

    public LiveData<Integer> getNumberOfExecution() {
        return mRepository.getNumberOfExecution();
    }

    public LiveData<List<ExerciseExecution>> getExerciseExecutionList() {
        return mRepository.getLastUsedKilograms();
    }
}
