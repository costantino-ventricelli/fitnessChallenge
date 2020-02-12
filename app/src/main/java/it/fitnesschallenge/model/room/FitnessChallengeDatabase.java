/**
 * Questa classe definisce il DB con le sue entità e lo popola quando viene creato
 * la versione del DB indica al sistema se c'è stato un upgrade del DB, nel caso in cui esso avviene
 * il DB viene ricreato
 */
package it.fitnesschallenge.model.room;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.ExerciseList;

@Database(entities = {Exercise.class, Workout.class, PersonalExerciseWorkoutCrossReference.class, PersonalExercise.class},
        version = 13, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class FitnessChallengeDatabase extends RoomDatabase {

    private static FitnessChallengeDatabase instance;
    private static final String TAG = "FitnessChallengeDB";

    public abstract ExerciseDAO getExerciseDAO();
    public abstract WorkoutDAO getWorkoutDAO();
    public abstract WorkoutWithExerciseDAO getWorkoutWithExerciseDAO();


    /**
     * Questo metodo crea il DB
     * synchronized serve a non creare più istanze del DB contemporaneamente
     */
     static synchronized FitnessChallengeDatabase getInstance(Context context){
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FitnessChallengeDatabase.class,
                    context.getString(R.string.fitness_challenge_db))
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
            Log.d(TAG, "Istanza db creata");
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d(TAG, "onCreate database");
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
             List<Exercise> exerciseList = new ExerciseList().getList();
             Log.d(TAG, "Popolo il db");
             for(Exercise tuple : exerciseList)
                exerciseDAO.insert(tuple);
             return null;
         }
     }
}
