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
import com.zynar.starvoca.R;
import com.zynar.starvoca.vocabulary.VocaDao;
import com.zynar.starvoca.vocabulary.VocaDatabase;
import com.zynar.starvoca.vocabulary.VocaItem;

import java.util.ArrayList;
import java.util.List;

public class WordsMainFragment extends Fragment {

    // words아이템
    private VocaDatabase vocaDatabase;
    private VocaDao vocaDao;
    private List<VocaItem> vocaItems;

    // 룸 데이터베이스
    private WordsDatabase wordsDatabase;
    private WordsDao wordsDao;
    private List<WordsItem> wordsItems;


    // 레이아웃
    private RecyclerView rv_words;
    private FloatingActionButton fab_add_words;

    //
    private WordsItemDecoration wordsItemDecoration;

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

        // 룸 데이터 베이스
        wordsDatabase = WordsDatabase.getInstance(getContext());
        wordsDao = wordsDatabase.wordsDao();
        wordsItems = new ArrayList<>();





        // 리사이클러뷰
        wordsItems = new ArrayList<>();
        vocaDatabase = VocaDatabase.getInstance(getContext());
        vocaDao = vocaDatabase.vocaDao();
        vocaItems = new ArrayList<>();
        // 레이아웃 연결
        rv_words = view.findViewById(R.id.rv_words);
        fab_add_words = view.findViewById(R.id.fab_words_add);

        // fab 버튼 클릭
        fab_add_words.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), WordsAddActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);

            }
        });

        // 마진 적용
        wordsItemDecoration = new WordsItemDecoration(getContext());
        rv_words.addItemDecoration(wordsItemDecoration);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 저장되어있던 DB를 가져온다
        wordsItems = wordsDao.getWordsItems();
        vocaItems = vocaDao.getVocaItems();

        WordsRVAdapter wordsRVAdapter = new WordsRVAdapter(wordsItems, vocaItems, requireContext(), 0);

        rv_words.setHasFixedSize(true);
        rv_words.setAdapter(wordsRVAdapter);
    }
}