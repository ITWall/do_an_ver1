package com.graphhopper.http.resources;

import com.codahale.metrics.annotation.Timed;
import control.PersonControl;
import io.dropwizard.hibernate.UnitOfWork;
import model.entity.Edge;
import model.response.ResponseRegister;
import model.person.Account;
import model.person.Fullname;
import model.person.Person;
import model.person.Status;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("/api/person")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonAPI {

    private PersonControl personControl;

    public PersonAPI(PersonControl personControl) {
        this.personControl = personControl;
    }

    @POST
    @Timed
    @Path("/register")
    @UnitOfWork
    public ResponseRegister searchPath(Person person){
        return personControl.register(person);
    }

    @GET
    @UnitOfWork
    public List<Fullname> findByName(@QueryParam("name") Optional<String> name) {
        if (name.isPresent()) {
            return personControl.findByName(name.get());
        } else {
            return personControl.findAllName();
        }
    }

    @GET
    @UnitOfWork
    @Path("all")
    public List<Fullname> findAll() {
        return personControl.findAllName();
    }

    @GET
    @UnitOfWork
    @Path("allstatus")
    public List<Status> findAllStatus() {
        return personControl.findAllStatus();
    }

    @GET
    @UnitOfWork
    @Path("allaccount")
    public List<Account> findAllAccount() {
        return personControl.findAllAccount();
    }

    @GET
    @UnitOfWork
    @Path("allperson")
    public List<Person> findAllPerson() {
        return personControl.findAllPerson();
    }

    @GET
    @UnitOfWork
    @Path("login")
    public List<Person> findPersonById(@QueryParam("email") Optional<String> email, @QueryParam("password") Optional<String> password) {
        return personControl.findByEmailPasswordPerson(email.get(), password.get());
    }

    @GET
    @UnitOfWork
    @Path("account/insert")
    public ResponseRegister insertAccount(@QueryParam("email") String email, @QueryParam("password") String password) {
        Account acc = new Account();
        acc.setEmail(email);
        acc.setPassword(password);
        acc.setStatus(personControl.findByStatus("OK").get(0));
        return personControl.getAccountDAO().insertAccount(acc);
    }

    @GET
    @UnitOfWork
    @Path("fullname/find")
    public List<Fullname> insertFullname(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname) {
        return personControl.getFullnameDAO().findByFirstnameAndLastname(firstname, lastname);
    }

    @GET
    @UnitOfWork
    @Path("device/getAll")
    public List<Fullname> getAllDevice() {
        return personControl.getAllDevices();
    }

    @POST
    @Timed
    @Path("/edge/insert")
    @UnitOfWork
    public ResponseRegister insertEdge(Edge edge){
        return personControl.insertEdge(edge);
    }
}
