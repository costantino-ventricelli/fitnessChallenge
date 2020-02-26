package it.fitnesschallenge.model.room.reference.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.room.entity.PersonalExerciseWorkoutCrossReference;
import it.fitnesschallenge.model.room.entity.Workout;

public class WorkoutWithExercise {

    @Embedded
    private Workout workout;
    @Relation(
            parentColumn = "workout_id",
            entityColumn = "exercise_id",
            associateBy = @Junction(PersonalExerciseWorkoutCrossReference.class)
    )
    private List<PersonalExercise> personalExerciseList;

    public WorkoutWithExercise() {
        // Required empty constructor
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public List<PersonalExercise> getPersonalExerciseList() {
        return personalExerciseList;
    }

    public void setPersonalExerciseList(List<PersonalExercise> personalExerciseList) {
        this.personalExerciseList = personalExerciseList;
    }
}
