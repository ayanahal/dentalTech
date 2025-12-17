package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM Notifications ORDER BY date DESC, time DESC";
        List<Notification> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapNotification(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public Notification findById(Long id) {
        String sql = "SELECT * FROM Notifications WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapNotification(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void create(Notification n) { throw new UnsupportedOperationException(); }
    @Override public void update(Notification n) { throw new UnsupportedOperationException(); }

    @Override public void delete(Notification n) { if (n != null) deleteById(n.getId()); }

    @Override
    public void deleteById(Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Notifications WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Notification> findByUtilisateur(Long userId) {
        return findList("WHERE utilisateur_id=?", userId);
    }

    @Override
    public List<Notification> findUnreadByUtilisateur(Long userId) {
        return findList("WHERE utilisateur_id=? AND lue=false", userId);
    }

    @Override
    public List<Notification> findByDate(Long userId, LocalDate date) {
        return findList("WHERE utilisateur_id=? AND date=?", userId, Date.valueOf(date));
    }

    @Override
    public List<Notification> findByType(Long userId, TypeNotification type) {
        return findList("WHERE utilisateur_id=? AND type=?", userId, type.name());
    }

    @Override
    public List<Notification> findByTitre(Long userId, TitreNotification titre) {
        return findList("WHERE utilisateur_id=? AND titre=?", userId, titre.name());
    }

    @Override
    public List<Notification> findByPriorite(Long userId, PrioriteNotification priorite) {
        return findList("WHERE utilisateur_id=? AND priorite=?", userId, priorite.name());
    }

    @Override
    public void markAsRead(Long notificationId) {
        update("UPDATE Notifications SET lue=true WHERE id=?", notificationId);
    }

    @Override
    public void markAllAsReadForUser(Long userId) {
        update("UPDATE Notifications SET lue=true WHERE utilisateur_id=?", userId);
    }

    private List<Notification> findList(String where, Object... params) {
        String sql = "SELECT * FROM Notifications " + where + " ORDER BY date DESC, time DESC";
        List<Notification> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapNotification(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    private void update(String sql, Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
