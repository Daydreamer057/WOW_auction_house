package entity;

import java.io.Serializable;
import java.util.Objects;

public class CurrencyId implements Serializable {

    private int item;
    private int realm;

    public CurrencyId() {}

    public CurrencyId(int item, int realm) {
        this.item = item;
        this.realm = realm;
    }

    // hashCode and equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyId)) return false;
        CurrencyId that = (CurrencyId) o;
        return item == that.item && realm == that.realm;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, realm);
    }
}