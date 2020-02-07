/**
 * Questa classe astratta "implementa" il DataBase vero e proprio, infatti crea un collegamento tra
 * i DAO e il DB
 */
package it.fitnesschallenge.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Questa annotazione indica che la classe è un DB e le entità che lo compongono sono elencate nell'
 * array entries, la versone indica come per SQLite la verisone del DB attuale, quindi nell'eventualità
 * di un aggiornamento alla struttura del DB verranno aggiunte le nuove entità, però l'aggiunta di
 * fallbackToDestructiveMigration dice che nel caso non si trovino i Migrations, ovvero i metodi necessari
 * alla migrazione dal vecchio al nuovo DB esso verrà distrutto e ricostruito.
 */
@Database(entities = {Exercise.class}, version = 1)
public abstract class FitnessChallengeDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "FitnessChallengeDB";

    private static FitnessChallengeDatabase instance;

    public abstract ExerciseDAO exerciseDAO();

    public static synchronized FitnessChallengeDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FitnessChallengeDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    /**
     * Questo metodo popola il DB quando viene chiamato la prima volta con onCreate
     */
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void>{
        private ExerciseDAO exerciseDAO;

        private PopulateDBAsyncTask(FitnessChallengeDatabase database){
            exerciseDAO = database.exerciseDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // TODO: aggiungere una Dummy Class con tutti gli esercizi per popolare il DB
            return null;
        }
    }

}
