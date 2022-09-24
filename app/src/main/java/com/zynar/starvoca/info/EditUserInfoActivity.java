package com.zynar.starvoca.info;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zynar.starvoca.databinding.ActivityEditUserInfoBinding;
import com.zynar.starvoca.login.UserAccount;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditUserInfoActivity extends AppCompatActivity {

    private ActivityEditUserInfoBinding mBinding;

    /* 캐시 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityEditUserInfoBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        /* 유저의 정보를 Edit Text 저장 */
        editSetInit();

        mBinding.tvChangeProfile.setOnClickListener(v -> {
            changeProfile();
        });

        /* 입력된 정보를 업데이트 */
        mBinding.tvSave.setOnClickListener(v -> {
            updateUserInfo();
        });

    }

    private void changeProfile() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try (FileOutputStream fos = openFileOutput("UserProfile", Context.MODE_PRIVATE)) {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        File file = new File(getFilesDir(), "UserProfile");
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                        Glide.with(EditUserInfoActivity.this).load(bitmap).into(mBinding.civProfile);
                    }
                }
            });


    private void updateUserInfo() {
        /* 입력된 정보를 업데이트 */
        String nickname = String.valueOf(mBinding.etNickname.getText());
        String message = String.valueOf(mBinding.etMessage.getText());

        View radioButton = mBinding.radioEditMyinfo.findViewById(mBinding.radioEditMyinfo.getCheckedRadioButtonId());
        int gender = mBinding.radioEditMyinfo.indexOfChild(radioButton);

        if(nickname.isEmpty() || checkNickname(nickname)) {
            /* 닉네임이 빈칸이거나 잘못됨 */
            Toast.makeText(this, "닉네임이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            /* UserAccount 저장 */
            UserAccount.getInstance().setNickname(nickname);
            UserAccount.getInstance().setMessage(message);
            UserAccount.getInstance().setGender(gender);

            /* 파이어베이스 업데이트 */
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("UserAccount")
                    .child(getIntent().getStringExtra("loginType"))
                    .child(UserAccount.getInstance().getUid())
                    .setValue(UserAccount.getInstance()).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {

                            /* 파이어베이스 업데이트 완료 했으니 캐시 업데이트도 함 */
                            UserAccount.getInstance().setPreferences(this);

                            Toast.makeText(this, "정보가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
            });
        }
    }

    private void editSetInit() {
        /* 유저의 정보를 Edit Text 저장 */
        UserAccount userAccount = UserAccount.getInstance();
        mBinding.etNickname.setText(userAccount.getNickname());
        mBinding.etMessage.setText(userAccount.getMessage());
        mBinding.etEmail.setText(userAccount.getEmail());

        switch (userAccount.getGender()) {
            case 0:
                mBinding.radioBtnMan.setChecked(true);
            case 1:
                mBinding.radioBtnGirl.setChecked(true);
            default:
                mBinding.radioBtnPrivate.setChecked(true);
        }
    }

    private boolean checkNickname(String nickName) {
        Pattern pattern = Pattern.compile("^[가-힣ㄱ-ㅎa-zA-Z0-9._-]{2,18}$");
        Matcher matcher = pattern.matcher(nickName);
        return !matcher.find();
    }
}