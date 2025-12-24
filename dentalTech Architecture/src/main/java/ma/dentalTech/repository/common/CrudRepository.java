

package ma.dentalTech.repository.common;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;

import java.util.List;
import java.util.Optional;


public interface CrudRepository<T, ID> {


    List<T> findAll();

    Optional<AgendaMensuel> findById(ID id);

    void create(T newElement);

    CabinetMedical update(T newValuesElement);

    void delete(T patient);

    void deleteById(ID id);

    void syncAntecedentsForPatient(Patient patient);

    List<Patient> searchByTelephoneLike(String prefix);

    List<Patient> findBySexe(Sexe sexe);

    List<Patient> findByAssurance(Assurance assurance);

    CabinetMedical save(CabinetMedical cabinet);

    boolean existsById(Long id);

    AgendaMensuel save(AgendaMensuel toUpdate);

    CabinetMedical save(RDV rdv);

    Statistiques save(Statistiques statistique);
}



