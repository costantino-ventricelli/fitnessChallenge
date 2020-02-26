package it.fitnesschallenge.model.room.data.access.object;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.fitnesschallenge.model.room.reference.entity.ExerciseAndPersonalExercise;

@Dao
public interface ExerciseAndPersonalExerciseDao {

    @Transaction
    @Query("SELECT * FROM exercise")
    LiveData<List<ExerciseAndPersonalExercise>> selectAllPersonalExercise();
}
