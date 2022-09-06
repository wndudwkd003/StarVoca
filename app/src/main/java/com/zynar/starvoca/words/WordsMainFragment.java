package com.zynar.starvoca.words;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zynar.starvoca.AppDatabase;
import com.zynar.starvoca.R;
import com.zynar.starvoca.vocabulary.VocaItem;

import java.util.ArrayList;
import java.util.List;

public class WordsMainFragment extends Fragment {

    private AppDatabase db;
    // words 데이터베이스
    private List<WordsItem> wordsItems;
    // voca 데이터베이스
    private List<VocaItem> vocaItems;
    // 레이아웃
    private RecyclerView rv_words;

    public WordsMainFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_words_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());

        // words 데이터베이스
        wordsItems = new ArrayList<>();


        // voca 데이터베이스
        vocaItems = new ArrayList<>();

        // 레이아웃 연결
        rv_words = view.findViewById(R.id.rv_words);
        FloatingActionButton fab_add_words = view.findViewById(R.id.fab_words_add);

        // fab 버튼 클릭
        fab_add_words.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), WordsAddActivity.class);
            intent.putExtra("type", 0);
            startActivity(intent);

        });

        // 마진 적용
        WordsItemDecoration wordsItemDecoration = new WordsItemDecoration(requireContext());
        rv_words.addItemDecoration(wordsItemDecoration);
    }

    @Override
    public void onResume() {
        super.onResume();

        wordsItems = db.wordsDao().getWordsItems();
        vocaItems = db.vocaDao().getVocaItems();

        WordsRVAdapter wordsRVAdapter = new WordsRVAdapter(wordsItems, vocaItems, requireContext(), 0);
        rv_words.setHasFixedSize(true);
        rv_words.setAdapter(wordsRVAdapter);
    }
}