package com.zynar.starvoca.words;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zynar.starvoca.R;
import com.zynar.starvoca.VocaManager;
import com.zynar.starvoca.databinding.FragmentVocaMainBinding;
import com.zynar.starvoca.databinding.FragmentWordsMainBinding;

public class WordsMainFragment extends Fragment {

    private FragmentWordsMainBinding mBinding;
    public WordsMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        mBinding = FragmentWordsMainBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // recycler view
        RecyclerView rv_words = view.findViewById(R.id.rv_words);
        rv_words.setHasFixedSize(true);

        // adapter
        WordsRVAdapter wordsRVAdapter = new WordsRVAdapter(requireContext(), 0);

        // view model
        WordsMainViewModel mainViewModel = new ViewModelProvider(this).get(WordsMainViewModel.class);

        // 어댑터 초기 값
        wordsRVAdapter.setWordsItems(mainViewModel.getWordsItems());
        wordsRVAdapter.setVocaItems(mainViewModel.getVocaItems());

        // set adapter
        rv_words.setAdapter(wordsRVAdapter);

        TextView tv_isVoid = view.findViewById(R.id.tv_isVoid);

        // observe
        mainViewModel.liveData_WordsItem().observe(getViewLifecycleOwner(), wordsItems -> {
            // VocaItems 비어있으면 단어장을 추가하라는 tv 출력
            if(!wordsItems.isEmpty()) {
                tv_isVoid.setVisibility(View.GONE);
            } else tv_isVoid.setVisibility(View.VISIBLE);

            VocaManager.getInstance().setWordsItemList(wordsItems);
            wordsRVAdapter.setWordsItems(VocaManager.getInstance().getWordsItemList());
        });

        mainViewModel.livedata_VocaItems().observe(getViewLifecycleOwner(), vocaItems -> {
            VocaManager.getInstance().setVocaItemList(vocaItems);
            wordsRVAdapter.setVocaItems(VocaManager.getInstance().getVocaItemList());
        });

        // words add fab
        FloatingActionButton fab_add_words = view.findViewById(R.id.fab_words_add);
        fab_add_words.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), WordsAddActivity.class);
            intent.putExtra("type", 0);
            startActivity(intent);
        });

    }

}