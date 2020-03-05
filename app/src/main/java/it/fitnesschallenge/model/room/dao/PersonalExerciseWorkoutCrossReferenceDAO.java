package it.fitnesschallenge.model.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import it.fitnesschallenge.model.room.entity.PersonalExerciseWorkoutCrossReference;

@Dao
public interface PersonalExerciseWorkoutCrossReferenceDAO {

    @Insert
    void createReference(PersonalExerciseWorkoutCrossReference personalExerciseWorkoutCrossReference);
}
