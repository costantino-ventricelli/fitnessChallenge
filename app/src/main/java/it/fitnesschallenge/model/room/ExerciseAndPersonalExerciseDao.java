package it.fitnesschallenge.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface ExerciseAndPersonalExerciseDao {

    @Transaction
    @Query("SELECT * FROM exercise")
    LiveData<List<ExerciseAndPersonalExercise>> selectAllPersonalExercise();
}
