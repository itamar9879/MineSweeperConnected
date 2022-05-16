/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minesweeperworking;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author itamar
 */
@Entity
@Table(name = "Steps")
@NamedQueries({
    @NamedQuery(name = "Steps.findAll", query = "SELECT s FROM Steps s"),
    @NamedQuery(name = "Steps.findById", query = "SELECT s FROM Steps s WHERE s.id = :id"),
    @NamedQuery(name = "Steps.findByMove", query = "SELECT s FROM Steps s WHERE s.move = :move"),
    @NamedQuery(name = "Steps.findByRowValue", query = "SELECT s FROM Steps s WHERE s.rowValue = :rowValue"),
    @NamedQuery(name = "Steps.findByColValue", query = "SELECT s FROM Steps s WHERE s.colValue = :colValue"),
    @NamedQuery(name = "Steps.findByValue", query = "SELECT s FROM Steps s WHERE s.value = :value")})
public class Steps implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Move")
    private Character move;
    @Basic(optional = false)
    @Column(name = "RowValue")
    private int rowValue;
    @Basic(optional = false)
    @Column(name = "ColValue")
    private int colValue;
    @Basic(optional = false)
    @Column(name = "Value")
    private int value;
    @JoinColumn(name = "GameID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Games gameID;

    public Steps() {
    }

    public Steps(Integer id) {
        this.id = id;
    }

    public Steps(Integer id, Character move, int rowValue, int colValue, int value) {
        this.id = id;
        this.move = move;
        this.rowValue = rowValue;
        this.colValue = colValue;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Character getMove() {
        return move;
    }

    public void setMove(Character move) {
        this.move = move;
    }

    public int getRowValue() {
        return rowValue;
    }

    public void setRowValue(int rowValue) {
        this.rowValue = rowValue;
    }

    public int getColValue() {
        return colValue;
    }

    public void setColValue(int colValue) {
        this.colValue = colValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Games getGameID() {
        return gameID;
    }

    public void setGameID(Games gameID) {
        this.gameID = gameID;
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
        if (!(object instanceof Steps)) {
            return false;
        }
        Steps other = (Steps) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "minesweeperworking.Steps[ id=" + id + " ]";
    }
    
}
