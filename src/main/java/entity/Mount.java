package entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "mount")
public class Mount implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private int id;

    @Column(name ="name")
    private String name;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Mount orElse(Object o) {
        if(o==null) return null;
        return (Mount) o;
    }
}
