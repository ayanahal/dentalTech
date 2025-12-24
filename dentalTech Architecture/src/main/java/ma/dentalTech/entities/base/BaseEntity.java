package ma.dentalTech.entities.base;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.RoleType;

/**
 * Classe de base pour toutes les entités du système.
 * Contient les champs d'audit communs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    protected Long id;
    protected LocalDate dateCreation;
    protected LocalDateTime dateDerniereModification;
    protected String modifiePar;
    protected String creePar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public abstract void setPatientId(long patientId);

    public void setStatut(String statut) {
    }

    public void setDossierMedicalId(long dossierMedicalId) {
    }

    public void setMedecinId(long medecinId) {
    }

    public void setForme(String forme) {
    }

    public void setOrdonnanceId(long ordonnanceId) {
    }

    public void setMedicamentId(long medicamentId) {
    }

    public void setConsultation(Consultation c) {

    }

    public abstract String getLibelle();

    public abstract LocalDateTime getDateCharge();

    public abstract String getCategorie();

    public abstract String getModePaiement();

    public abstract CabinetMedical getCabinet();

    public abstract void setCreatedAt(LocalDateTime now);

    public abstract void setUpdatedAt(LocalDateTime now);

    public abstract void setPrenom(String test);

    public abstract void setNom(String nom);

    public abstract void setAdresse(String adresse);

    public abstract void setTel1(String tel1);

    public abstract void setEmail(String email);

    public abstract void setSiteWeb(String siteWeb);

    public abstract void setLogo(String logo);

    public abstract Object getNom();

    public abstract Object getEmail();

    public abstract Object getAdresse();

    public abstract void setTelephone(String telephone);

    public abstract void setPoste(String poste);

    public abstract void setDateEmbauche(LocalDate localDate);

    public abstract void setRole(RoleType roleType);

    public abstract void setMontantRestant(double montantRestant);

    public abstract void setNiveauAcces(int niveauAcces);

    public abstract void setNumeroOrdre(String numeroOrdre);

    public abstract void setDepartement(String departement);

    public abstract void setDescription(String description);

    public abstract void setPermissions(List<String> list);

    public abstract void setNumero(String numero);

    public abstract void setMontantTotal(double montantTotal);

    public abstract void setDateEcheance(LocalDate localDate);

    public abstract void setMontantPaye(double montantPaye);

    public abstract void setTitre(String titre);

    public abstract <E extends Enum<E>> Enum<E> getMois();

    public abstract int getAnnee();

    public abstract Long getMedecinId();

    public abstract boolean isEmpty();

    public abstract AgendaMensuel get();

    public abstract boolean isPresent();
}
