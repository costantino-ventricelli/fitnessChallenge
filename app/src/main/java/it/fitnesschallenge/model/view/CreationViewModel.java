package it.fitnesschallenge.model.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class CreationViewModel extends AndroidViewModel {

    private LiveData<Integer> mLiveDataProgress;

    public CreationViewModel(@NonNull Application application) {
        super(application);
        setLiveDataProgress(0);
    }

    public void setLiveDataProgress(int progress) {
        MutableLiveData mutableLiveData = new MutableLiveData();
        mutableLiveData.setValue(progress);
        mLiveDataProgress = mutableLiveData;
    }

    public LiveData<Integer> getLiveDataProgress(){
        return mLiveDataProgress;
    }
}
