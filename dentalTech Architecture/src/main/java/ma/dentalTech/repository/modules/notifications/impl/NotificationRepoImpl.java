package ma.dentalTech.repository.modules.notifications.impl;

import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.repository.modules.notifications.api.NotificationRepo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationRepoImpl implements NotificationRepo {

    private final Connection connection;

    public NotificationRepoImpl(Connection connection) {
        this.connection = connection;
    }

    /* ===================== CRUD ===================== */

    @Override
    public Notification save(Notification n) {
        String sql = """
            INSERT INTO Notifications
            (titre, message, date, time, type, priorite, lue, utilisateur_id)
            VALUES (?,?,?,?,?,?,?,?)
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, n.getTitre().name());
            ps.setString(2, n.getMessage());
            ps.setDate(3, Date.valueOf(n.getDate()));
            ps.setTime(4, Time.valueOf(n.getTime()));
            ps.setString(5, n.getType().name());
            ps.setString(6, n.getPriorite().name());
            ps.setBoolean(7, n.isLue());
            ps.setLong(8, n.getUtilisateur().getId());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                n.setId(rs.getLong(1));
            }
            return n;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur insertion notification", e);
        }
    }

    @Override
    public Optional<Notification> findById(Long id) {
        String sql = "SELECT * FROM Notifications WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? Optional.of(map(rs)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Notification> findAll() {
        return findMany("SELECT * FROM Notifications");
    }

    @Override
    public void deleteById(Long id) {
        execute("DELETE FROM Notifications WHERE id=?", id);
    }

    /* ===================== Recherches ===================== */

    @Override
    public List<Notification> findByUtilisateur(Long utilisateurId) {
        return findMany(
                "SELECT * FROM Notifications WHERE utilisateur_id=?",
                utilisateurId
        );
    }

    @Override
    public List<Notification> findUnreadByUtilisateur(Long utilisateurId) {
        return findMany(
                "SELECT * FROM Notifications WHERE utilisateur_id=? AND lue=false",
                utilisateurId
        );
    }

    @Override
    public List<Notification> findByDate(Long utilisateurId, LocalDate date) {
        return findMany(
                "SELECT * FROM Notifications WHERE utilisateur_id=? AND date=?",
                utilisateurId, Date.valueOf(date)
        );
    }

    @Override
    public List<Notification> findByType(Long utilisateurId, TypeNotification type) {
        return findMany(
                "SELECT * FROM Notifications WHERE utilisateur_id=? AND type=?",
                utilisateurId, type.name()
        );
    }

    @Override
    public List<Notification> findByTitre(Long utilisateurId, TitreNotification titre) {
        return findMany(
                "SELECT * FROM Notifications WHERE utilisateur_id=? AND titre=?",
                utilisateurId, titre.name()
        );
    }

    @Override
    public List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite) {
        return findMany(
                "SELECT * FROM Notifications WHERE utilisateur_id=? AND priorite=?",
                utilisateurId, priorite.name()
        );
    }

    /* ===================== Actions ===================== */

    @Override
    public void markAsRead(Long notificationId) {
        execute(
                "UPDATE Notifications SET lue=true WHERE id=?",
                notificationId
        );
    }

    @Override
    public void markAllAsReadForUser(Long utilisateurId) {
        execute(
                "UPDATE Notifications SET lue=true WHERE utilisateur_id=?",
                utilisateurId
        );
    }

    /* ===================== Helpers ===================== */

    private List<Notification> findMany(String sql, Object... params) {
        List<Notification> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void execute(String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Mapping ResultSet → Entité Notification
     * (inclut BaseEntity)
     */
    private Notification map(ResultSet rs) throws SQLException {

        Utilisateur u = new Utilisateur();
        u.setId(rs.getLong("utilisateur_id"));

        Notification n = new Notification();

        /* ---- BaseEntity ---- */
        n.setId(rs.getLong("id"));
        n.setDateCreation(rs.getDate("dateCreation").toLocalDate());
        n.setDateDerniereModification(
                rs.getTimestamp("dateDerniereModification").toLocalDateTime()
        );
        n.setCreePar(rs.getString("creePar"));
        n.setModifiePar(rs.getString("modifiePar"));

        /* ---- Notification ---- */
        n.setTitre(TitreNotification.valueOf(rs.getString("titre")));
        n.setMessage(rs.getString("message"));
        n.setDate(rs.getDate("date").toLocalDate());
        n.setTime(rs.getTime("time").toLocalTime());
        n.setType(TypeNotification.valueOf(rs.getString("type")));
        n.setPriorite(PrioriteNotification.valueOf(rs.getString("priorite")));
        n.setLue(rs.getBoolean("lue"));
        n.setUtilisateur(u);

        return n;
    }
}
