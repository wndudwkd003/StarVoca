package com.zynar.starvoca.words;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.zynar.starvoca.R;

import java.util.ArrayList;
import java.util.List;

public class WordsAddActivity extends AppCompatActivity {

    private WordsItem wordsItem;
    private EditText et_word;
    private EditText et_meaning;
    private EditText et_pronunciation;
    private EditText et_memo;
    private Spinner spinner_language;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_add);

        // 인텐트 엑스트라
        int intentType = getIntent().getIntExtra("type", 0);
        int intentPos = getIntent().getIntExtra("position", 0);

        // 레이아웃 설정
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        et_word = findViewById(R.id.et_word);
        et_meaning = findViewById(R.id.et_meaning);
        et_pronunciation = findViewById(R.id.et_pronunciation);
        et_memo = findViewById(R.id.et_memo);
        spinner_language = findViewById(R.id.spinner_language);
        Button btn_save = findViewById(R.id.btn_save);

        WordsDatabase wordsDatabase = WordsDatabase.getInstance(WordsAddActivity.this);
        WordsDao wordsDao = wordsDatabase.wordsDao();
        
        if (intentType == 0) {
            // 단어 추가
            btn_save.setOnClickListener(view -> {
                toolbar.setTitle(R.string.add_word);

                WordsItem wordsItem = new WordsItem();
                wordsItem.setWord(et_word.getText().toString());
                wordsItem.setMeaning(et_meaning.getText().toString());
                wordsItem.setPronunciation(et_pronunciation.getText().toString());
                wordsItem.setMemo(et_memo.getText().toString());
                wordsItem.setLanguage(spinner_language.getSelectedItem().toString());
                wordsItem.setCondition(0);

                wordsDao.insertWords(wordsItem);

                Toast.makeText(WordsAddActivity.this, "단어가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            });
        } else if(intentType == 1) {
            // 단어 수정
            btn_save.setOnClickListener(view -> {
                toolbar.setTitle(R.string.edit_word);

                List<WordsItem> wordsItems = wordsDao.getWordsItems();
                wordsItem = wordsItems.get(intentPos);
                et_word.setText(wordsItem.getWord());
                et_meaning.setText(wordsItem.getMeaning());
                et_pronunciation.setText(wordsItem.getPronunciation());
                et_memo.setText(wordsItem.getMemo());
                id = wordsItem.getId();
                
                // words add의 빈칸을 db에 넣음
                WordsItem wordsItem = new WordsItem();
                wordsItem.setId(id);
                wordsItem.setWord(et_word.getText().toString());
                wordsItem.setMeaning(et_meaning.getText().toString());
                wordsItem.setPronunciation(et_pronunciation.getText().toString());
                wordsItem.setMemo(et_memo.getText().toString());
                wordsItem.setLanguage(spinner_language.getSelectedItem().toString());
                wordsItem.setCondition(0);
                wordsDao.updateWords(wordsItem);
                
                Toast.makeText(view.getContext(), "목록이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            });
        }
    }
}