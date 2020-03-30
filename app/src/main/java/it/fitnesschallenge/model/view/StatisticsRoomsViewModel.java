package it.fitnesschallenge.model.view;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.fitnesschallenge.model.ExecutionList;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.entity.ExerciseExecution;
import it.fitnesschallenge.model.room.entity.Room;
import it.fitnesschallenge.model.room.entity.Workout;

public class StatisticsRoomsViewModel extends AndroidViewModel {

    private static final String TAG = "StatisticsViewModel";

    private FitnessChallengeRepository mRepository;

    public StatisticsRoomsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new FitnessChallengeRepository(application);
    }

    public LiveData<Integer> getNumberOfExecution() {
        return mRepository.getNumberOfExecution();
    }

    public LiveData<List<ExerciseExecution>> getExerciseExecutionList() {
        return mRepository.getLastUsedKilograms();
    }

    public LiveData<List<Workout>> getWorkoutList() {
        return mRepository.getWorkoutList();
    }

    public void writeExecutionsInLocalDB(List<ExecutionList> executions) {
        WriteExecutionsInLocal writeExecutionsInLocal = new WriteExecutionsInLocal(mRepository);
        writeExecutionsInLocal.execute(executions);
    }

    @Deprecated
    /**
     * Le room non dovrebbero mai essere salvate in locale, non si possono salvare tutti gli utenti
     * in locale, inoltre i bisogna trovare un modo per calcolare i codici univoci in automatico
     * salvarle in FireBase ci da la possibilità di autogenerare il codice identificativo della room
     */
    public LiveData<List<Room>> getAllRooms() {
        return mRepository.getAllRoom();
    }


    private static class WriteExecutionsInLocal extends AsyncTask<List<ExecutionList>, Void, Void> {

        private FitnessChallengeRepository mRepository;

        WriteExecutionsInLocal(FitnessChallengeRepository repository) {
            mRepository = repository;
        }

        @Override
        protected Void doInBackground(List<ExecutionList>... executionsList) {
            Log.d(TAG, "Scrivo nel DB");
            List<ExecutionList> executions = executionsList[0];
            for (ExecutionList execution : executions) {
                List<ExerciseExecution> exerciseExecutionList = execution.getExerciseList();
                for (ExerciseExecution exerciseExecution : exerciseExecutionList) {
                    mRepository.insertExecution(exerciseExecution);
                }
            }
            return null;
        }
    }
}
