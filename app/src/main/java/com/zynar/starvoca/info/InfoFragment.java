package com.zynar.starvoca.info;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.zynar.starvoca.AppDatabase;
import com.zynar.starvoca.R;
import com.zynar.starvoca.VocaManager;
import com.zynar.starvoca.databinding.CustomDialogCsvHelperBinding;
import com.zynar.starvoca.databinding.CustomQuestionDialogBinding;
import com.zynar.starvoca.databinding.FragmentInfoBinding;
import com.zynar.starvoca.login.LoginActivity;
import com.zynar.starvoca.login.UserAccount;
import com.zynar.starvoca.words.WordsItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InfoFragment extends Fragment{
    private UserAccount userAccount = UserAccount.getInstance();
    private FragmentInfoBinding mBinding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentInfoBinding.inflate(inflater, container, false);

        /* 로그인 타입을 메인 액티비티에서 받아옴 */
        String loginType = getArguments() != null ? getArguments().getString("loginType") : "";

        /* 목록 리스트 */
        List<String> data = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, data);
        mBinding.listMyinfo.setAdapter(adapter);

        /* 내 정보 프래그먼트 탭 목록 */
        data.add("내 정보 수정");
        data.add("단어 슬롯 구매");
        data.add("클라우드 백업");
        data.add("CSV 파일 관리");
        data.add("앱 환경설정");
        data.add("문의하기");
        data.add("로그인");
        if(loginType.equals("noLogin")) data.set(6, "로그인");
        else data.set(6, "로그아웃");

        adapter.notifyDataSetChanged();

        mBinding.listMyinfo.setOnItemClickListener((adapterView, view1, i, l) -> {
            if (i == 0) {
                /* 내 정보 수정 */
                if(loginType.equals("noLogin")) {
                    Toast.makeText(requireContext(), "로그인 후 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(requireContext(), EditUserInfoActivity.class).putExtra("loginType", loginType));
                }
            }else if (i == 3) {
                /* CSV 파일 관리 */
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("CSV 파일 관리");
                String[] arr = {"내 기기에서 불러오기", "단어장 내보내기", "CSV 파일이란?"};
                builder.setItems(arr, (dialogInterface, i1) -> {
                    if(i1 == 0) {
                        startActivity(new Intent(requireContext(), CsvManagementActivity.class)
                                .putExtra("ActiveFragment", "fragCsvLoad"));
                    } else if(i1 == 1) {
                        startActivity(new Intent(requireContext(), CsvManagementActivity.class)
                                .putExtra("ActiveFragment", "fragShareVoca"));
                    } else if(i1 == 2) {
                        Dialog dialog = new Dialog(requireContext());
                        /* 다이얼로그 데이터 바인딩 */
                        CustomDialogCsvHelperBinding dialog1Binding = CustomDialogCsvHelperBinding.inflate(getLayoutInflater());
                        dialog.setContentView(dialog1Binding.getRoot());



                        /* 취소 버튼 */
                        //dialog1Binding.btnCancel.setOnClickListener(v -> dialog.onBackPressed());

                        dialog.show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else if (i == 5) {
                /* 문의하기 */
                inquiry();
            } else if(i == 6) {
                /* 로그인, 로그아웃 */
                String loginText = adapter.getItem(i);
                if(loginText.equals("로그아웃")) {
                    logOut();
                } else {
                    logIn();
                }
            }
        });
        return mBinding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        /* Info 프래그먼트의 유저 정보 세팅 */
        userAccount = UserAccount.getInstance();

        /* 앱 내부 저장소에 있는 프로필 이미지를 불러옴 */
        File file = new File(requireContext().getFilesDir(), "UserProfile");
        if(file.isFile()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

            Glide.with(requireContext()).load(bitmap).into(mBinding.civProfile);
        } else Glide.with(requireContext()).load(R.drawable.ic_baseline_person_24).into(mBinding.civProfile);

        /* 유저 닉네임 등등 설정 */
        mBinding.tvNickname.setText(userAccount.getNickname());
        mBinding.tvId.setText(!userAccount.getEmail().equals("") ? userAccount.getEmail() : "비로그인");
        mBinding.tvMessage.setText(userAccount.getMessage());
        mBinding.tvWordsCnt.setText(VocaManager.getInstance().getWordsCnt() + " / " + userAccount.getMaxCntWords());
    }

    private void logIn() {
        /* 로그인 */
        /* 자동 로그인 해제 */
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userShared", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("autoLogin", false);
        editor.apply();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void logOut() {
        /* 로그아웃 */



        /* 사용자 정보 제거 */
        File file = new File(requireContext().getFilesDir(), "UserProfile");
        if(file.isFile()) {
            requireContext().deleteFile("UserProfile");
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userShared", 0);
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
        requireActivity().finish();
    }

    private void inquiry() {
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] address = {"onheaven003@gmail.com"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT, "[Star-Voca 문의사항] 제목없음");
        email.putExtra(Intent.EXTRA_TEXT, "StarVoca 문의하기 입니다.\n상세히 작성해주시면 신속한 처리가 가능합니다.\n\nuid: "+userAccount.getUid()+"\n닉네임: "+ userAccount.getNickname() +"\n날짜: "+time+"\n내용: ");
        startActivity(email);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}