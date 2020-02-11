/**
 * Questa classe rappresenta una tabella del DB l'architerrura della Room usa dei marcatori per
 * identidicare le varie componenti del DB ad esempio @Entity o @PrimaryKey
 */

package it.fitnesschallenge.model.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercise")
public class ExerciseTable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exercise_id")
    private int exerciseId;
    @ColumnInfo(name = "image_reference")
    private int imageReference;
    @ColumnInfo(name = "exercise_ame")
    private String exerciseName;
    @ColumnInfo(name =  "exercise_description")
    private String exerciseDescription;

    public ExerciseTable(int imageReference, String exerciseName, String exerciseDescription) {
        this.imageReference = imageReference;
        this.exerciseName = exerciseName;
        this.exerciseDescription = exerciseDescription;
    }

    public int getId() {
        return exerciseId;
    }

    public int getImageReference() {
        return imageReference;
    }

    public String getExerciseDescription(){
        return exerciseDescription;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setId(int id) {
        this.exerciseId = id;
    }
}
