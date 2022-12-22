package com.zynar.starvoca.words;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zynar.starvoca.vocabulary.VocaAddActivity;

public class WordsMainFragment extends Fragment {

    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean mFabFlag = false;

    private FragmentWordsMainBinding mBinding;
    public WordsMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentWordsMainBinding.inflate(inflater, container, false);

        rotateOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim);

        // recycler view
        mBinding.rvWords.setHasFixedSize(true);
        // adapter
        WordsRVAdapter wordsRVAdapter = new WordsRVAdapter(requireContext(), 0);

        // view model
        WordsMainViewModel mainViewModel = new ViewModelProvider(this).get(WordsMainViewModel.class);

        // 어댑터 초기 값
        wordsRVAdapter.setWordsItems(mainViewModel.getWordsItems());
        wordsRVAdapter.setVocaItems(mainViewModel.getVocaItems());

        // set adapter
        mBinding.rvWords.setAdapter(wordsRVAdapter);





        // observe
        mainViewModel.liveData_WordsItem().observe(getViewLifecycleOwner(), wordsItems -> {
            // VocaItems 비어있으면 단어장을 추가하라는 tv 출력
            if(!wordsItems.isEmpty()) {
                mBinding.tvIsVoid.setVisibility(View.GONE);
            } else mBinding.tvIsVoid.setVisibility(View.VISIBLE);

            VocaManager.getInstance().setWordsItemList(wordsItems);
            wordsRVAdapter.setWordsItems(VocaManager.getInstance().getWordsItemList());
        });

        mainViewModel.livedata_VocaItems().observe(getViewLifecycleOwner(), vocaItems -> {
            VocaManager.getInstance().setVocaItemList(vocaItems);
            wordsRVAdapter.setVocaItems(VocaManager.getInstance().getVocaItemList());
        });


        mBinding.fab.setOnClickListener(v -> {
            onFabButton();
        });

        mBinding.fabDel.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "단어장 삭제", Toast.LENGTH_LONG).show();
        });

        mBinding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), WordsAddActivity.class);
            intent.putExtra("type", 0);
            startActivity(intent);
        });

        return mBinding.getRoot();
    }


    private void onFabButton() {
        // floating button 클릭 애니메이션 효과
        if(!mFabFlag){
            mBinding.fabAdd.setVisibility(View.VISIBLE);
            mBinding.fabDel.setVisibility(View.VISIBLE);
            mBinding.fabAdd.startAnimation(fromBottom);
            mBinding.fabDel.startAnimation(fromBottom);
            mBinding.fabAdd.setClickable(true);
            mBinding.fabDel.setClickable(true);
            mBinding.fab.startAnimation(rotateOpen);
        } else {
            mBinding.fabAdd.setVisibility(View.INVISIBLE);
            mBinding.fabAdd.setVisibility(View.INVISIBLE);
            mBinding.fabAdd.startAnimation(toBottom);
            mBinding.fabAdd.setClickable(false);
            mBinding.fabDel.setClickable(false);
            mBinding.fabDel.startAnimation(toBottom);
            mBinding.fab.startAnimation(rotateClose);
        }
        mFabFlag = !mFabFlag;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}