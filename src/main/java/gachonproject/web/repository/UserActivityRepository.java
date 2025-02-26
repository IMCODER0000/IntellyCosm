package gachonproject.web.repository;


import gachonproject.web.domain.UserActivity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class UserActivityRepository {


    @PersistenceContext
    private EntityManager em;


    public void save(UserActivity userActivity) {
        em.persist(userActivity);
    }

    public List<UserActivity> findUserActivities() {

        return em.createQuery("select u from UserActivity u", UserActivity.class).getResultList();

    }

    public List<UserActivity> findUserActivitiesLast30Days() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        TypedQuery<UserActivity> query = em.createQuery(
                "SELECT u FROM UserActivity u WHERE u.date >= :thirtyDaysAgo ORDER BY u.date DESC",
                UserActivity.class
        );
        query.setParameter("thirtyDaysAgo", thirtyDaysAgo);
        System.out.println("00000000000-0000000000000");
        System.out.println(query.getResultList());
        System.out.println("00000000000-0000000000000");
        return query.getResultList();
    }


}
