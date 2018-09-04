package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResultPath {
    private LatLng startPoint;
    private LatLng endPoint;
    private List<Instruction> instructionList;

    @JsonProperty
    public LatLng getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(LatLng startPoint) {
        this.startPoint = startPoint;
    }

    @JsonProperty
    public LatLng getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LatLng endPoint) {
        this.endPoint = endPoint;
    }

    @JsonProperty
    public List<Instruction> getInstructionList() {
        return instructionList;
    }

    public void setInstructionList(List<Instruction> instructionList) {
        this.instructionList = instructionList;
    }

    public ResultPath(LatLng startPoint, LatLng endtPoint, List<Instruction> instructionList) {
        this.startPoint = startPoint;
        this.endPoint = endtPoint;
        this.instructionList = instructionList;
    }

    public ResultPath() {

    }
}
