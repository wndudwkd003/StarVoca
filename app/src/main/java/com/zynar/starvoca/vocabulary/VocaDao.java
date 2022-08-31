package com.zynar.starvoca.vocabulary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VocaDao {
    @Insert // 삽입
    void insertVoca(VocaItem vocaItem);

    @Update // 수정
    void updateVoca(VocaItem vocaItem);

    @Delete // 삭제
    void deleteVoca(VocaItem vocaItem);

    // 조회
    @Query("SELECT * FROM VocaItem ORDER BY id DESC")    // 쿼리 : 데이터 베이스에 요청하는 명령문
    List<VocaItem> getVocaItems();
    
}
