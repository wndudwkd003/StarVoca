package com.zynar.starvoca.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zynar.starvoca.MainActivity;
import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;

    @SuppressLint("StaticFieldLeak")
    public static Activity loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        loginActivity = LoginActivity.this;

        SharedPreferences sp = getSharedPreferences("userShared", 0);
        String loginType = sp.getString("loginType", "noLogin");
        boolean autoLogin = sp.getBoolean("autoLogin", false);

        /* 한 번 로그인 하면 자동으로 자동 로그인 되도록 설정 */
        if(autoLogin) {
            if(loginType.equals("noLogin")) {
                noLogin();
            } else if(loginType.equals("email")) {
                autoEmailLogin();
            }
        }

        /* 이메일 로그인 */
        mBinding.btnLoginEmail.setOnClickListener(v -> {
            emailLogin();
        });

        /* 이메일 가입 */
        mBinding.tvRegisterEmail.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        /* 로그인 없이 앱 시작 */
        mBinding.tvNoLogin.setOnClickListener(v ->{
            noLogin();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void autoEmailLogin() {
        /* 자동 이메일 로그인 */

        /* 로딩바 */
        mBinding.includeProgress.clLayout.setVisibility(View.VISIBLE);

        SharedPreferences sp = getSharedPreferences("userShared", 0);
        String email = sp.getString("email", "");
        String pw = sp.getString("pw", "");

        if(email.isEmpty() || pw.isEmpty()) {
            mBinding.includeProgress.clLayout.setVisibility(View.GONE);
            return;
        }

        /* 파이어베이스 이메일 로그인 */
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                    // 로그인 성공
                    Toast.makeText(this, R.string.complete_login, Toast.LENGTH_SHORT).show();

                    /* 로그인 성공하면 데이터베이스 접근해서 정보를 불러옴 */
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                    dbRef.child("UserAccount").child("Email").child(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {

                            /* 불러온 데이터를 UserAccount 대입 */
                            UserAccount userAccount = task1.getResult().getValue(UserAccount.class);

                            UserAccount.getInstance().setUid(userAccount.getUid());
                            UserAccount.getInstance().setEmail(userAccount.getEmail());
                            UserAccount.getInstance().setNickname(userAccount.getNickname());
                            UserAccount.getInstance().setGender(userAccount.getGender());
                            UserAccount.getInstance().setMessage(userAccount.getMessage());
                            UserAccount.getInstance().setMaxCntWords(userAccount.getMaxCntWords());

                            mBinding.includeProgress.clLayout.setVisibility(View.GONE);

                            /* 로그인 타입 Intent 전달 및 메인 액티비티 이동 */
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("loginType", "email");
                            startActivity(intent);

                            // 로그인 액티비티 제거
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

    private void emailLogin() {
        Intent intent = new Intent(LoginActivity.this, LoginEmailActivity.class);
        startActivity(intent);
    }

    private void noLogin() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("loginType", "noLogin");
        startActivity(intent);
        finish();
    }
}