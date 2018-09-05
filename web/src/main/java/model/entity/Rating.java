package model.entity;


import javax.persistence.*;

@Entity
@Table(name = "rating")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "inserRating",
                procedureName = "insertRating",
                parameters = {
                        @StoredProcedureParameter(name = "deviceID", mode = ParameterMode.IN, type = Integer.class),
                        @StoredProcedureParameter(name = "name", mode = ParameterMode.IN, type = String.class),
                        @StoredProcedureParameter(name = "latitude", mode = ParameterMode.IN, type = Double.class),
                        @StoredProcedureParameter(name = "longitude", mode = ParameterMode.IN, type = Double.class),
                        @StoredProcedureParameter(name = "edgeID", mode = ParameterMode.IN, type = Integer.class),
                        @StoredProcedureParameter(name = "baseNode", mode = ParameterMode.IN, type = Integer.class),
                        @StoredProcedureParameter(name = "adjustNode", mode = ParameterMode.IN, type = Integer.class),
                        @StoredProcedureParameter(name = "distance", mode = ParameterMode.IN, type = Double.class),
                        @StoredProcedureParameter(name = "speed", mode = ParameterMode.IN, type = Double.class),
                        @StoredProcedureParameter(name = "polyline", mode = ParameterMode.IN, type = String.class),
                        @StoredProcedureParameter(name = "trafficStatus", mode = ParameterMode.IN, type = Integer.class),
                        @StoredProcedureParameter(name = "time", mode = ParameterMode.IN, type = Long.class),
                        @StoredProcedureParameter(name = "status", mode = ParameterMode.IN, type = Integer.class),
                },
                resultClasses = Rating.class)
})
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Time")
    private long time;

    @Column(name = "Status")
    private int status;

    @Column(name = "TrafficStatus")
    private int trafficStatus;

    @JoinColumn(name = "PlaceID", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private Place place;

    @JoinColumn(name = "DeviceID", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private Device device;

    public Rating() {
    }

    public Rating(long time, int status, int trafficStatus, Place place, Device device) {
        this.time = time;
        this.status = status;
        this.trafficStatus = trafficStatus;
        this.place = place;
        this.device = device;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTrafficStatus() {
        return trafficStatus;
    }

    public void setTrafficStatus(int trafficStatus) {
        this.trafficStatus = trafficStatus;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
