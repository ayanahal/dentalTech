package ma.dentalTech.service.modules.notifications.impl;

import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.repository.modules.notifications.api.NotificationRepo;
import ma.dentalTech.service.modules.notifications.api.NotificationService;
import ma.dentalTech.entities.enums.*;

import java.time.LocalDate;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;

    public NotificationServiceImpl(NotificationRepo notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    /* ================= Création ================= */

    @Override
    public Notification createNotification(Notification notification) {
        if (notification == null)
            throw new IllegalArgumentException("Notification null");

        if (notification.getUtilisateur() == null ||
                notification.getUtilisateur().getId() == null)
            throw new IllegalArgumentException("Utilisateur obligatoire");

        return notificationRepo.save(notification);
    }

    /* ================= Consultation ================= */

    @Override
    public Notification getNotificationById(Long id) {
        return notificationRepo.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Notification introuvable : " + id));
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepo.findAll();
    }

    @Override
    public List<Notification> getNotificationsByUtilisateur(Long utilisateurId) {
        return notificationRepo.findByUtilisateur(utilisateurId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long utilisateurId) {
        return notificationRepo.findUnreadByUtilisateur(utilisateurId);
    }

    @Override
    public List<Notification> getNotificationsByDate(Long utilisateurId, LocalDate date) {
        return notificationRepo.findByDate(utilisateurId, date);
    }

    @Override
    public List<Notification> getNotificationsByType(Long utilisateurId, TypeNotification type) {
        return notificationRepo.findByType(utilisateurId, type);
    }

    @Override
    public List<Notification> getNotificationsByTitre(Long utilisateurId, TitreNotification titre) {
        return notificationRepo.findByTitre(utilisateurId, titre);
    }

    @Override
    public List<Notification> getNotificationsByPriorite(Long utilisateurId, PrioriteNotification priorite) {
        return notificationRepo.findByPriorite(utilisateurId, priorite);
    }

    /* ================= Actions ================= */

    @Override
    public void markNotificationAsRead(Long notificationId) {
        notificationRepo.markAsRead(notificationId);
    }

    @Override
    public void markAllNotificationsAsRead(Long utilisateurId) {
        notificationRepo.markAllAsReadForUser(utilisateurId);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepo.deleteById(notificationId);
    }
}
