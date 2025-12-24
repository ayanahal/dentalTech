package ma.dentalTech.entities.agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.users.Medecin;

/**
 * Entité représentant un rendez-vous.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RDV  extends BaseEntity {

    private LocalDate date;
    private LocalTime heure;
    private String motif;
    private StatutRDV statut;
    private String noteMedecin;

    private Long agendaMensuelId;

    private Patient patient;
    private Medecin medecin;
    private DossierMedical dossierMedical;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RDV)) return false;
        RDV that = (RDV) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public void setPatientId(long patientId) {

    }

    @Override
    public String getLibelle() {
        return "";
    }

    @Override
    public LocalDateTime getDateCharge() {
        return null;
    }

    @Override
    public String getCategorie() {
        return "";
    }

    @Override
    public String getModePaiement() {
        return "";
    }

    @Override
    public CabinetMedical getCabinet() {
        return null;
    }

    @Override
    public void setCreatedAt(LocalDateTime now) {

    }

    @Override
    public void setUpdatedAt(LocalDateTime now) {

    }

    @Override
    public void setPrenom(String test) {

    }

    @Override
    public void setNom(String nom) {

    }

    @Override
    public void setAdresse(String adresse) {

    }

    @Override
    public void setTel1(String tel1) {

    }

    @Override
    public void setEmail(String email) {

    }

    @Override
    public void setSiteWeb(String siteWeb) {

    }

    @Override
    public void setLogo(String logo) {

    }

    @Override
    public Object getNom() {
        return null;
    }

    @Override
    public Object getEmail() {
        return null;
    }

    @Override
    public Object getAdresse() {
        return null;
    }

    @Override
    public String toString() {
        return """
            RDV {
                id = %d,
                date = %s,
                heure = %s,
                motif = '%s',
                statut = %s
            }
            """.formatted(id, date, heure, motif, statut);
    }
}
