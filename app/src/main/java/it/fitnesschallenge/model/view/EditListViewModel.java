package it.fitnesschallenge.model.view;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.room.entity.PersonalExerciseWorkoutCrossReference;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.entity.reference.WorkoutWithExercise;

public class EditListViewModel extends AndroidViewModel {

    private MutableLiveData<List<PersonalExercise>> mPersonalExerciseListLiveData;
    private List<PersonalExercise> mPersonalExerciseList;
    private List<PersonalExercise> mDeleteExerciseList;
    private FitnessChallengeRepository mRepository;
    private boolean mFirstExecution;
    private Workout mWorkout;

    public EditListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new FitnessChallengeRepository(application);
        mPersonalExerciseListLiveData = new MutableLiveData<>();
        mFirstExecution = true;
        mWorkout = null;
    }

    public MutableLiveData<List<PersonalExercise>> getPersonalExerciseList() {
        return mPersonalExerciseListLiveData;
    }

    public void setPersonalExerciseList(List<PersonalExercise> personalExerciseList) {
        this.mPersonalExerciseList = personalExerciseList;
        this.mPersonalExerciseListLiveData.setValue(personalExerciseList);
    }

    public LiveData<WorkoutWithExercise> getSavedExerciseList(long workoutId) {
        return mRepository.getWorkoutWithExerciseList(workoutId);
    }

    public LiveData<List<Workout>> getWorkoutList() {
        return mRepository.getWorkoutList();
    }

    public boolean isFirstExecution() {
        return mFirstExecution;
    }

    public void setFirstExecution(boolean mFirstExecution) {
        this.mFirstExecution = mFirstExecution;
    }

    public Workout getWorkout() {
        return mWorkout;
    }

    public void setWorkout(Workout mWorkout) {
        this.mWorkout = mWorkout;
    }

    public void addNewExerciseToWorkout() {
        AddNewExerciseToWorkout addNewExerciseToWorkout = new AddNewExerciseToWorkout(mRepository);
        addNewExerciseToWorkout.execute(new WorkoutWithExercise(mWorkout, mPersonalExerciseList));
    }

    public void deleteExerciseFromWorkout() {
        DeleteExerciseFromWorkout deleteExerciseFromWorkout = new DeleteExerciseFromWorkout(mRepository);
        deleteExerciseFromWorkout.execute(mDeleteExerciseList);
        //TODO: finire cancellazione e update workout.
    }

    public LiveData<Boolean> addWorkoutWithExercise() {
        return null;
    }

    static class AddNewExerciseToWorkout extends AsyncTask<WorkoutWithExercise, Void, Void> {

        private FitnessChallengeRepository mRepository;

        AddNewExerciseToWorkout(FitnessChallengeRepository repository) {
            this.mRepository = repository;
        }

        @Override
        protected Void doInBackground(WorkoutWithExercise... workoutWithExercises) {
            Workout workout = workoutWithExercises[0].getWorkout();
            List<PersonalExercise> personalExerciseList = workoutWithExercises[0].getPersonalExerciseList();
            ArrayList<Long> newIdArrayList = new ArrayList<>();
            for (PersonalExercise personalExercise : personalExerciseList) {
                if (personalExercise.getPersonalExerciseId() < 0)
                    newIdArrayList.add(mRepository.insertPersonalExercise(personalExercise));
            }
            for (Long id : newIdArrayList) {
                PersonalExerciseWorkoutCrossReference crossReference = new PersonalExerciseWorkoutCrossReference(workout.getWorkOutId(), id);
                mRepository.insertPersonalExerciseWorkoutReference(crossReference);
            }
            return null;
        }
    }

    static class DeleteExerciseFromWorkout extends AsyncTask<List<PersonalExercise>, Void, Void> {

        private FitnessChallengeRepository mRepository;

        DeleteExerciseFromWorkout(FitnessChallengeRepository mRepository) {
            this.mRepository = mRepository;
        }

        @Override
        protected Void doInBackground(List<PersonalExercise>... personalExercises) {
            List<PersonalExercise> personalExerciseList = personalExercises[0];
            for (PersonalExercise personalExercise : personalExerciseList) {
                if (personalExercise.getPersonalExerciseId() < 0) {
                    personalExerciseList.remove(personalExercise);
                }
            }
            mRepository.deletePersonalExerciseList(personalExerciseList);
            return null;
        }
    }
}
