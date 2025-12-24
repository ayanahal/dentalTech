package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.util.List;
import java.util.Optional;

public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Optional<Statistiques>> getPatientById(Long id) {
        return Optional.ofNullable(patientRepository.findById(id));
    }

    @Override
    public void createPatient(Patient patient) {
        patientRepository.create(patient);
        if (patient.getAntecedents() != null) {
            patientRepository.syncAntecedentsForPatient(patient);
        }
    }

    @Override
    public void updatePatient(Patient patient) {
        patientRepository.update(patient);
        if (patient.getAntecedents() != null) {
            patientRepository.syncAntecedentsForPatient(patient);
        }
    }

    @Override
    public void deletePatient(Patient patient) {
        if (patient != null) patientRepository.delete(patient);
    }

    @Override
    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    @Override
    public Optional<Patient> getPatientByTelephone(String telephone) {
        return patientRepository.findByTelephone(telephone);
    }

    @Override
    public List<Patient> searchPatientsByNomPrenom(String keyword) {
        return patientRepository.searchByNomPrenom(keyword);
    }

    @Override
    public List<Patient> searchPatientsByTelephonePrefix(String prefix) {
        return patientRepository.searchByTelephoneLike(prefix);
    }

    @Override
    public List<Patient> getPatientsBySexe(Sexe sexe) {
        return patientRepository.findBySexe(sexe);
    }

    @Override
    public List<Patient> getPatientsByAssurance(Assurance assurance) {
        return patientRepository.findByAssurance(assurance);
    }

    @Override
    public List<Patient> getPatientsPage(int limit, int offset) {
        return patientRepository.findPage(limit, offset);
    }

    @Override
    public long countPatients() {
        return patientRepository.count();
    }

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        patientRepository.addAntecedentToPatient(patientId, antecedentId);
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        patientRepository.removeAntecedentFromPatient(patientId, antecedentId);
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        patientRepository.removeAllAntecedentsFromPatient(patientId);
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        return patientRepository.getAntecedentsOfPatient(patientId);
    }

    @Override
    public void syncAntecedents(Patient patient) {
        patientRepository.syncAntecedentsForPatient(patient);
    }
}

