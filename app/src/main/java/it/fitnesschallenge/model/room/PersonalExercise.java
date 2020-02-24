package it.fitnesschallenge.model.room;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "personal_exercise")
public class PersonalExercise implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "exercise_id")
    private int exerciseId;
    private int steps;
    private int repetition;
    @Ignore
    private boolean isDeleted;

    @Ignore
    public PersonalExercise(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public PersonalExercise(int exerciseId, int steps, int repetition) {
        this.exerciseId = exerciseId;
        this.repetition = repetition;
        this.steps = steps;
        this.isDeleted = false;
    }

    public PersonalExercise(Parcel in){
        this.steps = in.readInt();
        this.repetition = in.readInt();
        if (in.readInt() == 0)
            this.isDeleted = true;
        else
            this.isDeleted = false;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof PersonalExercise) {
            return ((PersonalExercise) obj).getExerciseId() == this.exerciseId;
        } else
            return false;
    }
}
