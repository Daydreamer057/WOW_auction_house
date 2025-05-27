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
@Table(name = "realm")
public class Realm implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private int id;

    @Column(name ="name")
    private String name;

    @Column(name ="locale")
    private String locale;

    @Column(name ="connected_realm_id")
    private int connectedRealmId;

    @OneToMany(mappedBy = "realm", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Currency> currencies;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int getConnectedRealmId() {
        return connectedRealmId;
    }

    public void setConnectedRealmId(int connectedRealmId) {
        this.connectedRealmId = connectedRealmId;
    }

    public Set<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Set<Currency> currencies) {
        this.currencies = currencies;
    }
}
