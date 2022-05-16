/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minesweeperworking;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import minesweeperworking.exceptions.NonexistentEntityException;

/**
 *
 * @author itamar
 */
public class StepsJpaController implements Serializable {

    public StepsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Steps steps) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Games gameID = steps.getGameID();
            if (gameID != null) {
                gameID = em.getReference(gameID.getClass(), gameID.getId());
                steps.setGameID(gameID);
            }
            em.persist(steps);
            if (gameID != null) {
                gameID.getStepsCollection().add(steps);
                gameID = em.merge(gameID);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Steps steps) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Steps persistentSteps = em.find(Steps.class, steps.getId());
            Games gameIDOld = persistentSteps.getGameID();
            Games gameIDNew = steps.getGameID();
            if (gameIDNew != null) {
                gameIDNew = em.getReference(gameIDNew.getClass(), gameIDNew.getId());
                steps.setGameID(gameIDNew);
            }
            steps = em.merge(steps);
            if (gameIDOld != null && !gameIDOld.equals(gameIDNew)) {
                gameIDOld.getStepsCollection().remove(steps);
                gameIDOld = em.merge(gameIDOld);
            }
            if (gameIDNew != null && !gameIDNew.equals(gameIDOld)) {
                gameIDNew.getStepsCollection().add(steps);
                gameIDNew = em.merge(gameIDNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = steps.getId();
                if (findSteps(id) == null) {
                    throw new NonexistentEntityException("The steps with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Steps steps;
            try {
                steps = em.getReference(Steps.class, id);
                steps.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The steps with id " + id + " no longer exists.", enfe);
            }
            Games gameID = steps.getGameID();
            if (gameID != null) {
                gameID.getStepsCollection().remove(steps);
                gameID = em.merge(gameID);
            }
            em.remove(steps);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Steps> findStepsEntities() {
        return findStepsEntities(true, -1, -1);
    }

    public List<Steps> findStepsEntities(int maxResults, int firstResult) {
        return findStepsEntities(false, maxResults, firstResult);
    }

    private List<Steps> findStepsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Steps.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Steps findSteps(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Steps.class, id);
        } finally {
            em.close();
        }
    }

    public int getStepsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Steps> rt = cq.from(Steps.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
