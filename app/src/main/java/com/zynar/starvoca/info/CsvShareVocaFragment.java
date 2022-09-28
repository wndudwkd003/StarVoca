package com.zynar.starvoca.info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zynar.starvoca.R;
import com.zynar.starvoca.VocaManager;
import com.zynar.starvoca.databinding.FragmentCsvShareVocaBinding;

public class CsvShareVocaFragment extends Fragment {
    private FragmentCsvShareVocaBinding mBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentCsvShareVocaBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        /* 리사이클러뷰 어댑터 */
        VocaCsvRvAdapter vocaCsvRvAdapter = new VocaCsvRvAdapter(requireContext(), VocaManager.getInstance().getVocaItemList());
        mBinding.rvWords.setHasFixedSize(true);
        mBinding.rvWords.setAdapter(vocaCsvRvAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}