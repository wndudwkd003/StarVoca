package com.zynar.starvoca.vocabulary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zynar.starvoca.R;
import com.zynar.starvoca.words.WordsDao;
import com.zynar.starvoca.words.WordsDatabase;
import com.zynar.starvoca.words.WordsItem;
import com.zynar.starvoca.words.WordsItemDecoration;
import com.zynar.starvoca.words.WordsRVAdapter;

import java.util.ArrayList;
import java.util.List;

public class VocaReadActivity extends AppCompatActivity {

    private List<VocaItem> vocaItems;
    private VocaDao vocaDao;
    private WordsDao wordsDao;
    private RecyclerView recyclerView;
    private int parentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voca_read);

        Intent intent = getIntent();
        parentPos = intent.getIntExtra("parentPos", 0);

        VocaDatabase vocaDatabase = VocaDatabase.getInstance(this);
        vocaDao = vocaDatabase.vocaDao();
        vocaItems = vocaDao.getVocaItems();

        VocaItem vocaItem = vocaItems.get(parentPos);
        WordsDatabase wordsDatabase = WordsDatabase.getInstance(this);
        wordsDao = wordsDatabase.wordsDao();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.main_menu_voca) + " - " + vocaItem.getVoca());
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.rv_words);

        // 마진
        WordsItemDecoration wordsItemDecoration = new WordsItemDecoration(this);
        recyclerView.addItemDecoration(wordsItemDecoration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Integer> wordsId = new Gson().fromJson(vocaItems.get(parentPos).getWordsId(), new TypeToken<List<Integer>>(){}.getType());

        List<WordsItem> wordsItemList = new ArrayList<>();
        for(int i : wordsId) {
            WordsItem wordsItem = wordsDao.getWordsListForVoca(i);
            wordsItemList.add(wordsItem);
        }
        //
        vocaItems = vocaDao.getVocaItems();

        WordsRVAdapter wordsRVAdapter = new WordsRVAdapter(wordsItemList, vocaItems, this, 1);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(wordsRVAdapter);
    }
}