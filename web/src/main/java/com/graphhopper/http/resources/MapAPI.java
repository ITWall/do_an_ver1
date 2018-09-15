package com.graphhopper.http.resources;

import com.codahale.metrics.annotation.Timed;
import control.RatingControl;
import io.dropwizard.hibernate.UnitOfWork;
import model.entity.Edge;
import model.entity.Rating;
import model.response.Response;
import model.response.ResponseRegister;
import model.person.Account;
import model.person.Fullname;
import model.person.Person;
import model.person.Status;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("/api/map")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MapAPI {

    private RatingControl ratingControl;

    public MapAPI(RatingControl ratingControl) {
        this.ratingControl = ratingControl;
    }

    @POST
    @Timed
    @Path("/register")
    @UnitOfWork
    public ResponseRegister searchPath(Person person){
        return ratingControl.register(person);
    }

    @GET
    @UnitOfWork
    public List<Fullname> findByName(@QueryParam("name") Optional<String> name) {
        if (name.isPresent()) {
            return ratingControl.findByName(name.get());
        } else {
            return ratingControl.findAllName();
        }
    }

    @GET
    @UnitOfWork
    @Path("all")
    public List<Fullname> findAll() {
        return ratingControl.findAllName();
    }

    @GET
    @UnitOfWork
    @Path("allstatus")
    public List<Status> findAllStatus() {
        return ratingControl.findAllStatus();
    }

    @GET
    @UnitOfWork
    @Path("allaccount")
    public List<Account> findAllAccount() {
        return ratingControl.findAllAccount();
    }

    @GET
    @UnitOfWork
    @Path("allperson")
    public List<Person> findAllPerson() {
        return ratingControl.findAllPerson();
    }

    @GET
    @UnitOfWork
    @Path("login")
    public List<Person> findPersonById(@QueryParam("email") Optional<String> email, @QueryParam("password") Optional<String> password) {
        return ratingControl.findByEmailPasswordPerson(email.get(), password.get());
    }

    @GET
    @UnitOfWork
    @Path("account/insert")
    public ResponseRegister insertAccount(@QueryParam("email") String email, @QueryParam("password") String password) {
        Account acc = new Account();
        acc.setEmail(email);
        acc.setPassword(password);
        acc.setStatus(ratingControl.findByStatus("OK").get(0));
        return ratingControl.getAccountDAO().insertAccount(acc);
    }

    @GET
    @UnitOfWork
    @Path("fullname/find")
    public List<Fullname> insertFullname(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname) {
        return ratingControl.getFullnameDAO().findByFirstnameAndLastname(firstname, lastname);
    }

    @GET
    @UnitOfWork
    @Path("device/getAll")
    public List getAllDevice() {
        return ratingControl.getAllDevices();
    }

//    @POST
//    @Timed
//    @Path("/edge/insert")
//    @UnitOfWork
//    public ResponseRegister insertEdge(Edge edge){
//        return ratingControl.insertEdge(edge);
//    }

    @POST
    @Timed
    @Path("/rating/insert")
    @UnitOfWork
    public Response insertRating(Rating rating){
        return ratingControl.insertRating(rating);
    }

    @POST
    @Timed
    @Path("/rating/insertEdge")
    @UnitOfWork
    public Response insertEdge(Rating rating){
        return ratingControl.insertEdge(rating);
    }
}
