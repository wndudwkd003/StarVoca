package com.zynar.starvoca.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zynar.starvoca.AppDatabase;
import com.zynar.starvoca.MainActivity;
import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.ActivityLoginBinding;
import com.zynar.starvoca.databinding.ActivityLoginEmailBinding;
import com.zynar.starvoca.words.WordsItem;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginEmailActivity extends AppCompatActivity {
    private ActivityLoginEmailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        /* 파이어베이스 인증 */
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        /* 툴바 */
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        /* 로그인 버튼 */
        mBinding.btnLogin.setOnClickListener(v -> {
            mBinding.btnLogin.bringToFront();

            /* 로딩바 */
            mBinding.includeProgress.clLayout.setVisibility(View.VISIBLE);

            String email = String.valueOf(mBinding.etEmail.getText());
            String pw = String.valueOf(mBinding.etPw.getText());

            /* 로그인 예외 */
            if(email.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, R.string.et_isEmpty, Toast.LENGTH_SHORT).show();
                mBinding.includeProgress.clLayout.setVisibility(View.GONE);

            } else if(!checkEmail(email)) {
                Toast.makeText(this, R.string.email_not_protocol, Toast.LENGTH_SHORT).show();
                mBinding.includeProgress.clLayout.setVisibility(View.GONE);

            } else {
                firebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        /* 파이어베이스 로그인 계정 일치하고 이메일 인증 까지 완료 */
                        if(firebaseAuth.getCurrentUser().isEmailVerified()) {

                            // 로그인 성공
                            Toast.makeText(this, R.string.complete_login, Toast.LENGTH_SHORT).show();

                            /* 로그인 성공하면 데이터베이스 접근해서 정보를 불러옴 */
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                            dbRef.child("UserAccount").child("Email").child(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {

                                    /* 자동 로그인을 위한 이메일과 패스워드 저장 */
                                    SharedPreferences sp = getSharedPreferences("userShared", 0);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("email", email);
                                    editor.putString("pw", pw);
                                    editor.apply();

                                    /* 불러온 데이터를 UserAccount 대입 */
                                    UserAccount userAccount = task1.getResult().getValue(UserAccount.class);

                                    UserAccount.getInstance().setUid(userAccount.getUid());
                                    UserAccount.getInstance().setEmail(userAccount.getEmail());
                                    UserAccount.getInstance().setNickname(userAccount.getNickname());
                                    UserAccount.getInstance().setGender(userAccount.getGender());
                                    UserAccount.getInstance().setMessage(userAccount.getMessage());
                                    UserAccount.getInstance().setMaxCntWords(userAccount.getMaxCntWords());

                                    /* 로그인 타입 Intent 전달 및 메인 액티비티 이동 */
                                    Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
                                    intent.putExtra("loginType", "email");
                                    startActivity(intent);

                                    /* 로그인 선택 액티비티 제거 */
                                    LoginActivity loginActivity = (LoginActivity) LoginActivity.loginActivity;
                                    loginActivity.finish();

                                    /* 로그인 액티비티 제거 */
                                    finish();
                                }
                            });

                        } else {
                            Toast.makeText(this, R.string.fail_login_v_email, Toast.LENGTH_SHORT).show();
                            mBinding.includeProgress.clLayout.setVisibility(View.GONE);
                        }

                    } else {
                        Toast.makeText(this, R.string.fail_login, Toast.LENGTH_SHORT).show();
                        mBinding.includeProgress.clLayout.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    private boolean checkEmail(String email) {
        Pattern pattern = Pattern.compile("^[a-z0-9_+.-]+@([a-z0-9-]+\\.)+[a-z0-9]{2,4}$");
        Matcher matcher = pattern.matcher(email);

        if(email.contains(" ")) return false;
        else return matcher.find();
    }
}