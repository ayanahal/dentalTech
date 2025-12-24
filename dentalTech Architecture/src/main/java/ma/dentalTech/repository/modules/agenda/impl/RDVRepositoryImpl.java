package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.configuration.SessionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RDVRepositoryImpl implements RDVRepository {

    private Connection getConnection() throws SQLException {
        return SessionFactory.getConnection();
    }

    public CabinetMedical save(RDV rdv) {
        if (rdv.getId() == null || rdv.getId() == 0) {
            return insert(rdv);
        } else {
            return update(rdv);
        }
    }

    private RDV insert(RDV rdv) {
        String sql = """
            INSERT INTO rdv (
                date_rdv, heure, motif, statut, note_medecin,
                agenda_mensuel_id, patient_id, medecin_id, dossier_medical_id,
                created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setRDVParameters(ps, rdv);

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(10, Timestamp.valueOf(now));
            ps.setTimestamp(11, Timestamp.valueOf(now));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        rdv.setId(rs.getLong(1));
                    }
                }
            }

            return rdv;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion du RDV", e);
        }
    }

    public CabinetMedical update(RDV rdv) {
        String sql = """
            UPDATE rdv SET
                date_rdv = ?, heure = ?, motif = ?, statut = ?, note_medecin = ?,
                agenda_mensuel_id = ?, patient_id = ?, medecin_id = ?, dossier_medical_id = ?,
                updated_at = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setRDVParameters(ps, rdv);
            ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(11, rdv.getId());

            ps.executeUpdate();
            return rdv;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du RDV", e);
        }
    }

    @Override
    public void delete(RDV patient) {

    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = """
            SELECT r.*,
                   p.nom as patient_nom, p.prenom as patient_prenom,
                   m.nom as medecin_nom, m.prenom as medecin_prenom
            FROM rdv r
            LEFT JOIN patient p ON r.patient_id = p.id
            LEFT JOIN medecin m ON r.medecin_id = m.id
            WHERE r.id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToRDV(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du RDV par ID", e);
        }

        return Optional.empty();
    }

    @Override
    public void create(RDV newElement) {

    }

    @Override
    public List<RDV> findAll() {
        List<RDV> rdvs = new ArrayList<>();
        String sql = """
            SELECT r.*,
                   p.nom as patient_nom, p.prenom as patient_prenom,
                   m.nom as medecin_nom, m.prenom as medecin_prenom
            FROM rdv r
            LEFT JOIN patient p ON r.patient_id = p.id
            LEFT JOIN medecin m ON r.medecin_id = m.id
            ORDER BY r.date_rdv DESC, r.heure DESC
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rdvs.add(mapToRDV(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les RDV", e);
        }

        return rdvs;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM rdv WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du RDV", e);
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
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public AgendaMensuel save(AgendaMensuel toUpdate) {
        return null;
    }

    @Override
    public List<RDV> findByMedecinAndDate(Long medecinId, LocalDate date) {
        List<RDV> rdvs = new ArrayList<>();
        String sql = """
            SELECT r.*,
                   p.nom as patient_nom, p.prenom as patient_prenom,
                   m.nom as medecin_nom, m.prenom as medecin_prenom
            FROM rdv r
            LEFT JOIN patient p ON r.patient_id = p.id
            LEFT JOIN medecin m ON r.medecin_id = m.id
            WHERE r.medecin_id = ? AND r.date_rdv = ?
            ORDER BY r.heure
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rdvs.add(mapToRDV(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des RDV par médecin et date", e);
        }

        return rdvs;
    }

    @Override
    public List<RDV> findByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end) {
        List<RDV> rdvs = new ArrayList<>();
        String sql = """
            SELECT r.*,
                   p.nom as patient_nom, p.prenom as patient_prenom,
                   m.nom as medecin_nom, m.prenom as medecin_prenom
            FROM rdv r
            LEFT JOIN patient p ON r.patient_id = p.id
            LEFT JOIN medecin m ON r.medecin_id = m.id
            WHERE r.medecin_id = ? AND r.date_rdv BETWEEN ? AND ?
            ORDER BY r.date_rdv, r.heure
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rdvs.add(mapToRDV(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des RDV par médecin et plage de dates", e);
        }

        return rdvs;
    }

    @Override
    public List<RDV> findByPatient(Long patientId) {
        List<RDV> rdvs = new ArrayList<>();
        String sql = """
            SELECT r.*,
                   p.nom as patient_nom, p.prenom as patient_prenom,
                   m.nom as medecin_nom, m.prenom as medecin_prenom
            FROM rdv r
            LEFT JOIN patient p ON r.patient_id = p.id
            LEFT JOIN medecin m ON r.medecin_id = m.id
            WHERE r.patient_id = ?
            ORDER BY r.date_rdv DESC, r.heure DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rdvs.add(mapToRDV(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des RDV par patient", e);
        }

        return rdvs;
    }

    @Override
    public List<RDV> findByAgenda(Long agendaId) {
        List<RDV> rdvs = new ArrayList<>();
        String sql = """
            SELECT r.*,
                   p.nom as patient_nom, p.prenom as patient_prenom,
                   m.nom as medecin_nom, m.prenom as medecin_prenom
            FROM rdv r
            LEFT JOIN patient p ON r.patient_id = p.id
            LEFT JOIN medecin m ON r.medecin_id = m.id
            WHERE r.agenda_mensuel_id = ?
            ORDER BY r.date_rdv, r.heure
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rdvs.add(mapToRDV(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des RDV par agenda", e);
        }

        return rdvs;
    }

    private void setRDVParameters(PreparedStatement ps, RDV rdv) throws SQLException {
        ps.setDate(1, Date.valueOf(rdv.getDate()));
        ps.setTime(2, Time.valueOf(rdv.getHeure()));
        ps.setString(3, rdv.getMotif());
        ps.setString(4, rdv.getStatut().name());
        ps.setString(5, rdv.getNoteMedecin());

        ps.setLong(6, rdv.getAgendaMensuelId());

        if (rdv.getPatient() != null && rdv.getPatient().getId() != null) {
            ps.setLong(7, rdv.getPatient().getId());
        } else {
            ps.setNull(7, Types.BIGINT);
        }

        if (rdv.getMedecin() != null && rdv.getMedecin().getId() != null) {
            ps.setLong(8, rdv.getMedecin().getId());
        } else {
            ps.setNull(8, Types.BIGINT);
        }

        if (rdv.getDossierMedical() != null && rdv.getDossierMedical().getId() != null) {
            ps.setLong(9, rdv.getDossierMedical().getId());
        } else {
            ps.setNull(9, Types.BIGINT);
        }
    }

    private RDV mapToRDV(ResultSet rs) throws SQLException {
        RDV rdv = new RDV();
        rdv.setId(rs.getLong("id"));

        Date dateRdv = rs.getDate("date_rdv");
        if (dateRdv != null) {
            rdv.setDate(dateRdv.toLocalDate());
        }

        Time heureRdv = rs.getTime("heure");
        if (heureRdv != null) {
            rdv.setHeure(heureRdv.toLocalTime());
        }

        rdv.setMotif(rs.getString("motif"));

        String statutStr = rs.getString("statut");
        if (statutStr != null) {
            rdv.setStatut(StatutRDV.valueOf(statutStr));
        }

        rdv.setNoteMedecin(rs.getString("note_medecin"));
        rdv.setAgendaMensuelId(rs.getLong("agenda_mensuel_id"));

        // Patient
        Patient patient = new Patient();
        patient.setId(rs.getLong("patient_id"));
        patient.setNom(rs.getString("patient_nom"));
        patient.setPrenom(rs.getString("patient_prenom"));
        rdv.setPatient(patient);

        // Medecin
        Medecin medecin = new Medecin();
        medecin.setId(rs.getLong("medecin_id"));
        medecin.setNom(rs.getString("medecin_nom"));
        medecin.setPrenom(rs.getString("medecin_prenom"));
        rdv.setMedecin(medecin);

        // Dossier Medical (juste l'ID pour l'instant)
        DossierMedical dossier = new DossierMedical();
        dossier.setId(rs.getLong("dossier_medical_id"));
        rdv.setDossierMedical(dossier);

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            rdv.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            rdv.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return rdv;
    }
}
