package ma.dentalTech.service.modules.notifications.api;

import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.entities.enums.*;

import java.time.LocalDate;
import java.util.List;

public interface NotificationService {

    /* ===== Création ===== */

    Notification createNotification(Notification notification);

    /* ===== Consultation ===== */

    Notification getNotificationById(Long id);

    List<Notification> getAllNotifications();

    List<Notification> getNotificationsByUtilisateur(Long utilisateurId);

    List<Notification> getUnreadNotifications(Long utilisateurId);

    List<Notification> getNotificationsByDate(Long utilisateurId, LocalDate date);

    List<Notification> getNotificationsByType(Long utilisateurId, TypeNotification type);

    List<Notification> getNotificationsByTitre(Long utilisateurId, TitreNotification titre);

    List<Notification> getNotificationsByPriorite(Long utilisateurId, PrioriteNotification priorite);

    /* ===== Actions ===== */

    void markNotificationAsRead(Long notificationId);

    void markAllNotificationsAsRead(Long utilisateurId);

    void deleteNotification(Long notificationId);
}
