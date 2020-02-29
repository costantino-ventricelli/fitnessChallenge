/**
 * Questo ViewModel gestisce l'esecuzione del workout creando una lista contenente tutti gli esercizi
 * di un allenamento, e collegandoci un ListIterator così da poter scorrere la lista in tutte le
 * direzioni, inoltre gestisce anche l' interazione con il DB locale.
 */
package it.fitnesschallenge.model.view;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.room.entity.ExerciseExecution;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.entity.reference.WorkoutWithExercise;

public class PlayingWorkoutModelView extends AndroidViewModel {

    private static final String TAG = "PlayingWorkoutModelView";

    // Questo array conterrà tutte le esecuzioni degli esercizi dell'allenamento
    private ArrayList<ExerciseExecution> mExerciseExecutionList;
    /*
     Questo array conterrà tutti gli esercizi del DB per ottenere info su di essi durante l'esecuzione
     dell'allenamento.
    */
    private ArrayList<Exercise> mExerciseArrayList;
    // Questo è l'iteratore legato a PersonalExercise che permetterà di scorrere la lista degli esercizi
    private ListIterator<PersonalExercise> mPersonalExerciseListIterator;
    // Questo array contiene tutti gi esercizi legati al workout
    private ArrayList<PersonalExercise> mPersonalExerciseList;
    private MutableLiveData<WorkoutWithExercise> mWorkoutWithExercise;
    private MutableLiveData<List<ExerciseExecution>> mExerciseExecution;
    // Questo è il collegamento al repository locale
    private FitnessChallengeRepository mRepository;
    // Questo contiene l'id dell'allenamento attuale
    private MutableLiveData<Long> mWorkoutId;

    public PlayingWorkoutModelView(@NonNull Application application) {
        super(application);
        mRepository = new FitnessChallengeRepository(application);
        mWorkoutId = new MutableLiveData<>(-1L);
        mPersonalExerciseListIterator = null;
        mExerciseExecutionList = new ArrayList<>();
        mExerciseExecution = new MutableLiveData<>();
        mWorkoutWithExercise = new MutableLiveData<>();

        /*
         Questa inizializzazione preleva tutti gli esercizi dal DB locale, per gestire poi le info
         necessarie per ogni esercizio in fase di esecuzione
        */
        LiveData<List<Exercise>> mExerciseList = mRepository.getListExercise();
        mExerciseArrayList = (ArrayList<Exercise>) mExerciseList.getValue();
    }

    /**
     * Questo metodo dovrebbe essere richiamato dopo setActiveWorkout, in quanto permette di prelevare
     * la lista degli esercizi dal DB locale e impostare il ListIterator per muoversi all'interno
     * della lista.
     *
     * @return il valore di ritorno indica se l'operazione è stata effettuata con successo.
     */
    public boolean setWorkoutList() {
        //FIXME: problema con il prelievo del workout con gli esercizi
        if (mWorkoutId.getValue() != -1)
            mWorkoutWithExercise.setValue(mRepository.getWorkoutWithExerciseList(mWorkoutId.getValue()).getValue());
        return mPersonalExerciseListIterator != null;
    }

    public void setExerciseExecution(ExerciseExecution exerciseExecution) {
        mExerciseExecutionList.add(exerciseExecution);
        this.mExerciseExecution.setValue(mExerciseExecutionList);
    }

    /**
     * Questo metodo verifica la presenza di altri esercizi e successivamente restituisce l'erercizio
     * successivo
     * @return restituisce un LiveData contenente il prossimo esercizio
     */
    public LiveData<PersonalExercise> getNextExercise() {
        if (mPersonalExerciseListIterator.hasNext())
            return new MutableLiveData<>(mPersonalExerciseListIterator.next());
        else
            return null;
    }

    /**
     * Questo metodo è identico a getNextExercise solo che restituisce l'esercizio precendente
     */
    public LiveData<PersonalExercise> getPrevExercise() {
        if (mPersonalExerciseListIterator.hasPrevious())
            return new MutableLiveData<>(mPersonalExerciseListIterator.previous());
        else
            return null;
    }

    /**
     * Questo metodo preleva le informazioni dell'eserecizio che si sta attualmente eseguendo, facendo
     * @param personalExercise indica l'esercizio in esecuzione e viene usato per individuare
     *                         quale è l'esercizio in esecuzione (VEDERE METODO EQUALS DI EXERCISE)
     * @return il valore di ritorno è un live data contente le informazioni riguardanti un esercizo
     */
    public LiveData<Exercise> getExerciseInformation(PersonalExercise personalExercise) {
        return new MutableLiveData<>(mExerciseArrayList.get(
                mExerciseArrayList.indexOf(new Exercise(personalExercise))
        ));
    }


