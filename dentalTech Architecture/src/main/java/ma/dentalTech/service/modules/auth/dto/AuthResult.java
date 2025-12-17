package ma.dentalTech.service.modules.auth.dto;

import java.util.Set;
import ma.dentalTech.entities.enums.RoleType;

public record AuthResult(
        UserPrincipal principal,
        Set<RoleType> roles,
        Set<String> privileges
) {}
