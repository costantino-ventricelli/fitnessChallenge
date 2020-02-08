package it.fitnesschallenge.model;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExerciseDAO {

    @Insert
    void insert(ExerciseTable listLiveData);

    @Query("SELECT * FROM exercise")
    LiveData<List<ExerciseTable>> selectAllExercise();
}
