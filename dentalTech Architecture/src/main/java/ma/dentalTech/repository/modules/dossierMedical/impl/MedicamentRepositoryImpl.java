package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicamentRepositoryImpl implements MedicamentRepo {

    @Override
    public List<Medicament> findAll() {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM medicament ORDER BY nom";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                medicaments.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des médicaments", e);
        }
        return medicaments;
    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = "SELECT * FROM medicament WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapMedicament(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du médicament ID: " + id, e);
        }
    }

    @Override
    public void create(Medicament medicament) {
        String sql = "INSERT INTO medicament (nom, laboratoire, type, forme, remboursable, " +
                "prix_unitaire, description) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, medicament.getNom());
            ps.setString(2, medicament.getLaboratoire());
            ps.setString(3, medicament.getType());
            ps.setString(4, medicament.getForme().name());
            ps.setBoolean(5, medicament.isRemboursable());
            ps.setDouble(6, medicament.getPrixUnitaire());
            ps.setString(7, medicament.getDescription());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    medicament.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du médicament", e);
        }
    }

    @Override
    public CabinetMedical update(Medicament medicament) {
        String sql = "UPDATE medicament SET nom = ?, laboratoire = ?, type = ?, forme = ?, " +
                "remboursable = ?, prix_unitaire = ?, description = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, medicament.getNom());
            ps.setString(2, medicament.getLaboratoire());
            ps.setString(3, medicament.getType());
            ps.setString(4, medicament.getForme().name());
            ps.setBoolean(5, medicament.isRemboursable());
            ps.setDouble(6, medicament.getPrixUnitaire());
            ps.setString(7, medicament.getDescription());
            ps.setLong(8, medicament.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du médicament ID: " + medicament.getId(), e);
        }
        return null;
    }

    @Override
    public void delete(Medicament medicament) {
        if (medicament != null) {
            deleteById(medicament.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM medicament WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du médicament ID: " + id, e);
        }
    }

    @Override
    public List<Medicament> findByNomContaining(String keyword) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE nom LIKE ? ORDER BY nom";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            ps.setString(1, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(RowMappers.mapMedicament(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médicaments par nom contenant: " + keyword, e);
        }
        return medicaments;
    }

    @Override
    public List<Medicament> findByLaboratoire(String laboratoire) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE laboratoire = ? ORDER BY nom";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, laboratoire);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(RowMappers.mapMedicament(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médicaments par laboratoire: " + laboratoire, e);
        }
        return medicaments;
    }

    @Override
    public List<Medicament> findByForme(FormeMedicament forme) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE forme = ? ORDER BY nom";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, forme.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(RowMappers.mapMedicament(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médicaments par forme: " + forme, e);
        }
        return medicaments;
    }

    @Override
    public List<Medicament> findByRemboursable(boolean remboursable) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE remboursable = ? ORDER BY nom";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, remboursable);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(RowMappers.mapMedicament(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médicaments remboursables: " + remboursable, e);
        }
        return medicaments;
    }

    @Override
    public List<Medicament> findByType(String type) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE type = ? ORDER BY nom";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(RowMappers.mapMedicament(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médicaments par type: " + type, e);
        }
        return medicaments;
    }

    @Override
    public List<Medicament> findByPrixUnitaireBetween(Double min, Double max) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE prix_unitaire BETWEEN ? AND ? ORDER BY prix_unitaire";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, min);
            ps.setDouble(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(RowMappers.mapMedicament(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médicaments par prix entre " + min + " et " + max, e);
        }
        return medicaments;
    }
}