package gachonproject.mobile.repository;


import gachonproject.mobile.domain.analysis.Analysis;
import gachonproject.mobile.domain.analysis.AnalysisCosmeticRegistration;
import gachonproject.mobile.domain.analysis.AnalysisIngredient;
import gachonproject.mobile.domain.ingredient.Ingredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class AnalysisRepository {

    @PersistenceContext
    EntityManager em;


    public Analysis findById(Long id) {
        return em.find(Analysis.class, id);
    }


    public List<Ingredient> findByNameIn(List<String> ingredientNames) {
        try {
            List<Ingredient> analysisIngredients = new ArrayList<>();
            for (String name : ingredientNames) {
                List<Ingredient> ingredients = em.createQuery("SELECT i FROM Ingredient i WHERE i.name LIKE :name", Ingredient.class)
                        .setParameter("name", "%" + name + "%")
                        .getResultList();
                analysisIngredients.addAll(ingredients);
            }
            return analysisIngredients;
        } catch (Exception e) {
            throw e;
        }
    }


    public AnalysisIngredient createAnalysisIngredient(AnalysisIngredient analysisIngredient) {

        em.persist(analysisIngredient);
        return analysisIngredient;
    }


    public void createAnalysis(Analysis analysis){
        em.persist(analysis);
    }

    public void updateAnalysis(Analysis analysis){
        em.merge(analysis);
    }

    public List<Analysis> findAllNoRegistration() {
        return em.createQuery("SELECT a FROM Analysis a WHERE a.registration_status = :isStatus", Analysis.class)
                .setParameter("isStatus", Boolean.FALSE)
                .getResultList();
    }

    public List<AnalysisCosmeticRegistration> findAllCosmeticRegistration() {
        return em.createQuery("SELECT a FROM AnalysisCosmeticRegistration a", AnalysisCosmeticRegistration.class)
                .getResultList();
    }

    public List<Analysis> findAll1() {
        return em.createQuery("SELECT a FROM Analysis a", Analysis.class)
                .getResultList();
    }

    public List<Analysis> findAll() {
        return em.createQuery("SELECT new gachonproject.web.dto.CosmeticAddDTO", Analysis.class)
                .getResultList();
    }

    public void createAnalysisCosmeticRegistration(AnalysisCosmeticRegistration analysisCosmeticRegistration){

        em.persist(analysisCosmeticRegistration);
    }
    public void deleteAnalysisCosmeticRegistration(AnalysisCosmeticRegistration analysisCosmeticRegistration){

        em.remove(analysisCosmeticRegistration);
    }

    public void updateAnalysisCosmeticRegistration(AnalysisCosmeticRegistration analysisCosmeticRegistration){
        em.merge(analysisCosmeticRegistration);
    }

    public AnalysisCosmeticRegistration findAnalysisCosmeticRegistrationByAnalysisId(Long analysis_id){
        return em.createQuery("SELECT a FROM AnalysisCosmeticRegistration a WHERE a.analysis_id = :analysis_id", AnalysisCosmeticRegistration.class)
                .setParameter("analysis_id", analysis_id)
                .getSingleResult();
    }

    public List<AnalysisCosmeticRegistration> analysisCosmeticRegistrationsFindAll(){
        return em.createQuery("SELECT a FROM AnalysisCosmeticRegistration a", AnalysisCosmeticRegistration.class)
                .getResultList();
    }

    public AnalysisCosmeticRegistration findAnalysisCosmeticRegistrationById(Long analysis_cosmetic_registration_id){
        return em.find(AnalysisCosmeticRegistration.class, analysis_cosmetic_registration_id);
    }

    public List<AnalysisCosmeticRegistration> findAnalysisCosmeticRegistrationByAnalysisName(String name){
        return em.createQuery("SELECT a FROM AnalysisCosmeticRegistration a WHERE a.cosmetic_name = :name", AnalysisCosmeticRegistration.class)
                .setParameter("name", name)
                .getResultList();
    }

    public void deleteAnalysisCosmeticRegistrationById(Long id){
        em.remove(em.find(AnalysisCosmeticRegistration.class, id));
    }






}



