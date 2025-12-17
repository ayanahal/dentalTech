package ma.dentalTech.service.modules.auth.test;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.service.modules.auth.api.*;
import ma.dentalTech.service.modules.auth.dto.*;
import ma.dentalTech.service.modules.auth.impl.*;
import ma.dentalTech.service.modules.users.api.UserManagementService;
import ma.dentalTech.service.modules.users.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class TestAuth {

    public static void main(String[] args) {

        /* =========================
           1. Fake UserManagementService (CORRECT)
           ========================= */
        UserManagementService userService = new UserManagementService() {

            @Override
            public List<UserAccountDto> searchUsersByKeyword(String keyword) {
                return List.of(
                        new UserAccountDto(
                                1L,
                                "ADMIN TEST",
                                "admin@test.ma",
                                "admin",
                                Sexe.Homme,
                                LocalDate.of(1990, 1, 1),
                                Set.of(RoleType.ADMIN),
                                Set.of("USER_CREATE", "USER_DELETE", "ROLE_MANAGE")
                        )
                );
            }

            /* ---- Méthodes exigées par l’interface ---- */

            @Override public UserAccountDto createAdmin(CreateAdminRequest request) { return null; }
            @Override public UserAccountDto createMedecin(CreateMedecinRequest request) { return null; }
            @Override public UserAccountDto createSecretaire(CreateSecretaireRequest request) { return null; }

            @Override public UserAccountDto getUserById(Long id) { return null; }
            @Override public List<UserAccountDto> getAllUsers() { return List.of(); }

            @Override
            public UserAccountDto updateUserProfile(UpdateUserProfileRequest request) {
                return null;
            }

            @Override public void assignRoleToUser(Long utilisateurId, RoleType roleType) {}
            @Override public void removeRoleFromUser(Long utilisateurId, RoleType roleType) {}
        };

        /* =========================
           2. Services AUTH
           ========================= */
        CredentialsValidator validator = new CredentialsValidatorImpl();
        PasswordEncoder passwordEncoder = new SimplePasswordEncoder();
        AuthService authService = new AuthServiceImpl(userService, validator, passwordEncoder);
        AuthorizationService authorizationService = new AuthorizationServiceImpl();

        /* =========================
           3. AUTHENTIFICATION
           ========================= */
        AuthRequest request = new AuthRequest("admin", "123456");

        AuthResult result = authService.authenticate(request);

        System.out.println("Utilisateur : " + result.principal().login());
        System.out.println("Rôles : " + result.roles());
        System.out.println("Privilèges : " + result.privileges());

        /* =========================
           4. AUTORISATION
           ========================= */
        UserPrincipal principal = result.principal();

        System.out.println("ADMIN ? " +
                authorizationService.hasRole(principal, RoleType.ADMIN));

        System.out.println("MEDECIN ? " +
                authorizationService.hasRole(principal, RoleType.MEDECIN));

        System.out.println("USER_DELETE ? " +
                authorizationService.hasPrivilege(principal, "USER_DELETE"));

        try {
            authorizationService.checkRole(principal, RoleType.MEDECIN);
        } catch (Exception e) {
            System.out.println("Exception attendue : " + e.getMessage());
        }
    }
}
