package com.zynar.starvoca.words;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zynar.starvoca.AppDatabase;
import com.zynar.starvoca.VocaManager;
import com.zynar.starvoca.vocabulary.VocaItem;

import java.util.List;

public class WordsMainViewModel extends AndroidViewModel {
    AppDatabase db;

    public WordsMainViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public List<WordsItem> getWordsItems() {
        return db.wordsDao().getWordsItems();
    }

    public List<VocaItem> getVocaItems() {
        return db.vocaDao().getVocaItems();
    }

    public LiveData<List<WordsItem>> liveData_WordsItem() {
        return db.wordsDao().liveData_WordsItem();
    }

    public LiveData<List<VocaItem>> livedata_VocaItems() {
        return db.vocaDao().liveData_VocaItems();
    }
}
