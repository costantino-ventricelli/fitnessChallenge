/**
 * Questa classe è necessaria per l'implementazione della relazione tra workout ed exercise table,
 * in pratica permette di eseguire query di Join tra workout e exercise table senza che debbano essere
 * scritte, incrementando l'efficienza del DB
 */
package it.fitnesschallenge.model.room;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class WorkoutAndExercise {

    @Embedded
    private Workout workout;
    @Relation(
            parentColumn = "workout_id",
            entityColumn = "exercise_id",
            associateBy = @Junction(WorkoutListExercise.class)
    )
    private List<ExerciseTable> exerciseTableList;
}
