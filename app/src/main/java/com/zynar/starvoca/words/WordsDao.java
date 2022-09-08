package com.zynar.starvoca.words;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordsDao {
    @Insert // 삽입
    void insertWords(WordsItem wordsItem);

    @Update // 수정
    void updateWords(WordsItem wordsItem);

    @Delete // 삭제
    void deleteWords(WordsItem wordsItem);

    // 조회
    @Query("SELECT * FROM WordsItem ORDER BY id DESC")    // 쿼리 : 데이터 베이스에 요청하는 명령문
    List<WordsItem> getWordsItems();

    @Query("SELECT * FROM WordsItem WHERE id = :i ORDER BY id DESC")
    WordsItem getWordsListForVoca(int i);

    @Query("SELECT * FROM WordsItem ORDER BY id DESC")
    LiveData<List<WordsItem>> liveData_WordsItem();
}
