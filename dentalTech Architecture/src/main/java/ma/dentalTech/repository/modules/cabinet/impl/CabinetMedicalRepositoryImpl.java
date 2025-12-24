package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.configuration.SessionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CabinetMedicalRepositoryImpl implements CabinetMedicalRepository {

    private Connection getConnection() throws SQLException {
        return SessionFactory.getConnection();
    }

    // ========== CRUD Méthodes de Base ==========

    public CabinetMedical save(CabinetMedical cabinet) {
        if (cabinet.getId() == null || cabinet.getId() == 0) {
            return insert(cabinet);
        } else {
            return update(cabinet).getCabinet();
        }
    }

    private CabinetMedical insert(CabinetMedical cabinet) {
        String sql = """
            INSERT INTO cabinet_medical (
                nom, email, logo, adresse, cin, tel1, tel2, 
                site_web, instagram, facebook, description,
                created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setCabinetParameters(ps, cabinet);

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(12, Timestamp.valueOf(now));
            ps.setTimestamp(13, Timestamp.valueOf(now));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        cabinet.setId(rs.getLong(1));
                    }
                }
            }

            return cabinet;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion du cabinet", e);
        }
    }

    public CabinetMedical update(CabinetMedical cabinet) {
        String sql = """
            UPDATE cabinet_medical SET
                nom = ?, email = ?, logo = ?, adresse = ?, cin = ?, 
                tel1 = ?, tel2 = ?, site_web = ?, instagram = ?, 
                facebook = ?, description = ?, updated_at = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cabinet.getNom());
            ps.setString(2, cabinet.getEmail());
            ps.setString(3, cabinet.getLogo());
            ps.setString(4, cabinet.getAdresse());
            ps.setString(5, cabinet.getCin());
            ps.setString(6, cabinet.getTel1());
            ps.setString(7, cabinet.getTel2());
            ps.setString(8, cabinet.getSiteWeb());
            ps.setString(9, cabinet.getInstagram());
            ps.setString(10, cabinet.getFacebook());
            ps.setString(11, cabinet.getDescription());
            ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(13, cabinet.getId());

            ps.executeUpdate();
            return cabinet;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du cabinet", e);
        }
    }

    @Override
    public void delete(CabinetMedical patient) {

    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = "SELECT * FROM cabinet_medical WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapToCabinetMedical(rs).get());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du cabinet par ID", e);
        }

        return Optional.empty();
    }

    @Override
    public void create(CabinetMedical newElement) {

    }

    @Override
    public List<CabinetMedical> findAll() {
        List<CabinetMedical> cabinets = new ArrayList<>();
        String sql = "SELECT * FROM cabinet_medical ORDER BY nom";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cabinets.add(RowMappers.mapToCabinetMedical(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les cabinets", e);
        }

        return cabinets;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM cabinet_medical WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du cabinet", e);
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
    public Optional<CabinetMedical> findByNom(String nom) {
        String sql = "SELECT * FROM cabinet_medical WHERE LOWER(nom) = LOWER(?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nom);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapToCabinetMedical(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par nom", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<CabinetMedical> findByEmail(String email) {
        String sql = "SELECT * FROM cabinet_medical WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapToCabinetMedical(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par email", e);
        }

        return Optional.empty();
    }

    @Override
    public List<CabinetMedical> searchByNomOrAdresse(String keyword) {
        List<CabinetMedical> cabinets = new ArrayList<>();
        String sql = """
            SELECT * FROM cabinet_medical 
            WHERE LOWER(nom) LIKE LOWER(?) 
               OR LOWER(adresse) LIKE LOWER(?)
            ORDER BY nom
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cabinets.add(RowMappers.mapToCabinetMedical(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par mot-clé", e);
        }

        return cabinets;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM cabinet_medical WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence", e);
        }

        return false;
    }

    @Override
    public AgendaMensuel save(AgendaMensuel toUpdate) {
        return null;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM cabinet_medical";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des cabinets", e);
        }

        return 0;
    }

    @Override
    public List<CabinetMedical> findPage(int limit, int offset) {
        List<CabinetMedical> cabinets = new ArrayList<>();
        String sql = "SELECT * FROM cabinet_medical ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cabinets.add(RowMappers.mapToCabinetMedical(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la pagination", e);
        }

        return cabinets;
    }

    // ========== Relations Many-to-Many avec Staff ==========

    @Override
    public void addStaffToCabinet(Long cabinetId, Long staffId) {
        String sql = "INSERT INTO cabinet_staff (cabinet_id, staff_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setLong(2, staffId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du staff au cabinet", e);
        }
    }

    @Override
    public void removeStaffFromCabinet(Long cabinetId, Long staffId) {
        String sql = "DELETE FROM cabinet_staff WHERE cabinet_id = ? AND staff_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setLong(2, staffId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du staff du cabinet", e);
        }
    }

    @Override
    public void removeAllStaffFromCabinet(Long cabinetId) {
        String sql = "DELETE FROM cabinet_staff WHERE cabinet_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de tous les staff du cabinet", e);
        }
    }

    @Override
    public List<Staff> getStaffOfCabinet(Long cabinetId) {
        List<Staff> staffList = new ArrayList<>();
        String sql = """
            SELECT s.* FROM staff s
            INNER JOIN cabinet_staff cs ON s.id = cs.staff_id
            WHERE cs.cabinet_id = ?
            ORDER BY s.nom
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    staffList.add(RowMappers.mapToStaff(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du staff du cabinet", e);
        }

        return staffList;
    }

    @Override
    public List<CabinetMedical> getCabinetsOfStaff(Long staffId) {
        List<CabinetMedical> cabinets = new ArrayList<>();
        String sql = """
            SELECT cm.* FROM cabinet_medical cm
            INNER JOIN cabinet_staff cs ON cm.id = cs.cabinet_id
            WHERE cs.staff_id = ?
            ORDER BY cm.nom
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, staffId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cabinets.add(RowMappers.mapToCabinetMedical(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des cabinets du staff", e);
        }

        return cabinets;
    }

    @Override
    public CabinetMedical save(RDV toUpdate) {
        return null;
    }

    @Override
    public Statistiques save(Statistiques statistique) {
        return null;
    }

    // ========== Méthode Utile Privée ==========

    private void setCabinetParameters(PreparedStatement ps, CabinetMedical cabinet) throws SQLException {
        ps.setString(1, cabinet.getNom());
        ps.setString(2, cabinet.getEmail());
        ps.setString(3, cabinet.getLogo());
        ps.setString(4, cabinet.getAdresse());
        ps.setString(5, cabinet.getCin());
        ps.setString(6, cabinet.getTel1());
        ps.setString(7, cabinet.getTel2());
        ps.setString(8, cabinet.getSiteWeb());
        ps.setString(9, cabinet.getInstagram());
        ps.setString(10, cabinet.getFacebook());
        ps.setString(11, cabinet.getDescription());
    }
}