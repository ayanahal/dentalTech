package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.users.Admin;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.AdminRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminRepositoryImpl implements AdminRepository {

    @Override
    public List<Admin> findAll() {
        String sql = """
            SELECT u.*, s.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Admins a ON s.id = a.id
            """;

        List<Admin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapAdmin(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = """
            SELECT u.*, s.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Admins a ON s.id = a.id
            WHERE u.id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapAdmin(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void create(Admin a) { throw new UnsupportedOperationException(); }
    @Override public CabinetMedical update(Admin a) { throw new UnsupportedOperationException(); }

    @Override
    public void deleteById(Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Utilisateurs WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void delete(Admin a) { if (a != null) deleteById(a.getId()); }

    @Override
    public List<Admin> findAllOrderByNom() {
        String sql = """
            SELECT u.*, s.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Admins a ON s.id = a.id
            ORDER BY u.nom
            """;

        List<Admin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapAdmin(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        String sql = """
            SELECT u.*, s.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Admins a ON s.id = a.id
            WHERE u.email = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapAdmin(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