    /**
     * Questo metodo preleva i workout attivi dal DB locale.
     * @return il valore di ritorno è un booleano che indica se c'è un workout attivo nel DB locale
     * e nel frattempo detta la variabile del view model contenente il workoutId, per poi essere
     * prelevata ed utilizzata nel fragment che la richiederà.
     */
    public LiveData<Boolean> setActiveWorkoutFromLocal() {
        List<Workout> workoutList = mRepository.getWorkoutList().getValue();
        if (workoutList != null)
            for (Workout workout : workoutList) {
                if (workout.isActive())
                    mWorkoutId.setValue((long) workout.getWorkOutId());
            }
        return new MutableLiveData<>(mWorkoutId.getValue() != -1);
    }

    /**
     * Questo metodo consente di prelevare i workout con i relativi esercizi
     *
     * @return ritorna il workout con la lista degli esercizi individuati
     */
    public MutableLiveData<WorkoutWithExercise> getWorkoutWithExercise() {
        return mWorkoutWithExercise;
    }

    /**
     * Questo metodo verrà richiamato quando il workout verrà prelevato dal DB Firebase
     *
     * @param workoutId contiene l'id del workout prelevato.
     */
    private void setWorkoutId(long workoutId) {
        Log.d(TAG, "Setto l'id del workout precedente " + mWorkoutId.getValue() + " successivo: " + workoutId);
        this.mWorkoutId.setValue(workoutId);
    }

    /**
     * Questo metodo permette di scrivere il workout con i relativi esercizi all'interno del DB locale
     *
     * @param workoutWithExercise contiene il workout con la lista degli esercizi da memorizzare
     */
    public void writeWorkoutWithExercise(final WorkoutWithExercise workoutWithExercise, final LifecycleOwner owner) {
        mRepository.getWorkoutIdWithStartDate(workoutWithExercise.getWorkout().getStartDate()).observe(owner,
                new Observer<Long>() {
                    @Override
                    public void onChanged(Long aLong) {
                        Log.d(TAG, "WorkoutId: " + aLong);
                        if (aLong == null) {
                            Log.d(TAG, "Inserisco il nuovo workout");
                            InsertWorkoutWithExercise insertWorkoutWithExercise = new InsertWorkoutWithExercise(mRepository);
                            insertWorkoutWithExercise.execute(workoutWithExercise);
                            insertWorkoutWithExercise.getInsertedId().observe(owner, new Observer<Long>() {
                                @Override
                                public void onChanged(Long aLong) {
                                    Log.d(TAG, "Rilevato inserimento del nuovo id");
                                    setWorkoutId(aLong);
                                }
                            });
                        } else
                            setWorkoutId(aLong);
                    }
                });
    }

    public MutableLiveData<Long> getWorkoutId() {
        return mWorkoutId;
    }

    /**
     * L'inserimento di nuovi workout con esercizi devono essere inseriti in maniera asincrona perchè
     * l'inserimento non utilizza i LiveData quindi dobbiamo essere noi a gestire il multithreading
     * inoltre abbiamo fatto in modo da ottenere l'id del workout inserito, così possiamo utilizzarlo
     * in seguito per altre operazioni, la sincronizzazione tra processi è ottenuta con i live data,
     * tra l'async task e l'UI process
     */
    static class InsertWorkoutWithExercise extends AsyncTask<WorkoutWithExercise, Void, Long> {

        private FitnessChallengeRepository mRepository;
        private MutableLiveData<Long> mInsertedId;

        InsertWorkoutWithExercise(FitnessChallengeRepository fitnessChallengeRepository) {
            mRepository = fitnessChallengeRepository;
            mInsertedId = new MutableLiveData<>();
        }

        MutableLiveData<Long> getInsertedId() {
            return mInsertedId;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            Log.d(TAG, "Id inserito: " + aLong);
            mInsertedId.setValue(aLong);
        }

        @Override
        protected Long doInBackground(WorkoutWithExercise... workoutWithExercises) {
            WorkoutWithExercise workoutWithExercise = workoutWithExercises[0];
            return mRepository.insertWorkoutWithExercise(workoutWithExercise);
        }
    }
}
