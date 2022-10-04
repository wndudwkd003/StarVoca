package com.zynar.starvoca;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.opencsv.CSVReader;
import com.zynar.starvoca.vocabulary.VocaItem;
import com.zynar.starvoca.words.WordsItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AppCSVSupport implements DefaultLifecycleObserver {


    /* 아이템 */
    private VocaItem vocaItem = new VocaItem();
    private List<WordsItem> wordsItems = new ArrayList<>();

    /* csv 파일 */
    private Uri uri;
    private boolean permission;

    public AppCSVSupport() {
        /* 권한 체크 */
        permission = isExternalStorageWritable() && isExternalStorageReadable();

    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    /* CSV 파일 SETTING, return 값으로 불러오지 못한 단어들의 수를 반환 */
    public int setCsv(Context context) {

        /* cnt = 단어장 이름과 단어들을 구별하기 위한반복 카운트
        , passCnt = 양식에 맞지 않아 불러오지 못한 단어장 카운트 */
        int cnt = -1, passCnt = 0;

        try {
            /* 파일의 절대 주소를 불러옴 */
            String filePath = PathUtil.getPath(context, uri);
            CSVReader reader = new CSVReader(new FileReader(filePath));

            /* 한 줄씩 불러옴 */
            String[] nextLine;
            while((nextLine = reader.readNext()) != null) {
                cnt++;
                /* 한 줄의 csv 내용을 리스트에 넣음 */
                List<String> line = new ArrayList<>();
                for(int i=0; i<nextLine.length; i++) {
                    line.add(nextLine[i]);
                }

                if(cnt == 1 || cnt == 2) {
                    /* cnt = 1, 2이면 단어장 이름과 단어장 설명을 넣음 */
                    vocaItem.setVoca(line.get(0));
                } else if (cnt >= 4) {
                    /* cnt >= 4면 단어로 간주 단어들의 정보를 넣음 */

                    /* line의 0, 1의 요소가 비어있으면 불러오지 못한 단어로 간주 */
                    if(line.get(0).isEmpty() || line.get(1).isEmpty()) {
                        passCnt++;
                        continue;
                    } else {
                        WordsItem wordsItem = new WordsItem();
                        wordsItem.setWord(line.get(0));
                        wordsItem.setMeaning(line.get(1));
                        wordsItem.setPronunciation(line.get(2));
                        wordsItem.setMemo(line.get(3));
                        wordsItem.setLanguage(line.get(4).isEmpty() ? "english" : line.get(4));
                        wordsItems.add(wordsItem);
                    }
                }
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            Log.d("__star__", e.getMessage());
        }

        return passCnt;
    }

    public boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }


    public String readCSVFile(String path){
        String filedata = null;
        File file=new File(path);
        try {

            Scanner scanner=new Scanner(file);
            while (scanner.hasNextLine()){

                String line=scanner.nextLine();
                String [] splited=line.split(",");
                String row="";
                for (String s:splited){

                    row=row+s+"  ";

                }

                filedata=filedata+row+"\n";

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("__star__", e.getMessage());
        }

        return filedata;

    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
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
