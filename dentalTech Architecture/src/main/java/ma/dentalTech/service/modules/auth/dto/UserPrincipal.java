package ma.dentalTech.service.modules.auth.dto;

import java.util.Set;
import ma.dentalTech.entities.enums.RoleType;

public record UserPrincipal(
        Long id,
        String login,
        String email,
        Set<RoleType> roles,
        Set<String> privileges
) {}
