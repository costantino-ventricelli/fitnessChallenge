/**
 * Questo DAO permette di eseguire la query di Join descritta nella classe WorkoutAndExercise
 * utilizzando l'id del workout per filtrare i dati
 */
package it.fitnesschallenge.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface WorkoutAndExerciseDAO {

    @Transaction
    @Query("SELECT * FROM Workout WHERE work_out_id =  :workOutId")
    LiveData<List<WorkoutAndExercise>> getWorkOutAndExercise(int workOutId);
}
