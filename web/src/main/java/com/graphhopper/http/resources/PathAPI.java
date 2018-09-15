package com.graphhopper.http.resources;

import com.codahale.metrics.annotation.Timed;
import control.PathControl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/path")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PathAPI {

    @POST
    @Timed
    @Path("/search")
    public List<model.entity.Path> searchPath(model.entity.Path path){
        PathControl pathControl = new PathControl();
        return pathControl.findPath(path);
    }
}
