package entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Currency {

    @EmbeddedId
    private CurrencyId id;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @MapsId("realmId")
    @JoinColumn(name = "realm_id")
    private Realm realm;

    private BigDecimal cost;

    // Getters and Setters
    public CurrencyId getId() { return id; }
    public void setId(CurrencyId id) { this.id = id; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public Realm getRealm() { return realm; }
    public void setRealm(Realm realm) { this.realm = realm; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
}
