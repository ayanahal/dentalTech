package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InterventionMedecinRepositoryImpl implements InterventionMedecinRepo {

    @Override
    public List<InterventionMedecin> findAll() {
        List<InterventionMedecin> interventions = new ArrayList<>();
        String sql = "SELECT * FROM intervention_medecin ORDER BY num_dent";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                interventions.add(RowMappers.mapInterventionMedecin(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des interventions", e);
        }
        return interventions;
    }

    @Override
    public InterventionMedecin findById(Long id) {
        String sql = "SELECT * FROM intervention_medecin WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapInterventionMedecin(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'intervention ID: " + id, e);
        }
    }

    @Override
    public void create(InterventionMedecin intervention) {
        String sql = "INSERT INTO intervention_medecin (prix_patient, num_dent, consultation_id, acte_id) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, intervention.getPrixPatient());
            ps.setInt(2, intervention.getNumDent());

            if (intervention.getConsultationId() != null) {
                ps.setLong(3, intervention.getConsultationId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setLong(4, intervention.getActeId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    intervention.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'intervention", e);
        }
    }

    @Override
    public void update(InterventionMedecin intervention) {
        String sql = "UPDATE intervention_medecin SET prix_patient = ?, num_dent = ?, " +
                "consultation_id = ?, acte_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, intervention.getPrixPatient());
            ps.setInt(2, intervention.getNumDent());

            if (intervention.getConsultationId() != null) {
                ps.setLong(3, intervention.getConsultationId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setLong(4, intervention.getActeId());
            ps.setLong(5, intervention.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'intervention ID: " + intervention.getId(), e);
        }
    }

    @Override
    public void delete(InterventionMedecin intervention) {
        if (intervention != null) {
            deleteById(intervention.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM intervention_medecin WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'intervention ID: " + id, e);
        }
    }

    @Override
    public List<InterventionMedecin> findByConsultationId(Long consultationId) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        String sql = "SELECT * FROM intervention_medecin WHERE consultation_id = ? ORDER BY num_dent";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des interventions par consultation ID: " + consultationId, e);
        }
        return interventions;
    }

    @Override
    public List<InterventionMedecin> findByActeId(Long acteId) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        String sql = "SELECT * FROM intervention_medecin WHERE acte_id = ? ORDER BY num_dent";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, acteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des interventions par acte ID: " + acteId, e);
        }
        return interventions;
    }

    @Override
    public List<InterventionMedecin> findByNumDent(Integer numDent) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        String sql = "SELECT * FROM intervention_medecin WHERE num_dent = ? ORDER BY id";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, numDent);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des interventions par numéro de dent: " + numDent, e);
        }
        return interventions;
    }

    @Override
    public List<InterventionMedecin> findByPrixPatientBetween(Double min, Double max) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        String sql = "SELECT * FROM intervention_medecin WHERE prix_patient BETWEEN ? AND ? ORDER BY prix_patient";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, min);
            ps.setDouble(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des interventions par prix entre " + min + " et " + max, e);
        }
        return interventions;
    }

    @Override
    public double calculerTotalInterventionsConsultation(Long consultationId) {
        String sql = "SELECT SUM(prix_patient) as total FROM intervention_medecin WHERE consultation_id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du total des interventions pour la consultation ID: " + consultationId, e);
        }
        return 0.0;
    }

    @Override
    public long countInterventionsByActe(Long acteId) {
        String sql = "SELECT COUNT(*) FROM intervention_medecin WHERE acte_id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, acteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des interventions par acte ID: " + acteId, e);
        }
        return 0;
    }
}