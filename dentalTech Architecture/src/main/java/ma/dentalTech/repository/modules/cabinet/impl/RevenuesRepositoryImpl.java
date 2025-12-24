package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.cabinet.api.RevenuesRepository;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.configuration.SessionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RevenuesRepositoryImpl implements RevenuesRepository {

    private Connection getConnection() throws SQLException {
        return SessionFactory.getConnection();
    }

    // ========== CRUD Méthodes de Base ==========

    public Revenues save(Revenues revenue) {
        if (revenue.getId() == null || revenue.getId() == 0) {
            return insert(revenue);
        } else {
            return update(revenue);
        }
    }

    private Revenues insert(Revenues revenue) {
        String sql = """
            INSERT INTO revenues (
                titre, description, montant, date_revenue, 
                cabinet_id, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setRevenueParameters(ps, revenue);

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setTimestamp(7, Timestamp.valueOf(now));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        revenue.setId(rs.getLong(1));
                    }
                }
            }

            return revenue;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion du revenu", e);
        }
    }

    public CabinetMedical update(Revenues revenue) {
        String sql = """
            UPDATE revenues SET
                titre = ?, description = ?, montant = ?, 
                date_revenue = ?, cabinet_id = ?, updated_at = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setRevenueParameters(ps, revenue);
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(7, revenue.getId());

            ps.executeUpdate();
            return revenue;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du revenu", e);
        }
    }

    @Override
    public void delete(Revenues patient) {

    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = "SELECT * FROM revenues WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapToRevenues(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du revenu par ID", e);
        }

        return Optional.empty();
    }

    @Override
    public void create(Revenues newElement) {

    }

    @Override
    public List<Revenues> findAll() {
        List<Revenues> revenues = new ArrayList<>();
        String sql = "SELECT * FROM revenues ORDER BY date_revenue DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                revenues.add(RowMappers.mapToRevenues(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les revenus", e);
        }

        return revenues;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM revenues WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du revenu", e);
        }
    }

    @Override
    public void syncAntecedentsForPatient(Patient patient) {

    }

    @Override
    public List<Patient> searchByTelephoneLike(String prefix) {
        return List.of();
    }

    @Override
    public List<Patient> findBySexe(Sexe sexe) {
        return List.of();
    }

    @Override
    public List<Patient> findByAssurance(Assurance assurance) {
        return List.of();
    }

    // ========== Méthodes Spécifiques ==========

    @Override
    public List<Revenues> findByCabinet(Long cabinetId) {
        List<Revenues> revenues = new ArrayList<>();
        String sql = "SELECT * FROM revenues WHERE cabinet_id = ? ORDER BY date_revenue DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    revenues.add(RowMappers.mapToRevenues(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des revenus par cabinet", e);
        }

        return revenues;
    }

    @Override
    public List<Revenues> findByCabinetAndDateBetween(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        List<Revenues> revenues = new ArrayList<>();
        String sql = """
            SELECT * FROM revenues 
            WHERE cabinet_id = ? 
            AND date_revenue BETWEEN ? AND ?
            ORDER BY date_revenue DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    revenues.add(RowMappers.mapToRevenues(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des revenus par cabinet et période", e);
        }

        return revenues;
    }

    @Override
    public List<Revenues> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        List<Revenues> revenues = new ArrayList<>();
        String sql = """
            SELECT * FROM revenues 
            WHERE date_revenue BETWEEN ? AND ?
            ORDER BY date_revenue DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    revenues.add(RowMappers.mapToRevenues(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des revenus par période", e);
        }

        return revenues;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM revenues WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence du revenu", e);
        }

        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM revenues";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des revenus", e);
        }

        return 0;
    }

    @Override
    public List<Revenues> findPage(int limit, int offset) {
        List<Revenues> revenues = new ArrayList<>();
        String sql = "SELECT * FROM revenues ORDER BY id DESC LIMIT ? OFFSET ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    revenues.add(RowMappers.mapToRevenues(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la pagination des revenus", e);
        }

        return revenues;
    }

    // ========== Méthodes Utilitaires ==========

    private void setRevenueParameters(PreparedStatement ps, Revenues revenue) throws SQLException {
        ps.setString(1, revenue.getTitre());
        ps.setString(2, revenue.getDescription());
        ps.setDouble(3, revenue.getMontant());
        ps.setTimestamp(4, Timestamp.valueOf(revenue.getDate()));

        // Cabinet
        if (revenue.getCabinet() != null && revenue.getCabinet().getId() != null) {
            ps.setLong(5, revenue.getCabinet().getId());
        } else {
            ps.setNull(5, Types.BIGINT);
        }
    }

    // ========== Méthodes Statistiques Additionnelles (utiles) ==========

    public double getTotalRevenuesByCabinet(Long cabinetId) {
        String sql = "SELECT SUM(montant) FROM revenues WHERE cabinet_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du total des revenus", e);
        }

        return 0.0;
    }

    public double getTotalRevenuesByCabinetAndPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT SUM(montant) FROM revenues 
            WHERE cabinet_id = ? 
            AND date_revenue BETWEEN ? AND ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du total des revenus par période", e);
        }

        return 0.0;
    }
}