package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.cabinet.api.ChargesRepository;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.configuration.SessionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChargesRepositoryImpl implements ChargesRepository {

    private Connection getConnection() throws SQLException {
        return SessionFactory.getConnection();
    }

    // ========== CRUD Méthodes de Base ==========

    public Charges save(Charges charge) {
        if (charge.getId() == null || charge.getId() == 0) {
            return insert(charge);
        } else {
            return update(charge);
        }
    }

    private Charges insert(Charges charge) {
        String sql = """
            INSERT INTO charges (
                libelle, montant, date_charge, categorie, description,
                mode_paiement, cabinet_id, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setChargeParameters(ps, charge);

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(8, Timestamp.valueOf(now));
            ps.setTimestamp(9, Timestamp.valueOf(now));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        charge.setId(rs.getLong(1));
                    }
                }
            }

            return charge;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion de la charge", e);
        }
    }

    public CabinetMedical update(Charges charge) {
        String sql = """
            UPDATE charges SET
                libelle = ?, montant = ?, date_charge = ?, categorie = ?, 
                description = ?, mode_paiement = ?, cabinet_id = ?, updated_at = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setChargeParameters(ps, charge);
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(9, charge.getId());

            ps.executeUpdate();
            return charge;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la charge", e);
        }
    }

    @Override
    public void delete(Charges patient) {

    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = "SELECT * FROM charges WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapToCharges(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la charge par ID", e);
        }

        return Optional.empty();
    }

    @Override
    public void create(Charges newElement) {

    }

    @Override
    public List<Charges> findAll() {
        List<Charges> charges = new ArrayList<>();
        String sql = "SELECT * FROM charges ORDER BY date_charge DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                charges.add(RowMappers.mapToCharges(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les charges", e);
        }

        return charges;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM charges WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la charge", e);
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
    public List<Charges> findByCabinet(Long cabinetId) {
        List<Charges> charges = new ArrayList<>();
        String sql = "SELECT * FROM charges WHERE cabinet_id = ? ORDER BY date_charge DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    charges.add(RowMappers.mapToCharges(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des charges par cabinet", e);
        }

        return charges;
    }

    @Override
    public List<Charges> findByCabinetAndDateBetween(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        List<Charges> charges = new ArrayList<>();
        String sql = """
            SELECT * FROM charges 
            WHERE cabinet_id = ? 
            AND date_charge BETWEEN ? AND ?
            ORDER BY date_charge DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    charges.add(RowMappers.mapToCharges(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des charges par cabinet et période", e);
        }

        return charges;
    }

    @Override
    public List<Charges> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        List<Charges> charges = new ArrayList<>();
        String sql = """
            SELECT * FROM charges 
            WHERE date_charge BETWEEN ? AND ?
            ORDER BY date_charge DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    charges.add(RowMappers.mapToCharges(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des charges par période", e);
        }

        return charges;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM charges WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence de la charge", e);
        }

        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM charges";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des charges", e);
        }

        return 0;
    }

    @Override
    public List<Charges> findPage(int limit, int offset) {
        List<Charges> charges = new ArrayList<>();
        String sql = "SELECT * FROM charges ORDER BY id DESC LIMIT ? OFFSET ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    charges.add(RowMappers.mapToCharges(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la pagination des charges", e);
        }

        return charges;
    }

    // ========== Méthode Utile Privée ==========

    private void setChargeParameters(PreparedStatement ps, Charges charge) throws SQLException {
        ps.setString(1, charge.getLibelle());
        ps.setDouble(2, charge.getMontant());
        ps.setTimestamp(3, Timestamp.valueOf(charge.getDateCharge()));
        ps.setString(4, charge.getCategorie());
        ps.setString(5, charge.getDescription());
        ps.setString(6, charge.getModePaiement());
        ps.setLong(7, charge.getCabinet().getId());
    }
}