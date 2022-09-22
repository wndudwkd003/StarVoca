package com.zynar.starvoca.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;
    public static Activity loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);

        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        loginActivity = (LoginActivity) LoginActivity.this;

        mBinding.btnLoginEmail.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LoginEmailActivity.class);
            startActivity(intent);
        });

        mBinding.tvRegisterEmail.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }
}