/**
 * Questa interfaccia implementa il design pattern del Data Access Object rendendo questa l'unica
 * classe in grado di accedere all'entita Exercise in questo caso, aumentando la sicurezza dell'app
 */
package it.fitnesschallenge.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExerciseDAO {
    /**
     * Le annotazioni @Insert, @Update, @Delete indicano funzioni auto generate dalla Room che esequono
     * queste operazioni di base.
     * L'annotazone @Query indica una query specificata da noi, la cosa potente di questo metodo implementativo
     * del DB locale è che Andorid Studio e Andorid SO eseguono controlli preliminari sul codice che scriviamo
     */

    @Insert
    void insert(Exercise exercise);

    @Update
    void update(Exercise exercise);

    @Delete
    void delete(Exercise exercise);

    /*
     * Da qui in poi vanno inserite tutte le query che si renderanno necessarie per lo sviluppo
     * dell applicativo
     */

    // Query che seleziona tutte le tuple della tabella Exercise
    @Query("SELECT * FROM Exercise")
    List<Exercise> selectAllEntry();

}
