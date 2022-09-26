package com.zynar.starvoca.info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.ActivityCsvManagementBinding;

public class CsvManagementActivity extends AppCompatActivity {
    private int wordsCnt;
    private ActivityCsvManagementBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityCsvManagementBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        wordsCnt = getIntent().getIntExtra("wordsCnt", 0);

        /* 일단 csv 파일 불러오는 프래그먼트 실행 */
        CsvLoadFragment csvLoadFragment = new CsvLoadFragment();
        getSupportFragmentManager().beginTransaction().replace(mBinding.flMain.getId(), csvLoadFragment).commit();

        /* 툴바 뒤로가기버튼 */
        mBinding.toolbar.setNavigationOnClickListener(v-> onBackPressed());
    }

    /* 프래그먼트 추가 메소드 */
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(mBinding.flMain.getId(), fragment).addToBackStack(null).commit();

    }

    public int getWordsCnt(){
        return wordsCnt;
    }

}