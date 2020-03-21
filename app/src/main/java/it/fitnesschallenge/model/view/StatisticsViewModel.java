package it.fitnesschallenge.model.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.entity.Exercise;
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
