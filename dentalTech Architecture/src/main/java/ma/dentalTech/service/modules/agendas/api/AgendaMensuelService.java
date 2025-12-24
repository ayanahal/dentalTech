package ma.dentalTech.service.modules.agendas.api;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.patient.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendaMensuelService {

    // ========== CRUD Operations ==========
    CabinetMedical createAgenda(CabinetMedical agenda);

    Patient getAgendaById(Long id);

    List<AgendaMensuel> getAllAgendas();

    AgendaMensuel updateAgenda(Long id, AgendaMensuel agenda);

    void deleteAgenda(Long id);

    // ========== Search Operations ==========
    Optional<AgendaMensuel> getAgendaByMedecinAndMoisAndAnnee(Long medecinId, Mois mois, int annee);

    List<AgendaMensuel> getAgendasByMedecin(Long medecinId);

    // ========== Jours Non Disponibles Management ==========
    List<LocalDate> getJoursNonDisponibles(Long agendaId);

    void addJourNonDisponible(Long agendaId, LocalDate date);

    void removeJourNonDisponible(Long agendaId, LocalDate date);

    void clearJoursNonDisponibles(Long agendaId);

    // ========== Validation & Business Logic ==========
    boolean isDateDisponible(Long agendaId, LocalDate date);

    boolean agendaExistsForMedecinAndMois(Long medecinId, Mois mois, int annee);

    // ========== Statistics ==========
    long countAgendasByMedecin(Long medecinId);

    List<AgendaMensuel> getAgendasByMedecinAndYear(Long medecinId, int annee);

    AgendaMensuel createAgenda(AgendaMensuel agenda);
}
