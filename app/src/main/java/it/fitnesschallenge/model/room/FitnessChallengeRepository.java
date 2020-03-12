/**
 * Questa classe implementa il pattern di Repository facendo da mediatore tra le sorgenti dati,
 * così che non sia l'app stessa ad accedere ai dati, consentendo una modifica del metodo di persistenza
 * senza variare l'interazione con i dati stessi
 */
package it.fitnesschallenge.model.room;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;

import it.fitnesschallenge.model.room.dao.ExerciseDAO;
import it.fitnesschallenge.model.room.dao.ExerciseExecutionDAO;
import it.fitnesschallenge.model.room.dao.PersonalExerciseDAO;
import it.fitnesschallenge.model.room.dao.PersonalExerciseWorkoutCrossReferenceDAO;
import it.fitnesschallenge.model.room.dao.WorkoutDAO;
import it.fitnesschallenge.model.room.dao.WorkoutWithExerciseDAO;
import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.room.entity.PersonalExerciseWorkoutCrossReference;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.entity.reference.PersonalExerciseWithExecution;
import it.fitnesschallenge.model.room.entity.reference.WorkoutWithExercise;

public class FitnessChallengeRepository {

    private static final String TAG = "FitnessChallengeReposit";

    private ExerciseDAO exerciseDAO;
    private WorkoutDAO workoutDAO;
    private WorkoutWithExerciseDAO workoutWithExerciseDAO;
    private PersonalExerciseDAO personalExerciseDAO;
    private ExerciseExecutionDAO exerciseExecutionDAO;
    private PersonalExerciseWorkoutCrossReferenceDAO personalExerciseWorkoutCrossReferenceDAO;
    private LiveData<List<Exercise>> listExercise;
    private LiveData<WorkoutWithExercise> workoutWithExerciseList;
    private LiveData<Workout> workoutLiveData;
    private LiveData<List<Workout>> workoutList;
    private LiveData<PersonalExerciseWithExecution> personalExerciseWithExecution;

    public FitnessChallengeRepository(Application application){
        FitnessChallengeDatabase database = FitnessChallengeDatabase.getInstance(application);
        exerciseDAO = database.getExerciseDAO();
        workoutWithExerciseDAO = database.getWorkoutWithExerciseDAO();
        workoutDAO = database.getWorkoutDAO();
        personalExerciseDAO = database.getPersonalExerciseDAO();
        personalExerciseWorkoutCrossReferenceDAO = database.getPersonalExerciseWorkoutCrossReferenceDAO();
        exerciseExecutionDAO = database.getExerciseExecutionDAO();
        workoutWithExerciseList = new MutableLiveData<>();
    }

    public LiveData<List<Exercise>> getListExerciseLiveData() {
        listExercise = exerciseDAO.selectAllExercise();
        return listExercise;
    }

    public LiveData<WorkoutWithExercise> getWorkoutWithExerciseList(long workoutId) {
        workoutWithExerciseList = workoutWithExerciseDAO.getWorkoutWithExercise(workoutId);
        return workoutWithExerciseList;
    }

    public List<PersonalExercise> getPersonalExerciseList(long workoutId) {
        return workoutWithExerciseDAO.getPersonalExerciseList(workoutId);
    }

    public LiveData<Workout> getWorkoutLiveData(int workoutId) {
        workoutLiveData = workoutDAO.getWorkout(workoutId);
        return workoutLiveData;
    }

    public LiveData<List<Workout>> getWorkoutList() {
        workoutList = workoutDAO.getAllWorkOut();
        return workoutList;
    }

    public LiveData<Exercise> getExercise(int exerciseId) {
        return exerciseDAO.selectExercise(exerciseId);
    }

    public LiveData<Long> getWorkoutIdWithStartDate(Date date) {
        Log.d(TAG, "Start date: " + date.toString());
        return workoutDAO.getWorkoutStartDate(date);
    }

    public LiveData<PersonalExerciseWithExecution> getLastExecutionExecution(long personalExerciseId) {
        return exerciseExecutionDAO.selectLastExerciseExecution(personalExerciseId);
    }

    public long insertWorkoutWithExercise(WorkoutWithExercise workoutWithExercise) {
        long workoutId = workoutDAO.insertWorkout(workoutWithExercise.getWorkout());
        long[] exerciseIds = personalExerciseDAO.insertPersonalExercise(workoutWithExercise.getPersonalExerciseList());
        Log.d(TAG, "Workout id: " + workoutId);
        for (long exerciseId : exerciseIds) {
            Log.d(TAG, "Exercise id: " + exerciseId);
            personalExerciseWorkoutCrossReferenceDAO.createReference(new PersonalExerciseWorkoutCrossReference(
                    workoutId,
                    exerciseId));
        }
        return workoutId;
    }
}
