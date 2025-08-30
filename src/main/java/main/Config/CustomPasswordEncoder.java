package main.Config;

import main.Utility.PasswordUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = PasswordUtil.generateSalt();
        return PasswordUtil.hashPassword(rawPassword.toString(), salt);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return PasswordUtil.validatePassword(rawPassword.toString(), encodedPassword);
    }
}
