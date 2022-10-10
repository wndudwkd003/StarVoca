package com.zynar.starvoca.info;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zynar.starvoca.AppDatabase;
import com.zynar.starvoca.OnBackPressedListener;
import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.FragmentCsvLoadApplyBinding;
import com.zynar.starvoca.vocabulary.VocaItem;
import com.zynar.starvoca.words.WordsItem;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class CsvLoadApplyFragment extends Fragment{

    private FragmentCsvLoadApplyBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentCsvLoadApplyBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* if문 안에 작성할 것 */
        if (getArguments() != null) {
            AppDatabase db = AppDatabase.getInstance(requireContext());

            /* 번들로 값 세팅 */
            int failedCnt = getArguments().getInt("failedCnt");
            String[] voca = getArguments().getStringArray("VocaItem");
            String vocaName = voca[0];
            String vocaExplanation = voca[1];

            /* 사용자가 csv로 불러온 단어 리스트 */
            List<WordsItem> wordsItemList = getArguments().getParcelableArrayList("WordsItemList");

            /* 화면 세팅 */
            setUiText(failedCnt, vocaName, vocaExplanation, wordsItemList);

            /* 선택된 단어들의 id를 리스트로 관리 */
            List<Integer> wordsIDs = new ArrayList<>();
            for(int i=0; i< wordsItemList.size(); i++) {
                wordsIDs.add(i);
            }

            /* 리사이클러뷰 어댑터 */
            WordsCsvRvAdapter wordsCsvRvAdapter = new WordsCsvRvAdapter(requireContext(), wordsItemList);
            mBinding.rvWords.setHasFixedSize(true);

            wordsCsvRvAdapter.setOnCheckBoxClickListener(new WordsCsvRvAdapter.CheckBoxClickListener() {
                @Override
                public void onClickCheckBox(int flag, int pos) {
                    /* 체크박스를 눌렀을 때 리스너로 상태(눌렸는지, 위치)를 불러옴 */
                    if(flag == 0) {
                        /* 체크박스 해제 */
                        wordsIDs.remove(pos);
                    } else {
                        wordsIDs.add(pos, pos);
                    }
                    Log.d("__star__", wordsIDs.toString());
                }
            });
            mBinding.rvWords.setAdapter(wordsCsvRvAdapter);

            /* 확인 버튼 클릭 */
            requireActivity().findViewById(R.id.tv_csv_save).setOnClickListener(v -> {
                /* 불러온 CSV 적용 */

                int selCnt = 0;

                for(int i : wordsIDs) {
                    /* wordsIDs의 선택된 words만 db에 저장 */
                    WordsItem item = wordsItemList.get(i);
                    db.wordsDao().insertWords(item);
                    selCnt++;
                }

                if(!mBinding.cbNoCreateVoca.isChecked()) {
                    /* 단어장을 만들고 거기에 넣음 */
                    List<WordsItem> wordsItems = db.wordsDao().getWordsItems();
                    int endId = 0;
                    if(!wordsItems.isEmpty()) {
                        WordsItem wordsItem = wordsItems.get(0);
                        Log.d("__star__", wordsItem.toString());
                        endId = wordsItem.getId();
                    }

                    /* words 아이디를 저장 */
                    List<Integer> wordsId = new ArrayList<>();
                    for(int i=endId - selCnt + 1; i<=endId; i++) {
                        wordsId.add(i);
                    }

                    /* 단어장 등록 */
                    VocaItem vocaItem = new VocaItem();
                    vocaItem.setVoca(vocaName == null ? "새로 추가한 단어장" : vocaName);
                    vocaItem.setExplanation(vocaExplanation == null ? "" : vocaExplanation);
                    vocaItem.setWordsId(new Gson().toJson(wordsId));
                    db.vocaDao().insertVoca(vocaItem);

                }

                Toast.makeText(requireContext(), "내 단어가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
                ((CsvManagementActivity)requireActivity()).setFlag(true);
                requireActivity().onBackPressed();
            });

            /* 단어장 체크 박스 클릭 */
            mBinding.cbNoCreateVoca.setOnClickListener(v -> {
                if(mBinding.cbNoCreateVoca.isChecked()) {
                    /* 단어장 만들지 않기 */
                    mBinding.cdVoca.setVisibility(View.GONE);
                } else {
                    /* 단어장 만들기*/
                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(1000);
                    mBinding.cdVoca.setAnimation(animation);
                    mBinding.cdVoca.setVisibility(View.VISIBLE);
                }
            });

        }
        else {
            Toast.makeText(requireContext(), "잘못된 파일입니다.", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUiText(int failedCnt, String vocaName, String vocaExplanation, List<WordsItem> wordsItemList) {
        mBinding.etVoca.setText(vocaName);
        mBinding.etExplanation.setText(vocaExplanation);

        mBinding.tvFailedWords.setText("불러오지 못한 단어 "
                + failedCnt +"개");
        mBinding.tvSelectWords.setText("저장 가능한 단어 "
                + ((CsvManagementActivity)requireActivity()).getWordsCnt()
                + " | 선택된 단어 " + wordsItemList.size() +"개");
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().findViewById(R.id.tv_csv_save).setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().findViewById(R.id.tv_csv_save).setVisibility(View.GONE);
        mBinding = null;
    }
}