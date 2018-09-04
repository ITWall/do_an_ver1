package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Instruction {
    private double distance;
    private double time;
    private String Polyline;

    public Instruction(double distance, double time, String polyline) {
        this.distance = distance;
        this.time = time;
        Polyline = polyline;
    }

    public Instruction() {

    }
    @JsonProperty
    public double getDistance() {

        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    @JsonProperty
    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
    @JsonProperty
    public String getPolyline() {
        return Polyline;
    }

    public void setPolyline(String polyline) {
        Polyline = polyline;
    }
}
