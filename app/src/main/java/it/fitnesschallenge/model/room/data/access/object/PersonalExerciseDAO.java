package it.fitnesschallenge.model.room.data.access.object;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

import it.fitnesschallenge.model.room.entity.PersonalExercise;

@Dao
public interface PersonalExerciseDAO {


    @Insert
    void insertPersonalExercise(List<PersonalExercise> personalExerciseList);
}
