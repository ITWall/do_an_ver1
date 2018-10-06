package model.dao;

import io.dropwizard.hibernate.AbstractDAO;
import model.entity.Edge;
import model.entity.Place;
import model.entity.Rating;
import model.response.Response;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;

import javax.persistence.StoredProcedureQuery;
import java.util.List;

public class RatingDAO extends AbstractDAO<Rating> {
    private SessionFactory sessionFactory;
    public RatingDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    public Response insertRating(Rating rating) {
        // Getting the named stored procedure from the persistence unit and settting the parameters values.
        StoredProcedureQuery spInsertRating = this.currentSession().createNamedStoredProcedureQuery("insertRating");
        spInsertRating.setParameter("deviceID", rating.getDevice().getId());
        spInsertRating.setParameter("name", rating.getPlace().getName());
        spInsertRating.setParameter("latitude", rating.getPlace().getLatitude());
        spInsertRating.setParameter("longitude", rating.getPlace().getLongitude());
        spInsertRating.setParameter("trafficStatus", rating.getTrafficStatus());
        spInsertRating.setParameter("time", rating.getTime());
        spInsertRating.setParameter("status", rating.getStatus());
        List<Rating> l = null;
        Response response = new Response();
        try {
            l = (List<Rating>) spInsertRating.getResultList();
            if (l.size() > 0) {
                Response responseInsertAllEdge = insertAllEdges(rating);
                if (responseInsertAllEdge.getCode() == 200) {
                    response = insertAllPlaceEdge(l.get(0).getPlace(), rating.getPlace().getEdges());
                } else {
                    response.setCode(500);
                    response.setMessage("Failed");
                    return response;
                }
            } else {
                response.setCode(500);
                response.setMessage("Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(500);
            response.setMessage("Failed");
        }
        return response;
    }

    private Response insertAllPlaceEdge(Place place, List<Edge> edges) {
        Response response = new Response();
        // Getting the named stored procedure from the persistence unit and settting the parameters values.
        for (Edge edge : edges) {
            response = insertPlaceEdge(place, edge);
            if (response.getCode() == 500) {
                return response;
            }
        }
        response.setMessage("Success");
        response.setCode(200);
        return response;
    }

    private Response insertPlaceEdge(Place place, Edge edge) {
        Response response = new Response();
        // Getting the named stored procedure from the persistence unit and settting the parameters values.
        StoredProcedureQuery spInsertPlaceEdge = this.currentSession().createNamedStoredProcedureQuery("insertPlaceEdge");
        spInsertPlaceEdge.setParameter("placeID", place.getId());
        spInsertPlaceEdge.setParameter("edgeID", edge.getId());
        try {
            spInsertPlaceEdge.executeUpdate();
            response.setCode(200);
            response.setMessage("Success");
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Failed");
            response.setCode(500);
            return response;
        }
        return response;
    }

    public Response insertAllEdges(Rating rating) {
        Response response = new Response();
        int trafficStatus = rating.getTrafficStatus();
        for (Edge edge : rating.getPlace().getEdges()) {
            response = insertEdge(edge, trafficStatus);
            if (response.getCode() == 500) {
                return response;
            }
        }
        response.setMessage("Success");
        response.setCode(200);
        return response;
    }

    private Response insertEdge(Edge edge, int trafficStatus) {
        Response response = new Response();
        StoredProcedureQuery spInsertEdge = this.currentSession().createNamedStoredProcedureQuery("insertEdge");
        spInsertEdge.setParameter("id", edge.getId());
        spInsertEdge.setParameter("baseNode", edge.getBaseNode());
        spInsertEdge.setParameter("adjustNode", edge.getAdjustNode());
        spInsertEdge.setParameter("distance", edge.getDistance());
        spInsertEdge.setParameter("speed", edge.getSpeed());
        spInsertEdge.setParameter("polyline", edge.getPolyline());
        spInsertEdge.setParameter("trafficStatus", trafficStatus);
        try {
            spInsertEdge.executeUpdate();
            response.setMessage("Success");
            response.setCode(200);
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(500);
            response.setMessage("Failed");
            return response;
        }

        return response;
    }

    public Response updateEdge(Edge edge, int trafficStatus) {
        Response response = new Response();
        Session session = this.sessionFactory.openSession();
        ManagedSessionContext.bind(session);
        session.beginTransaction();
        StoredProcedureQuery spInsertEdge = session.createNamedStoredProcedureQuery("updateEdge");
        spInsertEdge.setParameter("id", edge.getId());
        spInsertEdge.setParameter("trafficStatus", trafficStatus);
        try {
            spInsertEdge.executeUpdate();
            session.getTransaction().commit();
            response.setMessage("Success");
            response.setCode(200);
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(500);
            response.setMessage("Failed");
            session.getTransaction().commit();
            return response;
        } finally {
            session.close();
        }

        return response;
    }
}
