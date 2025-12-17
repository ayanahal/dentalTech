package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActeRepositoryImpl implements ActeRepository {

    @Override
    public List<Acte> findAll() {
        List<Acte> actes = new ArrayList<>();
        String sql = "SELECT * FROM acte ORDER BY categorie, libelle";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                actes.add(RowMappers.mapActe(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des actes", e);
        }
        return actes;
    }

    @Override
    public Acte findById(Long id) {
        String sql = "SELECT * FROM acte WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapActe(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'acte ID: " + id, e);
        }
    }

    @Override
    public void create(Acte acte) {
        String sql = "INSERT INTO acte (libelle, categorie, prix_base) VALUES (?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, acte.getLibelle());
            ps.setString(2, acte.getCategorie());
            ps.setDouble(3, acte.getPrixBase());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    acte.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'acte", e);
        }
    }

    @Override
    public void update(Acte acte) {
        String sql = "UPDATE acte SET libelle = ?, categorie = ?, prix_base = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, acte.getLibelle());
            ps.setString(2, acte.getCategorie());
            ps.setDouble(3, acte.getPrixBase());
            ps.setLong(4, acte.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'acte ID: " + acte.getId(), e);
        }
    }

    @Override
    public void delete(Acte acte) {
        if (acte != null) {
            deleteById(acte.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM acte WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'acte ID: " + id, e);
        }
    }

    @Override
    public List<Acte> findByCategorie(String categorie) {
        List<Acte> actes = new ArrayList<>();
        String sql = "SELECT * FROM acte WHERE categorie = ? ORDER BY libelle";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, categorie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actes.add(RowMappers.mapActe(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des actes par catégorie: " + categorie, e);
        }
        return actes;
    }

    @Override
    public List<Acte> findByLibelleContaining(String keyword) {
        List<Acte> actes = new ArrayList<>();
        String sql = "SELECT * FROM acte WHERE libelle LIKE ? ORDER BY libelle";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            ps.setString(1, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actes.add(RowMappers.mapActe(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des actes par libellé contenant: " + keyword, e);
        }
        return actes;
    }

    @Override
    public List<Acte> findByPrixBetween(Double min, Double max) {
        List<Acte> actes = new ArrayList<>();
        String sql = "SELECT * FROM acte WHERE prix_base BETWEEN ? AND ? ORDER BY prix_base";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, min);
            ps.setDouble(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actes.add(RowMappers.mapActe(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des actes par prix entre " + min + " et " + max, e);
        }
        return actes;
    }

    @Override
    public List<InterventionMedecin> getInterventionsOfActe(Long acteId) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        String sql = "SELECT i.* FROM intervention_medecin i WHERE i.acte_id = ? ORDER BY i.num_dent";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, acteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des interventions de l'acte ID: " + acteId, e);
        }
        return interventions;
    }

    @Override
    public long countActesByCategorie(String categorie) {
        String sql = "SELECT COUNT(*) FROM acte WHERE categorie = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, categorie);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des actes par catégorie: " + categorie, e);
        }
        return 0;
    }
}