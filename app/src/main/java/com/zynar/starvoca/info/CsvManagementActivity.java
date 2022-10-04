package com.zynar.starvoca.info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zynar.starvoca.AppSupport;
import com.zynar.starvoca.R;
import com.zynar.starvoca.VocaManager;
import com.zynar.starvoca.databinding.ActivityCsvManagementBinding;
import com.zynar.starvoca.login.UserAccount;

public class CsvManagementActivity extends AppCompatActivity {
    private int wordsCnt;
    private ActivityCsvManagementBinding mBinding;
    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityCsvManagementBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        /* 권한 */
        AppSupport appSupport = new AppSupport();
        appSupport.setPermission(CsvManagementActivity.this);

        wordsCnt = UserAccount.getInstance().getMaxCntWords() - VocaManager.getInstance().getWordsCnt();


        /* 인텐트 엑스트라에 맞는 프래그먼트 실행 */
        String activeFragment = getIntent().getStringExtra("ActiveFragment");
        if(activeFragment.equals("fragCsvLoad")) {
            /* 프래그먼트 실행 */
            CsvLoadFragment csvLoadFragment = new CsvLoadFragment();
            getSupportFragmentManager().beginTransaction().replace(mBinding.flMain.getId(), csvLoadFragment).commit();
        } else if(activeFragment.equals("fragShareVoca")) {
            /* 프래그먼트 실행 */
            CsvShareVocaFragment csvShareVocaFragment = new CsvShareVocaFragment();
            getSupportFragmentManager().beginTransaction().replace(mBinding.flMain.getId(), csvShareVocaFragment).commit();
        }

        /* 툴바 뒤로가기버튼 */
        mBinding.toolbar.setNavigationOnClickListener(v-> onBackPressed());
    }

    /* 프래그먼트 추가 메소드 */
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(mBinding.flMain.getId(), fragment).addToBackStack(null).commit();

    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getWordsCnt(){
        return wordsCnt;
    }

}