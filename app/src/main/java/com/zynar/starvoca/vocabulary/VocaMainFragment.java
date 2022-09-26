package com.zynar.starvoca.vocabulary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zynar.starvoca.R;
import com.zynar.starvoca.words.WordsItemDecoration;

public class VocaMainFragment extends Fragment {

    public VocaMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voca_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // recycler view
        RecyclerView rv_voca = view.findViewById(R.id.rv_voca);
        rv_voca.setHasFixedSize(true);

        // adapter
        VocaRVAdapter vocaRVAdapter = new VocaRVAdapter(getContext());

        // view model
        VocaMainViewModel mainViewModel = new ViewModelProvider(this).get(VocaMainViewModel.class);
        vocaRVAdapter.setVocaItems(mainViewModel.getVocaItems());

        // set adapter
        rv_voca.setAdapter(vocaRVAdapter);

        // isVoid
        TextView tv_isVoid = view.findViewById(R.id.tv_isVoid);

        // observe
        mainViewModel.livedata_VocaItems().observe(getViewLifecycleOwner(), vocaItems -> {
            // VocaItems 비어있으면 단어장을 추가하라는 tv 출력
            if(!vocaItems.isEmpty()) {
                tv_isVoid.setVisibility(View.GONE);
            } else tv_isVoid.setVisibility(View.VISIBLE);

            vocaRVAdapter.setVocaItems(vocaItems);
        });





        // voca add fab
        FloatingActionButton fab_add_voca = view.findViewById(R.id.fab_voca_add);
        fab_add_voca.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), VocaAddActivity.class);
            intent.putExtra("type", 0);
            startActivity(intent);
        });

    }

}

