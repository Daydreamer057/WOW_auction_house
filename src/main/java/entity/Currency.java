package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "currency")
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

    private Long cost;

    // Getters and Setters


    public CurrencyId getId() {
        return id;
    }

    public void setId(CurrencyId id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }
}
