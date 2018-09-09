package com.graphhopper.http.resources;

import com.codahale.metrics.annotation.Timed;
import control.ReportControl;
import model.Report;
import model.response.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/report")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReportAPI {
    @POST
    @Timed
    @Path("/location")
    public Response searchPath(Report report){
        ReportControl reportControl = new ReportControl();
        return reportControl.receiveReport(report);
    }
}
