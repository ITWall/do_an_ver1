package model.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Latitude")
    private double latitude;

    @Column(name = "Longitude")
    private double longitude;

    //    @JoinColumn(name = "EdgeID", referencedColumnName = "ID", nullable = false)
//    @ManyToOne
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "place_edge",
            joinColumns = {@JoinColumn(name = "placeID")},
            inverseJoinColumns = {@JoinColumn(name = "edgeID")}
    )
    private List<Edge> edges;

    public Place() {
    }

    public Place(String name, double latitude, double longitude, List<Edge> edges) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.edges = edges;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}
