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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import it.fitnesschallenge.model.User;
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
    private List<Exercise> mExerciseList;
    // Questo è l'iteratore legato a PersonalExercise che permetterà di scorrere la lista degli esercizi
    private ListIterator<PersonalExercise> mPersonalExerciseListIterator;
    // Questo array contiene tutti gi esercizi legati al workout
    private ArrayList<PersonalExercise> mPersonalExerciseList;
    /*
     * Questo LiveData permette di notificare all'activity chiamante che il workout e gli esercizi sono
     * stati prelevati
     */
    private MutableLiveData<WorkoutWithExercise> mWorkoutWithExerciseLiveData;
    private MutableLiveData<List<ExerciseExecution>> mExerciseExecution;
    // Questo è il collegamento al repository locale
    private FitnessChallengeRepository mRepository;
    // Questo contiene l'id dell'allenamento attuale
    private MutableLiveData<Long> mWorkoutId;
    private WorkoutWithExercise mWorkoutWithExercise;

    // Queste variabili permettono di mantenere attivo l'utente mentre esegue il workout
    private User mUser;
    private FirebaseUser mFireStoreUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    public PlayingWorkoutModelView(@NonNull Application application) {
        super(application);
        mRepository = new FitnessChallengeRepository(application);
        mWorkoutId = new MutableLiveData<>(-1L);
        mPersonalExerciseListIterator = null;
        mExerciseExecutionList = new ArrayList<>();
        mExerciseExecution = new MutableLiveData<>();
        mWorkoutWithExerciseLiveData = new MutableLiveData<>();
    }

    /**
     * Questo metodo dovrebbe essere richiamato dopo setActiveWorkout, in quanto permette di prelevare
     * la lista degli esercizi dal DB locale e impostare il ListIterator per muoversi all'interno
     * della lista.
     */
    public void setWorkoutList(LifecycleOwner lifecycleOwner) {
        /*
         Questa inizializzazione preleva tutti gli esercizi dal DB locale, per gestire poi le info
         necessarie per ogni esercizio in fase di esecuzione
        */
        if (mExerciseList == null)
            mRepository.getListExerciseLiveData().observe(lifecycleOwner, new Observer<List<Exercise>>() {
                @Override
                public void onChanged(List<Exercise> exercises) {
                    mExerciseList = exercises;
                    Log.d(TAG, "Lista esercizi acquisita");
                }
            });
        if (mWorkoutId.getValue() != -1) {
            mRepository.getWorkoutWithExerciseList(mWorkoutId.getValue()).observe(lifecycleOwner, new Observer<WorkoutWithExercise>() {
                @Override
                public void onChanged(WorkoutWithExercise workoutWithExercise) {
                    Log.d(TAG, "Observer di WorkoutWithExercise");
                    mWorkoutWithExercise = workoutWithExercise;
                    Log.d(TAG, "WorkoutWithExercise: " + workoutWithExercise.getPersonalExerciseList().toString());
                    mPersonalExerciseList = (ArrayList<PersonalExercise>) mWorkoutWithExercise.getPersonalExerciseList();
                    Log.d(TAG, "Nella lista degli esercizio ci sono: " + mPersonalExerciseList.size() + " elementi\n" +
                            "Lista: " + mPersonalExerciseList.toString());
                    mPersonalExerciseListIterator = mPersonalExerciseList.listIterator();
                    Log.d(TAG, "Settato l'iteratore e la lista degli esercizi personale\n" +
                            "Primo inidice puntato dall'iteratore: " + mPersonalExerciseListIterator.nextIndex() + "\n" +
                            "Primo elemento teorico puntato dall'iteratore: " + mPersonalExerciseList.get(mPersonalExerciseListIterator.nextIndex()).getExerciseId());
                    mWorkoutWithExerciseLiveData.setValue(mWorkoutWithExercise);
                }
            });
        }
    }

    public void setExerciseExecution(ExerciseExecution exerciseExecution) {
        mExerciseExecutionList.add(exerciseExecution);
        this.mExerciseExecution.setValue(mExerciseExecutionList);
    }

    /**
     * Questo metodo verifica la presenza di altri esercizi e successivamente restituisce l'erercizio
     * successivo
     * @return restituisce il prossimo esercizio
     */
    public PersonalExercise getNextExercise() {
        Log.d(TAG, "Prelevo esercizio successivo");
        if (mPersonalExerciseListIterator.hasNext())
            return mPersonalExerciseListIterator.next();
        else
            return null;
    }

    /**
     * Questo metodo è identico a getNextExercise solo che restituisce l'esercizio precendente
     */
    public PersonalExercise getPrevExercise() {
        Log.d(TAG, "Prelevo esercizio precedente");
        if (mPersonalExerciseListIterator.hasPrevious())
            return mPersonalExerciseListIterator.previous();
        else
            return null;
    }

    public boolean thereIsNext() {
        return mPersonalExerciseListIterator.hasNext();
    }

    public boolean thereIsPrev() {
        return mPersonalExerciseListIterator.hasPrevious();
    }

    /**
     * Questo metodo ritorna semplicemente la lista degli esercizi necessari a creare la RecyclerView
     *
     * @return un ArrayList contenente gli esercizi selezionati dal DB che dovranno verrannno eseguiti
     */
    public ArrayList<PersonalExercise> getPersonalExerciseList() {
        return mPersonalExerciseList;
    }

    /**
     * Questo metodo preleva le informazioni dell'eserecizio che si sta attualmente eseguendo, facendo
     * @param personalExercise indica l'esercizio in esecuzione e viene usato per individuare
     *                         quale è l'esercizio in esecuzione (VEDERE METODO EQUALS DI EXERCISE)
     * @return il valore di ritorno è un live data contente le informazioni riguardanti un esercizo
     */
    public MutableLiveData<Exercise> getExerciseInformation(PersonalExercise personalExercise) {
        MutableLiveData<Exercise> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(mExerciseList.get(mPersonalExerciseList.indexOf(personalExercise)));
        return mutableLiveData;
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
        return mWorkoutWithExerciseLiveData;
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

    /**
     * Questo metodo permette di ottenere il riferimento all'id del workout selezionato
     * @return LiveData contente l'id del workout attutale.
     */
    public MutableLiveData<Long> getWorkoutId() {
        return mWorkoutId;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
    }

    public FirebaseUser getFireStoreUser() {
        return mFireStoreUser;
    }

    public void setFireStoreUser(FirebaseUser mFireStoreUser) {
        this.mFireStoreUser = mFireStoreUser;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public void setAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public FirebaseFirestore getDatabase() {
        return mDatabase;
    }

    public void setDatabase(FirebaseFirestore mDatabase) {
        this.mDatabase = mDatabase;
    }

    /**
     * L'inserimento di nuovi workout con esercizi devono essere inseriti in maniera asincrona perchè
     * l'inserimento non utilizza i LiveData quindi dobbiamo essere noi a gestire il multithreading
     * inoltre abbiamo fatto in modo da ottenere l'id del workout inserito, così possiamo utilizzarlo
     * in seguito per altre operazioni, la sincronizzazione tra processi è ottenuta con i live data,
     * tra l'async task e l'UI thread
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
