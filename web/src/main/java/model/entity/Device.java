package model.entity;

import javax.persistence.*;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
@Entity
@Table(name = "device")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "getAllDevices",
                                    procedureName = "getAllDevices",
                                    resultClasses = Device.class),
        @NamedStoredProcedureQuery(name = "insertDevice",
                                    procedureName = "insertDevice",
                                    parameters = {
                                            @StoredProcedureParameter(name = "id", mode = ParameterMode.IN, type = String.class)
                                    })
})
public class Device {
    @Id
    private int id;

    @Column(name = "Warning")
    private int warning;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWarning() {
        return warning;
    }

    public void setWarning(int warning) {
        this.warning = warning;
    }

    public Device() {
    }

    public Device(int id, int warning) {
        this.id = id;
        this.warning = warning;
    }
}
