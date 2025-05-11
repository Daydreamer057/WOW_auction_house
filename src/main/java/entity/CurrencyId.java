package entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CurrencyId implements Serializable {

    private int itemId;
    private int realmId;

    public CurrencyId() {}

    public CurrencyId(int itemId, int realmId) {
        this.itemId = itemId;
        this.realmId = realmId;
    }

    // Getters & Setters (required for Hibernate)
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getRealmId() { return realmId; }
    public void setRealmId(int realmId) { this.realmId = realmId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyId)) return false;
        CurrencyId that = (CurrencyId) o;
        return itemId == that.itemId && realmId == that.realmId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, realmId);
    }
}
