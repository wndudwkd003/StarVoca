package com.zynar.starvoca.info;

import android.os.Bundle;
import android.os.Parcelable;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentCsvLoadBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.btnLoadCsv.setOnClickListener(v -> {

            /* CSV 서포트 클래스 */
            AppCSVSupport appCSVSupport = new AppCSVSupport();
            int failedCnt = appCSVSupport.setCsv(requireContext());

            /* 클래스에서 받은 데이터 세팅 */
            List<WordsItem> wordsItemList = appCSVSupport.getWordsItems();
            VocaItem vocaItem = appCSVSupport.getVocaItem();

            /* 번들로 값 전달 */
            Bundle bundle = new Bundle();
            bundle.putInt("failedCnt", failedCnt);
            bundle.putStringArray("VocaItem", new String[]{vocaItem.getVoca(), vocaItem.getExplanation()});
            bundle.putParcelableArrayList("WordsItemList", (ArrayList<? extends Parcelable>) wordsItemList);

            /* 프래그먼트 생성 및 번들 세팅 */
            CsvLoadApplyFragment csvLoadApplyFragment = new CsvLoadApplyFragment();
            csvLoadApplyFragment.setArguments(bundle);

            /* 단어장 생성 적용 프래그먼트로 이동 */
            ((CsvManagementActivity)requireActivity()).replaceFragment(csvLoadApplyFragment);

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}