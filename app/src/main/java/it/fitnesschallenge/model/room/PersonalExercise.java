package it.fitnesschallenge.model.room;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "personal_exercise")
public class PersonalExercise extends Exercise implements Parcelable {

    private int steps;
    private int repetition;
    @Ignore
    private boolean isDeleted;

    public PersonalExercise(int imageReference, String exerciseName, String exerciseDescription, int steps, int repetition) {
        super(imageReference, exerciseName, exerciseDescription);
        this.repetition = repetition;
        this.steps = steps;
        this.isDeleted = false;
    }

    public PersonalExercise(Parcel in){
        super(in.readInt(), in.readString(), in.readString());
        this.steps = in.readInt();
        this.repetition = in.readInt();
        if (in.readInt() == 0)
            this.isDeleted = true;
        else
            this.isDeleted = false;
    }

    public PersonalExercise(Exercise exercise){
        super(exercise.getImageReference(), exercise.getExerciseName(), exercise.getExerciseDescription());
    }

    public int getSteps() {
        return steps;
    }

    public int getRepetition() {
        return repetition;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getImageReference());
        dest.writeString(getExerciseName());
        dest.writeString(getExerciseDescription());
        dest.writeInt(steps);
        dest.writeInt(repetition);
        if (isDeleted)
            dest.writeInt(1);
        else
            dest.writeInt(0);
    }

    public static final Parcelable.Creator<PersonalExercise> CREATOR
            = new Parcelable.Creator<PersonalExercise>() {
        public PersonalExercise createFromParcel(Parcel in) {
            return new PersonalExercise(in);
        }

        public PersonalExercise[] newArray(int size) {
            return new PersonalExercise[size];
        }
    };
}
