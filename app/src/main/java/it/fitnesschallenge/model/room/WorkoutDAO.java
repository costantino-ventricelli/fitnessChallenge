/**
 * Questo DAO permette di eseguire operazioni sulle tuple dell'entità workout
 */
package it.fitnesschallenge.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface WorkoutDAO {

    @Insert
    void insertWorkout(Workout workout);

    @Delete
    void deleteWorkout(Workout workout);

    @Transaction
    @Query("SELECT * FROM workout WHERE workout_id = :workoutId")
    LiveData<Workout> getWorkout(int workoutId);

    @Transaction
    @Query("SELECT * FROM workout")
    LiveData<List<Workout>> getAllWorkOut();
}
