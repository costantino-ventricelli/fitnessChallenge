/**
 * Questa classe definisce il DB con le sue entità e lo popola quando viene creato
 * la versione del DB indica al sistema se c'è stato un upgrade del DB, nel caso in cui esso avviene
 * il DB viene ricreato
 */
package it.fitnesschallenge.model.room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.ExerciseList;

@Database(entities = {ExerciseTable.class, Workout.class, WorkoutListExercise.class}, version = 4, exportSchema = false)
public abstract class FitnessChallengeDatabase extends RoomDatabase {

    private static FitnessChallengeDatabase instance;

    public abstract ExerciseDAO getExerciseDAO();
    public abstract WorkoutDAO getWorkoutDao();
    public abstract WorkoutAndExerciseDAO getWorkoutListAndExereciseDAO();


    /**
     * Questo metodo crea il DB
     * synchronized serve a non creare più istanze del DB contemporaneamente
     */
     static synchronized FitnessChallengeDatabase getInstance(Context context){
        if(instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FitnessChallengeDatabase.class,
                    context.getString(R.string.fitness_challenge_db))
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

     private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void>{

         private ExerciseDAO exerciseDAO;

         private PopulateDBAsyncTask(FitnessChallengeDatabase db){
             exerciseDAO = db.getExerciseDAO();
         }

         @Override
         protected Void doInBackground(Void... voids) {
             List<ExerciseTable> exerciseList = new ExerciseList().getList();
             for(ExerciseTable tuple : exerciseList)
                exerciseDAO.insert(tuple);
             return null;
         }
     }
}
