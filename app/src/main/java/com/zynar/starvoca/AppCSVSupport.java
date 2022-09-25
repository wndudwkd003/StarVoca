package com.zynar.starvoca;

import android.content.Context;
import android.util.Log;

import com.zynar.starvoca.vocabulary.VocaItem;
import com.zynar.starvoca.words.WordsItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AppCSVSupport {

    private VocaItem vocaItem = new VocaItem();
    private List<WordsItem> wordsItems = new ArrayList<>();

    /* CSV 파일 SETTING, return 값으로 불러오지 못한 단어들의 수를 반환 */
    public int setCsv(Context context) {

        /* 파일 불러옴 */
        InputStream is = context.getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
        );

        String line;
        int cnt = 0, passCnt = 0;

        try {
            while ((line=reader.readLine()) != null) {

                /* ,을 기준으로 배열에 저장 */
                String[] tokens = line.split(",", -1);

                /* 단어장 정보 설정 */
                if(cnt==1) {
                    vocaItem.setVoca(tokens[0]);
                } else if(cnt==2) {
                    vocaItem.setExplanation(tokens[0]);
                }

                /* 단어 정보 설정 */
                if(cnt >= 4){
                    WordsItem wordsItem = new WordsItem();
                    wordsItem.setWord(tokens[0]);
                    wordsItem.setMeaning(tokens[1]);
                    wordsItem.setPronunciation(tokens[2]);
                    wordsItem.setMemo(tokens[3]);
                    wordsItem.setLanguage(tokens[4]);

                    /* 단어의 이름과 의미가 공백인지 체크 공백이면 건너뜀 */
                    if(wordsItem.getWord().isEmpty() || wordsItem.getMeaning().isEmpty()) {
                        passCnt++;
                        continue;
                    }

                    /* wordsItems add */
                    wordsItems.add(wordsItem);
                }
                cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return passCnt;
    }

    public VocaItem getVocaItem() {
        return vocaItem;
    }

    public void setVocaItem(VocaItem vocaItem) {
        this.vocaItem = vocaItem;
    }

    public List<WordsItem> getWordsItems() {
        return wordsItems;
    }

    public void setWordsItems(List<WordsItem> wordsItems) {
        this.wordsItems = wordsItems;
    }
}
