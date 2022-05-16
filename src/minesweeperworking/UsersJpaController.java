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
public class UsersJpaController implements Serializable {

    public UsersJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Users users) {
        if (users.getGamesCollection() == null) {
            users.setGamesCollection(new ArrayList<Games>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Games> attachedGamesCollection = new ArrayList<Games>();
            for (Games gamesCollectionGamesToAttach : users.getGamesCollection()) {
                gamesCollectionGamesToAttach = em.getReference(gamesCollectionGamesToAttach.getClass(), gamesCollectionGamesToAttach.getId());
                attachedGamesCollection.add(gamesCollectionGamesToAttach);
            }
            users.setGamesCollection(attachedGamesCollection);
            em.persist(users);
            for (Games gamesCollectionGames : users.getGamesCollection()) {
                Users oldUserIDOfGamesCollectionGames = gamesCollectionGames.getUserID();
                gamesCollectionGames.setUserID(users);
                gamesCollectionGames = em.merge(gamesCollectionGames);
                if (oldUserIDOfGamesCollectionGames != null) {
                    oldUserIDOfGamesCollectionGames.getGamesCollection().remove(gamesCollectionGames);
                    oldUserIDOfGamesCollectionGames = em.merge(oldUserIDOfGamesCollectionGames);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Users users) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users persistentUsers = em.find(Users.class, users.getId());
            Collection<Games> gamesCollectionOld = persistentUsers.getGamesCollection();
            Collection<Games> gamesCollectionNew = users.getGamesCollection();
            List<String> illegalOrphanMessages = null;
            for (Games gamesCollectionOldGames : gamesCollectionOld) {
                if (!gamesCollectionNew.contains(gamesCollectionOldGames)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Games " + gamesCollectionOldGames + " since its userID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Games> attachedGamesCollectionNew = new ArrayList<Games>();
            for (Games gamesCollectionNewGamesToAttach : gamesCollectionNew) {
                gamesCollectionNewGamesToAttach = em.getReference(gamesCollectionNewGamesToAttach.getClass(), gamesCollectionNewGamesToAttach.getId());
                attachedGamesCollectionNew.add(gamesCollectionNewGamesToAttach);
            }
            gamesCollectionNew = attachedGamesCollectionNew;
            users.setGamesCollection(gamesCollectionNew);
            users = em.merge(users);
            for (Games gamesCollectionNewGames : gamesCollectionNew) {
                if (!gamesCollectionOld.contains(gamesCollectionNewGames)) {
                    Users oldUserIDOfGamesCollectionNewGames = gamesCollectionNewGames.getUserID();
                    gamesCollectionNewGames.setUserID(users);
                    gamesCollectionNewGames = em.merge(gamesCollectionNewGames);
                    if (oldUserIDOfGamesCollectionNewGames != null && !oldUserIDOfGamesCollectionNewGames.equals(users)) {
                        oldUserIDOfGamesCollectionNewGames.getGamesCollection().remove(gamesCollectionNewGames);
                        oldUserIDOfGamesCollectionNewGames = em.merge(oldUserIDOfGamesCollectionNewGames);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = users.getId();
                if (findUsers(id) == null) {
                    throw new NonexistentEntityException("The users with id " + id + " no longer exists.");
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
            Users users;
            try {
                users = em.getReference(Users.class, id);
                users.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The users with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Games> gamesCollectionOrphanCheck = users.getGamesCollection();
            for (Games gamesCollectionOrphanCheckGames : gamesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the Games " + gamesCollectionOrphanCheckGames + " in its gamesCollection field has a non-nullable userID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(users);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Users> findUsersEntities() {
        return findUsersEntities(true, -1, -1);
    }

    public List<Users> findUsersEntities(int maxResults, int firstResult) {
        return findUsersEntities(false, maxResults, firstResult);
    }

    private List<Users> findUsersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Users.class));
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

    public Users findUsers(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Users.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Users> rt = cq.from(Users.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
