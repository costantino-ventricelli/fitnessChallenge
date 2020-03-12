package it.fitnesschallenge.model.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.fitnesschallenge.model.room.entity.ExerciseExecution;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.room.entity.reference.PersonalExerciseWithExecution;

@Dao
public interface ExerciseExecutionDAO {

    @Insert
    void insertExecution(PersonalExercise personalExercise, List<ExerciseExecution> exerciseExecution);

    @Transaction
    @Query("SELECT * FROM exercise_execution " +
            "NATURAL JOIN personal_exercise " +
            "WHERE exercise_id = :personalExerciseId AND execution_date " +
            "= (SELECT MAX(execution_date) FROM exercise_execution)")
    LiveData<PersonalExerciseWithExecution> selectLastExerciseExecution(long personalExerciseId);
}
