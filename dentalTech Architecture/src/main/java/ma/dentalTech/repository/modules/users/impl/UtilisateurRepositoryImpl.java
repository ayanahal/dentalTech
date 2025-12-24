package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    @Override
    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM Utilisateurs ORDER BY id";
        List<Utilisateur> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = "SELECT * FROM Utilisateurs WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapUtilisateur(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void create(Utilisateur u) { throw new UnsupportedOperationException("Création via service"); }
    @Override public CabinetMedical update(Utilisateur u) { throw new UnsupportedOperationException("Update via service"); }

    @Override
    public void delete(Utilisateur u) { if (u != null) deleteById(u.getId()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Utilisateurs WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        String sql = "SELECT * FROM Utilisateurs WHERE email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapUtilisateur(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Utilisateur> findByLogin(String login) {
        String sql = "SELECT * FROM Utilisateurs WHERE login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapUtilisateur(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public boolean existsByEmail(String email) {
        return exists("SELECT 1 FROM Utilisateurs WHERE email = ?", email);
    }

    @Override public boolean existsByLogin(String login) {
        return exists("SELECT 1 FROM Utilisateurs WHERE login = ?", login);
    }

    private boolean exists(String sql, String value) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Utilisateur> searchByNom(String keyword) {
        String sql = "SELECT * FROM Utilisateurs WHERE nom LIKE ?";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public List<Utilisateur> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Utilisateurs LIMIT ? OFFSET ?";
        List<Utilisateur> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    // ----- Roles -----
    @Override
    public List<String> getRoleLibellesOfUser(Long userId) {
        String sql = """
            SELECT r.libelle
            FROM Roles r
            JOIN Utilisateur_Roles ur ON ur.role_id = r.id
            WHERE ur.utilisateur_id = ?
            """;

        List<String> roles = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) roles.add(rs.getString(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return roles;
    }

    @Override
    public void addRoleToUser(Long userId, Long roleId) {
        String sql = "INSERT INTO Utilisateur_Roles(utilisateur_id, role_id) VALUES (?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        String sql = "DELETE FROM Utilisateur_Roles WHERE utilisateur_id=? AND role_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
