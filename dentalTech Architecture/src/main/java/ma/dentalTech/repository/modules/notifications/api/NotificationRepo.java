package ma.dentalTech.repository.modules.notifications.api;

import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.entities.enums.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NotificationRepo {

    /* ===== CRUD ===== */

    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    List<Notification> findAll();

    void deleteById(Long id);

    /* ===== Cas d’utilisation (diagrammes UML) ===== */

    List<Notification> findByUtilisateur(Long utilisateurId);

    List<Notification> findUnreadByUtilisateur(Long utilisateurId);

    List<Notification> findByDate(Long utilisateurId, LocalDate date);

    List<Notification> findByType(Long utilisateurId, TypeNotification type);

    List<Notification> findByTitre(Long utilisateurId, TitreNotification titre);

    List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite);

    /* ===== Actions ===== */

    void markAsRead(Long notificationId);

    void markAllAsReadForUser(Long utilisateurId);
}
