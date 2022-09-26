package com.zynar.starvoca;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zynar.starvoca.community.CommunityFragment;
import com.zynar.starvoca.databinding.ActivityMainBinding;
import com.zynar.starvoca.info.InfoFragment;
import com.zynar.starvoca.learn.LearnMainFragment;
import com.zynar.starvoca.login.UserAccount;
import com.zynar.starvoca.vocabulary.VocaMainFragment;
import com.zynar.starvoca.words.WordsMainFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // 메인 5개 메뉴 프래그먼트
    private final VocaMainFragment vocaMainFragment = new VocaMainFragment();
    private final WordsMainFragment wordsMainFragment = new WordsMainFragment();
    private final LearnMainFragment learnMainFragment = new LearnMainFragment();
    private final CommunityFragment communityFragment = new CommunityFragment();
    private final InfoFragment infoFragment = new InfoFragment();

    // 클래스를 선언
    private PermissionSupport permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 데이터 바인딩 */
        // Binding
        com.zynar.starvoca.databinding.ActivityMainBinding mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        /* 권한 요청 */
        
        /* 매인 엑티비티 시작 전 로그인 정보 설정 */
        String loginType = setUserAccount();

        /* loginType 변수를 번들로 info 프래그먼트에 전달 */
        Bundle bundle = new Bundle();
        bundle.putString("loginType", loginType);
        infoFragment.setArguments(bundle);

        /* ImageButton Init */
        ImageButton imb_add_voca = mBinding.imbAddVoca;
        ImageButton imb_add_words = mBinding.imbAddWords;
        ImageButton imb_search = mBinding.imbSearch;
        ImageButton imb_sort = mBinding.imbSort;

        // 툴바 초기화
        MaterialToolbar toolbar = mBinding.toolbar;

        // 바텀 네비게이션 메뉴 클릭
        mBinding.naviMainMenu.setOnItemSelectedListener(item -> {
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

        mBinding.naviMainMenu.setSelectedItemId(R.id.main_menu_voca);

    }

    private String setUserAccount() {
        /* 로그인 타입을 Intent를 통해서 받아옴 */
        String loginType = getIntent().getStringExtra("loginType");

        SharedPreferences sp = getSharedPreferences("userShared", 0);
        SharedPreferences.Editor editor = sp.edit();

        /* SharedPreferences 저장되어있는 정보를 쓸 수 있게 불러옴 */
        String uid = sp.getString("uid", "");
        String email = sp.getString("email","");
        String nickname = sp.getString("nickname","");
        int gender = sp.getInt("gender", 2);
        String message = sp.getString("message", "");
        int maxCntWords = sp.getInt("maxCntWords", 100);

        if(uid.isEmpty()) {
            uid = UUID.randomUUID().toString();
            nickname = "닉네임"+uid.substring(0, 7);

            editor.putString("loginType", "noLogin");
        }

        
        if(loginType.equals("noLogin")) {
            /*로그인 없이 앱 실행*/
            UserAccount.getInstance().setUid(uid);
            UserAccount.getInstance().setEmail(email);
            UserAccount.getInstance().setNickname(nickname);
            UserAccount.getInstance().setGender(gender);
            UserAccount.getInstance().setMessage(message);
            UserAccount.getInstance().setMaxCntWords(maxCntWords);

        } else if(loginType.equals("Email")) {

            UserAccount userAccount = UserAccount.getInstance();
            uid = userAccount.getUid();
            email = userAccount.getEmail();
            nickname = userAccount.getEmail();
            gender = userAccount.getGender();
            message = userAccount.getMessage();
            maxCntWords = userAccount.getMaxCntWords();


            /* 파일이 있는지 확인 */
            File file = new File(getFilesDir(), "UserProfile");
            if(!file.isFile()) {
                /* 파이어베이스 서버에서 프로필 이미지를 다운받음 */
                try {
                    /* 임시파일 생성 */
                    File tempFile = File.createTempFile("images", "jpeg");
                    StorageReference sgRef = FirebaseStorage.getInstance().getReference();
                    sgRef.child("UserAccount")
                            .child("Email")
                            .child(userAccount.getUid())
                            .child("ProfileImage").getFile(tempFile).addOnSuccessListener(taskSnapshot -> {

                                try{
                                    FileOutputStream fos = openFileOutput("UserProfile", Context.MODE_PRIVATE);
                                    Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getPath());
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            editor.putString("loginType", "Email");
        }

        /* 한번 진행 했기 때문에 오토 로그인 활성화 */
        editor.putBoolean("autoLogin", true);

        /* 불러온 정보 preferences 저장 */
        editor.putString("uid", uid);
        editor.putString("email", email);
        editor.putString("nickname", nickname);
        editor.putInt("gender", gender);
        editor.putString("message", message);
        editor.putInt("maxCntWords", maxCntWords);
        editor.apply();

        return loginType;
    }

}

