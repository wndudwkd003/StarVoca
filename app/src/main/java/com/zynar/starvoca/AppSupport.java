package com.zynar.starvoca;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppSupport {
    public boolean checkPW(String pw) {
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])((?=.*\\d)(?=.*\\W)).{6,18}$");
        Matcher matcher = pattern.matcher(pw);

        if(pw.contains(" ")) return false;
        else return matcher.find();
    }
}
