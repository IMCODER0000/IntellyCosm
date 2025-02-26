package gachonproject.web.repository;


import gachonproject.web.domain.Admin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class AdminRepository {

    @PersistenceContext
    private EntityManager em;

    public Admin findByEmail(String email) {
        try {
            return em.createQuery("SELECT a FROM Admin a WHERE a.email = :email", Admin.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            // 관리자를 찾지 못한 경우
            return null;
        }
    }

    public void update(Admin admin) {
        em.merge(admin);
    }


    public void signUp(Admin admin) {
        em.persist(admin);
    }

    public Admin findById(Long admin_id){
        return em.find(Admin.class, admin_id);
    }



}
