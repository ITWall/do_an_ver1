package model.dao;

import io.dropwizard.hibernate.AbstractDAO;
import model.person.Person;
import model.response.ResponseRegister;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

public class PersonDAO extends AbstractDAO<PersonDAO> {

    public PersonDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Person> findAll(){
        return list(namedQuery("findAllPerson"));
    }

    public List<Person> findByEmailPasswordAccount(String email, String password) {
        return list(
                namedQuery("findByEmailPasswordPerson")
                        .setParameter("email", email)
                        .setParameter("password", password)
        );
    }

    public ResponseRegister insertPerson(Person person){
        ResponseRegister response = new ResponseRegister();
        try {
            this.currentSession().persist(person);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            response.setCode(500);
            response.setMessage("There was an error processing your request");
            return response;
        }
        System.out.println("Person inserted");
        response.setCode(200);
        response.setMessage("Success");
        return response;
    }
}
