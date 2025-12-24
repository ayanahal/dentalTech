package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepo {

    @Override
    public List<Ordonnance> findAll() {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance ORDER BY date DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ordonnances.add(RowMappers.mapOrdonnance(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ordonnances", e);
        }
        return ordonnances;
    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = "SELECT * FROM ordonnance WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapOrdonnance(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'ordonnance ID: " + id, e);
        }
    }

    @Override
    public void create(Ordonnance ordonnance) {
        String sql = "INSERT INTO ordonnance (date, dossier_medical_id, medecin_id) VALUES (?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(ordonnance.getDate()));
            ps.setLong(2, ordonnance.getDossierMedical().getId());
            ps.setLong(3, ordonnance.getMedecin().getId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    ordonnance.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }

    @Override
    public CabinetMedical update(Ordonnance ordonnance) {
        String sql = "UPDATE ordonnance SET date = ?, dossier_medical_id = ?, medecin_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(ordonnance.getDate()));
            ps.setLong(2, ordonnance.getDossierMedical().getId());
            ps.setLong(3, ordonnance.getMedecin().getId());
            ps.setLong(4, ordonnance.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'ordonnance ID: " + ordonnance.getId(), e);
        }
        return null;
    }

    @Override
    public void delete(Ordonnance ordonnance) {
        if (ordonnance != null) {
            deleteById(ordonnance.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM ordonnance WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'ordonnance ID: " + id, e);
        }
    }

    @Override
    public List<Ordonnance> findByDossierMedicalId(Long dossierId) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance WHERE dossier_medical_id = ? ORDER BY date DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(RowMappers.mapOrdonnance(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des ordonnances par dossier ID: " + dossierId, e);
        }
        return ordonnances;
    }

    @Override
    public List<Ordonnance> findByMedecinId(Long medecinId) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance WHERE medecin_id = ? ORDER BY date DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(RowMappers.mapOrdonnance(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des ordonnances par médecin ID: " + medecinId, e);
        }
        return ordonnances;
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance WHERE DATE(date) = ? ORDER BY date";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(RowMappers.mapOrdonnance(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des ordonnances par date: " + date, e);
        }
        return ordonnances;
    }

    @Override
    public List<Ordonnance> findBetweenDates(LocalDate start, LocalDate end) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance WHERE date BETWEEN ? AND ? ORDER BY date";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(RowMappers.mapOrdonnance(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des ordonnances entre " + start + " et " + end, e);
        }
        return ordonnances;
    }

    @Override
    public List<Prescription> getPrescriptionsOfOrdonnance(Long ordonnanceId) {
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
            throw new RuntimeException("Erreur lors de la récupération des prescriptions de l'ordonnance ID: " + ordonnanceId, e);
        }
        return prescriptions;
    }

    @Override
    public void addPrescriptionToOrdonnance(Long ordonnanceId, Long prescriptionId) {
        String sql = "UPDATE prescription SET ordonnance_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, ordonnanceId);
            ps.setLong(2, prescriptionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de la prescription à l'ordonnance", e);
        }
    }

    @Override
    public void removePrescriptionFromOrdonnance(Long ordonnanceId, Long prescriptionId) {
        String sql = "UPDATE prescription SET ordonnance_id = NULL WHERE id = ? AND ordonnance_id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, prescriptionId);
            ps.setLong(2, ordonnanceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la prescription de l'ordonnance", e);
        }
    }
}