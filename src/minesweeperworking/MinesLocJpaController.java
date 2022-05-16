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
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import minesweeperworking.exceptions.IllegalOrphanException;
import minesweeperworking.exceptions.NonexistentEntityException;
import minesweeperworking.exceptions.PreexistingEntityException;

/**
 *
 * @author itamar
 */
public class MinesLocJpaController implements Serializable {

    public MinesLocJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MinesLoc minesLoc) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Board boardOrphanCheck = minesLoc.getBoard();
        if (boardOrphanCheck != null) {
            MinesLoc oldMinesLocOfBoard = boardOrphanCheck.getMinesLoc();
            if (oldMinesLocOfBoard != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Board " + boardOrphanCheck + " already has an item of type MinesLoc whose board column cannot be null. Please make another selection for the board field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Board board = minesLoc.getBoard();
            if (board != null) {
                board = em.getReference(board.getClass(), board.getId());
                minesLoc.setBoard(board);
            }
            em.persist(minesLoc);
            if (board != null) {
                board.setMinesLoc(minesLoc);
                board = em.merge(board);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMinesLoc(minesLoc.getMineID()) != null) {
                throw new PreexistingEntityException("MinesLoc " + minesLoc + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MinesLoc minesLoc) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MinesLoc persistentMinesLoc = em.find(MinesLoc.class, minesLoc.getMineID());
            Board boardOld = persistentMinesLoc.getBoard();
            Board boardNew = minesLoc.getBoard();
            List<String> illegalOrphanMessages = null;
            if (boardNew != null && !boardNew.equals(boardOld)) {
                MinesLoc oldMinesLocOfBoard = boardNew.getMinesLoc();
                if (oldMinesLocOfBoard != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Board " + boardNew + " already has an item of type MinesLoc whose board column cannot be null. Please make another selection for the board field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (boardNew != null) {
                boardNew = em.getReference(boardNew.getClass(), boardNew.getId());
                minesLoc.setBoard(boardNew);
            }
            minesLoc = em.merge(minesLoc);
            if (boardOld != null && !boardOld.equals(boardNew)) {
                boardOld.setMinesLoc(null);
                boardOld = em.merge(boardOld);
            }
            if (boardNew != null && !boardNew.equals(boardOld)) {
                boardNew.setMinesLoc(minesLoc);
                boardNew = em.merge(boardNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = minesLoc.getMineID();
                if (findMinesLoc(id) == null) {
                    throw new NonexistentEntityException("The minesLoc with id " + id + " no longer exists.");
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
            MinesLoc minesLoc;
            try {
                minesLoc = em.getReference(MinesLoc.class, id);
                minesLoc.getMineID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The minesLoc with id " + id + " no longer exists.", enfe);
            }
            Board board = minesLoc.getBoard();
            if (board != null) {
                board.setMinesLoc(null);
                board = em.merge(board);
            }
            em.remove(minesLoc);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MinesLoc> findMinesLocEntities() {
        return findMinesLocEntities(true, -1, -1);
    }

    public List<MinesLoc> findMinesLocEntities(int maxResults, int firstResult) {
        return findMinesLocEntities(false, maxResults, firstResult);
    }

    private List<MinesLoc> findMinesLocEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MinesLoc.class));
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

    public MinesLoc findMinesLoc(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MinesLoc.class, id);
        } finally {
            em.close();
        }
    }

    public int getMinesLocCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MinesLoc> rt = cq.from(MinesLoc.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
