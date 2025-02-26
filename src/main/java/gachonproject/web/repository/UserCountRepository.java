package gachonproject.web.repository;


import gachonproject.web.domain.UserCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;


@Repository
public class UserCountRepository {

    @PersistenceContext
    private EntityManager em;




    public void Connection(UserCount userCount){

        em.persist(userCount);
    }

    public void Out(){
        UserCount userCount = em.createQuery("SELECT u FROM UserCount u", UserCount.class)
                .setMaxResults(1)
                .getSingleResult();

        em.remove(userCount);
    }


}
