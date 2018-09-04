package control;

import model.dao.*;
import model.entity.Edge;
import model.response.ResponseRegister;
import model.person.Account;
import model.person.Fullname;
import model.person.Person;
import model.person.Status;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

public class PersonControl {
    private FullnameDAO fullnameDAO;
    private StatusDAO statusDAO;
    private AccountDAO accountDAO;
    private PersonDAO personDAO;
    private DeviceDAO deviceDAO;
    private EdgeDAO edgeDAO;
    public PersonControl() {

    }

    public PersonControl(FullnameDAO fullnameDAO, StatusDAO statusDAO, AccountDAO accountDAO, PersonDAO personDAO, DeviceDAO deviceDAO, EdgeDAO edgeDAO) {
        this.fullnameDAO = fullnameDAO;
        this.statusDAO = statusDAO;
        this.accountDAO = accountDAO;
        this.personDAO = personDAO;
        this.deviceDAO = deviceDAO;
        this.edgeDAO = edgeDAO;
    }

    public FullnameDAO getFullnameDAO() {
        return fullnameDAO;
    }

    public void setFullnameDAO(FullnameDAO fullnameDAO) {
        this.fullnameDAO = fullnameDAO;
    }

    public StatusDAO getStatusDAO() {
        return statusDAO;
    }

    public void setStatusDAO(StatusDAO statusDAO) {
        this.statusDAO = statusDAO;
    }

    public AccountDAO getAccountDAO() {
        return accountDAO;
    }

    public void setAccountDAO(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public PersonDAO getPersonDAO() {
        return personDAO;
    }

    public void setPersonDAO(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public ResponseRegister register(Person person){
        ResponseRegister responseRegister = new ResponseRegister();
        if(person != null){
//            if(personDAO.insertPerson(person).getCode() == 200){
//                responseRegister.setCode(200);
//                responseRegister.setMessage("Cannot insert person to table");
//            }
            if(fullnameDAO.insertFullname(person.getFullname()).getCode() == 200){
                List<Fullname> list = fullnameDAO.findByFirstnameAndLastname(person.getFullname().getFirstname(), person.getFullname().getLastname());
                System.out.println("size_list: " + list.size());
                person.setFullname(fullnameDAO.findByFirstnameAndLastname(person.getFullname().getFirstname(), person.getFullname().getLastname()).get(0));
                person.getAccount().setStatus(statusDAO.findByName("OK").get(0));
                if(getAccountDAO().insertAccount(person.getAccount()).getCode() == 200){
                    person.setAccount(getAccountDAO().findByEmailPassword(person.getAccount().getEmail(), person.getAccount().getPassword()).get(0));
                    if(getPersonDAO().insertPerson(person).getCode() == 200){
                        responseRegister.setCode(200);
                        responseRegister.setMessage("Insert person successfully");
                    }
                }else{
                    responseRegister.setCode(500);
                    responseRegister.setMessage("Cannot insert account to table");
                }
            }else{
                responseRegister.setCode(500);
                responseRegister.setMessage("Cannot insert fullname to table");
            }
        }else{
            responseRegister.setCode(100);
            responseRegister.setMessage("Person is null");
        }
        return responseRegister;
    }

    public List<Fullname> findByName(String name){
        return fullnameDAO.findByName(name);
    }

    public List<Fullname> findAllName(){
        return fullnameDAO.findAll();
    }

    public List<Status> findByStatus(String status){
        return statusDAO.findByName(status);
    }

    public List<Status> findAllStatus(){
        return statusDAO.findAll();
    }

    public List<Account> findAllAccount(){
        return accountDAO.findAll();
    }

    public List<Person> findAllPerson(){
        return personDAO.findAll();
    }

    public List<Person> findByEmailPasswordPerson(String email, String password){
        return personDAO.findByEmailPasswordAccount(email, password);
    }

    public List getAllDevices(){
        return deviceDAO.getAllDevices();
    }

    public ResponseRegister insertEdge(Edge edge){
        return edgeDAO.insertEdge(edge);
    }
}
