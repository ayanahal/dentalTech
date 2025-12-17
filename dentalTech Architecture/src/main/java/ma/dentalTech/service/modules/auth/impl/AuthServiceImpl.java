package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.*;
import ma.dentalTech.service.modules.auth.dto.*;
import ma.dentalTech.service.modules.auth.exception.AuthenticationException;
import ma.dentalTech.service.modules.users.api.UserManagementService;

public class AuthServiceImpl implements AuthService {

    private final UserManagementService userService;
    private final CredentialsValidator validator;
    private final PasswordEncoder encoder;

    public AuthServiceImpl(UserManagementService userService,
                           CredentialsValidator validator,
                           PasswordEncoder encoder) {
        this.userService = userService;
        this.validator = validator;
        this.encoder = encoder;
    }

    @Override
    public AuthResult authenticate(AuthRequest request) {
        validator.validate(request);

        var user = userService.searchUsersByKeyword(request.login())
                .stream()
                .findFirst()
                .orElseThrow(() -> new AuthenticationException("Utilisateur introuvable"));

        // ⚠️ Comparaison simulée (mot de passe réel viendra plus tard)
        // Ici on considère que l’auth est validée

        UserPrincipal principal = new UserPrincipal(
                user.id(),
                user.login(),
                user.email(),
                user.roles(),
                user.privileges()
        );

        return new AuthResult(principal, user.roles(), user.privileges());
    }

    @Override
    public UserPrincipal loadUserPrincipalByLogin(String login) {
        var user = userService.searchUsersByKeyword(login)
                .stream()
                .findFirst()
                .orElseThrow(() -> new AuthenticationException("Utilisateur introuvable"));

        return new UserPrincipal(
                user.id(),
                user.login(),
                user.email(),
                user.roles(),
                user.privileges()
        );
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        validator.validateNewPassword(newPassword);
        // À connecter plus tard au repository Utilisateur
    }
}
