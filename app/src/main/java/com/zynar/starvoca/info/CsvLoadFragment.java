package com.zynar.starvoca.info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zynar.starvoca.AppCSVSupport;
import com.zynar.starvoca.MainActivity;
import com.zynar.starvoca.databinding.FragmentCsvLoadBinding;
import com.zynar.starvoca.vocabulary.VocaItem;
import com.zynar.starvoca.words.WordsItem;

import java.util.ArrayList;
import java.util.List;

public class CsvLoadFragment extends Fragment {
    private FragmentCsvLoadBinding mBinding;
    private MainActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentCsvLoadBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.btnLoadCsv.setOnClickListener(v -> {
            List<WordsItem> wordsItemList = new ArrayList<>();
            VocaItem vocaItem = new VocaItem();

            AppCSVSupport appCSVSupport = new AppCSVSupport();
            int failedCnt = appCSVSupport.setCsv(requireContext());
            Log.d("__star__", String.valueOf(failedCnt));

            wordsItemList = appCSVSupport.getWordsItems();
            vocaItem = appCSVSupport.getVocaItem();

            Log.d("__star__", wordsItemList.toString());
            Log.d("__star__", vocaItem.toString());

            mainActivity.replaceFragment(CsvLoadApplyFragment.newInstance());

        });
    }
}