package it.fitnesschallenge.model.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

@Entity(tableName = "exercise_execution", primaryKeys = {"exercise_id", "execution_date"})
public class ExerciseExecution {

    @NonNull
    @ColumnInfo(name = "exercise_id")
    private int exerciseId;
    @NonNull
    @ColumnInfo(name = "execution_date")
    private Date executionDate;
    @ColumnInfo(name = "used_kilograms")
    private List<Float> usedKilograms;

    public ExerciseExecution(@NonNull Date executionDate, List<Float> usedKilograms) {
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

    public void setExecutionDate(@NotNull Date executionDate) {
        this.executionDate = executionDate;
    }

    public List<Float> getUsedKilograms() {
        return usedKilograms;
    }

    public void setUsedKilograms(List<Float> usedKilograms) {
        this.usedKilograms = usedKilograms;
    }
}
