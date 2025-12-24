package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SituationFinanciereRepositoryImpl implements SituationFinanciereRepo {

    @Override
    public List<SituationFinanciere> findAll() {
        List<SituationFinanciere> situations = new ArrayList<>();
        String sql = "SELECT * FROM situation_financiere ORDER BY id";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                situations.add(RowMappers.mapSituationFinanciere(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des situations financières", e);
        }
        return situations;
    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = "SELECT * FROM situation_financiere WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapSituationFinanciere(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la situation financière ID: " + id, e);
        }
    }

    @Override
    public void create(SituationFinanciere situation) {
        String sql = "INSERT INTO situation_financiere (totale_des_actes, totale_paye, credit, " +
                "statut, en_promo, dossier_medical_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, situation.getTotaleDesActes());
            ps.setDouble(2, situation.getTotalePaye());
            ps.setDouble(3, situation.getCredit());
            ps.setString(4, situation.getStatut().name());
            ps.setBoolean(5, situation.isEnPromo());
            ps.setLong(6, situation.getDossierMedical().getId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    situation.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la situation financière", e);
        }
    }

    @Override
    public CabinetMedical update(SituationFinanciere situation) {
        String sql = "UPDATE situation_financiere SET totale_des_actes = ?, totale_paye = ?, credit = ?, " +
                "statut = ?, en_promo = ?, dossier_medical_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, situation.getTotaleDesActes());
            ps.setDouble(2, situation.getTotalePaye());
            ps.setDouble(3, situation.getCredit());
            ps.setString(4, situation.getStatut().name());
            ps.setBoolean(5, situation.isEnPromo());
            ps.setLong(6, situation.getDossierMedical().getId());
            ps.setLong(7, situation.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la situation financière ID: " + situation.getId(), e);
        }
        return null;
    }

    @Override
    public void delete(SituationFinanciere situation) {
        if (situation != null) {
            deleteById(situation.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM situation_financiere WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la situation financière ID: " + id, e);
        }
    }

    @Override
    public Optional<SituationFinanciere> findByDossierMedicalId(Long dossierId) {
        String sql = "SELECT * FROM situation_financiere WHERE dossier_medical_id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapSituationFinanciere(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la situation financière par dossier ID: " + dossierId, e);
        }
    }

    @Override
    public List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut) {
        List<SituationFinanciere> situations = new ArrayList<>();
        String sql = "SELECT * FROM situation_financiere WHERE statut = ? ORDER BY id";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    situations.add(RowMappers.mapSituationFinanciere(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des situations financières par statut: " + statut, e);
        }
        return situations;
    }

    @Override
    public List<SituationFinanciere> findByCreditGreaterThan(Double montant) {
        List<SituationFinanciere> situations = new ArrayList<>();
        String sql = "SELECT * FROM situation_financiere WHERE credit > ? ORDER BY credit DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, montant);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    situations.add(RowMappers.mapSituationFinanciere(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des situations financières avec crédit > " + montant, e);
        }
        return situations;
    }

    @Override
    public List<SituationFinanciere> findByEnPromo(boolean enPromo) {
        List<SituationFinanciere> situations = new ArrayList<>();
        String sql = "SELECT * FROM situation_financiere WHERE en_promo = ? ORDER BY id";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, enPromo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    situations.add(RowMappers.mapSituationFinanciere(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des situations financières en promo: " + enPromo, e);
        }
        return situations;
    }

    @Override
    public void updateCredit(Long situationId, Double nouveauCredit) {
        String sql = "UPDATE situation_financiere SET credit = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, nouveauCredit);
            ps.setLong(2, situationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du crédit de la situation ID: " + situationId, e);
        }
    }

    @Override
    public void updateStatut(Long situationId, StatutSituationFinanciere statut) {
        String sql = "UPDATE situation_financiere SET statut = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, statut.name());
            ps.setLong(2, situationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la situation ID: " + situationId, e);
        }
    }

    @Override
    public double calculerTotalCredit() {
        String sql = "SELECT SUM(credit) as total_credit FROM situation_financiere";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total_credit");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du total des crédits", e);
        }
        return 0.0;
    }
}