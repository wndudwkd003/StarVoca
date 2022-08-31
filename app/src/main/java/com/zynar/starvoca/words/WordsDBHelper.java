package com.zynar.starvoca.words;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordsDBHelper extends SQLiteOpenHelper {

    // DB 버전과 이름
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "StarVoca_Words";

    // DBHelper 생성자
    public WordsDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 데이터 베이스가 생성될 때 호출
        // id, word, meaning, pronunciation, memo, language
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS WordsList (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, meaning TEXT NOT NULL, pronunciation TEXT, memo TEXT, language TEXT NOT NULL, condition INTEGER NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // SELECT 데이터 베이스 조회
    public ArrayList<WordsItem> getWordsList() {

        // ArrayList 생성
        ArrayList<WordsItem> wordsItems = new ArrayList<>();
        // 읽기 데이터 베이스 생성
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        // db 커서, 내림차순 정렬
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM WordsList ORDER BY id DESC", null);

        // 0이 아니면 무조건 데이터가 있음
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
                String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
                String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow("pronunciation"));
                String memo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));
                String language = cursor.getString(cursor.getColumnIndexOrThrow("language"));
                int condition = cursor.getInt(cursor.getColumnIndexOrThrow("condition"));

                // WordsItem 생성
                WordsItem wordsItem = new WordsItem();
                wordsItem.setId(id);
                wordsItem.setWord(word);
                wordsItem.setMeaning(meaning);
                wordsItem.setPronunciation(pronunciation);
                wordsItem.setMemo(memo);
                wordsItem.setLanguage(language);
                wordsItem.setCondition(condition);

                // ArrayList에 add
                wordsItems.add(wordsItem);
            }
        }
        cursor.close();

        return wordsItems;
    }

    // SELECT 데이터 베이스 조회
    public ArrayList<WordsItem> getWordsListForVoca(List<Integer> list) {

        // ArrayList 생성
        ArrayList<WordsItem> wordsItems = new ArrayList<>();
        // 읽기 데이터 베이스 생성
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        // db 커서, 내림차순 정렬
        for(int i:list) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM WordsList WHERE id = '" + i + "' ORDER BY id DESC", null);

            // 0이 아니면 무조건 데이터가 있음
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
                    String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
                    String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow("pronunciation"));
                    String memo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));
                    String language = cursor.getString(cursor.getColumnIndexOrThrow("language"));
                    int condition = cursor.getInt(cursor.getColumnIndexOrThrow("condition"));

                    // WordsItem 생성
                    WordsItem wordsItem = new WordsItem();
                    wordsItem.setId(id);
                    wordsItem.setWord(word);
                    wordsItem.setMeaning(meaning);
                    wordsItem.setPronunciation(pronunciation);
                    wordsItem.setMemo(memo);
                    wordsItem.setLanguage(language);
                    wordsItem.setCondition(condition);

                    // ArrayList에 add
                    wordsItems.add(0, wordsItem);
                }
            }
            cursor.close();
        }


        return wordsItems;
    }


    // INSERT 데이터 베이스 삽입
    public void insertWords(String word, String meaning, String pronunciation, String memo, String language, int condition) {
        // 데이터 베이스를 쓰기로 설정
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        // 쿼리문 실행
        sqLiteDatabase.execSQL("INSERT INTO WordsList (word, meaning, pronunciation, memo, language, condition) VALUES ('" + word + "', '" + meaning + "', '" + pronunciation + "', '" + memo + "', '" + language + "', '" + condition + "');");
        Log.v("test", "데이터 베이스 만들어짐");
    }

    // UPDATE 데이터 베이스 수정
    public void updateWords(int id, String word, String meaning, String pronunciation, String memo, String language, int condition) {
        // 데이터 베이스를 쓰기로 설정
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        // 쿼리문 id를 where문으로 찾아서 수정
        sqLiteDatabase.execSQL("UPDATE WordsList SET word = '" + word + "', meaning = '" + meaning + "', pronunciation = '" + pronunciation + "', memo = '" + memo + "', language = '" + language + "', condition = '" + condition + "' WHERE id = '" + id + "'");
    }

    // DELETE 데이터 베이스 삭제
    public void deleteWords(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        // 삭제 쿼리문
        sqLiteDatabase.execSQL("DELETE FROM WordsList WHERE id = '" + id + "'");
    }
}
