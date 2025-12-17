package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SimplePasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return Base64.getEncoder()
                .encodeToString(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
