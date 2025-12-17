package ma.dentalTech.service.modules.users.utils;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.service.modules.users.dto.UserAccountDto;

import java.util.Set;

public class UserMapper {

    private UserMapper() {}

    public static UserAccountDto toDto(
            Utilisateur user,
            Set<RoleType> roles,
            Set<String> privileges
    ) {
        return new UserAccountDto(
                user.getId(),
                user.getNom(),
                user.getEmail(),
                user.getLogin(),
                user.getSexe(),
                user.getDateNaissance(),
                roles,
                privileges
        );
    }
}
