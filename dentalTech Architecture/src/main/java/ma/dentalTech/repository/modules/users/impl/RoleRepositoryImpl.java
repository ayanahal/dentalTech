package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.RoleRepository;

import java.sql.*;
import java.util.*;

public class RoleRepositoryImpl implements RoleRepository {

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM Roles";
        List<Role> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapRole(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = "SELECT * FROM Roles WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapRole(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void create(Role r) { throw new UnsupportedOperationException(); }
    @Override public CabinetMedical update(Role r) { throw new UnsupportedOperationException(); }
    @Override public void delete(Role r) { if (r != null) deleteById(r.getId()); }

    @Override
    public void deleteById(Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Roles WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Role> findByLibelle(String libelle) {
        return findOne("SELECT * FROM Roles WHERE libelle=?", libelle);
    }

    @Override
    public Optional<Role> findByType(RoleType type) {
        return findOne("SELECT * FROM Roles WHERE type=?", type.name());
    }

    private Optional<Role> findOne(String sql, String value) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapRole(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<String> getPrivileges(Long roleId) {
        String sql = "SELECT privilege FROM Role_Privileges WHERE role_id=?";
        List<String> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getString(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public void addPrivilege(Long roleId, String privilege) {
        execute("INSERT INTO Role_Privileges(role_id, privilege) VALUES (?,?)", roleId, privilege);
    }

    @Override
    public void removePrivilege(Long roleId, String privilege) {
        execute("DELETE FROM Role_Privileges WHERE role_id=? AND privilege=?", roleId, privilege);
    }

    @Override
    public boolean existsByLibelle(String libelle) {
        return exists("SELECT 1 FROM Roles WHERE libelle=?", libelle);
    }

    @Override
    public List<Role> findRolesByUtilisateurId(Long userId) {
        String sql = """
            SELECT r.*
            FROM Roles r
            JOIN Utilisateur_Roles ur ON ur.role_id = r.id
            WHERE ur.utilisateur_id = ?
            """;

        List<Role> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRole(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        execute("INSERT INTO Utilisateur_Roles(utilisateur_id, role_id) VALUES (?,?)", userId, roleId);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        execute("DELETE FROM Utilisateur_Roles WHERE utilisateur_id=? AND role_id=?", userId, roleId);
    }

    private boolean exists(String sql, String value) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private void execute(String sql, Long id, String val) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setString(2, val);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    private void execute(String sql, Long id1, Long id2) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id1);
            ps.setLong(2, id2);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
