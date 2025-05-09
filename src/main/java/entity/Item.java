package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;

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

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Currency> currencies;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<Currency> getCurrencies() { return currencies; }
    public void setCurrencies(Set<Currency> currencies) { this.currencies = currencies; }

    public Item orElse(Object o) {
        if(o==null) return null;
        return (Item) o;
    }
}