package it.fitnesschallenge.model.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Date;
import java.util.List;

import it.fitnesschallenge.model.room.entity.ExerciseExecution;

@Dao
public interface ExerciseExecutionDAO {

    @Insert
    void insertExecution(ExerciseExecution exerciseExecution);

    @Transaction
    @Query("SELECT exercise_id, execution_date, used_kilograms FROM exercise_execution " +
            "NATURAL JOIN personal_exercise " +
            "WHERE exercise_id = :personalExerciseId AND execution_date " +
            "= (SELECT MAX(execution_date) FROM exercise_execution)")
    LiveData<ExerciseExecution> selectLastExerciseExecution(long personalExerciseId);

    @Transaction
    @Query("SELECT * FROM exercise_execution WHERE execution_date = :currentDate")
    LiveData<List<ExerciseExecution>> selectExecutionInDate(Date currentDate);
}
