package control;

import com.google.transit.realtime.GtfsRealtime;
import com.graphhopper.GHRequest;
import com.graphhopper.http.GraphHopperApplication;
import com.graphhopper.routing.AlternativeRoute;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.*;
import com.graphhopper.routing.weighting.FastestWeighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.*;
import com.graphhopper.util.details.PathDetailsBuilderFactory;
import model.Instruction;
import model.TimerManagement;
import model.dao.*;
import model.entity.Edge;
import model.entity.Rating;
import model.response.Response;
import model.response.ResponseRegister;
import model.person.Account;
import model.person.Fullname;
import model.person.Person;
import model.person.Status;

import java.util.*;

public class PersonControl {
    private FullnameDAO fullnameDAO;
    private StatusDAO statusDAO;
    private AccountDAO accountDAO;
    private PersonDAO personDAO;
    private DeviceDAO deviceDAO;
    private EdgeDAO edgeDAO;
    private RatingDAO ratingDAO;

    private static final int DELAY = 0;
    private static final int PERIOD = 1000;

    public PersonControl() {

    }

    public PersonControl(FullnameDAO fullnameDAO, StatusDAO statusDAO, AccountDAO accountDAO, PersonDAO personDAO, DeviceDAO deviceDAO, EdgeDAO edgeDAO, RatingDAO ratingDAO) {
        this.fullnameDAO = fullnameDAO;
        this.statusDAO = statusDAO;
        this.accountDAO = accountDAO;
        this.personDAO = personDAO;
        this.deviceDAO = deviceDAO;
        this.edgeDAO = edgeDAO;
        this.ratingDAO = ratingDAO;
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

    public Response insertRating(Rating rating){
        rating = setupRating(rating);
        if(rating == null) {
            Response response = new Response();
            response.setCode(500);
            response.setMessage("Failed");
            return response;
        }
        return ratingDAO.insertRating(rating);
    }

    private Rating setupRating(Rating rating) {
        FlagEncoder encoder = new CarFlagEncoder();
        EncodingManager em = new EncodingManager(encoder);
        QueryResult queryResult = GraphHopperApplication.graphHopper.getLocationIndex().findClosest(rating.getPlace().getLatitude(), rating.getPlace().getLongitude(),EdgeFilter.ALL_EDGES);
        if(!queryResult.isValid()){
            return null;
        } else {
            EdgeIteratorState edge = queryResult.getClosestEdge();
            long existingFlags = edge.getFlags();
            rating.getPlace().getEdge().setId(edge.getEdge());
            rating.getPlace().getEdge().setAdjustNode(edge.getAdjNode());
            rating.getPlace().getEdge().setBaseNode(edge.getBaseNode());
            rating.getPlace().getEdge().setDistance(edge.getDistance());
            //save speed
            GraphHopperApplication.hashMapSpeed.put(edge.getEdge(), encoder.getSpeed(existingFlags));
            // set speed
//        edge.setFlags(encoder.setSpeed(existingFlags, encoder.getSpeed(existingFlags)/8/report.getLevel()));
            switch (rating.getTrafficStatus()){
                case 1:
                    edge.setFlags(encoder.setSpeed(existingFlags, 20));
                    rating.getPlace().getEdge().setSpeed(20);
                    break;
                case 2:
                    edge.setFlags(encoder.setSpeed(existingFlags, 10));
                    rating.getPlace().getEdge().setSpeed(10);
                    break;
                case 3:
                    edge.setFlags(encoder.setSpeed(existingFlags, 5));
                    rating.getPlace().getEdge().setSpeed(5);
                    break;
                case 4:
                    edge.setFlags(encoder.setSpeed(existingFlags, 0));
                    rating.getPlace().getEdge().setSpeed(0);
                    break;
            }
            System.out.println(encoder.getSpeed(edge.getFlags()));
            TimerManagement timerManagement = new TimerManagement(new Timer());
            timerManagement.getTimer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    timerManagement.setInterval(timerManagement.getInterval()-1);
                    if(timerManagement.getInterval() == 0){
                        edge.setFlags(encoder.setSpeed(existingFlags, GraphHopperApplication.hashMapSpeed.get(edge.getEdge())));
                        System.out.println(encoder.getSpeed(edge.getFlags()));
                        GraphHopperApplication.hashMapSpeed.remove(edge.getEdge());
                        timerManagement.getTimer().cancel();
                        GraphHopperApplication.hashMapDelay.remove(getKeyFromValue(GraphHopperApplication.hashMapDelay, timerManagement));
                        System.out.println("hashmap_size: " + GraphHopperApplication.hashMapDelay.size());
                    }
                }
            }, DELAY, PERIOD);
            GraphHopperApplication.hashMapDelay.put(edge.getEdge(), timerManagement);
            rating.getPlace().getEdge().setPolyline(getPolylineBetween2Nodes(edge.getBaseNode(), edge.getAdjNode()));
        }
        return rating;
    }

    private Integer getKeyFromValue(HashMap<Integer, TimerManagement> hashMap, TimerManagement timerManagement){
        Set<Map.Entry<Integer, TimerManagement>> set = hashMap.entrySet();
        for(Map.Entry<Integer, TimerManagement> entry: set){
            if (timerManagement.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return null;
    }

    private String getPolylineBetween2Nodes(int baseNode, int adjNode) {
        FlagEncoder encoder = new CarFlagEncoder();
        EncodingManager em = new EncodingManager(encoder);
        Graph graph = GraphHopperApplication.graphHopper.getGraphHopperStorage().getBaseGraph();
//        List<Path> paths = alternativeRoute.calcPaths(baseNode, adjNode);
        Path path = new Dijkstra(graph, new FastestWeighting(encoder), TraversalMode.EDGE_BASED_2DIR).calcPath(baseNode, adjNode);
        return PolylineManager.encodePolyline(path.calcPoints());
    }
}
