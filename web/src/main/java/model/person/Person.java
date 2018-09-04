package model.person;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;


import javax.persistence.*;

@Entity
@Table(name = "person")
@NamedQueries({
        @NamedQuery(name = "findAllPerson",
                query = "select p from Person p"),
        @NamedQuery(name = "findByEmailPasswordPerson",
                query = "select p from Person p " +
                        "where p.account.email like :email and " +
                        "p.account.password like :password")
})
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;
    @JoinColumn(name = "AccountID", referencedColumnName = "ID")
    @OneToOne
    private Account account;
    @JoinColumn(name = "FullnameID", referencedColumnName = "ID")
    @OneToOne
    private Fullname fullname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Fullname getFullname() {
        return fullname;
    }

    public void setFullname(Fullname fullname) {
        this.fullname = fullname;
//        System.out.println("Helloworld");
    }

    public Person() {

    }
}
