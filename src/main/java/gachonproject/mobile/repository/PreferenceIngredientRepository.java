package gachonproject.mobile.repository;


import gachonproject.mobile.domain.preferedIngredient.PreferedIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PreferenceIngredientRepository {

    @PersistenceContext
    private EntityManager em;

    public boolean deletePreference(List<PreferedIngredient> preferedIngredients){
        try {
            for(PreferedIngredient preferedIngredient : preferedIngredients){
                em.remove(preferedIngredient);

            }
            return true;

        }catch(Exception e) {
            return false;
        }

    }
    public boolean createPreference(List<PreferedIngredient> preferedIngredients){
        try {
            for(PreferedIngredient preferedIngredient : preferedIngredients){
                em.persist(preferedIngredient);

            }
            return true;

        }catch(Exception e) {
            return false;
        }

    }



    public List<PreferedIngredient> findAll(Long member_id){
        return em.createQuery("SELECT p FROM PreferedIngredient p WHERE p.member.id = : member_id", PreferedIngredient.class)
                .setParameter("member_id", member_id)
                .getResultList();
    }


}
