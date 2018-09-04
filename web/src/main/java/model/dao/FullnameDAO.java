package model.dao;

import io.dropwizard.hibernate.AbstractDAO;
import model.person.Account;
import model.person.Fullname;
import model.response.ResponseRegister;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

public class FullnameDAO extends AbstractDAO<Fullname> {
    public FullnameDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Fullname> findAll(){
        return list(namedQuery("findAllName"));
    }

    public List<Fullname> findByName(String name) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(name).append("%");
        return list(
                namedQuery("findByName")
                        .setParameter("name", builder.toString())
        );
    }

    public List<Fullname> findByFirstnameAndLastname(String firstname, String lastname){
        return list(
                namedQuery("findByFirstnameAndLastname")
                        .setParameter("firstname", firstname)
                        .setParameter("lastname", lastname)
        );
    }

    public ResponseRegister insertFullname(Fullname fullname){
        ResponseRegister response = new ResponseRegister();
        try {
            this.currentSession().persist(fullname);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            response.setCode(500);
            response.setMessage("There was an error processing your request");
            return response;
        }
        System.out.println("Fullname inserted");
        response.setCode(200);
        response.setMessage("Success");
        return response;
    }
}
