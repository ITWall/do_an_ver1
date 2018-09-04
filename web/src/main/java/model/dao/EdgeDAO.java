package model.dao;

import io.dropwizard.hibernate.AbstractDAO;
import model.entity.Device;
import model.entity.Edge;
import model.response.ResponseRegister;
import org.hibernate.SessionFactory;

import javax.persistence.StoredProcedureQuery;
import java.util.List;

public class EdgeDAO extends AbstractDAO<Edge> {
    public EdgeDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ResponseRegister insertEdge(Edge edge) {
        // Getting the named stored procedure from the persistence unit and settting the parameters values.
        ResponseRegister response = new ResponseRegister();
        StoredProcedureQuery spInsertDevice = this.currentSession().createNamedStoredProcedureQuery("insertEdge");
        spInsertDevice.setParameter("id", edge.getId());
        spInsertDevice.setParameter("baseNode", edge.getBaseNode());
        spInsertDevice.setParameter("adjustNode", edge.getAdjustNode());
        spInsertDevice.setParameter("distance", edge.getDistance());
        spInsertDevice.setParameter("speed", edge.getSpeed());
        spInsertDevice.setParameter("polyline", edge.getPolyline());
        spInsertDevice.setParameter("trafficStatus", edge.getTrafficStatus());
        try {
            this.currentSession().persist(edge);
        } catch (Exception ex){
            ex.printStackTrace();
            response.setCode(500);
            response.setMessage("There was an error processing your request");
            return response;
        }
        response.setCode(200);
        response.setMessage("Success");
        return response;
    }
}
