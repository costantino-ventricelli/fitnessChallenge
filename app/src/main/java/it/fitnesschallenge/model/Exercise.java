/**
 * Classe che descrive l'entità esercizio, abbiamo usato il modello della Room perché più affidabile
 * del modello SQLite diretto
 */

package it.fitnesschallenge.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * L'annotazione @Entity indica appunto un'entità del DB
 * L'annotazione @PrimaryKey indica la chiave primaria dell'entità, di tipo auto generata o (Auto increment)
 */
@Entity (tableName = "Exercise")
public class Exercise{
    @PrimaryKey (autoGenerate = true)
    private int exerciseId;
    private int imageId;
    private String exerciseName;
    private String exerciseDescription;

    public Exercise(int imageId, String exerciseName, String exerciseDescription) {
        this.imageId = imageId;
        this.exerciseName = exerciseName;
        this.exerciseDescription = exerciseDescription;
    }

    public int getExerciseId(){
        return exerciseId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getExerciseDescription() {
        return exerciseDescription;
    }

    public void setExerciseDescription(String exerciseDescription) {
        this.exerciseDescription = exerciseDescription;
    }
}
