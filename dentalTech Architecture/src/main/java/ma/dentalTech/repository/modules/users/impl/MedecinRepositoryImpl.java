package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedecinRepositoryImpl implements MedecinRepository {

    @Override
    public List<Medecin> findAll() {
        String sql = """
            SELECT u.*, s.*, m.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Medecins m ON s.id = m.id
            ORDER BY u.id
            """;

        List<Medecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapMedecin(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = """
            SELECT u.*, s.*, m.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Medecins m ON s.id = m.id
            WHERE u.id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapMedecin(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void create(Medecin m) { throw new UnsupportedOperationException(); }
    @Override public CabinetMedical update(Medecin m) { throw new UnsupportedOperationException(); }
    @Override public void delete(Medecin m) { if (m != null) deleteById(m.getId()); }

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
    public List<Medecin> findAllOrderByNom() {
        String sql = """
            SELECT u.*, s.*, m.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Medecins m ON s.id = m.id
            ORDER BY u.nom
            """;

        List<Medecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapMedecin(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public List<Medecin> findBySpecialite(String specialiteLike) {
        String sql = """
            SELECT u.*, s.*, m.*
            FROM Utilisateurs u
            JOIN Staffs s ON u.id = s.id
            JOIN Medecins m ON s.id = m.id
            WHERE m.specialite LIKE ?
            """;

        List<Medecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + specialiteLike + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedecin(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }
}
