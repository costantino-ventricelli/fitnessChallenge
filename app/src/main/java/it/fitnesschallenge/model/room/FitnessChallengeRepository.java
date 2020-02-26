/**
 * Questa classe implementa il pattern di Repository facendo da mediatore tra le sorgenti dati,
 * così che non sia l'app stessa ad accedere ai dati, consentendo una modifica del metodo di persistenza
 * senza variare l'interazione con i dati stessi
 */
package it.fitnesschallenge.model.room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import it.fitnesschallenge.model.room.data.access.object.ExerciseDAO;
import it.fitnesschallenge.model.room.data.access.object.PersonalExerciseDAO;
import it.fitnesschallenge.model.room.data.access.object.PersonalExerciseWorkoutCrossReferenceDAO;
import it.fitnesschallenge.model.room.data.access.object.WorkoutDAO;
import it.fitnesschallenge.model.room.data.access.object.WorkoutWithExerciseDAO;
import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.room.entity.PersonalExerciseWorkoutCrossReference;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.reference.entity.WorkoutWithExercise;


public class FitnessChallengeRepository {

    private ExerciseDAO exerciseDAO;
    private WorkoutDAO workoutDAO;
    private WorkoutWithExerciseDAO workoutWithExerciseDAO;
    private PersonalExerciseDAO personalExerciseDAO;
    private PersonalExerciseWorkoutCrossReferenceDAO personalExerciseWorkoutCrossReferenceDAO;
    private LiveData<List<Exercise>> listExercise;
    private LiveData<WorkoutWithExercise> workoutWithExerciseList;
    private LiveData<Workout> workoutLiveData;
    private LiveData<List<Workout>> workoutList;

    public FitnessChallengeRepository(Application application){
        FitnessChallengeDatabase database = FitnessChallengeDatabase.getInstance(application);
        exerciseDAO = database.getExerciseDAO();
        workoutWithExerciseDAO = database.getWorkoutWithExerciseDAO();
        workoutDAO = database.getWorkoutDAO();
        personalExerciseDAO = database.getPersonalExerciseDAO();
        personalExerciseWorkoutCrossReferenceDAO = database.getPersonalExerciseWorkoutCrossRederenceDAO();
    }

    public LiveData<List<Exercise>> getListExercise() {
        listExercise = exerciseDAO.selectAllExercise();
        return listExercise;
    }

    public LiveData<WorkoutWithExercise> getWorkoutWithExerciseList(int workoutId) {
        workoutWithExerciseList = workoutWithExerciseDAO.getWorkoutWithExercise(workoutId);
        return workoutWithExerciseList;
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

    public void insertWorkoutWithExercise(WorkoutWithExercise workoutWithExercise) {
        workoutDAO.insertWorkout(workoutWithExercise.getWorkout());
        personalExerciseDAO.insertPersonalExercise(workoutWithExercise.getPersonalExerciseList());
        for (PersonalExercise personalExercise : workoutWithExercise.getPersonalExerciseList()) {
            personalExerciseWorkoutCrossReferenceDAO.createReference(new PersonalExerciseWorkoutCrossReference(
                    workoutWithExercise.getWorkout().getWorkOutId(),
                    personalExercise.getExerciseId()));
        }
    }
}
