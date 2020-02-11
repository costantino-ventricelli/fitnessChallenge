package it.fitnesschallenge.model.room;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface ExerciseDAO {

    @Insert
    void insert(Exercise listLiveData);

    @Transaction
    @Query("SELECT * FROM Exercise")
    LiveData<List<Exercise>> selectAllExercise();
}
