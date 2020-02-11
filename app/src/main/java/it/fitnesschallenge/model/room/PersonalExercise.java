package it.fitnesschallenge.model.room;


import androidx.room.Entity;

@Entity(tableName = "personal_exercise")
public class PersonalExercise extends Exercise {

    private int steps;
    private int repetition;

    public PersonalExercise(int imageReference, String exerciseName, String exerciseDescription, int steps, int repetition) {
        super(imageReference, exerciseName, exerciseDescription);

        this.repetition = repetition;
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public int getRepetition() {
        return repetition;
    }
}
