package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConsultationRepositoryImpl implements ConsultationRepo {

    @Override
    public List<Consultation> findAll() {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT c.* FROM consultation c ORDER BY c.date_consultation DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                consultations.add(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des consultations", e);
        }
        return consultations;
    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT c.* FROM consultation c WHERE c.id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapConsultation(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la consultation ID: " + id, e);
        }
    }

    @Override
    public void create(Consultation consultation) {
        String sql = "INSERT INTO consultation (date_consultation, statut, observation_medecin, " +
                "dossier_medical_id, medecin_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(consultation.getDateConsultation()));
            ps.setString(2, consultation.getStatut().name());
            ps.setString(3, consultation.getObservationMedecin());
            ps.setLong(4, consultation.getDossierMedical().getId());
            ps.setLong(5, consultation.getMedecin().getId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    consultation.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la consultation", e);
        }
    }

    @Override
    public void update(Consultation consultation) {
        String sql = "UPDATE consultation SET date_consultation = ?, statut = ?, " +
                "observation_medecin = ?, dossier_medical_id = ?, medecin_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(consultation.getDateConsultation()));
            ps.setString(2, consultation.getStatut().name());
            ps.setString(3, consultation.getObservationMedecin());
            ps.setLong(4, consultation.getDossierMedical().getId());
            ps.setLong(5, consultation.getMedecin().getId());
            ps.setLong(6, consultation.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la consultation ID: " + consultation.getId(), e);
        }
    }

    @Override
    public void delete(Consultation consultation) {
        if (consultation != null) {
            deleteById(consultation.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM consultation WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la consultation ID: " + id, e);
        }
    }

    @Override
    public List<Consultation> findByDossierMedicalId(Long dossierId) {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT c.* FROM consultation c WHERE c.dossier_medical_id = ? ORDER BY c.date_consultation DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consultations.add(RowMappers.mapConsultation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des consultations par dossier ID: " + dossierId, e);
        }
        return consultations;
    }

    @Override
    public List<Consultation> findByMedecinId(Long medecinId) {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT c.* FROM consultation c WHERE c.medecin_id = ? ORDER BY c.date_consultation DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consultations.add(RowMappers.mapConsultation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des consultations par médecin ID: " + medecinId, e);
        }
        return consultations;
    }

    @Override
    public List<Consultation> findByDate(LocalDate date) {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT c.* FROM consultation c WHERE DATE(c.date_consultation) = ? ORDER BY c.date_consultation";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consultations.add(RowMappers.mapConsultation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des consultations par date: " + date, e);
        }
        return consultations;
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT c.* FROM consultation c WHERE c.statut = ? ORDER BY c.date_consultation DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consultations.add(RowMappers.mapConsultation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des consultations par statut: " + statut, e);
        }
        return consultations;
    }

    @Override
    public List<Consultation> findBetweenDates(LocalDate start, LocalDate end) {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT c.* FROM consultation c WHERE c.date_consultation BETWEEN ? AND ? ORDER BY c.date_consultation";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consultations.add(RowMappers.mapConsultation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des consultations entre " + start + " et " + end, e);
        }
        return consultations;
    }

    @Override
    public List<InterventionMedecin> getInterventionsOfConsultation(Long consultationId) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        String sql = "SELECT i.* FROM intervention_medecin i WHERE i.consultation_id = ? ORDER BY i.num_dent";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des interventions de la consultation ID: " + consultationId, e);
        }
        return interventions;
    }

    @Override
    public void addInterventionToConsultation(Long consultationId, Long interventionId) {
        String sql = "UPDATE intervention_medecin SET consultation_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            ps.setLong(2, interventionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'intervention à la consultation", e);
        }
    }

    @Override
    public void removeInterventionFromConsultation(Long consultationId, Long interventionId) {
        String sql = "UPDATE intervention_medecin SET consultation_id = NULL WHERE id = ? AND consultation_id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, interventionId);
            ps.setLong(2, consultationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'intervention de la consultation", e);
        }
    }
}