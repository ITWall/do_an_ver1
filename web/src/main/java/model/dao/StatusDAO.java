package model.dao;

import io.dropwizard.hibernate.AbstractDAO;

import model.person.Status;
import org.hibernate.SessionFactory;

import java.util.List;

public class StatusDAO extends AbstractDAO<Status> {

    public StatusDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Status> findAll(){
        return list(namedQuery("findAllStatus"));
    }

    public List<Status> findByName(String status) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(status).append("%");
        return list(
                namedQuery("findByStatus")
                        .setParameter("status", builder.toString())
        );
    }
}
