package com.zynar.starvoca;

import android.content.Context;
import android.util.Log;

import com.zynar.starvoca.vocabulary.VocaItem;
import com.zynar.starvoca.words.WordsItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AppCSVSupport {

    /* CSV 파일 SETTING */
    public void setCsv(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
        );

        String line = "";
        VocaItem vocaItem = new VocaItem();
        List<WordsItem> wordsItems = new ArrayList<>();

        int cnt = 0;
        try {
            while ((line=reader.readLine()) != null) {

                String[] tokens = line.split(",");

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

                    /* wordsItems add */
                    wordsItems.add(wordsItem);
                }

                cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("test", e.getMessage() + "\n" + line);
        }

        Log.d("test", vocaItem.toString());
        for(WordsItem wordsItem : wordsItems) {
            Log.d("test", wordsItem.toString());
        }

    }
}
