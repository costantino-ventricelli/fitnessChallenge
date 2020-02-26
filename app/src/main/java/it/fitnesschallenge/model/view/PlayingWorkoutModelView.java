/**
 * Questo ViewModel gestisce l'esecuzione del workout creando una lista contenente tutti gli esercizi
 * di un allenamento, e collegandoci un ListIterator così da poter scorrere la lista in tutte le
 * direzioni, inoltre gestisce anche l' interazione con il DB locale.
 */
package it.fitnesschallenge.model.view;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import it.fitnesschallenge.model.room.entity.Exercise;
import it.fitnesschallenge.model.room.entity.ExerciseExecution;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.entity.PersonalExercise;
import it.fitnesschallenge.model.room.entity.Workout;
import it.fitnesschallenge.model.room.reference.entity.WorkoutWithExercise;

public class PlayingWorkoutModelView extends AndroidViewModel {

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
    private LiveData<WorkoutWithExercise> mWorkoutWithExercise;
    private MutableLiveData<List<ExerciseExecution>> mExerciseExecution;
    // Questo è il collegamento al repository locale
    private FitnessChallengeRepository mRepository;
    // Questo contiene l'id dell'allenamento attuale
    private int mWorkoutId;

    public PlayingWorkoutModelView(@NonNull Application application) {
        super(application);
        mRepository = new FitnessChallengeRepository(application);
        mWorkoutId = -1;
        mPersonalExerciseListIterator = null;
        mExerciseExecutionList = new ArrayList<>();
        mExerciseExecution = new MutableLiveData<>();

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
        if (mWorkoutId != -1) {
            mWorkoutWithExercise = mRepository.getWorkoutWithExerciseList(mWorkoutId);
            mPersonalExerciseList = (ArrayList<PersonalExercise>) mWorkoutWithExercise.getValue().getPersonalExerciseList();
            mPersonalExerciseListIterator = mPersonalExerciseList.listIterator();
        }
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
                    mWorkoutId = workout.getWorkOutId();
            }
        return new MutableLiveData<>(mWorkoutId != -1);
    }

    /**
     * Questo metodo consente di prelevare i workout con i relativi esercizi
     *
     * @return ritorna il workout con la lista degli esercizi individuati
     */
    public LiveData<WorkoutWithExercise> getWorkoutWithExercise() {
        return mWorkoutWithExercise;
    }

    /**
     * Questo metodo verrà richiamato quando il workout verrà prelevato dal DB Firebase
     *
     * @param workoutId contiene l'id del workout prelevato.
     */
    public void setWorkoutId(int workoutId) {
        this.mWorkoutId = workoutId;
    }

    /**
     * Questo metodo permette di scrivere il workout con i relativi esercizi all'interno del DB locale
     *
     * @param workoutWithExercise contiene il workout con la lista degli esercizi da memorizzare
     */
    public void writeWorkoutWithExercise(WorkoutWithExercise workoutWithExercise) {
        InsertWorkoutWithExercise insertWorkoutWithExercise = new InsertWorkoutWithExercise(mRepository);
        insertWorkoutWithExercise.execute(workoutWithExercise);
    }

    public MutableLiveData<Integer> getWorkoutId() {
        return new MutableLiveData<>(mWorkoutId);
    }

    /**
     * L'inserimento di nuovi workout con esercizi devono essere inseriti in maniera asincrona perchè
     * l'inserimento non utilizza i LiveData quindi dobbiamo essere noi a gestire il multithreading
     */
    static class InsertWorkoutWithExercise extends AsyncTask<WorkoutWithExercise, Void, Void> {

        private FitnessChallengeRepository mRepository;

        InsertWorkoutWithExercise(FitnessChallengeRepository fitnessChallengeRepository) {
            mRepository = fitnessChallengeRepository;
        }

        @Override
        protected Void doInBackground(WorkoutWithExercise... workoutWithExercises) {
            WorkoutWithExercise workoutWithExercise = workoutWithExercises[0];
            mRepository.insertWorkoutWithExercise(workoutWithExercise);
            return null;
        }
    }

}
