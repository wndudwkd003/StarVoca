package com.zynar.starvoca.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.zynar.starvoca.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn_login_email = findViewById(R.id.btn_login_email);
        btn_login_email.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, LoginEmailActivity.class);
            startActivity(intent);
        });

        TextView tv_register_email = findViewById(R.id.tv_register_email);
        tv_register_email.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }
}