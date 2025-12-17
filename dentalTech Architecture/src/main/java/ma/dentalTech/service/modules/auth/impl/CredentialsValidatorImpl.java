package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.CredentialsValidator;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;

public class CredentialsValidatorImpl implements CredentialsValidator {

    @Override
    public void validate(AuthRequest request) {
        if (request == null)
            throw new IllegalArgumentException("AuthRequest null");

        if (request.login() == null || request.login().isBlank())
            throw new IllegalArgumentException("Login vide");

        if (request.password() == null || request.password().isBlank())
            throw new IllegalArgumentException("Mot de passe vide");
    }

    @Override
    public void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 8)
            throw new IllegalArgumentException("Mot de passe trop court");
    }
}
