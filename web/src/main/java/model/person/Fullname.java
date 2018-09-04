package model.person;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "fullname")
@NamedQueries({
        @NamedQuery(name = "findAllName",
                query = "select f from Fullname f"),
        @NamedQuery(name = "findByName",
                query = "select f from Fullname f "
                        + "where f.firstname like :name "
                        + "or f.lastname like :name"),
        @NamedQuery(name = "findByFirstnameAndLastname",
                query = "select f from Fullname f "
                        + "where f.firstname like :firstname "
                        + "and f.lastname like :lastname")
})
public class Fullname {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(max = 255)
    @Column(name = "Firstname", length = 255)
    private String firstname;
    @Size(max = 255)
    @Column(name = "Lastname", length = 255)
    private String lastname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Fullname() {

    }
}
