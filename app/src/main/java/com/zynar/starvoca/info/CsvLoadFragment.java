package com.zynar.starvoca.info;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.zynar.starvoca.AppCSVSupport;
import com.zynar.starvoca.AppSupport;
import com.zynar.starvoca.MainActivity;
import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.FragmentCsvLoadBinding;
import com.zynar.starvoca.vocabulary.VocaItem;
import com.zynar.starvoca.words.WordsItem;

import java.util.ArrayList;
import java.util.List;

public class CsvLoadFragment extends Fragment {
    private FragmentCsvLoadBinding mBinding;

    private final AppCSVSupport appCSVSupport = new AppCSVSupport();
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
        if(uri != null) {
            /* 파일 선택 uri 설정 */
            appCSVSupport.setUri(uri);

            /* CSV 서포트 클래스 */
            int failedCnt = appCSVSupport.setCsv(requireContext());

            /* 번들로 값 전달 */
            Bundle bundle = new Bundle();
            bundle.putInt("failedCnt", failedCnt);
            bundle.putStringArray("VocaItem", new String[]{appCSVSupport.getVocaItem().getVoca(), appCSVSupport.getVocaItem().getExplanation()});
            bundle.putParcelableArrayList("WordsItemList", (ArrayList<? extends Parcelable>) appCSVSupport.getWordsItems());

            /* 프래그먼트 생성 및 번들 세팅 */
            CsvLoadApplyFragment csvLoadApplyFragment = new CsvLoadApplyFragment();
            csvLoadApplyFragment.setArguments(bundle);

            /* 단어장 생성 적용 프래그먼트로 이동 */
            ((CsvManagementActivity)requireActivity()).replaceFragment(csvLoadApplyFragment);
        } else {
            Toast.makeText(requireContext(), "파일이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentCsvLoadBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* 권한 설정 */
        if(!appCSVSupport.isPermission()) {
            AppSupport appSupport = new AppSupport();
            appSupport.setPermission(requireActivity());
        }

        /* 선택 버튼 */
        mBinding.btnLoadCsv.setOnClickListener(v -> {
            /* csv 파일 선택 */
            mGetContent.launch("text/*");
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().findViewById(R.id.tv_csv_save).setVisibility(View.GONE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}