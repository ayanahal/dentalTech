package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.service.modules.auth.api.AuthorizationService;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;
import ma.dentalTech.service.modules.auth.exception.AuthorizationException;

public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public boolean hasRole(UserPrincipal principal, RoleType role) {
        return principal.roles().contains(role);
    }

    @Override
    public boolean hasAnyRole(UserPrincipal principal, RoleType... roles) {
        for (RoleType r : roles)
            if (principal.roles().contains(r)) return true;
        return false;
    }

    @Override
    public boolean hasPrivilege(UserPrincipal principal, String privilege) {
        return principal.privileges().contains(privilege);
    }

    @Override
    public void checkRole(UserPrincipal principal, RoleType role) {
        if (!hasRole(principal, role))
            throw new AuthorizationException("Rôle requis manquant : " + role);
    }

    @Override
    public void checkAnyRole(UserPrincipal principal, RoleType... roles) {
        if (!hasAnyRole(principal, roles))
            throw new AuthorizationException("Aucun rôle autorisé");
    }

    @Override
    public void checkPrivilege(UserPrincipal principal, String privilege) {
        if (!hasPrivilege(principal, privilege))
            throw new AuthorizationException("Privilège requis manquant : " + privilege);
    }
}
