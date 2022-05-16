/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minesweeperworking;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import minesweeperworking.exceptions.IllegalOrphanException;
import minesweeperworking.exceptions.NonexistentEntityException;

/**
 *
 * @author itamar
 */
public class GamesJpaController implements Serializable {

    public GamesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Games games) {
        if (games.getStepsCollection() == null) {
            games.setStepsCollection(new ArrayList<Steps>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Board boardID = games.getBoardID();
            if (boardID != null) {
                boardID = em.getReference(boardID.getClass(), boardID.getId());
                games.setBoardID(boardID);
            }
            Users userID = games.getUserID();
            if (userID != null) {
                userID = em.getReference(userID.getClass(), userID.getId());
                games.setUserID(userID);
            }
            Collection<Steps> attachedStepsCollection = new ArrayList<Steps>();
            for (Steps stepsCollectionStepsToAttach : games.getStepsCollection()) {
                stepsCollectionStepsToAttach = em.getReference(stepsCollectionStepsToAttach.getClass(), stepsCollectionStepsToAttach.getId());
                attachedStepsCollection.add(stepsCollectionStepsToAttach);
            }
            games.setStepsCollection(attachedStepsCollection);
            em.persist(games);
            if (boardID != null) {
                boardID.getGamesCollection().add(games);
                boardID = em.merge(boardID);
            }
            if (userID != null) {
                userID.getGamesCollection().add(games);
                userID = em.merge(userID);
            }
            for (Steps stepsCollectionSteps : games.getStepsCollection()) {
                Games oldGameIDOfStepsCollectionSteps = stepsCollectionSteps.getGameID();
                stepsCollectionSteps.setGameID(games);
                stepsCollectionSteps = em.merge(stepsCollectionSteps);
                if (oldGameIDOfStepsCollectionSteps != null) {
                    oldGameIDOfStepsCollectionSteps.getStepsCollection().remove(stepsCollectionSteps);
                    oldGameIDOfStepsCollectionSteps = em.merge(oldGameIDOfStepsCollectionSteps);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Games games) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Games persistentGames = em.find(Games.class, games.getId());
            Board boardIDOld = persistentGames.getBoardID();
            Board boardIDNew = games.getBoardID();
            Users userIDOld = persistentGames.getUserID();
            Users userIDNew = games.getUserID();
            Collection<Steps> stepsCollectionOld = persistentGames.getStepsCollection();
            Collection<Steps> stepsCollectionNew = games.getStepsCollection();
            List<String> illegalOrphanMessages = null;
            for (Steps stepsCollectionOldSteps : stepsCollectionOld) {
                if (!stepsCollectionNew.contains(stepsCollectionOldSteps)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Steps " + stepsCollectionOldSteps + " since its gameID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (boardIDNew != null) {
                boardIDNew = em.getReference(boardIDNew.getClass(), boardIDNew.getId());
                games.setBoardID(boardIDNew);
            }
            if (userIDNew != null) {
                userIDNew = em.getReference(userIDNew.getClass(), userIDNew.getId());
                games.setUserID(userIDNew);
            }
            Collection<Steps> attachedStepsCollectionNew = new ArrayList<Steps>();
            for (Steps stepsCollectionNewStepsToAttach : stepsCollectionNew) {
                stepsCollectionNewStepsToAttach = em.getReference(stepsCollectionNewStepsToAttach.getClass(), stepsCollectionNewStepsToAttach.getId());
                attachedStepsCollectionNew.add(stepsCollectionNewStepsToAttach);
            }
            stepsCollectionNew = attachedStepsCollectionNew;
            games.setStepsCollection(stepsCollectionNew);
            games = em.merge(games);
            if (boardIDOld != null && !boardIDOld.equals(boardIDNew)) {
                boardIDOld.getGamesCollection().remove(games);
                boardIDOld = em.merge(boardIDOld);
            }
            if (boardIDNew != null && !boardIDNew.equals(boardIDOld)) {
                boardIDNew.getGamesCollection().add(games);
                boardIDNew = em.merge(boardIDNew);
            }
            if (userIDOld != null && !userIDOld.equals(userIDNew)) {
                userIDOld.getGamesCollection().remove(games);
                userIDOld = em.merge(userIDOld);
            }
            if (userIDNew != null && !userIDNew.equals(userIDOld)) {
                userIDNew.getGamesCollection().add(games);
                userIDNew = em.merge(userIDNew);
            }
            for (Steps stepsCollectionNewSteps : stepsCollectionNew) {
                if (!stepsCollectionOld.contains(stepsCollectionNewSteps)) {
                    Games oldGameIDOfStepsCollectionNewSteps = stepsCollectionNewSteps.getGameID();
                    stepsCollectionNewSteps.setGameID(games);
                    stepsCollectionNewSteps = em.merge(stepsCollectionNewSteps);
                    if (oldGameIDOfStepsCollectionNewSteps != null && !oldGameIDOfStepsCollectionNewSteps.equals(games)) {
                        oldGameIDOfStepsCollectionNewSteps.getStepsCollection().remove(stepsCollectionNewSteps);
                        oldGameIDOfStepsCollectionNewSteps = em.merge(oldGameIDOfStepsCollectionNewSteps);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = games.getId();
                if (findGames(id) == null) {
                    throw new NonexistentEntityException("The games with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Games games;
            try {
                games = em.getReference(Games.class, id);
                games.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The games with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Steps> stepsCollectionOrphanCheck = games.getStepsCollection();
            for (Steps stepsCollectionOrphanCheckSteps : stepsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Games (" + games + ") cannot be destroyed since the Steps " + stepsCollectionOrphanCheckSteps + " in its stepsCollection field has a non-nullable gameID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Board boardID = games.getBoardID();
            if (boardID != null) {
                boardID.getGamesCollection().remove(games);
                boardID = em.merge(boardID);
            }
            Users userID = games.getUserID();
            if (userID != null) {
                userID.getGamesCollection().remove(games);
                userID = em.merge(userID);
            }
            em.remove(games);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Games> findGamesEntities() {
        return findGamesEntities(true, -1, -1);
    }

    public List<Games> findGamesEntities(int maxResults, int firstResult) {
        return findGamesEntities(false, maxResults, firstResult);
    }

    private List<Games> findGamesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Games.class));
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

    public Games findGames(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Games.class, id);
        } finally {
            em.close();
        }
    }

    public int getGamesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Games> rt = cq.from(Games.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
