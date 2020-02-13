package it.fitnesschallenge.model.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.fitnesschallenge.model.room.Exercise;
import it.fitnesschallenge.model.room.FitnessChallengeRepository;
import it.fitnesschallenge.model.room.PersonalExercise;

public class AddExerciseToListModel extends AndroidViewModel {

    private LiveData<List<Exercise>> mExerciseList;
    private MutableLiveData<List<PersonalExercise>> mPersonalExerciseLiveData;
    private ArrayList<PersonalExercise> mPersonalExerciseList;

    public AddExerciseToListModel(@NonNull Application application) {
        super(application);
        FitnessChallengeRepository repository = new FitnessChallengeRepository(application);
        mExerciseList = repository.getListExercise();
        mPersonalExerciseLiveData = new MutableLiveData<>();
        mPersonalExerciseList = new ArrayList<>();
    }

    public LiveData<List<Exercise>> getExerciseList(){
        return mExerciseList;
    }

    public void addPersonalExercise(PersonalExercise personalExercise){
        mPersonalExerciseList.add(personalExercise);
        mPersonalExerciseLiveData.setValue(mPersonalExerciseList);
    }

    public void removePersonalExercise(PersonalExercise personalExercise){
        mPersonalExerciseList.remove(personalExercise);
        mPersonalExerciseLiveData.setValue(mPersonalExerciseList);
    }

    public int getPersonalIndexOf(PersonalExercise personalExercise){
        return mPersonalExerciseList.indexOf(personalExercise);
    }

    public LiveData<List<PersonalExercise>> getPersonalExerciseLiveData(){
        return mPersonalExerciseLiveData;
    }
}
