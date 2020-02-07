/**
 * Questa classe crea un maggiore livello di estrazione tra il DB e le classe che lo utilizzeranno
 * espone i metodi per accedere al DB, i quali richiamano quelli nel DAO in Thread asincroni rispetto
 * all'esecuzione principale
 */
package it.fitnesschallenge.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

// TODO: probabilmente tutte quelle sottoclassi si possono unire in una specializzando l'operazione con uno switch

public class FitnessChallengeRepositiry {

    private ExerciseDAO exerciseDAO;
    private List<Exercise> exerciseList;

    public FitnessChallengeRepositiry(Application application){
        FitnessChallengeDatabase database = FitnessChallengeDatabase.getInstance(application);
        exerciseDAO = database.exerciseDAO();
        exerciseList = exerciseDAO.selectAllEntry();
    }

    public void insert(Exercise exercise){
        new InsertAsyncTask(exerciseDAO).execute(exercise);
    }

    public void update(Exercise exercise){
        new UpdateAsyncTask(exerciseDAO).execute(exercise);
    }

    public void delete(Exercise exercise){
        new DeleteAsyncTask(exerciseDAO).execute(exercise);
    }

    public List<Exercise> getExerciseList(){
        return exerciseList;
    }

    private static class InsertAsyncTask extends AsyncTask<Exercise, Void, Void>{

        private  ExerciseDAO exerciseDAO;

        private InsertAsyncTask(ExerciseDAO exerciseDAO){
            this.exerciseDAO = exerciseDAO;
        }

        @Override
        protected Void doInBackground(Exercise... exercises) {
            exerciseDAO.insert(exercises[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Exercise, Void, Void>{

        private  ExerciseDAO exerciseDAO;

        private UpdateAsyncTask(ExerciseDAO exerciseDAO){
            this.exerciseDAO = exerciseDAO;
        }

        @Override
        protected Void doInBackground(Exercise... exercises) {
            exerciseDAO.update(exercises[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Exercise, Void, Void>{

        private  ExerciseDAO exerciseDAO;

        private DeleteAsyncTask(ExerciseDAO exerciseDAO){
            this.exerciseDAO = exerciseDAO;
        }

        @Override
        protected Void doInBackground(Exercise... exercises) {
            exerciseDAO.delete(exercises[0]);
            return null;
        }
    }
}
