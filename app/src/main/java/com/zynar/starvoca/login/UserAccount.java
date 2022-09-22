package com.zynar.starvoca.login;

public class UserAccount {
    private String uid;
    private String email;
    private String nickname;
    private int gender;

    public UserAccount() {}

    public UserAccount(String uid, String email, String nickname, int gender) {
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
    }

    private static final UserAccount singleton = new UserAccount();

    public static UserAccount getInstance() {
        return singleton;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
