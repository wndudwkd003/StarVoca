package com.zynar.starvoca.vocabulary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.zynar.starvoca.R;

import java.util.ArrayList;
import java.util.List;

public class VocaAddActivity extends AppCompatActivity {

    private EditText et_voca;
    private EditText et_explanation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voca_add);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        VocaDatabase vocaDatabase = VocaDatabase.getInstance(this);
        VocaDao vocaDao = vocaDatabase.vocaDao();
        List<VocaItem> vocaItems = vocaDao.getVocaItems();

        et_voca = findViewById(R.id.et_voca);
        et_explanation = findViewById(R.id.et_explanation);
        Button btn_save = findViewById(R.id.btn_save);

        int intentType = getIntent().getIntExtra("type", 0);
        int intentPos = getIntent().getIntExtra("position", 0);

        if (intentType == 0) {
            toolbar.setTitle("단어장 만들기");
            btn_save.setOnClickListener(view -> {
                VocaItem vocaItem = new VocaItem();
                vocaItem.setVoca(et_voca.getText().toString());
                vocaItem.setExplanation(et_explanation.getText().toString());
                vocaItem.setWordsId(new Gson().toJson(new ArrayList<Integer>()));
                vocaItem.setFavorites(0);
                vocaDao.insertVoca(vocaItem);
                Toast.makeText(VocaAddActivity.this, "새로운 단어장을 만들었습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            });

        }
        else if (intentType == 1) {
            toolbar.setTitle(R.string.edit_voca);
            VocaItem vocaItem = vocaItems.get(intentPos);
            et_voca.setText(vocaItem.getVoca());
            et_explanation.setText(vocaItem.getExplanation());

            btn_save.setOnClickListener(view -> {
                vocaItem.setVoca(et_voca.getText().toString());
                vocaItem.setExplanation(et_explanation.getText().toString());

                vocaDao.updateVoca(vocaItem);
                Toast.makeText(view.getContext(), "단어장이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            });
        }
    }
}