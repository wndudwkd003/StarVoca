package com.zynar.starvoca.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.ActivityRegisterBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // 회원가입 버튼
        mBinding.btnRegister.setOnClickListener(v->{
            String email = String.valueOf(mBinding.etEmail.getText());
            String pw = String.valueOf(mBinding.etPw.getText());
            String pw_ = String.valueOf(mBinding.etPw2.getText());
            String nickname = String.valueOf(mBinding.etNickname.getText());

            View radioButton = mBinding.radioEditMyinfo.findViewById(mBinding.radioEditMyinfo.getCheckedRadioButtonId());
            int gender = mBinding.radioEditMyinfo.indexOfChild(radioButton);

            if(email.isEmpty() || pw.isEmpty() || pw_.isEmpty() || nickname.isEmpty()) {
                Toast.makeText(this, R.string.et_isEmpty, Toast.LENGTH_SHORT).show();
            } else if(!checkEmail(email)) {
                Toast.makeText(this, R.string.email_not_protocol, Toast.LENGTH_SHORT).show();
            } else if(!pw.equals(pw_)){
                Toast.makeText(this, R.string.pw_not_equals, Toast.LENGTH_SHORT).show();
            } else if(!checkPW(pw)) {
                Toast.makeText(this, R.string.pw_not_protocol, Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(RegisterActivity.this, task -> {
                    if(task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        UserAccount userAccount = new UserAccount(firebaseUser.getUid(), email, nickname, gender, "", 100);

                        // 이메일 인증 보냄
                        firebaseUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {

                                // 가입신청 완료 & 데이터베이스 삽입
                                dbRef.child("UserAccount").child("Email").child(userAccount.getUid()).setValue(userAccount);
                                Toast.makeText(RegisterActivity.this, R.string.complete_register, Toast.LENGTH_SHORT).show();
                                onBackPressed();

                            }
                            else Toast.makeText(RegisterActivity.this, R.string.disconnect_sever, Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        Toast.makeText(RegisterActivity.this, R.string.fail_register, Toast.LENGTH_SHORT).show();
                        Log.d("test", task.getException().toString());
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

    private boolean checkPW(String pw) {
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])((?=.*\\d)(?=.*\\W)).{6,18}$");
        Matcher matcher = pattern.matcher(pw);

        if(pw.contains(" ")) return false;
        else return matcher.find();
    }
}