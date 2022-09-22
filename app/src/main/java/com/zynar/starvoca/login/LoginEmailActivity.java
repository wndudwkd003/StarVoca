package com.zynar.starvoca.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.zynar.starvoca.MainActivity;
import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.ActivityLoginBinding;
import com.zynar.starvoca.databinding.ActivityLoginEmailBinding;

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

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mBinding.btnLogin.setOnClickListener(v -> {
            String email = String.valueOf(mBinding.etEmail.getText());
            String pw = String.valueOf(mBinding.etPw.getText());

            if(email.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, R.string.et_isEmpty, Toast.LENGTH_SHORT).show();
            } else if(!checkEmail(email)) {
                Toast.makeText(this, R.string.email_not_protocol, Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                            // 로그인 성공
                            Toast.makeText(this, R.string.complete_login, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
                            startActivity(intent);

                            // 로그인 선택 액티비티 제거
                            LoginActivity loginActivity = (LoginActivity) LoginActivity.loginActivity;
                            loginActivity.finish();

                            // 로그인 액티비티 제거
                            finish();

                        } else {
                            Toast.makeText(this, R.string.fail_login_v_email, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, R.string.complete_login, Toast.LENGTH_SHORT).show();
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