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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zynar.starvoca.PathUtil;
import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.ActivityEditUserInfoBinding;
import com.zynar.starvoca.login.UserAccount;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditUserInfoActivity extends AppCompatActivity {

    private Uri uri = null;
    private ActivityEditUserInfoBinding mBinding;
    private boolean resetFlag = false;

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

        mBinding.tvResetProfile.setOnClickListener(v -> {
            resetFlag = true;
            Glide.with(EditUserInfoActivity.this).load(R.drawable.ic_baseline_person_24).into(mBinding.civProfile);
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
                        uri = result.getData().getData();
                        Glide.with(EditUserInfoActivity.this).load(uri).into(mBinding.civProfile);
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

            if(resetFlag) {
                /* 이미지 정보 제거 */
                deleteFile("UserProfile");
                StorageReference sgRef = FirebaseStorage.getInstance().getReference();
                sgRef.child("UserAccount")
                        .child(getIntent().getStringExtra("loginType"))
                        .child(UserAccount.getInstance().getUid())
                        .child("ProfileImage").delete();

            } else {
                /* 선택한 사진을 비트맵 -> 파일 변환 후 저장 */
                try {
                    if(uri != null) {
                        FileOutputStream fos = openFileOutput("UserProfile", Context.MODE_PRIVATE);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                        /* 실제 경로를 받아옴 */
                        String filePath = PathUtil.getPath(this, uri);
                        ExifInterface oldExif = new ExifInterface(filePath);
                        String exifOrientation  = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);

                        /* 비트맵을 압축하는 과정에서 Exif 정보가 날아감 */
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        /* 원본 Exif 제대로 복사함 */
                        ExifInterface newExif = new ExifInterface(getFilesDir()+"/UserProfile");
                        newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
                        newExif.saveAttributes();

                        /* 파이어베이스 이미지 업로드 */
                        StorageReference sgRef = FirebaseStorage.getInstance().getReference();
                        sgRef.child("UserAccount")
                                .child(getIntent().getStringExtra("loginType"))
                                .child(UserAccount.getInstance().getUid())
                                .child("ProfileImage")
                                .putFile(uri).addOnCompleteListener(task -> {
                                    if(!task.isSuccessful()) {
                                        Toast.makeText(this, "서버가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                    Log.d("test", e.getMessage());
                }
            }

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
                        else {
                            Toast.makeText(this, "서버가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
            });
        }
    }


    private void editSetInit() {
        /* 유저의 정보를 Edit Text 저장 */

        File file = new File(getFilesDir(), "UserProfile");
        if(file.isFile()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            Glide.with(EditUserInfoActivity.this).load(bitmap).into(mBinding.civProfile);
        } else {
            Glide.with(EditUserInfoActivity.this).load(R.drawable.ic_baseline_person_24).into(mBinding.civProfile);
        }

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