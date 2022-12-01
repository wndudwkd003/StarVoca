package com.zynar.starvoca.vocabulary;

import android.content.Intent;
import android.os.Bundle;
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
import com.zynar.starvoca.words.WordsItemDecoration;

public class VocaMainFragment extends Fragment {

    /* 데이터 바인딩 */
    private FragmentVocaMainBinding mBinding;

    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean mFabFlag = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentVocaMainBinding.inflate(inflater, container, false);

        rotateOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim);

        mBinding.fab.setOnClickListener(v -> {
            onFabButton();
        });

        mBinding.fabDel.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "단어장 삭제", Toast.LENGTH_LONG).show();
        });

        mBinding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VocaAddActivity.class);
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
            mBinding.fab.startAnimation(rotateOpen);
        } else {
            mBinding.fabAdd.setVisibility(View.INVISIBLE);
            mBinding.fabDel.setVisibility(View.INVISIBLE);
            mBinding.fabAdd.startAnimation(toBottom);
            mBinding.fabDel.startAnimation(toBottom);
            mBinding.fab.startAnimation(rotateClose);
        }
        mFabFlag = !mFabFlag;

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




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}

