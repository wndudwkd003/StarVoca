package com.zynar.starvoca.info;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zynar.starvoca.AppSupport;
import com.zynar.starvoca.PathUtil;
import com.zynar.starvoca.R;
import com.zynar.starvoca.databinding.ActivityEditUserInfoBinding;
import com.zynar.starvoca.databinding.CustomChangePwDialogBinding;
import com.zynar.starvoca.databinding.CustomQuestionDialogBinding;
import com.zynar.starvoca.databinding.CustomUserDeleteDialogBinding;
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

        /* 프로필 사진 변경 */
        mBinding.tvChangeProfile.setOnClickListener(v -> {
            changeProfile();
        });

        /* 입력된 정보를 업데이트 */
        mBinding.tvSave.setOnClickListener(v -> {
            updateUserInfo();
        });

        /* 프로필 사진 초기화 */
        mBinding.tvResetProfile.setOnClickListener(v -> {
            resetFlag = true;
            Glide.with(EditUserInfoActivity.this).load(R.drawable.ic_baseline_person_24).into(mBinding.civProfile);
        });

        /* 회원 탈퇴 */
        mBinding.tvUserDelete.setOnClickListener(v -> {
            deleteUser();
        });

        /* 비밀번호 변경 */
        mBinding.tvChangePw.setOnClickListener(v -> {
            changePW();
        });
    }

    private void changePW() {
        /* 비밀번호 변경 */
        Dialog dialog = new Dialog(this);

        /* 다이얼로그 데이터 바인딩 */
        CustomChangePwDialogBinding dialogBinding = CustomChangePwDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        /* 확인 버튼 */
        dialogBinding.btnSave.setOnClickListener(view -> {

            /* 패스워드 */
            String nowPW = dialogBinding.etNowPw.getText().toString();
            String changePW = dialogBinding.etChangePw.getText().toString();
            String changePW2 = dialogBinding.etChangePw2.getText().toString();

            if(nowPW.isEmpty() || changePW.isEmpty() || changePW2.isEmpty()) {
                Toast.makeText(EditUserInfoActivity.this, "패스워드를 정확하게 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!changePW.equals(changePW2)) {
                Toast.makeText(EditUserInfoActivity.this, "변경할 패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!new AppSupport().checkPW(changePW)) {
                Toast.makeText(EditUserInfoActivity.this, "패스워드 규칙에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 패스워드 재인증
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            UserAccount userAccount = UserAccount.getInstance();
            AuthCredential credential = EmailAuthProvider.getCredential(userAccount.getEmail(), nowPW);

            /* 재인증 로그인 */
            firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {

                    Dialog dialog1 = new Dialog(this);
                    /* 다이얼로그 데이터 바인딩 */
                    CustomQuestionDialogBinding dialog1Binding = CustomQuestionDialogBinding.inflate(getLayoutInflater());
                    dialog1.setContentView(dialog1Binding.getRoot());

                    dialog1Binding.tvTitle.setText("정말로 변경하시겠습니까?");
                    dialog1Binding.tvContent.setText("확인을 누르시면 다시 로그인합니다.");

                    /* 확인 버튼 */
                    dialog1Binding.btnSave.setOnClickListener(v -> {

                        firebaseUser.updatePassword(changePW).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {
                                /* pw 변경 완료 */
                                Toast.makeText(EditUserInfoActivity.this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();

                                /* pre pw 변경 */
                                SharedPreferences sharedPreferences = getSharedPreferences("userShared", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("pw", changePW);
                                editor.apply();

                                dialog1.cancel();
                                dialog.cancel();
                            }
                        });

                    });

                    /* 취소 버튼 */
                    dialog1Binding.btnCancel.setOnClickListener(v -> {
                        dialog1.onBackPressed();
                    });

                    dialog1.show();
                }
                else Toast.makeText(EditUserInfoActivity.this, "패스워드를 정확하게 입력하세요.", Toast.LENGTH_SHORT).show();
            });

        });

        /* 취소 버튼 */
        dialogBinding.btnCancel.setOnClickListener(v ->{
            dialog.onBackPressed();
        });

        dialog.show();
    }

    private void deleteUser() {
        /* 회원 탈퇴 */
        Dialog dialog = new Dialog(this);
        
        /* 다이얼로그 데이터 바인딩 */
        CustomUserDeleteDialogBinding dialogBinding = CustomUserDeleteDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        /* 확인 버튼 */
        dialogBinding.btnSave.setOnClickListener(view -> {

            /* 패스워드 */
            String pw = dialogBinding.etPw.getText().toString();
            if(pw.isEmpty()) {
                Toast.makeText(EditUserInfoActivity.this, "패스워드를 정확하게 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 패스워드 재인증
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            UserAccount userAccount = UserAccount.getInstance();
            AuthCredential credential = EmailAuthProvider.getCredential(userAccount.getEmail(), pw);

            /* 재인증 로그인 */
            firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {

                    Dialog dialog1 = new Dialog(this);
                    /* 다이얼로그 데이터 바인딩 */
                    CustomQuestionDialogBinding dialog1Binding = CustomQuestionDialogBinding.inflate(getLayoutInflater());
                    dialog1.setContentView(dialog1Binding.getRoot());

                    dialog1Binding.tvTitle.setText("정말로 탈퇴하시겠습니까?");
                    dialog1Binding.tvContent.setText("회원 탈퇴하셔도 비로그인으로 앱을 사용하실 수 있습니다.");

                    /* 확인 버튼 */
                    dialog1Binding.btnSave.setOnClickListener(v -> {
                        /* 데이터 베이스 삭제 */
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("UserAccount").child(getIntent().getStringExtra("loginType")).child(userAccount.getUid()).removeValue();

                        /* 유저 인증 데이터 삭제 */
                        firebaseUser.delete();

                        /* 유저 프로필 사진 삭제 */
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        storageReference.child("UserAccount").child(getIntent().getStringExtra("loginType")).child(userAccount.getUid()).delete();

                        /* 탈퇴 완료 */
                        Toast.makeText(EditUserInfoActivity.this, "탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                        /* 사용자 정보 제거 */
                        File file = new File(getFilesDir(), "UserProfile");
                        if(file.isFile()) {
                            deleteFile("UserProfile");
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("userShared", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("autoLogin", false);
                        editor.putString("loginType", null);
                        editor.putString("uid", null);
                        editor.putString("email", null);
                        editor.putString("pw", null);
                        editor.putString("nickname", null);
                        editor.apply();

                        /* 앱 재시작 */
                        PackageManager packageManager = getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                        ComponentName componentName = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                        startActivity(mainIntent);
                        System.exit(0);
                    });

                    /* 취소 버튼 */
                    dialog1Binding.btnCancel.setOnClickListener(v -> {
                        dialog1.onBackPressed();
                    });

                    dialog1.show();
                }
                else Toast.makeText(EditUserInfoActivity.this, "패스워드를 정확하게 입력하세요.", Toast.LENGTH_SHORT).show();
            });

        });

        dialogBinding.btnCancel.setOnClickListener(v -> {
            dialog.onBackPressed();
        });

        dialog.show();
    }

    private void changeProfile() {
        /* 프로필 사진 선택 */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    /* 갤러리 앱 사진 선택 */
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

                            /* 파이어베이스 업데이트 완료 했으니 setPreferences 업데이트도 함 */
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
        Log.d("test", String.valueOf(userAccount.getGender()));

        if(userAccount.getGender() == 0) {
            mBinding.radioBtnMan.setChecked(true);
        } else if(userAccount.getGender() == 1){
            mBinding.radioBtnGirl.setChecked(true);
        } else {
            mBinding.radioBtnPrivate.setChecked(true);
        }
    }

    private boolean checkNickname(String nickName) {
        Pattern pattern = Pattern.compile("^[가-힣ㄱ-ㅎa-zA-Z0-9._-]{2,18}$");
        Matcher matcher = pattern.matcher(nickName);
        return !matcher.find();
    }
}