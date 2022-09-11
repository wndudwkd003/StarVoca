package com.zynar.starvoca.vocabulary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zynar.starvoca.AppDatabase;
import com.zynar.starvoca.R;
import com.zynar.starvoca.words.WordsDao;
import com.zynar.starvoca.words.WordsItem;
import com.zynar.starvoca.words.WordsItemDecoration;
import com.zynar.starvoca.words.WordsRVAdapter;

import java.util.ArrayList;
import java.util.List;

public class VocaReadActivity extends AppCompatActivity {

    private List<VocaItem> vocaItems;
    private RecyclerView recyclerView;
    private int parentPos;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voca_read);

        db = AppDatabase.getInstance(this);

        Intent intent = getIntent();
        parentPos = intent.getIntExtra("parentPos", 0);

        vocaItems = db.vocaDao().getVocaItems();

        VocaItem vocaItem = vocaItems.get(parentPos);

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
            WordsItem wordsItem = db.wordsDao().getWordsListForVoca(i);
            wordsItemList.add(wordsItem);
        }
        //
        vocaItems = db.vocaDao().getVocaItems();

        WordsRVAdapter wordsRVAdapter = new WordsRVAdapter(this, 1);
        wordsRVAdapter.setWordsItems(wordsItemList);
        wordsRVAdapter.setVocaItems(vocaItems);


        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(wordsRVAdapter);
    }
}