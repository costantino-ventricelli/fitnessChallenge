package it.fitnesschallenge.model.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

import it.fitnesschallenge.model.room.entity.PersonalExercise;

@Dao
public interface PersonalExerciseDAO {


    @Insert
    long[] insertPersonalExercise(List<PersonalExercise> personalExerciseList);
}
