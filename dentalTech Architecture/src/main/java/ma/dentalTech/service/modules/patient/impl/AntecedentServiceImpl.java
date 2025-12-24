package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.service.modules.patient.api.AntecedentService;

import java.util.List;
import java.util.Optional;

public class AntecedentServiceImpl implements AntecedentService {

    private final AntecedentRepository antecedentRepository;

    public AntecedentServiceImpl(AntecedentRepository antecedentRepository) {
        this.antecedentRepository = antecedentRepository;
    }

    @Override
    public List<Antecedent> getAllAntecedents() {
        return antecedentRepository.findAll();
    }

    @Override
    public Optional<Optional<Statistiques>> getAntecedentById(Long id) {
        return Optional.ofNullable(antecedentRepository.findById(id));
    }

    @Override
    public void createAntecedent(Antecedent antecedent) {
        antecedentRepository.create(antecedent);
    }

    @Override
    public void updateAntecedent(Antecedent antecedent) {
        antecedentRepository.update(antecedent);
    }

    @Override
    public void deleteAntecedent(Antecedent antecedent) {
        if (antecedent != null) antecedentRepository.delete(antecedent);
    }

    @Override
    public Optional<Antecedent> getAntecedentByNom(String nom) {
        return antecedentRepository.findByNom(nom);
    }

    @Override
    public List<Antecedent> getAntecedentsByCategorie(CategorieAntecedent categorie) {
        return antecedentRepository.findByCategorie(categorie);
    }

    @Override
    public List<Antecedent> getAntecedentsByNiveauRisque(NiveauRisque niveau) {
        return antecedentRepository.findByNiveauRisque(niveau);
    }

    @Override
    public List<Antecedent> getAntecedentsPage(int limit, int offset) {
        return antecedentRepository.findPage(limit, offset);
    }

    @Override
    public long countAntecedents() {
        return antecedentRepository.count();
    }

    @Override
    public List<Patient> getPatientsHavingAntecedent(Long antecedentId) {
        return antecedentRepository.getPatientsHavingAntecedent(antecedentId);
    }
}
