package it.fitnesschallenge.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface WorkoutWithExerciseDAO {

    @Transaction
    @Query("SELECT * FROM workout WHERE workout_id = :workoutId")
    LiveData<WorkoutWithExercise> getWorkoutWithExercise(int workoutId);
}
