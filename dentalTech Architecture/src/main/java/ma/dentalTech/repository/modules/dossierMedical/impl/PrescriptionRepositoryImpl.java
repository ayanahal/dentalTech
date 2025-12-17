package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepositoryImpl implements PrescriptionRepo {

    @Override
    public List<Prescription> findAll() {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM prescription ORDER BY id";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prescriptions.add(RowMappers.mapPrescription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des prescriptions", e);
        }
        return prescriptions;
    }

    @Override
    public Prescription findById(Long id) {
        String sql = "SELECT * FROM prescription WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapPrescription(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la prescription ID: " + id, e);
        }
    }

    @Override
    public void create(Prescription prescription) {
        String sql = "INSERT INTO prescription (quantite, frequence, duree_en_jours, " +
                "ordonnance_id, medicament_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, prescription.getQuantite());
            ps.setString(2, prescription.getFrequence());
            ps.setInt(3, prescription.getDureeEnJours());
            ps.setLong(4, prescription.getOrdonnance().getId());
            ps.setLong(5, prescription.getMedicament().getId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    prescription.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la prescription", e);
        }
    }

    @Override
    public void update(Prescription prescription) {
        String sql = "UPDATE prescription SET quantite = ?, frequence = ?, duree_en_jours = ?, " +
                "ordonnance_id = ?, medicament_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, prescription.getQuantite());
            ps.setString(2, prescription.getFrequence());
            ps.setInt(3, prescription.getDureeEnJours());
            ps.setLong(4, prescription.getOrdonnance().getId());
            ps.setLong(5, prescription.getMedicament().getId());
            ps.setLong(6, prescription.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la prescription ID: " + prescription.getId(), e);
        }
    }

    @Override
    public void delete(Prescription prescription) {
        if (prescription != null) {
            deleteById(prescription.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM prescription WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la prescription ID: " + id, e);
        }
    }

    @Override
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM prescription WHERE ordonnance_id = ? ORDER BY id";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, ordonnanceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prescriptions.add(RowMappers.mapPrescription(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des prescriptions par ordonnance ID: " + ordonnanceId, e);
        }
        return prescriptions;
    }

    @Override
    public List<Prescription> findByMedicamentId(Long medicamentId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM prescription WHERE medicament_id = ? ORDER BY id";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medicamentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prescriptions.add(RowMappers.mapPrescription(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des prescriptions par médicament ID: " + medicamentId, e);
        }
        return prescriptions;
    }

    @Override
    public List<Prescription> findByQuantiteGreaterThan(Integer quantite) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM prescription WHERE quantite > ? ORDER BY quantite DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, quantite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prescriptions.add(RowMappers.mapPrescription(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des prescriptions avec quantité > " + quantite, e);
        }
        return prescriptions;
    }

    @Override
    public void updateQuantite(Long prescriptionId, Integer nouvelleQuantite) {
        String sql = "UPDATE prescription SET quantite = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, nouvelleQuantite);
            ps.setLong(2, prescriptionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la quantité de la prescription ID: " + prescriptionId, e);
        }
    }

    @Override
    public double calculerCoutTotalPrescription(Long prescriptionId) {
        String sql = """
            SELECT p.quantite * m.prix_unitaire as cout_total 
            FROM prescription p 
            JOIN medicament m ON p.medicament_id = m.id 
            WHERE p.id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, prescriptionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("cout_total");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du coût de la prescription ID: " + prescriptionId, e);
        }
        return 0.0;
    }
}