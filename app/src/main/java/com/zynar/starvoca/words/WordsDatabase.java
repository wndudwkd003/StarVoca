package com.zynar.starvoca.words;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {WordsItem.class}, version = 1)
public abstract class WordsDatabase extends RoomDatabase {
    private static WordsDatabase database;
    private static final String DATABASE_NAME = "StarVoca_Words";

    public synchronized static WordsDatabase getInstance(Context context)
    {
        if(database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), WordsDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()   // 스키마(데이터베이스) 버전 변경 가능
                    .allowMainThreadQueries()   // 메인 쓰레드에서 IO를 가능 하게 함
                    .build();

        }
        return database;
    }

    public abstract WordsDao wordsDao();
}
