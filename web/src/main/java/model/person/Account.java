package model.person;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "account")
@NamedQueries({
        @NamedQuery(name = "findAllAccount",
                query = "select a from Account a"),
        @NamedQuery(name = "findByEmailPasswordAccount",
                query = "select a from Account a where " +
                        "a.email like :email " +
                        "and a.password like :password"),
})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(max = 255)
    @Column(name = "Email", length = 255)
    private String email;
    @Size(max = 255)
    @Column(name = "Password", length = 255)
    private String password;
    @JoinColumn(name = "StatusID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account() {

    }
}
