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
public class BoardJpaController implements Serializable {

    public BoardJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Board board) {
        if (board.getGamesCollection() == null) {
            board.setGamesCollection(new ArrayList<Games>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MinesLoc minesLoc = board.getMinesLoc();
            if (minesLoc != null) {
                minesLoc = em.getReference(minesLoc.getClass(), minesLoc.getMineID());
                board.setMinesLoc(minesLoc);
            }
            Collection<Games> attachedGamesCollection = new ArrayList<Games>();
            for (Games gamesCollectionGamesToAttach : board.getGamesCollection()) {
                gamesCollectionGamesToAttach = em.getReference(gamesCollectionGamesToAttach.getClass(), gamesCollectionGamesToAttach.getId());
                attachedGamesCollection.add(gamesCollectionGamesToAttach);
            }
            board.setGamesCollection(attachedGamesCollection);
            em.persist(board);
            if (minesLoc != null) {
                Board oldBoardOfMinesLoc = minesLoc.getBoard();
                if (oldBoardOfMinesLoc != null) {
                    oldBoardOfMinesLoc.setMinesLoc(null);
                    oldBoardOfMinesLoc = em.merge(oldBoardOfMinesLoc);
                }
                minesLoc.setBoard(board);
                minesLoc = em.merge(minesLoc);
            }
            for (Games gamesCollectionGames : board.getGamesCollection()) {
                Board oldBoardIDOfGamesCollectionGames = gamesCollectionGames.getBoardID();
                gamesCollectionGames.setBoardID(board);
                gamesCollectionGames = em.merge(gamesCollectionGames);
                if (oldBoardIDOfGamesCollectionGames != null) {
                    oldBoardIDOfGamesCollectionGames.getGamesCollection().remove(gamesCollectionGames);
                    oldBoardIDOfGamesCollectionGames = em.merge(oldBoardIDOfGamesCollectionGames);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Board board) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Board persistentBoard = em.find(Board.class, board.getId());
            MinesLoc minesLocOld = persistentBoard.getMinesLoc();
            MinesLoc minesLocNew = board.getMinesLoc();
            Collection<Games> gamesCollectionOld = persistentBoard.getGamesCollection();
            Collection<Games> gamesCollectionNew = board.getGamesCollection();
            List<String> illegalOrphanMessages = null;
            if (minesLocOld != null && !minesLocOld.equals(minesLocNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain MinesLoc " + minesLocOld + " since its board field is not nullable.");
            }
            for (Games gamesCollectionOldGames : gamesCollectionOld) {
                if (!gamesCollectionNew.contains(gamesCollectionOldGames)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Games " + gamesCollectionOldGames + " since its boardID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (minesLocNew != null) {
                minesLocNew = em.getReference(minesLocNew.getClass(), minesLocNew.getMineID());
                board.setMinesLoc(minesLocNew);
            }
            Collection<Games> attachedGamesCollectionNew = new ArrayList<Games>();
            for (Games gamesCollectionNewGamesToAttach : gamesCollectionNew) {
                gamesCollectionNewGamesToAttach = em.getReference(gamesCollectionNewGamesToAttach.getClass(), gamesCollectionNewGamesToAttach.getId());
                attachedGamesCollectionNew.add(gamesCollectionNewGamesToAttach);
            }
            gamesCollectionNew = attachedGamesCollectionNew;
            board.setGamesCollection(gamesCollectionNew);
            board = em.merge(board);
            if (minesLocNew != null && !minesLocNew.equals(minesLocOld)) {
                Board oldBoardOfMinesLoc = minesLocNew.getBoard();
                if (oldBoardOfMinesLoc != null) {
                    oldBoardOfMinesLoc.setMinesLoc(null);
                    oldBoardOfMinesLoc = em.merge(oldBoardOfMinesLoc);
                }
                minesLocNew.setBoard(board);
                minesLocNew = em.merge(minesLocNew);
            }
            for (Games gamesCollectionNewGames : gamesCollectionNew) {
                if (!gamesCollectionOld.contains(gamesCollectionNewGames)) {
                    Board oldBoardIDOfGamesCollectionNewGames = gamesCollectionNewGames.getBoardID();
                    gamesCollectionNewGames.setBoardID(board);
                    gamesCollectionNewGames = em.merge(gamesCollectionNewGames);
                    if (oldBoardIDOfGamesCollectionNewGames != null && !oldBoardIDOfGamesCollectionNewGames.equals(board)) {
                        oldBoardIDOfGamesCollectionNewGames.getGamesCollection().remove(gamesCollectionNewGames);
                        oldBoardIDOfGamesCollectionNewGames = em.merge(oldBoardIDOfGamesCollectionNewGames);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = board.getId();
                if (findBoard(id) == null) {
                    throw new NonexistentEntityException("The board with id " + id + " no longer exists.");
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
            Board board;
            try {
                board = em.getReference(Board.class, id);
                board.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The board with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            MinesLoc minesLocOrphanCheck = board.getMinesLoc();
            if (minesLocOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Board (" + board + ") cannot be destroyed since the MinesLoc " + minesLocOrphanCheck + " in its minesLoc field has a non-nullable board field.");
            }
            Collection<Games> gamesCollectionOrphanCheck = board.getGamesCollection();
            for (Games gamesCollectionOrphanCheckGames : gamesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Board (" + board + ") cannot be destroyed since the Games " + gamesCollectionOrphanCheckGames + " in its gamesCollection field has a non-nullable boardID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(board);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Board> findBoardEntities() {
        return findBoardEntities(true, -1, -1);
    }

    public List<Board> findBoardEntities(int maxResults, int firstResult) {
        return findBoardEntities(false, maxResults, firstResult);
    }

    private List<Board> findBoardEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Board.class));
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

    public Board findBoard(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Board.class, id);
        } finally {
            em.close();
        }
    }

    public int getBoardCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Board> rt = cq.from(Board.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
