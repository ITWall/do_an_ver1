package model.dao;

import io.dropwizard.hibernate.AbstractDAO;
import model.entity.Edge;
import model.entity.Rating;
import model.response.Response;
import model.response.ResponseRegister;
import org.hibernate.SessionFactory;

import javax.persistence.StoredProcedureQuery;
import java.util.List;

public class RatingDAO extends AbstractDAO<Rating> {
    public RatingDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Response insertRating(Rating rating) {
        // Getting the named stored procedure from the persistence unit and settting the parameters values.
        StoredProcedureQuery spInsertDevice = this.currentSession().createNamedStoredProcedureQuery("insertRating");
        spInsertDevice.setParameter("deviceID", rating.getDevice().getId());
        spInsertDevice.setParameter("name", rating.getPlace().getName());
        spInsertDevice.setParameter("latitude", rating.getPlace().getLatitude());
        spInsertDevice.setParameter("longitude", rating.getPlace().getLongitude());
        spInsertDevice.setParameter("edgeID", rating.getPlace().getEdge().getId());
        spInsertDevice.setParameter("baseNode", rating.getPlace().getEdge().getBaseNode());
        spInsertDevice.setParameter("adjustNode", rating.getPlace().getEdge().getAdjustNode());
        spInsertDevice.setParameter("distance", rating.getPlace().getEdge().getDistance());
        spInsertDevice.setParameter("speed", rating.getPlace().getEdge().getSpeed());
        spInsertDevice.setParameter("polyline", rating.getPlace().getEdge().getPolyline());
        spInsertDevice.setParameter("trafficStatus", rating.getTrafficStatus());
        spInsertDevice.setParameter("time", rating.getTime());
        spInsertDevice.setParameter("status", rating.getStatus());
        List l = null;
        Response response = new Response();
        try {
            l = spInsertDevice.getResultList();
            response.setMessage("Success");
            response.setCode(200);
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(500);
            response.setMessage("Failed");
        }
        return response;
    }
}
