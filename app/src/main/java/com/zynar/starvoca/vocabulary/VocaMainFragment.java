package com.zynar.starvoca.vocabulary;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ProvidedTypeConverter;
import androidx.room.Room;
import androidx.room.TypeConverter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zynar.starvoca.R;
import com.zynar.starvoca.words.WordsItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class VocaMainFragment extends Fragment {

    // 레이아웃
    private RecyclerView rv_voca;
    private FloatingActionButton fab_add_voca;

    private WordsItemDecoration wordsItemDecoration;

    // Dao
    private VocaDatabase vocaDatabase;
    private VocaDao vocaDao;
    private List<VocaItem> vocaItems;

    public VocaMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voca_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vocaDatabase = VocaDatabase.getInstance(getContext());
        vocaDao = vocaDatabase.vocaDao();
        vocaItems = new ArrayList<>();

        // 인터페이스, 레이아웃 객체 할당
        rv_voca = view.findViewById(R.id.rv_voca);
        fab_add_voca = view.findViewById(R.id.fab_voca_add);

        // 단어장 추가 버튼 클릭
        fab_add_voca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), VocaAddActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });

        // 마진 적용
        wordsItemDecoration = new WordsItemDecoration(getContext());
        rv_voca.addItemDecoration(wordsItemDecoration);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 저장된 db를 불러온다
        vocaItems = vocaDao.getVocaItems();
        VocaRVAdapter vocaRVAdapter = new VocaRVAdapter(vocaItems, getContext());

        rv_voca.setHasFixedSize(true);
        rv_voca.setAdapter(vocaRVAdapter);
    }
}

