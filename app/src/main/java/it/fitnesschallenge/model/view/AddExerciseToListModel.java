package it.fitnesschallenge.model.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.fitnesschallenge.model.room.Exercise;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;

public class AddExerciseToListModel extends AndroidViewModel {

    private LiveData<List<Exercise>> mExerciseList;

    public AddExerciseToListModel(@NonNull Application application) {
        super(application);
        FitnessChallengeRepository repository = new FitnessChallengeRepository(application);
        mExerciseList = repository.getListExercise();
    }

    public LiveData<List<Exercise>> getExerciseList(){
        return mExerciseList;
    }
}
