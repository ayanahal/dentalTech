package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.StaffRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffRepositoryImpl implements StaffRepository {

    @Override
    public List<Staff> findAll() {
        String sql = """
            SELECT u.*, s.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            ORDER BY u.id
            """;

        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapStaff(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public Staff findById(Long id) {
        String sql = """
            SELECT u.*, s.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            WHERE u.id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapStaff(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void create(Staff staff) { throw new UnsupportedOperationException("Création via UtilisateurRepository"); }
    @Override public void update(Staff staff) { throw new UnsupportedOperationException("Update via UtilisateurRepository"); }
    @Override public void delete(Staff staff) { if (staff != null) deleteById(staff.getId()); }

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
    public List<Staff> findAllOrderByNom() {
        String sql = """
            SELECT u.*, s.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            ORDER BY u.nom
            """;
        List<Staff> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapStaff(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public List<Staff> findBySalaireBetween(Double min, Double max) {
        String sql = """
            SELECT u.*, s.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            WHERE s.salaire BETWEEN ? AND ?
            """;

        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, min);
            ps.setDouble(2, max);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public List<Staff> findByDateRecrutementAfter(LocalDate date) {
        String sql = """
            SELECT u.*, s.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            WHERE s.dateRecrutement > ?
            """;

        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }
}
