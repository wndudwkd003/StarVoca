package com.zynar.starvoca.vocabulary;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {VocaItem.class}, version = 1)
public abstract class VocaDatabase extends RoomDatabase {
    private static VocaDatabase database;
    private static final String DATABASE_NAME = "StarVoca_Voca";

    public synchronized static VocaDatabase getInstance(Context context)
    {
        if(database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), VocaDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()   // 스키마(데이터베이스) 버전 변경 가능
                    .allowMainThreadQueries()   // 메인 쓰레드에서 IO를 가능 하게 함
                    .build();

        }
        return database;
    }

    public abstract VocaDao vocaDao();
}
