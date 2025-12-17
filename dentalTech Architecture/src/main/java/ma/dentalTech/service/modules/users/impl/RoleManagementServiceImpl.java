package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.service.modules.users.api.RoleManagementService;

import java.util.List;

public class RoleManagementServiceImpl implements RoleManagementService {

    private final RoleRepository roleRepository;

    public RoleManagementServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(Role role) {
        if (roleRepository.existsByLibelle(role.getLibelle())) {
            throw new IllegalStateException("Rôle déjà existant");
        }
        roleRepository.create(role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        roleRepository.update(role);
        return role;
    }

    @Override
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role getRoleByType(RoleType type) {
        return roleRepository.findByType(type)
                .orElseThrow(() -> new IllegalArgumentException("Rôle introuvable"));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role updateRolePrivileges(Long roleId, List<String> privileges) {
        privileges.forEach(p -> roleRepository.addPrivilege(roleId, p));
        return roleRepository.findById(roleId);
    }
}
