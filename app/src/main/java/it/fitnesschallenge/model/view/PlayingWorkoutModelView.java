package it.fitnesschallenge.model.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import it.fitnesschallenge.model.room.Exercise;
import it.fitnesschallenge.model.room.ExerciseExecution;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.PersonalExercise;
import it.fitnesschallenge.model.room.Workout;
import it.fitnesschallenge.model.room.WorkoutWithExercise;

public class PlayingWorkoutModelView extends AndroidViewModel {

    private ArrayList<ExerciseExecution> mExerciseExecutionList;
    private ArrayList<Exercise> mExerciseArrayList;
    private ListIterator<PersonalExercise> mPersonalExerciseListIterator;
    private ArrayList<PersonalExercise> mPersonalExerciseList;
    private MutableLiveData<List<ExerciseExecution>> mExerciseExecution;
    private FitnessChallengeRepository mRepository;
    private int mWorkoutId;

    public PlayingWorkoutModelView(@NonNull Application application) {
        super(application);
        mRepository = new FitnessChallengeRepository(application);
        mWorkoutId = -1;

        MutableLiveData<WorkoutWithExercise> mWorkoutWithExercise = (MutableLiveData<WorkoutWithExercise>) mRepository.getWorkoutWithExerciseList(mWorkoutId);
        mPersonalExerciseList = (ArrayList<PersonalExercise>) mWorkoutWithExercise.getValue().getPersonalExerciseList();
        mPersonalExerciseListIterator = mPersonalExerciseList.listIterator();

        MutableLiveData<List<Exercise>> mExerciseList = (MutableLiveData<List<Exercise>>) mRepository.getListExercise();
        mExerciseArrayList = (ArrayList<Exercise>) mExerciseList.getValue();

        mExerciseExecutionList = new ArrayList<>();
        mExerciseExecution = new MutableLiveData<>();
    }

    public void setExerciseExecution(ExerciseExecution exerciseExecution) {
        mExerciseExecutionList.add(exerciseExecution);
        this.mExerciseExecution.setValue(mExerciseExecutionList);
    }

    public LiveData<PersonalExercise> getNextExercise() {
        if (mPersonalExerciseListIterator.hasNext())
            return new MutableLiveData<>(mPersonalExerciseListIterator.next());
        else
            return null;
    }

    public LiveData<PersonalExercise> getPrevExercise() {
        if (mPersonalExerciseListIterator.hasPrevious())
            return new MutableLiveData<>(mPersonalExerciseListIterator.previous());
        else
            return null;
    }

    public LiveData<Exercise> getExerciseInformation(PersonalExercise personalExercise) {
        return new MutableLiveData<>(mExerciseArrayList.get(
                mExerciseArrayList.indexOf(new Exercise(personalExercise))
        ));
    }

    public LiveData<Boolean> setActiveWorkoutFromLocal() {
        List<Workout> workoutList = mRepository.getWorkoutList().getValue();
        if (workoutList != null)
            for (Workout workout : workoutList) {
                if (workout.isActive())
                    mWorkoutId = workout.getWorkOutId();
            }
        return new MutableLiveData<>(mWorkoutId != -1);
    }
}
