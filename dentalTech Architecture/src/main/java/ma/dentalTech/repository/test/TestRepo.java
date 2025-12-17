package ma.dentalTech.repository.test;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;

public class TestRepo {
    public static void main(String[] args) {

        // Créer le repository
        DossierMedicalRepositoryImpl dossierRepo = new DossierMedicalRepositoryImpl();

        // Créer un dossier médical de test
        DossierMedical dossier = new DossierMedical();
        dossier.setIdPatient(1L); // Assure-toi que ce patient existe dans la DB
        dossier.setObservation("Test simple");

        // Sauvegarder
        dossierRepo.save(dossier);
        System.out.println("✅ Dossier médical sauvegardé avec succès");

        // Lire tous les dossiers et afficher le nombre
        System.out.println("Nombre de dossiers dans la DB : " + dossierRepo.findAll().size());
    }
}

