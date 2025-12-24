package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.configuration.SessionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgendaMensuelRepositoryImpl implements AgendaMensuelRepository {

    private Connection getConnection() throws SQLException {
        return SessionFactory.getConnection();
    }

    public AgendaMensuel save(AgendaMensuel agenda) {
        if (agenda.getId() == null || agenda.getId() == 0) {
            return insert(agenda);
        } else {
            return update(agenda);
        }
    }

    private AgendaMensuel insert(AgendaMensuel agenda) {
        String sql = """
            INSERT INTO agenda_mensuel (
                mois, annee, medecin_id, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, agenda.getMois().name());
            ps.setInt(2, agenda.getAnnee());
            ps.setLong(3, agenda.getMedecinId());

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.setTimestamp(5, Timestamp.valueOf(now));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        agenda.setId(rs.getLong(1));

                        // Sauvegarder les jours non disponibles
                        if (agenda.getJoursNonDisponibles() != null && !agenda.getJoursNonDisponibles().isEmpty()) {
                            saveJoursNonDisponibles(agenda.getId(), agenda.getJoursNonDisponibles());
                        }
                    }
                }
            }

            return agenda;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion de l'agenda", e);
        }
    }

    public CabinetMedical update(AgendaMensuel agenda) {
        String sql = """
            UPDATE agenda_mensuel SET
                mois = ?, annee = ?, medecin_id = ?, updated_at = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, agenda.getMois().name());
            ps.setInt(2, agenda.getAnnee());
            ps.setLong(3, agenda.getMedecinId());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(5, agenda.getId());

            ps.executeUpdate();

            // Mettre à jour les jours non disponibles
            clearJoursNonDisponibles(agenda.getId());
            if (agenda.getJoursNonDisponibles() != null && !agenda.getJoursNonDisponibles().isEmpty()) {
                saveJoursNonDisponibles(agenda.getId(), agenda.getJoursNonDisponibles());
            }

            return agenda;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'agenda", e);
        }
    }

    @Override
    public void delete(AgendaMensuel patient) {

    }

    private void saveJoursNonDisponibles(Long agendaId, List<LocalDate> jours) {
        String sql = "INSERT INTO agenda_jours_non_disponibles (agenda_id, jour) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (LocalDate jour : jours) {
                ps.setLong(1, agendaId);
                ps.setDate(2, Date.valueOf(jour));
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde des jours non disponibles", e);
        }
    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = """
            SELECT a.*, 
                   GROUP_CONCAT(DISTINCT j.jour) as jours_non_disponibles
            FROM agenda_mensuel a
            LEFT JOIN agenda_jours_non_disponibles j ON a.id = j.agenda_id
            WHERE a.id = ?
            GROUP BY a.id
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToAgendaMensuel(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'agenda par ID", e);
        }

        return Optional.empty();
    }

    @Override
    public void create(AgendaMensuel newElement) {

    }

    @Override
    public List<AgendaMensuel> findAll() {
        List<AgendaMensuel> agendas = new ArrayList<>();
        String sql = """
            SELECT a.*, 
                   GROUP_CONCAT(DISTINCT j.jour) as jours_non_disponibles
            FROM agenda_mensuel a
            LEFT JOIN agenda_jours_non_disponibles j ON a.id = j.agenda_id
            GROUP BY a.id
            ORDER BY a.annee DESC, a.mois DESC
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                agendas.add(mapToAgendaMensuel(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les agendas", e);
        }

        return agendas;
    }

    @Override
    public void deleteById(Long id) {
        // Supprimer d'abord les jours non disponibles
        clearJoursNonDisponibles(id);

        String sql = "DELETE FROM agenda_mensuel WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'agenda", e);
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

    @Override
    public Optional<AgendaMensuel> findByMedecinAndMoisAndAnnee(Long medecinId, String moisEnumName, int annee) {
        String sql = """
            SELECT a.*, 
                   GROUP_CONCAT(DISTINCT j.jour) as jours_non_disponibles
            FROM agenda_mensuel a
            LEFT JOIN agenda_jours_non_disponibles j ON a.id = j.agenda_id
            WHERE a.medecin_id = ? AND a.mois = ? AND a.annee = ?
            GROUP BY a.id
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setString(2, moisEnumName);
            ps.setInt(3, annee);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToAgendaMensuel(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'agenda par médecin, mois et année", e);
        }

        return Optional.empty();
    }

    @Override
    public List<AgendaMensuel> findByMedecin(Long medecinId) {
        List<AgendaMensuel> agendas = new ArrayList<>();
        String sql = """
            SELECT a.*, 
                   GROUP_CONCAT(DISTINCT j.jour) as jours_non_disponibles
            FROM agenda_mensuel a
            LEFT JOIN agenda_jours_non_disponibles j ON a.id = j.agenda_id
            WHERE a.medecin_id = ?
            GROUP BY a.id
            ORDER BY a.annee DESC, a.mois DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agendas.add(mapToAgendaMensuel(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des agendas par médecin", e);
        }

        return agendas;
    }

    @Override
    public List<LocalDate> getJoursNonDisponibles(Long agendaId) {
        List<LocalDate> jours = new ArrayList<>();
        String sql = "SELECT jour FROM agenda_jours_non_disponibles WHERE agenda_id = ? ORDER BY jour";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jours.add(rs.getDate("jour").toLocalDate());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des jours non disponibles", e);
        }

        return jours;
    }

    @Override
    public void addJourNonDisponible(Long agendaId, LocalDate date) {
        String sql = "INSERT INTO agenda_jours_non_disponibles (agenda_id, jour) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du jour non disponible", e);
        }
    }

    @Override
    public void removeJourNonDisponible(Long agendaId, LocalDate date) {
        String sql = "DELETE FROM agenda_jours_non_disponibles WHERE agenda_id = ? AND jour = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du jour non disponible", e);
        }
    }

    @Override
    public void clearJoursNonDisponibles(Long agendaId) {
        String sql = "DELETE FROM agenda_jours_non_disponibles WHERE agenda_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du nettoyage des jours non disponibles", e);
        }
    }

    private AgendaMensuel mapToAgendaMensuel(ResultSet rs) throws SQLException {
        AgendaMensuel agenda = new AgendaMensuel();
        agenda.setId(rs.getLong("id"));

        String moisStr = rs.getString("mois");
        if (moisStr != null) {
            agenda.setMois(Mois.valueOf(moisStr));
        }

        agenda.setAnnee(rs.getInt("annee"));
        agenda.setMedecinId(rs.getLong("medecin_id"));

        // Charger les jours non disponibles
        String joursStr = rs.getString("jours_non_disponibles");
        if (joursStr != null && !joursStr.isEmpty()) {
            List<LocalDate> jours = new ArrayList<>();
            String[] joursArray = joursStr.split(",");
            for (String jour : joursArray) {
                jours.add(Date.valueOf(jour.trim()).toLocalDate());
            }
            agenda.setJoursNonDisponibles(jours);
        }

        return agenda;
    }
}
