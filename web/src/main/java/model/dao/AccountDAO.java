package model.dao;

import io.dropwizard.hibernate.AbstractDAO;
import model.person.Account;
import model.response.ResponseRegister;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

public class AccountDAO extends AbstractDAO<Account> {

    public AccountDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Account> findAll(){
        return list(namedQuery("findAllAccount"));
    }

    public List<Account> findByEmailPassword(String email, String password) {
        return list(
                namedQuery("findByEmailPasswordAccount")
                        .setParameter("email", email)
                        .setParameter("password", password)
        );
    }

    public ResponseRegister insertAccount(Account account){
        ResponseRegister response = new ResponseRegister();
        try {
            this.currentSession().persist(account);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            response.setCode(500);
            response.setMessage("There was an error processing your request");
            return response;
        }
        System.out.println("Account inserted");
        response.setCode(200);
        response.setMessage("Success");
        return response;
    }
}
