package model.dao;

import io.dropwizard.hibernate.AbstractDAO;
import model.entity.Device;
import org.hibernate.SessionFactory;

import javax.persistence.StoredProcedureQuery;
import java.util.List;

public class DeviceDAO extends AbstractDAO<Device> {
    public DeviceDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List insertDevice(Device device) {
        // Getting the named stored procedure from the persistence unit and settting the parameters values.
        StoredProcedureQuery spInsertDevice = this.currentSession().createNamedStoredProcedureQuery("insertDevice");
        spInsertDevice.setParameter("id", device.getId());
        return spInsertDevice.getResultList();
    }

    public List getAllDevices() {
        StoredProcedureQuery spInsertDevice = this.currentSession().createNamedStoredProcedureQuery("getAllDevices");
        return spInsertDevice.getResultList();
    }
}

