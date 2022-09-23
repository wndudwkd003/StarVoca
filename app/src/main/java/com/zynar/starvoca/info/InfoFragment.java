package com.zynar.starvoca.info;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.zynar.starvoca.AppDatabase;
import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.FragmentInfoBinding;
import com.zynar.starvoca.login.LoginActivity;
import com.zynar.starvoca.login.UserAccount;
import com.zynar.starvoca.words.WordsItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InfoFragment extends Fragment{
    private UserAccount userAccount = UserAccount.getInstance();
    private FragmentInfoBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentInfoBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* 로그인 타입을 메인 액티비티에서 받아옴 */
        String loginType = getArguments() != null ? getArguments().getString("loginType") : "";

        List<String> data = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, data);
        mBinding.listMyinfo.setAdapter(adapter);

        /* 내 정보 프래그먼트 탭 목록 */
        data.add("내 정보 수정");
        data.add("단어 슬롯 구매");
        data.add("CSV 파일 관리");
        data.add("앱 환경설정");
        data.add("문의하기");
        data.add("개인정보처리방침");
        data.add("로그인");
        if(loginType.equals("noLogin")) data.set(6, "로그인");
        else data.set(6, "로그아웃");

        adapter.notifyDataSetChanged();

        mBinding.listMyinfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 4) {
                    inquiry();
                } else if(i == 6) {
                    String loginText = adapter.getItem(i);
                    if(loginText.equals("로그아웃")) {
                        logOut();
                    } else {
                        logIn();
                    }
                }
                else {
                    startActivity(new Intent(requireContext(), MyinfoActivity.class).putExtra("list-pos", i));
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        AppDatabase db = AppDatabase.getInstance(requireContext());
        List<WordsItem> list = db.wordsDao().getWordsItems();

        /* Info 프래그먼트의 유저 정보 세팅 */
        mBinding.tvNickname.setText(userAccount.getNickname());
        mBinding.tvId.setText(!userAccount.getEmail().equals("") ? userAccount.getEmail() : "비로그인");
        mBinding.tvMessage.setText(userAccount.getMessage());
        mBinding.tvWordsCnt.setText(list.size() + " / " + userAccount.getMaxCntWords());

    }

    private void logIn() {
        /* 로그인 */

        /* 자동 로그인 해제 */
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userShared", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("autoLogin", false);
        editor.apply();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void logOut() {
        /* 로그아웃 */

        /* 사용자 정보 제거 */
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userShared", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("autoLogin", false);
        editor.putString("loginType", null);
        editor.putString("uid", null);
        editor.putString("email", null);
        editor.putString("pw", null);
        editor.putString("nickname", null);
        editor.apply();

        /* 파이어베이스 로그아웃 */
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void inquiry() {
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] address = {"onheaven003@gmail.com"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT, "[Star-Voca 문의사항] 제목없음");
        email.putExtra(Intent.EXTRA_TEXT, "StarVoca 문의하기 입니다.\n상세히 작성해주시면 신속한 처리가 가능합니다.\n\n닉네임: \n날짜: "+time+"\n내용: ");
        startActivity(email);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}