package com.zynar.starvoca.info;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zynar.starvoca.OnBackPressedListener;
import com.zynar.starvoca.databinding.FragmentCsvLoadApplyBinding;
import com.zynar.starvoca.words.WordsItem;

import java.util.List;

public class CsvLoadApplyFragment extends Fragment{

    private FragmentCsvLoadApplyBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentCsvLoadApplyBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            /* 번들로 값 세팅 */
            int failedCnt = getArguments().getInt("failedCnt");
            String[] voca = getArguments().getStringArray("VocaItem");
            String vocaName = voca[0];
            String vocaExplanation = voca[1];

            List<WordsItem> wordsItemList = getArguments().getParcelableArrayList("WordsItemList");

            Log.d("__star__", wordsItemList.toString());

            /* 화면 세팅 */
            mBinding.etVoca.setText(vocaName);
            mBinding.etExplanation.setText(vocaExplanation);

            mBinding.tvFailedWords.setText("불러오지 못한 단어 " + failedCnt +"개");
            mBinding.tvSelectWords.setText("저장 가능한 단어 " + ((CsvManagementActivity)requireActivity()).getWordsCnt() +
                    " | 선택된 단어 " + wordsItemList.size() +"개");

            /* 리사이클러뷰 어댑터 */
            WordsCsvRvAdapter wordsCsvRvAdapter = new WordsCsvRvAdapter(requireContext(), wordsItemList);
            mBinding.rvWords.setHasFixedSize(true);
            mBinding.rvWords.setAdapter(wordsCsvRvAdapter);

        }


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}