package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DossierMedicalRepositoryImpl implements DossierMedicalRepo {

    @Override
    public List<DossierMedical> findAll() {
        List<DossierMedical> dossiers = new ArrayList<>();
        String sql = "SELECT d.*, p.* FROM dossier_medical d " +
                "JOIN patient p ON d.patient_id = p.id " +
                "ORDER BY d.date_creation DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DossierMedical dossier = RowMappers.mapDossierMedical(rs);
                dossier.setPatient(RowMappers.mapPatient(rs));
                dossiers.add(dossier);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des dossiers médicaux", e);
        }
        return dossiers;
    }

    @Override
    public DossierMedical findById(Long id) {
        String sql = "SELECT d.*, p.* FROM dossier_medical d " +
                "JOIN patient p ON d.patient_id = p.id " +
                "WHERE d.id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DossierMedical dossier = RowMappers.mapDossierMedical(rs);
                    dossier.setPatient(RowMappers.mapPatient(rs));
                    return dossier;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du dossier médical ID: " + id, e);
        }
    }

    @Override
    public void create(DossierMedical dossier) {
        String sql = "INSERT INTO dossier_medical (patient_id, date_creation) VALUES (?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, dossier.getPatient().getId());
            ps.setDate(2, Date.valueOf(dossier.getDateCreation()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    dossier.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du dossier médical", e);
        }
    }

    @Override
    public void update(DossierMedical dossier) {
        String sql = "UPDATE dossier_medical SET patient_id = ?, date_creation = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossier.getPatient().getId());
            ps.setDate(2, Date.valueOf(dossier.getDateCreation()));
            ps.setLong(3, dossier.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du dossier médical ID: " + dossier.getId(), e);
        }
    }

    @Override
    public void delete(DossierMedical dossier) {
        if (dossier != null) {
            deleteById(dossier.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM dossier_medical WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du dossier médical ID: " + id, e);
        }
    }

    @Override
    public Optional<DossierMedical> findByPatientId(Long patientId) {
        String sql = "SELECT d.*, p.* FROM dossier_medical d " +
                "JOIN patient p ON d.patient_id = p.id " +
                "WHERE d.patient_id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DossierMedical dossier = RowMappers.mapDossierMedical(rs);
                    dossier.setPatient(RowMappers.mapPatient(rs));
                    return Optional.of(dossier);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du dossier par patient ID: " + patientId, e);
        }
    }

    @Override
    public List<DossierMedical> findByPatientNomPrenom(String keyword) {
        List<DossierMedical> dossiers = new ArrayList<>();
        String sql = "SELECT d.*, p.* FROM dossier_medical d " +
                "JOIN patient p ON d.patient_id = p.id " +
                "WHERE p.nom LIKE ? OR p.prenom LIKE ? " +
                "ORDER BY p.nom, p.prenom";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DossierMedical dossier = RowMappers.mapDossierMedical(rs);
                    dossier.setPatient(RowMappers.mapPatient(rs));
                    dossiers.add(dossier);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des dossiers par nom/prenom", e);
        }
        return dossiers;
    }

    @Override
    public List<DossierMedical> findAllWithConsultations() {
        List<DossierMedical> dossiers = new ArrayList<>();
        String sql = "SELECT DISTINCT d.*, p.* FROM dossier_medical d " +
                "JOIN patient p ON d.patient_id = p.id " +
                "WHERE EXISTS (SELECT 1 FROM consultation c WHERE c.dossier_medical_id = d.id) " +
                "ORDER BY d.date_creation DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DossierMedical dossier = RowMappers.mapDossierMedical(rs);
                dossier.setPatient(RowMappers.mapPatient(rs));
                dossiers.add(dossier);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des dossiers avec consultations", e);
        }
        return dossiers;
    }

    @Override
    public long countDossiers() {
        String sql = "SELECT COUNT(*) FROM dossier_medical";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des dossiers médicaux", e);
        }
    }

    @Override
    public List<Consultation> getConsultationsOfDossier(Long dossierId) {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT c.* FROM consultation c " +
                "WHERE c.dossier_medical_id = ? " +
                "ORDER BY c.date_consultation DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consultations.add(RowMappers.mapConsultation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des consultations du dossier ID: " + dossierId, e);
        }
        return consultations;
    }

    @Override
    public List<Ordonnance> getOrdonnancesOfDossier(Long dossierId) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT o.* FROM ordonnance o " +
                "WHERE o.dossier_medical_id = ? " +
                "ORDER BY o.date DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(RowMappers.mapOrdonnance(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ordonnances du dossier ID: " + dossierId, e);
        }
        return ordonnances;
    }

    @Override
    public List<Certificat> getCertificatsOfDossier(Long dossierId) {
        List<Certificat> certificats = new ArrayList<>();
        String sql = "SELECT c.* FROM certificat c " +
                "WHERE c.dossier_medical_id = ? " +
                "ORDER BY c.date_debut DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    certificats.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des certificats du dossier ID: " + dossierId, e);
        }
        return certificats;
    }
}