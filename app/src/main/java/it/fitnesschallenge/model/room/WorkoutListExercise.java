/**
 * Questa classe rappresenta l'entità di collegamento per la relazione molti-a-molti tra Workout ed
 * ExerciseTable
 */
package it.fitnesschallenge.model.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"exercise_id", "workout_id"})
public class WorkoutListExercise {

    @ColumnInfo(name = "exercise_id")
    private int exerciseId;
    @ColumnInfo(name = "workout_id")
    private int workoutId;
    @ColumnInfo(name = "step")
    private int step;
    @ColumnInfo(name = "repetition")
    private int repetition;

    public WorkoutListExercise(int step, int repetition) {
        this.step = step;
        this.repetition = repetition;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public int getStep() {
        return step;
    }

    public int getRepetition() {
        return repetition;
    }
}
