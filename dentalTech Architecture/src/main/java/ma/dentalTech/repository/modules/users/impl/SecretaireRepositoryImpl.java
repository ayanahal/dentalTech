package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Secretaire;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.SecretaireRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    @Override
    public List<Secretaire> findAll() {
        String sql = """
            SELECT u.*, s.*, sec.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Secretaires sec ON s.id = sec.id
            ORDER BY u.id
            """;

        List<Secretaire> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapSecretaire(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public Secretaire findById(Long id) {
        String sql = """
            SELECT u.*, s.*, sec.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Secretaires sec ON s.id = sec.id
            WHERE u.id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapSecretaire(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void create(Secretaire s) { throw new UnsupportedOperationException(); }
    @Override public void update(Secretaire s) { throw new UnsupportedOperationException(); }
    @Override public void delete(Secretaire s) { if (s != null) deleteById(s.getId()); }

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
    public List<Secretaire> findAllOrderByNom() {
        String sql = """
            SELECT u.*, s.*, sec.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Secretaires sec ON s.id = sec.id
            ORDER BY u.nom
            """;

        List<Secretaire> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapSecretaire(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public Optional<Secretaire> findByNumCNSS(String numCNSS) {
        String sql = """
            SELECT u.*, s.*, sec.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Secretaires sec ON s.id = sec.id
            WHERE sec.numCNSS = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, numCNSS);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(RowMappers.mapSecretaire(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Secretaire> findByCommissionMin(Double minCommission) {
        String sql = """
            SELECT u.*, s.*, sec.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Secretaires sec ON s.id = sec.id
            WHERE sec.commission >= ?
            """;

        List<Secretaire> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, minCommission);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapSecretaire(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }
}
