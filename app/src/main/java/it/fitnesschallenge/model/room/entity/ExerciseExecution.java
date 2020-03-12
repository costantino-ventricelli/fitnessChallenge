package it.fitnesschallenge.model.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "exercise_execution", primaryKeys = {"exercise_id", "execution_date", "repetition"})
public class ExerciseExecution {

    @NonNull
    @ColumnInfo(name = "exercise_id")
    private int exerciseId;
    @NonNull
    @ColumnInfo(name = "execution_date")
    private Date executionDate;
    @ColumnInfo(name = "used_kilograms")
    private float usedKilograms;
    @NonNull
    private int repetition;

    public ExerciseExecution(Date executionDate, float usedKilograms, int repetition) {
        this.executionDate = executionDate;
        this.usedKilograms = usedKilograms;
        this.repetition = repetition;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public float getUsedKilograms() {
        return usedKilograms;
    }

    public void setUsedKilograms(float usedKilograms) {
        this.usedKilograms = usedKilograms;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }
}
