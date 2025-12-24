package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;

import java.util.List;
import java.util.Optional;

public interface PatientService {

    // CRUD Patient
    List<Patient> getAllPatients();
    Optional<Optional<Statistiques>> getPatientById(Long id);
    void createPatient(Patient patient);
    void updatePatient(Patient patient);
    void deletePatient(Patient patient);

    // Recherches
    Optional<Patient> getPatientByEmail(String email);
    Optional<Patient> getPatientByTelephone(String telephone);
    List<Patient> searchPatientsByNomPrenom(String keyword);
    List<Patient> searchPatientsByTelephonePrefix(String prefix);
    List<Patient> getPatientsBySexe(Sexe sexe);
    List<Patient> getPatientsByAssurance(Assurance assurance);

    // Pagination
    List<Patient> getPatientsPage(int limit, int offset);
    long countPatients();

    // Gestion des antécédents
    void addAntecedentToPatient(Long patientId, Long antecedentId);
    void removeAntecedentFromPatient(Long patientId, Long antecedentId);
    void removeAllAntecedentsFromPatient(Long patientId);
    List<Antecedent> getAntecedentsOfPatient(Long patientId);
    void syncAntecedents(Patient patient);
}

