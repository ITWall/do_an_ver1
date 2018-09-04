package com.graphhopper.http.resources;

import com.codahale.metrics.annotation.Timed;
import control.FindPathControl;
import model.ResultPath;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/api/search")
@Produces(MediaType.APPLICATION_JSON)
public class FindPathsAPI {
    @GET
    @Timed
    @Path("/point")
    public ResultPath searchPath(@QueryParam("startPoint") String startPoint, @QueryParam("endPoint") String endPoint){
        FindPathControl findPathControl = new FindPathControl();
        findPathControl.setStartPoint(startPoint);
        findPathControl.setEndPoint(endPoint);
        return findPathControl.findPathByPoint();
    }

    @GET
    @Timed
    @Path("/location")
    public ResultPath searchPathByLocation(@QueryParam("fromLocation") String fromLocation, @QueryParam("toLocation") String toLocation) {
        FindPathControl findPathControl = new FindPathControl();
        findPathControl.setFromLocation(fromLocation);
        findPathControl.setToLocation(toLocation);
        System.out.println(fromLocation +" - " + toLocation);
        return findPathControl.findPathByLocation();
    }
}
