package com.zynar.starvoca.words;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WordsItem implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id = 0; // 고유 id
    private String word;    // 단어 타이틀
    private String meaning; // 단어 뜻
    private String pronunciation;   // 단어 발음
    private String memo;    // 단어 메모
    private String language;    // 단어 언어
    private int condition;   // 단어 암기 상태

    public WordsItem() {
    }


    protected WordsItem(Parcel in) {
        id = in.readInt();
        word = in.readString();
        meaning = in.readString();
        pronunciation = in.readString();
        memo = in.readString();
        language = in.readString();
        condition = in.readInt();
    }

    public static final Creator<WordsItem> CREATOR = new Creator<WordsItem>() {
        @Override
        public WordsItem createFromParcel(Parcel in) {
            return new WordsItem(in);
        }

        @Override
        public WordsItem[] newArray(int size) {
            return new WordsItem[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "WordsItem{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", meaning='" + meaning + '\'' +
                ", pronunciation='" + pronunciation + '\'' +
                ", memo='" + memo + '\'' +
                ", language='" + language + '\'' +
                ", condition=" + condition +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(word);
        dest.writeString(meaning);
        dest.writeString(pronunciation);
        dest.writeString(memo);
        dest.writeString(language);
        dest.writeInt(condition);
    }
}
