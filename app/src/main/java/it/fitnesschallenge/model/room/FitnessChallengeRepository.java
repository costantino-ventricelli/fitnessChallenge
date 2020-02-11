/**
 * Questa classe implementa il pattern di Repository facendo da mediatore tra le sorgenti dati,
 * così che non sia l'app stessa ad accedere ai dati, consentendo una modifica del metodo di persistenza
 * senza variare l'interazione con i dati stessi
 */
package it.fitnesschallenge.model.room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class FitnessChallengeRepository {

    private ExerciseDAO exerciseDAO;
    private WorkoutAndExerciseDAO workoutAndExerciseDAO;
    private WorkoutDAO workoutDAO;
    private LiveData<List<ExerciseTable>> listExercise;
    private LiveData<List<Workout>> listWorkout;
    private LiveData<List<WorkoutAndExercise>> listWorkoutAndExercise;

    public FitnessChallengeRepository(Application application){
        FitnessChallengeDatabase database = FitnessChallengeDatabase.getInstance(application);
        exerciseDAO = database.getExerciseDAO();
        workoutDAO = database.getWorkoutDao();
        workoutAndExerciseDAO = database.getWorkoutListAndExereciseDAO();
    }

    public LiveData<List<ExerciseTable>> getListExercise() {
        listExercise = exerciseDAO.selectAllExercise();
        return listExercise;
    }

    public LiveData<List<Workout>> getListWorkout(int workoutId) {
        listWorkout = workoutDAO.getWorkout(workoutId);
        return listWorkout;
    }

    public LiveData<List<Workout>> getListWorkout(){
        listWorkout = workoutDAO.getAllWorkOut();
        return listWorkout;
    }

    public LiveData<List<WorkoutAndExercise>> getListWorkoutAndExercise(int workoutAndExercise) {
        listWorkoutAndExercise = workoutAndExerciseDAO.getWorkOutAndExercise(workoutAndExercise);
        return listWorkoutAndExercise;
    }
}
