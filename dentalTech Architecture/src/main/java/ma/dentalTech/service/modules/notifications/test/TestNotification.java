package ma.dentalTech.service.modules.notifications.test;

import ma.dentalTech.entities.users.*;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.repository.modules.notifications.api.NotificationRepo;
import ma.dentalTech.service.modules.notifications.api.NotificationService;
import ma.dentalTech.service.modules.notifications.impl.NotificationServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TestNotification {

    public static void main(String[] args) {

        /* ================= Fake Repository ================= */
        NotificationRepo repo = new NotificationRepo() {

            private final Map<Long, Notification> store = new HashMap<>();
            private long seq = 1;

            @Override
            public Notification save(Notification n) {
                n.setId(seq++);
                store.put(n.getId(), n);
                return n;
            }

            @Override
            public Optional<Notification> findById(Long id) {
                return Optional.ofNullable(store.get(id));
            }

            @Override
            public List<Notification> findAll() {
                return new ArrayList<>(store.values());
            }

            @Override
            public void deleteById(Long id) {
                store.remove(id);
            }

            @Override
            public List<Notification> findByUtilisateur(Long utilisateurId) {
                return store.values().stream()
                        .filter(n -> n.getUtilisateur().getId().equals(utilisateurId))
                        .toList();
            }

            @Override
            public List<Notification> findUnreadByUtilisateur(Long utilisateurId) {
                return store.values().stream()
                        .filter(n -> !n.isLue())
                        .filter(n -> n.getUtilisateur().getId().equals(utilisateurId))
                        .toList();
            }

            @Override
            public List<Notification> findByDate(Long utilisateurId, LocalDate date) {
                return store.values().stream()
                        .filter(n -> n.getUtilisateur().getId().equals(utilisateurId))
                        .filter(n -> n.getDate().equals(date))
                        .toList();
            }

            @Override
            public List<Notification> findByType(Long utilisateurId, TypeNotification type) {
                return store.values().stream()
                        .filter(n -> n.getUtilisateur().getId().equals(utilisateurId))
                        .filter(n -> n.getType() == type)
                        .toList();
            }

            @Override
            public List<Notification> findByTitre(Long utilisateurId, TitreNotification titre) {
                return store.values().stream()
                        .filter(n -> n.getUtilisateur().getId().equals(utilisateurId))
                        .filter(n -> n.getTitre() == titre)
                        .toList();
            }

            @Override
            public List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite) {
                return store.values().stream()
                        .filter(n -> n.getUtilisateur().getId().equals(utilisateurId))
                        .filter(n -> n.getPriorite() == priorite)
                        .toList();
            }

            @Override
            public void markAsRead(Long notificationId) {
                store.get(notificationId).setLue(true);
            }

            @Override
            public void markAllAsReadForUser(Long utilisateurId) {
                store.values().forEach(n -> {
                    if (n.getUtilisateur().getId().equals(utilisateurId)) {
                        n.setLue(true);
                    }
                });
            }
        };

        /* ================= Service ================= */
        NotificationService service = new NotificationServiceImpl(repo);

        Utilisateur u = new Utilisateur();
        u.setId(1L);

        Notification n = Notification.builder()
                .titre(TitreNotification.INFO)
                .message("Bienvenue dans DentalTech")
                .date(LocalDate.now())
                .time(LocalTime.now())
                .type(TypeNotification.MESSAGE_SYSTEME)
                .priorite(PrioriteNotification.NORMALE)
                .lue(false)
                .utilisateur(u)
                .build();

        service.createNotification(n);

        System.out.println("Notifications utilisateur : " +
                service.getNotificationsByUtilisateur(1L).size());

        service.markNotificationAsRead(n.getId());

        System.out.println("Notifications non lues : " +
                service.getUnreadNotifications(1L).size());

        System.out.println("TEST NOTIFICATION OK");
    }
}
