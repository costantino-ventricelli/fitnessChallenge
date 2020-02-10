package it.fitnesschallenge.model.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;

public class CreationViewModel extends AndroidViewModel {

    private static final String TAG = "CreationViewModel";

    private MutableLiveData<Integer> mLiveDataProgress;
    private ArrayList<Integer> mListSteps;
    private MutableLiveData<ArrayList<Integer>> mLiveDataSteps;
    private MutableLiveData<String> mEmail;
    private MutableLiveData<String> mGoal;
    private MutableLiveData<Date> mStartDate;

    public CreationViewModel(@NonNull Application application) {
        super(application);
        mListSteps = new ArrayList<>();
        mListSteps.add(1);
        mLiveDataSteps = new MutableLiveData<>();
        mGoal = new MutableLiveData<>();
        mEmail = new MutableLiveData<>();
        mStartDate = new MutableLiveData<>();
        mLiveDataProgress = new MutableLiveData<>();
        setLiveDataSteps();
    }

    public LiveData<String> getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail.setValue(email);
    }

    public LiveData<String> getGoal() {
        return mGoal;
    }

    public void setGoal(String goal) {
        mGoal.setValue(goal);
    }

    public LiveData<Date> getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date date) {
        mStartDate.setValue(date);
    }

    public void setLiveDataProgress(int progress) {
        mLiveDataProgress.setValue(progress);
    }

    public void setLiveDataSteps(){
        mLiveDataSteps.setValue(mListSteps);
    }

    public void nextStep(){
        Log.d(TAG, "nextStep");
        mListSteps.add(mListSteps.get(mListSteps.size() - 1) + 1);
        Log.d(TAG, "Step: " + mListSteps.size());
        setLiveDataSteps();
    }

    public void prevStep(){
        mListSteps.remove(mListSteps.size() - 1);
        setLiveDataSteps();
    }

    public LiveData<Integer> getLiveDataProgress(){
        return mLiveDataProgress;
    }

    public LiveData<ArrayList<Integer>> getLiveDataSteps(){
        return mLiveDataSteps;
    }
}