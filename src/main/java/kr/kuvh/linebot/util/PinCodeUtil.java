package kr.kuvh.linebot.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PinCodeUtil {

    private PasswordEncoder passwordEncoder;

    public PinCodeUtil(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encode(String pinCode, String email) {
        StringBuilder sb = new StringBuilder(pinCode);
        sb.append(":");
        sb.append(email);
        String raw = sb.toString();
        return passwordEncoder.encode(raw);
    }
}
