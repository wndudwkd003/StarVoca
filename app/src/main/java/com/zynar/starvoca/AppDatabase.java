package com.zynar.starvoca;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zynar.starvoca.vocabulary.VocaDao;
import com.zynar.starvoca.vocabulary.VocaItem;
import com.zynar.starvoca.words.WordsDao;
import com.zynar.starvoca.words.WordsItem;


@Database(entities = {VocaItem.class, WordsItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase database;
    private static final String DATABASE_NAME = "StarVoca_Database";

    public synchronized static AppDatabase getInstance(Context context)
    {
        if(database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()   // 스키마(데이터베이스) 버전 변경 가능
                    .allowMainThreadQueries()   // 메인 쓰레드에서 IO를 가능 하게 함
                    .build();
        }
        return database;
    }

    public abstract VocaDao vocaDao();
    public abstract WordsDao wordsDao();
}
