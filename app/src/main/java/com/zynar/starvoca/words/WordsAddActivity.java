package com.zynar.starvoca.words;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.zynar.starvoca.R;

import java.util.ArrayList;

public class WordsAddActivity extends AppCompatActivity {

    // words아이템, DBHelper
    private WordsDBHelper wordsDBHelper;
    private int intentType;
    private int intentPos;
    private ArrayList<WordsItem> wordsItems;
    private WordsItem wordsItem;

    // 레이아웃
    private MaterialToolbar toolbar;
    private EditText et_word;
    private EditText et_meaning;
    private EditText et_pronunciation;
    private EditText et_memo;
    private Spinner spinner_language;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_add);

        wordsItems = new ArrayList<>();

        // 인텐트 엑스트라
        this.wordsDBHelper = new WordsDBHelper(this);
        intentType = getIntent().getIntExtra("type", 0);
        intentPos = getIntent().getIntExtra("position", 0);

        // 레이아웃 설정
        toolbar = findViewById(R.id.toolbar);
        et_word = findViewById(R.id.et_word);
        et_meaning = findViewById(R.id.et_meaning);
        et_pronunciation = findViewById(R.id.et_pronunciation);
        et_memo = findViewById(R.id.et_memo);
        spinner_language = findViewById(R.id.spinner_language);
        btn_save = findViewById(R.id.btn_save);

        // 툴바 타이틀 변경
        if (intentType == 0) toolbar.setTitle(R.string.add_word);
        else if (intentType == 1) {
            wordsItems = wordsDBHelper.getWordsList();
            wordsItem = wordsItems.get(intentPos);

            // 타이틀 수정
            toolbar.setTitle(R.string.edit_word);

            et_word.setText(wordsItem.getWord());
            // 커서를 마지막으로 이동
            et_word.setSelection(et_word.getText().length());

            et_meaning.setText(wordsItem.getMeaning());
            et_pronunciation.setText(wordsItem.getPronunciation());
            et_memo.setText(wordsItem.getMemo());
        }

        // 툴바 뒤로가기 버튼 메소드
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (intentType == 0) {
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // words add의 빈칸을 db에 넣음
                    wordsDBHelper.insertWords(et_word.getText().toString(), et_meaning.getText().toString(), et_pronunciation.getText().toString(), et_memo.getText().toString(), spinner_language.getSelectedItem().toString(), 0);

                    Toast.makeText(WordsAddActivity.this, "단어가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });
        } else if(intentType == 1) {
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // words add의 빈칸을 db에 넣음
                    wordsDBHelper.updateWords(wordsItem.getId(), et_word.getText().toString(), et_meaning.getText().toString(), et_pronunciation.getText().toString(), et_memo.getText().toString(), spinner_language.getSelectedItem().toString(), wordsItem.getCondition());

                    wordsItem.setWord(et_word.getText().toString());
                    wordsItem.setMeaning(et_meaning.getText().toString());
                    wordsItem.setPronunciation(et_pronunciation.getText().toString());
                    wordsItem.setMemo(et_memo.getText().toString());
                    wordsItem.setLanguage(spinner_language.getSelectedItem().toString());

                    Toast.makeText(view.getContext(), "목록이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });
        }
    }


}