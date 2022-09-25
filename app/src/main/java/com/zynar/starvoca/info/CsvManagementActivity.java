package com.zynar.starvoca.info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.zynar.starvoca.databinding.ActivityCsvManagementBinding;

public class CsvManagementActivity extends AppCompatActivity {

    private ActivityCsvManagementBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityCsvManagementBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        /* 일단 csv 파일 불러오는 프래그먼트 실행 */
        CsvLoadFragment csvLoadFragment = new CsvLoadFragment();
        getSupportFragmentManager().beginTransaction().replace(mBinding.flMain.getId(), csvLoadFragment).commit();
    }

    public void changeFrag(boolean flag) {
        if(flag) {
            CsvLoadApplyFragment csvLoadApplyFragment = new CsvLoadApplyFragment();
            getSupportFragmentManager().beginTransaction().replace(mBinding.flMain.getId(), csvLoadApplyFragment).commit();
        }
    }

}