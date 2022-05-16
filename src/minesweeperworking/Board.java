/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minesweeperworking;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author itamar
 */
@Entity
@Table(name = "Board")
@NamedQueries({
    @NamedQuery(name = "Board.findAll", query = "SELECT b FROM Board b"),
    @NamedQuery(name = "Board.findById", query = "SELECT b FROM Board b WHERE b.id = :id"),
    @NamedQuery(name = "Board.findByRowCount", query = "SELECT b FROM Board b WHERE b.rowCount = :rowCount"),
    @NamedQuery(name = "Board.findByColumnCount", query = "SELECT b FROM Board b WHERE b.columnCount = :columnCount")})
public class Board implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "RowCount")
    private int rowCount;
    @Basic(optional = false)
    @Column(name = "ColumnCount")
    private int columnCount;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "board")
    private MinesLoc minesLoc;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "boardID")
    private Collection<Games> gamesCollection;

    public Board() {
    }

    public Board(Integer id) {
        this.id = id;
    }

    public Board(Integer id, int rowCount, int columnCount) {
        this.id = id;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public MinesLoc getMinesLoc() {
        return minesLoc;
    }

    public void setMinesLoc(MinesLoc minesLoc) {
        this.minesLoc = minesLoc;
    }

    public Collection<Games> getGamesCollection() {
        return gamesCollection;
    }

    public void setGamesCollection(Collection<Games> gamesCollection) {
        this.gamesCollection = gamesCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Board)) {
            return false;
        }
        Board other = (Board) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "minesweeperworking.Board[ id=" + id + " ]";
    }
    
}
