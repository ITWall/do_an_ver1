package model.person;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "status")
@NamedQueries({
        @NamedQuery(name = "findAllStatus",
                query = "select s from Status s"),
        @NamedQuery(name = "findByStatus",
                query = "select s from Status s "
                        + "where s.status like :status ")
})
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(max = 255)
    @Column(name = "Status", length = 255)
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Status() {

    }
}
