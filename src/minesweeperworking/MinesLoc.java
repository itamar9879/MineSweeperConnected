/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minesweeperworking;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author itamar
 */
@Entity
@Table(name = "MinesLoc")
@NamedQueries({
    @NamedQuery(name = "MinesLoc.findAll", query = "SELECT m FROM MinesLoc m"),
    @NamedQuery(name = "MinesLoc.findByMineID", query = "SELECT m FROM MinesLoc m WHERE m.mineID = :mineID"),
    @NamedQuery(name = "MinesLoc.findByBoardID", query = "SELECT m FROM MinesLoc m WHERE m.boardID = :boardID"),
    @NamedQuery(name = "MinesLoc.findByMineRow", query = "SELECT m FROM MinesLoc m WHERE m.mineRow = :mineRow"),
    @NamedQuery(name = "MinesLoc.findByMineCount", query = "SELECT m FROM MinesLoc m WHERE m.mineCount = :mineCount")})
public class MinesLoc implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "MineID")
    private Integer mineID;
    @Basic(optional = false)
    @Column(name = "BoardID")
    private int boardID;
    @Basic(optional = false)
    @Column(name = "MineRow")
    private int mineRow;
    @Basic(optional = false)
    @Column(name = "MineCount")
    private int mineCount;
    @JoinColumn(name = "MineID", referencedColumnName = "ID", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Board board;

    public MinesLoc() {
    }

    public MinesLoc(Integer mineID) {
        this.mineID = mineID;
    }

    public MinesLoc(Integer mineID, int boardID, int mineRow, int mineCount) {
        this.mineID = mineID;
        this.boardID = boardID;
        this.mineRow = mineRow;
        this.mineCount = mineCount;
    }

    public Integer getMineID() {
        return mineID;
    }

    public void setMineID(Integer mineID) {
        this.mineID = mineID;
    }

    public int getBoardID() {
        return boardID;
    }

    public void setBoardID(int boardID) {
        this.boardID = boardID;
    }

    public int getMineRow() {
        return mineRow;
    }

    public void setMineRow(int mineRow) {
        this.mineRow = mineRow;
    }

    public int getMineCount() {
        return mineCount;
    }

    public void setMineCount(int mineCount) {
        this.mineCount = mineCount;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mineID != null ? mineID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MinesLoc)) {
            return false;
        }
        MinesLoc other = (MinesLoc) object;
        if ((this.mineID == null && other.mineID != null) || (this.mineID != null && !this.mineID.equals(other.mineID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "minesweeperworking.MinesLoc[ mineID=" + mineID + " ]";
    }
    
}
