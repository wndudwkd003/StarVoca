package com.zynar.starvoca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zynar.starvoca.community.CommunityFragment;
import com.zynar.starvoca.info.InfoFragment;
import com.zynar.starvoca.learn.LearnMainFragment;
import com.zynar.starvoca.vocabulary.VocaMainFragment;
import com.zynar.starvoca.words.WordsMainFragment;

public class MainActivity extends AppCompatActivity {


    // 툴바
    private MaterialToolbar toolbar;

    // 바텀 네비게이션 메인 메뉴
    private BottomNavigationView bottomNavigationView;

    // 메인 5개 메뉴 프래그먼트
    private final VocaMainFragment vocaMainFragment = new VocaMainFragment();
    private final WordsMainFragment wordsMainFragment = new WordsMainFragment();
    private final LearnMainFragment learnMainFragment = new LearnMainFragment();
    private final CommunityFragment communityFragment = new CommunityFragment();
    private final InfoFragment infoFragment = new InfoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 툴바 초기화
        toolbar = findViewById(R.id.toolbar);
        // 바텀 네비게이션 초기화
        bottomNavigationView = findViewById(R.id.navi_main_menu);

        // 바텀 네비게이션 메뉴 클릭
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.main_menu_voca) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, vocaMainFragment).commit();
                toolbar.setTitle(R.string.main_menu_voca);
            } else if (itemId == R.id.main_menu_words) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, wordsMainFragment).commit();
                toolbar.setTitle(R.string.main_menu_words);
            } else if (itemId == R.id.main_menu_learn) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, learnMainFragment).commit();
                toolbar.setTitle(R.string.main_menu_learn);
            } else if (itemId == R.id.main_menu_community) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, communityFragment).commit();
                toolbar.setTitle(R.string.main_menu_community);
            } else if (itemId == R.id.main_menu_info) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, infoFragment).commit();
                toolbar.setTitle(R.string.main_menu_info);
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.main_menu_voca);


    }
}