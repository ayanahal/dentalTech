package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CertificatRepositoryImpl implements CertificatRepo {

    @Override
    public List<Certificat> findAll() {
        List<Certificat> certificats = new ArrayList<>();
        String sql = "SELECT * FROM certificat ORDER BY date_debut DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                certificats.add(RowMappers.mapCertificat(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des certificats", e);
        }
        return certificats;
    }

    @Override
    public Certificat findById(Long id) {
        String sql = "SELECT * FROM certificat WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapCertificat(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du certificat ID: " + id, e);
        }
    }

    @Override
    public void create(Certificat certificat) {
        String sql = "INSERT INTO certificat (date_debut, date_fin, duree, note_medecin, " +
                "dossier_medical_id, medecin_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(certificat.getDateDebut()));
            ps.setDate(2, Date.valueOf(certificat.getDateFin()));
            ps.setInt(3, certificat.getDuree());
            ps.setString(4, certificat.getNoteMedecin());
            ps.setLong(5, certificat.getDossierMedical().getId());
            ps.setLong(6, certificat.getMedecin().getId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    certificat.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }
    }

    @Override
    public void update(Certificat certificat) {
        String sql = "UPDATE certificat SET date_debut = ?, date_fin = ?, duree = ?, " +
                "note_medecin = ?, dossier_medical_id = ?, medecin_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(certificat.getDateDebut()));
            ps.setDate(2, Date.valueOf(certificat.getDateFin()));
            ps.setInt(3, certificat.getDuree());
            ps.setString(4, certificat.getNoteMedecin());
            ps.setLong(5, certificat.getDossierMedical().getId());
            ps.setLong(6, certificat.getMedecin().getId());
            ps.setLong(7, certificat.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du certificat ID: " + certificat.getId(), e);
        }
    }

    @Override
    public void delete(Certificat certificat) {
        if (certificat != null) {
            deleteById(certificat.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM certificat WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du certificat ID: " + id, e);
        }
    }

    @Override
    public List<Certificat> findByDossierMedicalId(Long dossierId) {
        List<Certificat> certificats = new ArrayList<>();
        String sql = "SELECT * FROM certificat WHERE dossier_medical_id = ? ORDER BY date_debut DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    certificats.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des certificats par dossier ID: " + dossierId, e);
        }
        return certificats;
    }

    @Override
    public List<Certificat> findByMedecinId(Long medecinId) {
        List<Certificat> certificats = new ArrayList<>();
        String sql = "SELECT * FROM certificat WHERE medecin_id = ? ORDER BY date_debut DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    certificats.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des certificats par médecin ID: " + medecinId, e);
        }
        return certificats;
    }

    @Override
    public List<Certificat> findByDateBetween(LocalDate start, LocalDate end) {
        List<Certificat> certificats = new ArrayList<>();
        String sql = "SELECT * FROM certificat WHERE date_debut BETWEEN ? AND ? ORDER BY date_debut";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    certificats.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des certificats entre " + start + " et " + end, e);
        }
        return certificats;
    }

    @Override
    public List<Certificat> findByDureeGreaterThan(Integer duree) {
        List<Certificat> certificats = new ArrayList<>();
        String sql = "SELECT * FROM certificat WHERE duree > ? ORDER BY duree DESC";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, duree);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    certificats.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des certificats avec durée > " + duree, e);
        }
        return certificats;
    }

    @Override
    public boolean isCertificatValide(Long certificatId) {
        String sql = "SELECT date_fin FROM certificat WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, certificatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date dateFin = rs.getDate("date_fin");
                    if (dateFin != null) {
                        LocalDate dateFinLocal = dateFin.toLocalDate();
                        return LocalDate.now().isBefore(dateFinLocal) || LocalDate.now().isEqual(dateFinLocal);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification de validité du certificat ID: " + certificatId, e);
        }
        return false;
    }

    @Override
    public long countCertificatsByMedecin(Long medecinId) {
        String sql = "SELECT COUNT(*) FROM certificat WHERE medecin_id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des certificats par médecin ID: " + medecinId, e);
        }
        return 0;
    }
}