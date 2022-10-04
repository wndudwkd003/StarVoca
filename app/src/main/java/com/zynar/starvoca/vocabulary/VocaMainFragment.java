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
import com.zynar.starvoca.VocaManager;
import com.zynar.starvoca.databinding.FragmentVocaMainBinding;
import com.zynar.starvoca.words.WordsItemDecoration;

public class VocaMainFragment extends Fragment {

    /* 데이터 바인딩 */
    private FragmentVocaMainBinding mBinding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentVocaMainBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* 단어장 리사이클러뷰 어댑터 */
        VocaRVAdapter vocaRVAdapter = new VocaRVAdapter(requireContext());

        // recycler view
        mBinding.rvVoca.setHasFixedSize(true);

        // view model

        VocaMainViewModel mainViewModel = new ViewModelProvider(this).get(VocaMainViewModel.class);
        vocaRVAdapter.setVocaItems(mainViewModel.getVocaItems());

        // set adapter
        mBinding.rvVoca.setAdapter(vocaRVAdapter);


        // observe
        mainViewModel.livedata_VocaItems().observe(getViewLifecycleOwner(), vocaItems -> {
            // VocaItems 비어있으면 단어장을 추가하라는 tv 출력
            if(!vocaItems.isEmpty()) {
                mBinding.tvIsVoid.setVisibility(View.GONE);
            } else mBinding.tvIsVoid.setVisibility(View.VISIBLE);

            VocaManager.getInstance().setVocaItemList(vocaItems);
            vocaRVAdapter.setVocaItems(VocaManager.getInstance().getVocaItemList());
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

