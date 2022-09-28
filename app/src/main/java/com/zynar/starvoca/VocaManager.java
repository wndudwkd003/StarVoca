package com.zynar.starvoca;

import androidx.appcompat.app.AppCompatActivity;
import com.zynar.starvoca.vocabulary.VocaItem;
import com.zynar.starvoca.words.WordsItem;

import java.util.List;

public class VocaManager extends AppCompatActivity {

    private List<WordsItem> wordsItemList;
    private List<VocaItem> vocaItemList;
    private VocaManager() {

    }

    private static final VocaManager vocaManager = new VocaManager();

    public static VocaManager getInstance() {
        return vocaManager;
    }

    public List<WordsItem> getWordsItemList() {
        return wordsItemList;
    }

    public void setWordsItemList(List<WordsItem> wordsItemList) {
        this.wordsItemList = wordsItemList;
    }

    public List<VocaItem> getVocaItemList() {
        return vocaItemList;
    }

    public void setVocaItemList(List<VocaItem> vocaItemList) {
        this.vocaItemList = vocaItemList;
    }

    public int getWordsCnt() {
        return wordsItemList.size();
    }

}
