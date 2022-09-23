package com.zynar.starvoca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zynar.starvoca.community.CommunityFragment;
import com.zynar.starvoca.info.InfoFragment;
import com.zynar.starvoca.learn.LearnMainFragment;
import com.zynar.starvoca.login.UserAccount;
import com.zynar.starvoca.vocabulary.VocaMainFragment;
import com.zynar.starvoca.words.WordsMainFragment;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // UserAccount
    private UserAccount userAccount = UserAccount.getInstance();
    // 툴바
    private MaterialToolbar toolbar;

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

        /* 매인 엑티비티 시작 전 로그인 정보 설정 */
        setUserAccount();

        ImageButton imb_add_voca = findViewById(R.id.imb_add_voca);
        ImageButton imb_add_words = findViewById(R.id.imb_add_words);
        ImageButton imb_search = findViewById(R.id.imb_search);
        ImageButton imb_sort = findViewById(R.id.imb_sort);

        // 툴바 초기화
        toolbar = findViewById(R.id.toolbar);
        // 바텀 네비게이션 초기화
        // 바텀 네비게이션 메인 메뉴
        BottomNavigationView bottomNavigationView = findViewById(R.id.navi_main_menu);

        // 바텀 네비게이션 메뉴 클릭
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.main_menu_voca) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, vocaMainFragment).commit();
                imb_add_voca.setVisibility(View.VISIBLE);
                imb_add_words.setVisibility(View.GONE);
                imb_search.setVisibility(View.VISIBLE);
                imb_sort.setVisibility(View.VISIBLE);
                toolbar.setTitle(R.string.main_menu_voca);
            } else if (itemId == R.id.main_menu_words) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, wordsMainFragment).commit();
                imb_add_voca.setVisibility(View.GONE);
                imb_add_words.setVisibility(View.VISIBLE);
                imb_search.setVisibility(View.VISIBLE);
                imb_sort.setVisibility(View.VISIBLE);
                toolbar.setTitle(R.string.main_menu_words);
            } else if (itemId == R.id.main_menu_learn) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, learnMainFragment).commit();
                imb_add_voca.setVisibility(View.GONE);
                imb_add_words.setVisibility(View.GONE);
                imb_search.setVisibility(View.GONE);
                imb_sort.setVisibility(View.GONE);
                toolbar.setTitle(R.string.main_menu_learn);
            } else if (itemId == R.id.main_menu_community) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, communityFragment).commit();
                imb_add_voca.setVisibility(View.GONE);
                imb_add_words.setVisibility(View.GONE);
                imb_search.setVisibility(View.GONE);
                imb_sort.setVisibility(View.GONE);
                toolbar.setTitle(R.string.main_menu_community);
            } else if (itemId == R.id.main_menu_info) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, infoFragment).commit();
                imb_add_voca.setVisibility(View.GONE);
                imb_add_words.setVisibility(View.GONE);
                imb_search.setVisibility(View.GONE);
                imb_sort.setVisibility(View.GONE);
                toolbar.setTitle(R.string.main_menu_info);
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.main_menu_voca);


    }

    private void setUserAccount() {
        /* 로그인 타입을 Intent를 통해서 받아옴 */
        String loginType = getIntent().getStringExtra("loginType");

        SharedPreferences sp = getSharedPreferences("userShared", 0);
        SharedPreferences.Editor editor = sp.edit();

        /* SharedPreferences 저장되어있는 정보를 쓸 수 있게 불러옴 */
        String uid, email, nickname;
        int gender;
        uid = sp.getString("uid", "");
        email = sp.getString("email","");
        nickname = sp.getString("nickname","");
        gender = sp.getInt("gender", 2);

        if(uid.isEmpty()) {
            uid = UUID.randomUUID().toString();
            nickname = "닉네임"+uid.substring(0, 7);
        }

        
        if(loginType.equals("noLogin")) {
            /*로그인 없이 앱 실행*/
            userAccount.setUid(uid);
            userAccount.setEmail(email);
            userAccount.setNickname(nickname);
            userAccount.setGender(gender);

            setPreferencesEditor(
                    userAccount.getUid(),
                    userAccount.getEmail(),
                    userAccount.getNickname(),
                    userAccount.getGender()
            );

            editor.putString("loginType", "noLogin");
            editor.apply();

        } else if(loginType.equals("email")) {
            /* 파이어베이스 이메일 로그인 하여 앱 실행 */
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

            /* 데이터 베이스 정보 불러옴 */
            dbRef.child("UserAccount").child("Email").child(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    userAccount = task.getResult().getValue(UserAccount.class);

                    /* 불러온 정보 preferences 저장 */
                    setPreferencesEditor(
                            userAccount.getUid(),
                            userAccount.getEmail(),
                            userAccount.getNickname(),
                            userAccount.getGender()
                            );

                    editor.putString("loginType", "email");
                }
            });
        }
    }

    private void setPreferencesEditor(String uid, String email, String nickname, int gender) {
        SharedPreferences sp = getSharedPreferences("userShared", 0);
        SharedPreferences.Editor editor = sp.edit();

        /* 한번 진행 했기 때문에 오토 로그인 활성화 */
        editor.putBoolean("autoLogin", true);

        /* 사용자 정보를 SharedPreferences 저장 */
        editor.putString("uid", uid);
        editor.putString("email", email);
        editor.putString("nickname", nickname);
        editor.putInt("gender", gender);

        editor.apply();
    }
}