package ma.dentalTech.service.modules.users.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtils {

    private PasswordUtils() {}

    public static String hash(String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) sb.append(String.format("%02x", b));
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erreur hash mot de passe", e);
        }
    }
}
