package it.fitnesschallenge.model.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import it.fitnesschallenge.model.room.entity.Workout;

public class GetWorkoutFromDBModel extends AndroidViewModel {
    private static final String TAG = "GetWorkoutFromDBModel";

    private MutableLiveData<Workout> workoutMutableLiveData;

    public GetWorkoutFromDBModel(@NonNull Application application) {
        super(application);
        workoutMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Workout> getWorkoutMutableLiveData() {
        return workoutMutableLiveData;
    }
    public void setWorkoutMutableLiveData(Workout workout) {
        this.workoutMutableLiveData.setValue(workout);
    }
}
