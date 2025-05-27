package entity;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "item")
public class Item implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private int id;

    @Column(name = "pt_br")
    private String ptBr;

    @Column(name = "de_de")
    private String deDe;

    @Column(name = "en_gb")
    private String enGb;

    @Column(name = "es_es")
    private String esEs;

    @Column(name = "fr_fr")
    private String frFr;

    @Column(name = "it_it")
    private String itIt;

    @Column(name = "ru_ru")
    private String ruRu;

    @Column(name = "item_class")
    private String itemClass;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Currency> currencies;

    // Getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPtBr() {
        return ptBr;
    }

    public void setPtBr(String ptBr) {
        this.ptBr = ptBr;
    }

    public String getDeDe() {
        return deDe;
    }

    public void setDeDe(String deDe) {
        this.deDe = deDe;
    }

    public String getEnGb() {
        return enGb;
    }

    public void setEnGb(String enGb) {
        this.enGb = enGb;
    }

    public String getEsEs() {
        return esEs;
    }

    public void setEsEs(String esEs) {
        this.esEs = esEs;
    }

    public String getFrFr() {
        return frFr;
    }

    public void setFrFr(String frFr) {
        this.frFr = frFr;
    }

    public String getItIt() {
        return itIt;
    }

    public void setItIt(String itIt) {
        this.itIt = itIt;
    }

    public String getRuRu() {
        return ruRu;
    }

    public void setRuRu(String ruRu) {
        this.ruRu = ruRu;
    }

    public String getItemClass() {
        return itemClass;
    }

    public void setItemClass(String itemClass) {
        this.itemClass = itemClass;
    }

    public Set<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Set<Currency> currencies) {
        this.currencies = currencies;
    }

    public Item orElse(Object o) {
        if(o==null) return null;
        return (Item) o;
    }
}