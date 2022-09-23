package com.zynar.starvoca.login;

public class UserAccount {
    private String uid;
    private String email;
    private String nickname;
    private int gender;
    private String message;
    private int cntWords;
    private int maxCntWords;

    public UserAccount() {}

    public UserAccount(String uid, String email, String nickname, int gender, String message, int maxCntWords) {
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
        this.message = message;
        this.maxCntWords = maxCntWords;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMaxCntWords() {
        return maxCntWords;
    }

    public void setMaxCntWords(int maxCntWords) {
        this.maxCntWords = maxCntWords;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender=" + gender +
                '}';
    }
}
