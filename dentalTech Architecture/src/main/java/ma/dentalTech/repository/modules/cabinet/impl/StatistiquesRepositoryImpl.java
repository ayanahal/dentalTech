package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.configuration.SessionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatistiquesRepositoryImpl implements StatistiquesRepository {

    private Connection getConnection() throws SQLException {
        return SessionFactory.getConnection();
    }

    // ========== CRUD Méthodes de Base ==========

    public BaseEntity save(Statistiques statistique) {
        if (statistique.getId() == null || statistique.getId() == 0) {
            return insert(statistique);
        } else {
            return update(statistique);
        }
    }

    private Statistiques insert(Statistiques statistique) {
        String sql = """
            INSERT INTO statistiques (
                nom, categorie, chiffre, date_calcul, 
                cabinet_id, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setStatistiqueParameters(ps, statistique);

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setTimestamp(7, Timestamp.valueOf(now));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        statistique.setId(rs.getLong(1));
                    }
                }
            }

            return statistique;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion de la statistique", e);
        }
    }

    public CabinetMedical update(Statistiques statistique) {
        String sql = """
            UPDATE statistiques SET
                nom = ?, categorie = ?, chiffre = ?, 
                date_calcul = ?, cabinet_id = ?, updated_at = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setStatistiqueParameters(ps, statistique);
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(7, statistique.getId());

            ps.executeUpdate();
            return statistique;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la statistique", e);
        }
    }

    @Override
    public void delete(Statistiques patient) {

    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = "SELECT * FROM statistiques WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapToStatistiques(rs).get());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la statistique par ID", e);
        }

        return Optional.empty();
    }

    @Override
    public void create(Statistiques newElement) {

    }

    @Override
    public List<Statistiques> findAll() {
        List<Statistiques> statistiques = new ArrayList<>();
        String sql = "SELECT * FROM statistiques ORDER BY date_calcul DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                statistiques.add(RowMappers.mapToStatistiques(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les statistiques", e);
        }

        return statistiques;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM statistiques WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la statistique", e);
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

    @Override
    public CabinetMedical save(CabinetMedical cabinet) {
        return null;
    }

    // ========== Méthodes Spécifiques ==========

    @Override
    public List<Statistiques> findByCabinet(Long cabinetId) {
        List<Statistiques> statistiques = new ArrayList<>();
        String sql = "SELECT * FROM statistiques WHERE cabinet_id = ? ORDER BY date_calcul DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    statistiques.add(RowMappers.mapToStatistiques(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des statistiques par cabinet", e);
        }

        return statistiques;
    }

    @Override
    public List<Statistiques> findByCategorie(CategorieStatistique categorie) {
        List<Statistiques> statistiques = new ArrayList<>();
        String sql = "SELECT * FROM statistiques WHERE categorie = ? ORDER BY date_calcul DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categorie.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    statistiques.add(RowMappers.mapToStatistiques(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des statistiques par catégorie", e);
        }

        return statistiques;
    }

    @Override
    public List<Statistiques> findByCabinetAndCategorie(Long cabinetId, CategorieStatistique categorie) {
        List<Statistiques> statistiques = new ArrayList<>();
        String sql = """
            SELECT * FROM statistiques 
            WHERE cabinet_id = ? AND categorie = ? 
            ORDER BY date_calcul DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setString(2, categorie.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    statistiques.add(RowMappers.mapToStatistiques(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des statistiques par cabinet et catégorie", e);
        }

        return statistiques;
    }

    @Override
    public List<Statistiques> findByDate(LocalDate dateCalcul) {
        List<Statistiques> statistiques = new ArrayList<>();
        String sql = "SELECT * FROM statistiques WHERE date_calcul = ? ORDER BY categorie";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(dateCalcul));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    statistiques.add(RowMappers.mapToStatistiques(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des statistiques par date", e);
        }

        return statistiques;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM statistiques WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence de la statistique", e);
        }

        return false;
    }

    @Override
    public AgendaMensuel save(AgendaMensuel toUpdate) {
        return null;
    }

    @Override
    public CabinetMedical save(RDV rdv) {
        return null;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM statistiques";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des statistiques", e);
        }

        return 0;
    }

    @Override
    public List<Statistiques> findPage(int limit, int offset) {
        List<Statistiques> statistiques = new ArrayList<>();
        String sql = "SELECT * FROM statistiques ORDER BY id DESC LIMIT ? OFFSET ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    statistiques.add(RowMappers.mapToStatistiques(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la pagination des statistiques", e);
        }

        return statistiques;
    }

    // ========== Méthodes Utilitaires Privées ==========

    private void setStatistiqueParameters(PreparedStatement ps, Statistiques statistique) throws SQLException {
        ps.setString(1, statistique.getNom());
        ps.setString(2, statistique.getCategorie().name());
        ps.setDouble(3, statistique.getChiffre());
        ps.setDate(4, Date.valueOf(statistique.getDateCalcul()));

        // Cabinet
        if (statistique.getCabinet() != null && statistique.getCabinet().getId() != null) {
            ps.setLong(5, statistique.getCabinet().getId());
        } else {
            ps.setNull(5, Types.BIGINT);
        }
    }

    // ========== Méthodes Statistiques Additionnelles ==========

    public Double getLatestStatistiqueByCabinetAndCategorie(Long cabinetId, CategorieStatistique categorie) {
        String sql = """
            SELECT chiffre FROM statistiques 
            WHERE cabinet_id = ? AND categorie = ? 
            ORDER BY date_calcul DESC 
            LIMIT 1
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setString(2, categorie.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("chiffre");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la dernière statistique", e);
        }

        return null;
    }

    public List<Statistiques> findLatestByCabinet(Long cabinetId, int limit) {
        List<Statistiques> statistiques = new ArrayList<>();
        String sql = """
            SELECT * FROM statistiques 
            WHERE cabinet_id = ? 
            ORDER BY date_calcul DESC, created_at DESC 
            LIMIT ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    statistiques.add(RowMappers.mapToStatistiques(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des dernières statistiques", e);
        }

        return statistiques;
    }
}