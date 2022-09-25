package com.zynar.starvoca.info;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zynar.starvoca.AppCSVSupport;
import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.ActivityCsvLoadBinding;

public class CsvLoadActivity extends AppCompatActivity {

    private ActivityCsvLoadBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityCsvLoadBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        /* csv 로드 버튼 클릭 */
        mBinding.btnLoadCsv.setOnClickListener(view -> {
            loadCSV();
        });
    }

    private void loadCSV() {
        /* csv 로드 버튼 클릭 */
        AppCSVSupport appCSVSupport = new AppCSVSupport();
        appCSVSupport.setCsv(CsvLoadActivity.this);

    }
}