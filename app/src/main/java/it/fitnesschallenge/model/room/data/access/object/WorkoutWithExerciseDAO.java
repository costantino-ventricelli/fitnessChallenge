package it.fitnesschallenge.model.room.data.access.object;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import it.fitnesschallenge.model.room.reference.entity.WorkoutWithExercise;

@Dao
public interface WorkoutWithExerciseDAO {

    @Transaction
    @Query("SELECT * FROM workout WHERE workout_id = :workoutId")
    LiveData<WorkoutWithExercise> getWorkoutWithExercise(int workoutId);
}
