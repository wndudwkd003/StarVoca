package com.zynar.starvoca.info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.zynar.starvoca.R;

public class MyinfoActivity extends AppCompatActivity {

    private final EditMyinfoFragment editMyinfoFragment = new EditMyinfoFragment();
    private final BuySlotFragment buySlotFragment = new BuySlotFragment();
    private final CsvFragment csvFragment = new CsvFragment();
    private final SettingsFragment settingsFragment = new SettingsFragment();
    private final InquiryFragment inquiryFragment = new InquiryFragment();
    private final PriFragment priFragment = new PriFragment();
    private final LoginBlankFragment loginBlankFragment = new LoginBlankFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitle("");
        int getInt = getIntent().getIntExtra("list-pos", -1);

        switch (getInt) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_myinfo_1, editMyinfoFragment).commit();
                toolbar.setTitle("내 정보 수정");
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_myinfo_1, buySlotFragment).commit();
                toolbar.setTitle("단어 슬롯 구매");
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_myinfo_1, csvFragment).commit();
                toolbar.setTitle("CSV 파일 관리");
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_myinfo_1, settingsFragment).commit();
                toolbar.setTitle("앱 환경설정");
                break;
            case 4:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_myinfo_1, inquiryFragment).commit();
                toolbar.setTitle("문의하기");
                break;
            case 5:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_myinfo_1, priFragment).commit();
                toolbar.setTitle("개인정보처리방침");
                break;
            case 6:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_myinfo_1, loginBlankFragment).commit();
                toolbar.setTitle("로그인");
            default:
                break;
        }

    }
}