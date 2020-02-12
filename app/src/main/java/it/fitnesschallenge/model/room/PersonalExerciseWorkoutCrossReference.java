package it.fitnesschallenge.model.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(primaryKeys = {"workout_id", "exercise_id"},
        tableName = "personal_exercise_workout_cross_reference",
        indices = {@Index("exercise_id"), @Index("workout_id")})
public class PersonalExerciseWorkoutCrossReference {

    @ColumnInfo(name = "workout_id")
    private int workoutId;
    @ColumnInfo(name = "exercise_id")
    private int exerciseId;

    public PersonalExerciseWorkoutCrossReference(int workoutId, int exerciseId) {
        this.workoutId = workoutId;
        this.exerciseId = exerciseId;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public int getExerciseId() {
        return exerciseId;
    }
}
