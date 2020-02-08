package it.fitnesschallenge.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class FitnessChallengeRepository {

    private ExerciseDAO exerciseDAO;
    private LiveData<List<ExerciseTable>> listLiveData;

    public FitnessChallengeRepository(Application application){
        FitnessChallengeDatabase database = FitnessChallengeDatabase.getInstance(application);
        exerciseDAO = database.exerciseDAO();
        listLiveData = exerciseDAO.selectAllExercise();
    }

    public LiveData<List<ExerciseTable>> selectAllExercise(){
        return listLiveData;
    }
}
