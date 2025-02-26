package gachonproject.web.repository;


import gachonproject.web.domain.AnalysisActivity;
import gachonproject.web.domain.UserActivity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class AnalysisActivityRepository {

    @PersistenceContext
    private EntityManager em;

    public void saveAnalysisActivity(int Activity, LocalDate date){

        AnalysisActivity analysisActivity = new AnalysisActivity(Activity, date);
        em.persist(analysisActivity);

    }


    public List<AnalysisActivity> findAnalysisActivitiesLast30Days(){

        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        TypedQuery<AnalysisActivity> query = em.createQuery(
                "SELECT a FROM AnalysisActivity a WHERE a.date >= :thirtyDaysAgo ORDER BY a.date DESC",
                AnalysisActivity.class
        );
        query.setParameter("thirtyDaysAgo", thirtyDaysAgo);
        return query.getResultList();
    }


}
