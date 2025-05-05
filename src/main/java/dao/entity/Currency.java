package dao.entity;


import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "currency")
@IdClass(CurrencyId.class)
public class Currency implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Id
    @ManyToOne
    @JoinColumn(name = "realm_id", nullable = false)
    private Realm realm;

    @Column(name="cost", precision = 20, scale = 4)
    private BigDecimal cost;

    // Getters and setters
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public Realm getRealm() { return realm; }
    public void setRealm(Realm realm) { this.realm = realm; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public Currency orElse(Object o) {
        if(o==null) return null;
        return (Currency) o;
    }
}