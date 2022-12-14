package com.zynar.starvoca.info;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
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

        /* ????????? ????????? Edit Text ?????? */
        editSetInit();

        /* ????????? ?????? ?????? */
        mBinding.tvChangeProfile.setOnClickListener(v -> changeProfile());

        /* ????????? ????????? ???????????? */
        mBinding.tvSave.setOnClickListener(v -> updateUserInfo());

        /* ????????? ?????? ????????? */
        mBinding.tvResetProfile.setOnClickListener(v -> {
            resetFlag = true;
            Glide.with(EditUserInfoActivity.this).load(R.drawable.ic_baseline_person_24).into(mBinding.civProfile);
        });

        /* ?????? ?????? */
        mBinding.tvUserDelete.setOnClickListener(v -> deleteUser());

        /* ???????????? ?????? */
        mBinding.tvChangePw.setOnClickListener(v -> changePW());
    }

    private void changePW() {
        /* ???????????? ?????? */
        Dialog dialog = new Dialog(this);

        /* ??????????????? ????????? ????????? */
        CustomChangePwDialogBinding dialogBinding = CustomChangePwDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        /* ?????? ?????? */
        dialogBinding.btnSave.setOnClickListener(view -> {

            /* ???????????? */
            String nowPW = dialogBinding.etNowPw.getText().toString();
            String changePW = dialogBinding.etChangePw.getText().toString();
            String changePW2 = dialogBinding.etChangePw2.getText().toString();

            if(nowPW.isEmpty() || changePW.isEmpty() || changePW2.isEmpty()) {
                Toast.makeText(EditUserInfoActivity.this, "??????????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!changePW.equals(changePW2)) {
                Toast.makeText(EditUserInfoActivity.this, "????????? ??????????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!new AppSupport().checkPW(changePW)) {
                Toast.makeText(EditUserInfoActivity.this, "???????????? ????????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
                return;
            }

            // ???????????? ?????????
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            UserAccount userAccount = UserAccount.getInstance();
            AuthCredential credential = EmailAuthProvider.getCredential(userAccount.getEmail(), nowPW);

            /* ????????? ????????? */
            if (firebaseUser != null) {
                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {

                        Dialog dialog1 = new Dialog(this);
                        /* ??????????????? ????????? ????????? */
                        CustomQuestionDialogBinding dialog1Binding = CustomQuestionDialogBinding.inflate(getLayoutInflater());
                        dialog1.setContentView(dialog1Binding.getRoot());

                        dialog1Binding.tvTitle.setText("????????? ?????????????????????????");
                        dialog1Binding.tvContent.setText("????????? ???????????? ?????? ??????????????????.");

                        /* ?????? ?????? */
                        dialog1Binding.btnSave.setOnClickListener(v -> firebaseUser.updatePassword(changePW).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {
                                /* pw ?????? ?????? */
                                Toast.makeText(EditUserInfoActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                                /* pre pw ?????? */
                                SharedPreferences sharedPreferences = getSharedPreferences("userShared", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("pw", changePW);
                                editor.apply();

                                dialog1.cancel();
                                dialog.cancel();
                            }
                        }));

                        /* ?????? ?????? */
                        dialog1Binding.btnCancel.setOnClickListener(v -> dialog1.onBackPressed());

                        dialog1.show();
                    }
                    else Toast.makeText(EditUserInfoActivity.this, "??????????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                });
            }

        });

        /* ?????? ?????? */
        dialogBinding.btnCancel.setOnClickListener(v -> dialog.onBackPressed());

        dialog.show();
    }

    private void deleteUser() {
        /* ?????? ?????? */
        Dialog dialog = new Dialog(this);
        
        /* ??????????????? ????????? ????????? */
        CustomUserDeleteDialogBinding dialogBinding = CustomUserDeleteDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        /* ?????? ?????? */
        dialogBinding.btnSave.setOnClickListener(view -> {

            /* ???????????? */
            String pw = dialogBinding.etPw.getText().toString();
            if(pw.isEmpty()) {
                Toast.makeText(EditUserInfoActivity.this, "??????????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                return;
            }

            // ???????????? ?????????
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            UserAccount userAccount = UserAccount.getInstance();
            AuthCredential credential = EmailAuthProvider.getCredential(userAccount.getEmail(), pw);

            /* ????????? ????????? */
            if (firebaseUser != null) {
                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {

                        Dialog dialog1 = new Dialog(this);
                        /* ??????????????? ????????? ????????? */
                        CustomQuestionDialogBinding dialog1Binding = CustomQuestionDialogBinding.inflate(getLayoutInflater());
                        dialog1.setContentView(dialog1Binding.getRoot());

                        dialog1Binding.tvTitle.setText("????????? ?????????????????????????");
                        dialog1Binding.tvContent.setText("?????? ??????????????? ?????????????????? ?????? ???????????? ??? ????????????.");

                        /* ?????? ?????? */
                        dialog1Binding.btnSave.setOnClickListener(v -> {
                            /* ????????? ????????? ?????? */
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("UserAccount").child(getIntent().getStringExtra("loginType")).child(userAccount.getUid()).removeValue();

                            /* ?????? ?????? ????????? ?????? */
                            firebaseUser.delete();

                            /* ?????? ????????? ?????? ?????? */
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            storageReference.child("UserAccount").child(getIntent().getStringExtra("loginType")).child(userAccount.getUid()).delete();

                            /* ?????? ?????? */
                            Toast.makeText(EditUserInfoActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                            /* ????????? ?????? ?????? */
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

                            /* ??? ????????? */
                            PackageManager packageManager = getPackageManager();
                            Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                            ComponentName componentName = intent.getComponent();
                            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                            startActivity(mainIntent);
                            System.exit(0);
                        });

                        /* ?????? ?????? */
                        dialog1Binding.btnCancel.setOnClickListener(v -> dialog1.onBackPressed());

                        dialog1.show();
                    }
                    else Toast.makeText(EditUserInfoActivity.this, "??????????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                });
            }

        });

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.onBackPressed());

        dialog.show();
    }

    private void changeProfile() {
        /* ????????? ?????? ?????? */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    /* ????????? ??? ?????? ?????? */
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
        /* ????????? ????????? ???????????? */
        String nickname = String.valueOf(mBinding.etNickname.getText());
        String message = String.valueOf(mBinding.etMessage.getText());

        View radioButton = mBinding.radioEditMyinfo.findViewById(mBinding.radioEditMyinfo.getCheckedRadioButtonId());
        int gender = mBinding.radioEditMyinfo.indexOfChild(radioButton);

        if(nickname.isEmpty() || checkNickname(nickname)) {
            /* ???????????? ??????????????? ????????? */
            Toast.makeText(this, "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
        } else {
            /* UserAccount ?????? */
            UserAccount.getInstance().setNickname(nickname);
            UserAccount.getInstance().setMessage(message);
            UserAccount.getInstance().setGender(gender);

            if(resetFlag) {
                /* ????????? ?????? ?????? */
                deleteFile("UserProfile");
                StorageReference sgRef = FirebaseStorage.getInstance().getReference();
                sgRef.child("UserAccount")
                        .child(getIntent().getStringExtra("loginType"))
                        .child(UserAccount.getInstance().getUid())
                        .child("ProfileImage").delete();

            } else {
                /* ????????? ????????? ????????? -> ?????? ?????? ??? ?????? */
                try {
                    if(uri != null) {
                        FileOutputStream fos = openFileOutput("UserProfile", Context.MODE_PRIVATE);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                        /* ?????? ????????? ????????? */
                        String filePath = PathUtil.getPath(this, uri);
                        ExifInterface oldExif = null;
                        if (filePath != null) {
                            oldExif = new ExifInterface(filePath);
                        }
                        String exifOrientation  = null;
                        if (oldExif != null) {
                            exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
                        }

                        /* ???????????? ???????????? ???????????? Exif ????????? ????????? */
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        /* ?????? Exif ????????? ????????? */
                        ExifInterface newExif = new ExifInterface(getFilesDir()+"/UserProfile");
                        newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
                        newExif.saveAttributes();

                        /* ?????????????????? ????????? ????????? */
                        StorageReference sgRef = FirebaseStorage.getInstance().getReference();
                        sgRef.child("UserAccount")
                                .child(getIntent().getStringExtra("loginType"))
                                .child(UserAccount.getInstance().getUid())
                                .child("ProfileImage")
                                .putFile(uri).addOnCompleteListener(task -> {
                                    if(!task.isSuccessful()) {
                                        Toast.makeText(this, "????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                    Log.d("test", e.getMessage());
                }
            }

            /* ?????????????????? ???????????? */
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("UserAccount")
                    .child(getIntent().getStringExtra("loginType"))
                    .child(UserAccount.getInstance().getUid())
                    .setValue(UserAccount.getInstance()).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {

                            /* ?????????????????? ???????????? ?????? ????????? setPreferences ??????????????? ??? */
                            UserAccount.getInstance().setPreferences(this);

                            Toast.makeText(this, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        else {
                            Toast.makeText(this, "????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                        }
            });
        }
    }


    private void editSetInit() {
        /* ????????? ????????? Edit Text ?????? */

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
        Pattern pattern = Pattern.compile("^[???-??????-???a-zA-Z0-9._-]{2,18}$");
        Matcher matcher = pattern.matcher(nickName);
        return !matcher.find();
    }
}