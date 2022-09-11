package com.zynar.starvoca.vocabulary;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zynar.starvoca.AppDatabase;

import java.util.List;

public class VocaMainViewModel extends AndroidViewModel {
    AppDatabase db;

    public VocaMainViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public List<VocaItem> getVocaItems() {
        return db.vocaDao().getVocaItems();
    }

    public LiveData<List<VocaItem>> livedata_VocaItems() {
        return db.vocaDao().liveData_VocaItems();
    }
}
