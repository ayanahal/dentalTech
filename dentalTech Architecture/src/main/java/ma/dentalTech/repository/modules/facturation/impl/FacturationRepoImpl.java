package ma.dentalTech.repository.modules.facturation.impl;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.repository.modules.facturation.api.FacturationRepo;

import java.sql.*;
import java.util.*;

public class FacturationRepoImpl implements FacturationRepo {

    private final Connection connection;

    public FacturationRepoImpl(Connection connection) {
        this.connection = connection;
    }

    /* ================= CRUD ================= */

    @Override
    public Facture save(Facture facture) {
        String sql = """
            INSERT INTO facture
            (totale_facture, totale_paye, reste, statut, date_facture,
             situation_financiere_id, cree_par)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            facture.calculerReste();

            ps.setDouble(1, facture.getTotaleFacture());
            ps.setDouble(2, facture.getTotalePaye());
            ps.setDouble(3, facture.getReste());
            ps.setString(4, facture.getStatut().name());
            ps.setTimestamp(5, Timestamp.valueOf(facture.getDateFacture()));
            ps.setLong(6, facture.getSituationFinanciere().getId());
            ps.setString(7, facture.getCreePar());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                facture.setId(rs.getLong(1));
            }

            return facture;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save Facture", e);
        }
    }

    @Override
    public Optional<Facture> findById(Long id) {
        String sql = "SELECT * FROM facture WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById Facture", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Facture> findAll() {
        String sql = "SELECT * FROM facture";
        List<Facture> factures = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                factures.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Facture", e);
        }

        return factures;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM facture WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete Facture", e);
        }
    }

    /* ================= Recherches métier ================= */

    @Override
    public List<Facture> findBySituationFinanciere(Long situationFinanciereId) {
        return findBy("situation_financiere_id", situationFinanciereId);
    }

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        return findBy("statut", statut.name());
    }

    @Override
    public List<Facture> findImpaye() {
        return findBy("statut", StatutFacture.IMPAYEE.name());
    }

    @Override
    public void updateStatut(Long factureId, StatutFacture statut) {
        String sql = "UPDATE facture SET statut = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setLong(2, factureId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur updateStatut Facture", e);
        }
    }

    @Override
    public void updateMontantPaye(Long factureId, Double montantPaye) {
        String sql = """
            UPDATE facture
            SET totale_paye = ?, reste = totale_facture - ?
            WHERE id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, montantPaye);
            ps.setDouble(2, montantPaye);
            ps.setLong(3, factureId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur updateMontantPaye Facture", e);
        }
    }

    /* ================= Helpers ================= */

    private List<Facture> findBy(String column, Object value) {
        String sql = "SELECT * FROM facture WHERE " + column + " = ?";
        List<Facture> factures = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, value);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                factures.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findBy Facture", e);
        }

        return factures;
    }

    private Facture map(ResultSet rs) throws SQLException {

        Facture f = new Facture();

        /* ----- BaseEntity ----- */
        f.setId(rs.getLong("id"));
        f.setDateCreation(rs.getDate("date_creation").toLocalDate());
        f.setDateDerniereModification(
                rs.getTimestamp("date_derniere_modification").toLocalDateTime()
        );
        f.setCreePar(rs.getString("cree_par"));
        f.setModifiePar(rs.getString("modifie_par"));

        /* ----- Facture ----- */
        f.setTotaleFacture(rs.getDouble("totale_facture"));
        f.setTotalePaye(rs.getDouble("totale_paye"));
        f.setReste(rs.getDouble("reste"));
        f.setStatut(StatutFacture.valueOf(rs.getString("statut")));
        f.setDateFacture(rs.getTimestamp("date_facture").toLocalDateTime());

        return f;
    }

}
