package com.zynar.starvoca.vocabulary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.zynar.starvoca.R;
import com.zynar.starvoca.words.WordsAddActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VocaAddActivity extends AppCompatActivity {

    private VocaDatabase vocaDatabase;
    private VocaDao vocaDao;
    private int intentType;
    private int intentPos;
    private List<VocaItem> vocaItems;

    private MaterialToolbar toolbar;
    private EditText et_voca;
    private EditText et_explanation;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voca_add);
        vocaDatabase = VocaDatabase.getInstance(this);
        vocaDao = vocaDatabase.vocaDao();
        vocaItems = vocaDao.getVocaItems();
        //
        intentType = getIntent().getIntExtra("type", 0);
        intentPos = getIntent().getIntExtra("position", 0);
        //
        toolbar = findViewById(R.id.toolbar);
        et_voca = findViewById(R.id.et_voca);
        et_explanation = findViewById(R.id.et_explanation);
        btn_save = findViewById(R.id.btn_save);
        //
        if (intentType == 0) {
            toolbar.setTitle("단어장 만들기");
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VocaItem vocaItem = new VocaItem();
                    vocaItem.setVoca(et_voca.getText().toString());
                    vocaItem.setExplanation(et_explanation.getText().toString());
                    //vocaItem.setWordsId(new Gson().toJson(new ArrayList<Integer>()));
                    vocaItem.setWordsId(new Gson().toJson(new ArrayList<Integer>()));
                    vocaItem.setFavorites(0);
                    vocaDao.insertVoca(vocaItem);
                    Toast.makeText(VocaAddActivity.this, "새로운 단어장을 만들었습니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });

        }
        else if (intentType == 1) {
            toolbar.setTitle(R.string.edit_voca);
            VocaItem vocaItem = vocaItems.get(intentPos);
            et_voca.setText(vocaItem.getVoca());
            et_explanation.setText(vocaItem.getExplanation());
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vocaItem.setVoca(et_voca.getText().toString());
                    vocaItem.setExplanation(et_explanation.getText().toString());

                    vocaDao.updateVoca(vocaItem);
                    Toast.makeText(view.getContext(), "단어장이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });
        }

        // 툴바 뒤로가기 버튼 메소드
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

    }
}