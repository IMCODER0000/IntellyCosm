package gachonproject.mobile.repository;


import gachonproject.mobile.domain.analysis.Analysis;
import gachonproject.mobile.domain.comparison.ComparisonAnalysis;
import gachonproject.mobile.domain.comparison.ComparisonAnalysisMaping;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ComparisonAnalysisRepository {


    @PersistenceContext
    private EntityManager em;


    public void createComparisonAnalysis(ComparisonAnalysis comparisonAnalysis) {

        em.persist(comparisonAnalysis);
    }

    public void createComparisonAnalysisMaping(ComparisonAnalysisMaping comparisonAnalysisMaping) {
        em.persist(comparisonAnalysisMaping);
    }


    public ComparisonAnalysis findAnalysisByIds(Long comparisonAnalysis_id) {

         return em.createQuery("select c from ComparisonAnalysis c where c.id = :comparisonAnalysis_id" ,ComparisonAnalysis.class)
                .setParameter("comparisonAnalysis_id", comparisonAnalysis_id)
                .getSingleResult();




    }
    public List<ComparisonAnalysisMaping> findComparisonAnalysisMaping(Long comparisonAnalysis_id) {
        return em.createQuery("select c from ComparisonAnalysisMaping c where c.comparisonAnalysis.id = :comparisonAnalysis_id")
                .setParameter("comparisonAnalysis_id", comparisonAnalysis_id)
                .getResultList();
    }


}
