package it.fitnesschallenge.model.room.data.access.object;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Date;
import java.util.List;

import it.fitnesschallenge.model.room.entity.ExerciseExecution;
import it.fitnesschallenge.model.room.entity.PersonalExercise;

@Dao
public interface ExerciseExecutionDao {

    @Insert
    void insertExecution(PersonalExercise personalExercise, List<ExerciseExecution> exerciseExecution);

    @Transaction
    @Query("SELECT * FROM exercise_execution WHERE execution_date = :executionDate AND exercise_id = :exerciseId")
    LiveData<List<ExerciseExecution>> selectExecution(Date executionDate, int exerciseId);
}
