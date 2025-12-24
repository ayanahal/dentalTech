package ma.dentalTech.service.modules.agendas.api;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.StatutRDV;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface RDVService {

    // ========== CRUD Operations ==========
    RDV createRDV(RDV rdv);

    Optional<RDV> getRDVById(Long id);

    List<RDV> getAllRDVs();

    RDV updateRDV(Long id, RDV rdv);

    void deleteRDV(Long id);

    // ========== Search Operations ==========
    List<RDV> getRDVsByMedecinAndDate(Long medecinId, LocalDate date);

    List<RDV> getRDVsByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end);

    List<RDV> getRDVsByPatient(Long patientId);

    List<RDV> getRDVsByAgenda(Long agendaId);

    // ========== Status Management ==========
    RDV updateRDVStatus(Long rdvId, StatutRDV newStatus);

    List<RDV> getRDVsByStatus(StatutRDV status);

    List<RDV> getRDVsByMedecinAndStatus(Long medecinId, StatutRDV status);

    // ========== Planning & Availability ==========
    boolean isTimeSlotAvailable(Long medecinId, LocalDate date, LocalTime heure);

    List<LocalTime> getAvailableTimeSlots(Long medecinId, LocalDate date);

    List<RDV> getTodaysRDVsByMedecin(Long medecinId);

    List<RDV> getUpcomingRDVsByMedecin(Long medecinId, int daysAhead);

    // ========== Business Logic ==========
    void cancelRDV(Long rdvId, String reason);

    void confirmRDV(Long rdvId);

    void rescheduleRDV(Long rdvId, LocalDate newDate, LocalTime newHeure);

    // ========== Statistics ==========
    long countRDVsByMedecinAndDate(Long medecinId, LocalDate date);

    long countRDVsByPatient(Long patientId);

    long countRDVsByStatus(StatutRDV status);

    // ========== Notifications & Reminders ==========
    List<RDV> getRDVsRequiringReminder();

    List<RDV> getMissedRDVs();
}
