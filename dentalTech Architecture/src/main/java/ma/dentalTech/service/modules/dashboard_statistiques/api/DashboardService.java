package ma.dentalTech.service.modules.dashboard_statistiques.api;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DashboardService {

    // ========== CRUD Opérations ==========
    Statistiques createStatistique(Statistiques statistique);
    AgendaMensuel getStatistiqueById(Long id);
    List<Statistiques> getAllStatistiques();
    Statistiques updateStatistique(Long id, Statistiques statistique);
    void deleteStatistique(Long id);

    // ========== Recherches ==========
    List<Statistiques> getStatistiquesByCabinet(Long cabinetId);
    List<Statistiques> getStatistiquesByCategorie(CategorieStatistique categorie);
    List<Statistiques> getStatistiquesByCabinetAndCategorie(Long cabinetId, CategorieStatistique categorie);
    List<Statistiques> getStatistiquesByDate(LocalDate date);

    // ========== Vérifications ==========
    boolean statistiqueExistsById(Long id);
    long countStatistiques();

    // ========== Calculs en temps réel ==========
    Double calculateRevenusPeriod(LocalDate dateDebut, LocalDate dateFin);
    Double calculateChargesPeriod(LocalDate dateDebut, LocalDate dateFin);
    Double calculateBeneficeNetPeriod(LocalDate dateDebut, LocalDate dateFin);
    Long countConsultationsPeriod(LocalDate dateDebut, LocalDate dateFin);
    Long countNewPatientsPeriod(LocalDate dateDebut, LocalDate dateFin);

    // ========== Tableau de bord ==========
    Map<String, Object> getDashboardData(Long cabinetId, LocalDate dateDebut, LocalDate dateFin);
    Map<String, Double> getMonthlyStats(int year, Long cabinetId);
    Map<String, Long> getTopActes(LocalDate dateDebut, LocalDate dateFin, Long cabinetId);
    Map<String, Long> getTopMedecins(LocalDate dateDebut, LocalDate dateFin, Long cabinetId);

    // ========== Statistiques avancées ==========
    Double getLatestStatistiqueByCabinetAndCategorie(Long cabinetId, CategorieStatistique categorie);
    List<Statistiques> getLatestStatistiquesByCabinet(Long cabinetId, int limit);
}
