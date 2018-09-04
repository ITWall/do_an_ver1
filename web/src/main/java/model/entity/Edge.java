package model.entity;

import javax.persistence.*;

@Entity
@Table(name = "edge")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "insertEdge",
                procedureName = "insertEdge",
                parameters = {
                        @StoredProcedureParameter(name = "id", mode = ParameterMode.INOUT, type = Integer.class),
                        @StoredProcedureParameter(name = "baseNode", mode = ParameterMode.IN, type = Integer.class),
                        @StoredProcedureParameter(name = "adjustNode", mode = ParameterMode.IN, type = Integer.class),
                        @StoredProcedureParameter(name = "distance", mode = ParameterMode.IN, type = Double.class),
                        @StoredProcedureParameter(name = "speed", mode = ParameterMode.IN, type = Double.class),
                        @StoredProcedureParameter(name = "polyline", mode = ParameterMode.IN, type = String.class),
                        @StoredProcedureParameter(name = "trafficStatus", mode = ParameterMode.IN, type = Integer.class)
                })
})
public class Edge {
    @Id
    private int id;

    @Column(name = "BaseNode")
    private int baseNode;

    @Column(name = "AdjustNode")
    private int adjustNode;

    @Column(name = "Distance")
    private double distance;

    @Column(name = "Speed")
    private double speed;

    @Column(name = "Polyline")
    private String polyline;

    @Column(name = "TrafficStatus")
    private int trafficStatus;

    public Edge() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBaseNode() {
        return baseNode;
    }

    public void setBaseNode(int baseNode) {
        this.baseNode = baseNode;
    }

    public int getAdjustNode() {
        return adjustNode;
    }

    public void setAdjustNode(int adjustNode) {
        this.adjustNode = adjustNode;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public int getTrafficStatus() {
        return trafficStatus;
    }

    public void setTrafficStatus(int trafficStatus) {
        this.trafficStatus = trafficStatus;
    }

    public Edge(int id, int baseNode, int adjustNode, double distance, double speed, String polyline, int trafficStatus) {

        this.id = id;
        this.baseNode = baseNode;
        this.adjustNode = adjustNode;
        this.distance = distance;
        this.speed = speed;
        this.polyline = polyline;
        this.trafficStatus = trafficStatus;
    }
}
