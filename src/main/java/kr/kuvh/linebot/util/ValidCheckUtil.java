package kr.kuvh.linebot.util;

import java.util.regex.Pattern;

public class ValidCheckUtil {

    public static boolean isValidEmail(String email) {
        Pattern emailRegex = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b");
        return emailRegex.matcher(email).matches();
    }

    public static boolean isValidPinCode(String pinCode) {
        Pattern pinCodeRegex = Pattern.compile("^[0-9]{6}$");
        return pinCodeRegex.matcher(pinCode).matches();
    }
}
