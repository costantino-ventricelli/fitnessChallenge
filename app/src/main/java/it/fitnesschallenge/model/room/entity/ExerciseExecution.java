package it.fitnesschallenge.model.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "exercise_execution")
public class ExerciseExecution {

    @PrimaryKey
    @ColumnInfo(name = "exercise_id")
    private int exerciseId;
    @ColumnInfo(name = "execution_date")
    private Date executionDate;
    @ColumnInfo(name = "used_kilograms")
    private float usedKilograms;

    public ExerciseExecution(Date executionDate, float usedKilograms) {
        this.executionDate = executionDate;
        this.usedKilograms = usedKilograms;
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
}
