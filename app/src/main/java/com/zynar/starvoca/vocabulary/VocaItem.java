package com.zynar.starvoca.vocabulary;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class VocaItem {
    // 고유 아이디
    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    // 단어장 이름
    private String voca;
    // 설명
    private String explanation;
    // 단어들 아이디 모음
    private String wordsId;
    // 즐겨찾기
    private int favorites;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getWordsId() {
        return wordsId;
    }

    public void setWordsId(String wordsId) {
        this.wordsId = wordsId;
    }

    public int getFavorites() {
        return favorites;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    @Override
    public String toString() {
        return "VocaItem{" +
                "id=" + id +
                ", voca='" + voca + '\'' +
                ", explanation='" + explanation + '\'' +
                ", wordsId='" + wordsId + '\'' +
                ", favorites=" + favorites +
                '}';
    }
}
