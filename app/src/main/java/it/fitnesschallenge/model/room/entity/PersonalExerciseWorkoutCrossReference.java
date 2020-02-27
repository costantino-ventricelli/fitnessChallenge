package it.fitnesschallenge.model.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(primaryKeys = {"workout_id", "exercise_id"},
        tableName = "personal_exercise_workout_cross_reference",
        indices = {@Index("exercise_id"), @Index("workout_id")})
public class PersonalExerciseWorkoutCrossReference {

    @ColumnInfo(name = "workout_id")
    private long workoutId;
    @ColumnInfo(name = "exercise_id")
    private long exerciseId;

    public PersonalExerciseWorkoutCrossReference(long workoutId, long exerciseId) {
        this.workoutId = workoutId;
        this.exerciseId = exerciseId;
    }

    public long getWorkoutId() {
        return workoutId;
    }

    public long getExerciseId() {
        return exerciseId;
    }
}
