package com.zynar.starvoca.words;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.zynar.starvoca.AppDatabase;
import com.zynar.starvoca.R;

import java.util.List;

public class WordsAddActivity extends AppCompatActivity {

    private EditText et_word;
    private EditText et_meaning;
    private EditText et_pronunciation;
    private EditText et_memo;
    private Spinner spinner_language;

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

        AppDatabase db = AppDatabase.getInstance(WordsAddActivity.this);
        
        if (intentType == 0) {
            // 단어 추가
            toolbar.setTitle(R.string.add_word);
            btn_save.setOnClickListener(view -> {
                WordsItem wordsItem = new WordsItem();
                wordsItem.setWord(et_word.getText().toString());
                wordsItem.setMeaning(et_meaning.getText().toString());
                wordsItem.setPronunciation(et_pronunciation.getText().toString());
                wordsItem.setMemo(et_memo.getText().toString());
                wordsItem.setLanguage(spinner_language.getSelectedItem().toString());
                wordsItem.setCondition(0);
                db.wordsDao().insertWords(wordsItem);

                Toast.makeText(WordsAddActivity.this, "단어가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            });
        } else if(intentType == 1) {
            // 단어 수정
            toolbar.setTitle(R.string.edit_word);
            Log.d("tst", String.valueOf(intentPos));
            List<WordsItem> wordsItems = db.wordsDao().getWordsItems();
            WordsItem wordsItem = wordsItems.get(intentPos);
            et_word.setText(wordsItem.getWord());
            et_meaning.setText(wordsItem.getMeaning());
            et_pronunciation.setText(wordsItem.getPronunciation());
            et_memo.setText(wordsItem.getMemo());

            btn_save.setOnClickListener(view -> {
                // words add 빈칸을 db에 넣음
                wordsItem.setWord(et_word.getText().toString());
                wordsItem.setMeaning(et_meaning.getText().toString());
                wordsItem.setPronunciation(et_pronunciation.getText().toString());
                wordsItem.setMemo(et_memo.getText().toString());
                wordsItem.setLanguage(spinner_language.getSelectedItem().toString());
                wordsItem.setCondition(0);
                db.wordsDao().updateWords(wordsItem);

                Toast.makeText(view.getContext(), "목록이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            });
        }
    }


}