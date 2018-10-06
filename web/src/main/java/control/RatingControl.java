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

public class RatingControl {
    private FullnameDAO fullnameDAO;
    private StatusDAO statusDAO;
    private AccountDAO accountDAO;
    private PersonDAO personDAO;
    private DeviceDAO deviceDAO;
    private EdgeDAO edgeDAO;
    private RatingDAO ratingDAO;

    private static final int DELAY = 0;
    private static final int PERIOD = 300;

    private List<Integer> listIDEdge;

    public RatingControl() {

    }

    public RatingControl(FullnameDAO fullnameDAO, StatusDAO statusDAO, AccountDAO accountDAO, PersonDAO personDAO, DeviceDAO deviceDAO, EdgeDAO edgeDAO, RatingDAO ratingDAO) {
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

    public ResponseRegister register(Person person) {
        ResponseRegister responseRegister = new ResponseRegister();
        if (person != null) {
//            if(personDAO.insertPerson(person).getCode() == 200){
//                responseRegister.setCode(200);
//                responseRegister.setMessage("Cannot insert person to table");
//            }
            if (fullnameDAO.insertFullname(person.getFullname()).getCode() == 200) {
                List<Fullname> list = fullnameDAO.findByFirstnameAndLastname(person.getFullname().getFirstname(), person.getFullname().getLastname());
                System.out.println("size_list: " + list.size());
                person.setFullname(fullnameDAO.findByFirstnameAndLastname(person.getFullname().getFirstname(), person.getFullname().getLastname()).get(0));
                person.getAccount().setStatus(statusDAO.findByName("OK").get(0));
                if (getAccountDAO().insertAccount(person.getAccount()).getCode() == 200) {
                    person.setAccount(getAccountDAO().findByEmailPassword(person.getAccount().getEmail(), person.getAccount().getPassword()).get(0));
                    if (getPersonDAO().insertPerson(person).getCode() == 200) {
                        responseRegister.setCode(200);
                        responseRegister.setMessage("Insert person successfully");
                    }
                } else {
                    responseRegister.setCode(500);
                    responseRegister.setMessage("Cannot insert account to table");
                }
            } else {
                responseRegister.setCode(500);
                responseRegister.setMessage("Cannot insert fullname to table");
            }
        } else {
            responseRegister.setCode(100);
            responseRegister.setMessage("Person is null");
        }
        return responseRegister;
    }

    public List<Fullname> findByName(String name) {
        return fullnameDAO.findByName(name);
    }

    public List<Fullname> findAllName() {
        return fullnameDAO.findAll();
    }

    public List<Status> findByStatus(String status) {
        return statusDAO.findByName(status);
    }

    public List<Status> findAllStatus() {
        return statusDAO.findAll();
    }

    public List<Account> findAllAccount() {
        return accountDAO.findAll();
    }

    public List<Person> findAllPerson() {
        return personDAO.findAll();
    }

    public List<Person> findByEmailPasswordPerson(String email, String password) {
        return personDAO.findByEmailPasswordAccount(email, password);
    }

    public List getAllDevices() {
        return deviceDAO.getAllDevices();
    }

    public ResponseRegister insertEdge(Edge edge) {
        return edgeDAO.insertEdge(edge);
    }

    public Response insertRating(Rating rating) {
        rating = setupRating(rating);
        if (rating == null) {
            Response response = new Response();
            response.setCode(500);
            response.setMessage("Failed");
            return response;
        }
        return ratingDAO.insertRating(rating);
//        return new Response(200, "OK");
    }

    private Rating setupRating(Rating rating) {
        listIDEdge = new ArrayList<>();
        FlagEncoder encoder = new CarFlagEncoder();
        EncodingManager em = new EncodingManager(encoder);
        QueryResult queryResult = GraphHopperApplication.graphHopper.getLocationIndex().findClosest(rating.getPlace().getLatitude(), rating.getPlace().getLongitude(), EdgeFilter.ALL_EDGES);
        if (!queryResult.isValid()) {
            return null;
        } else {
            EdgeIteratorState edgeIteratorState = queryResult.getClosestEdge();
            long existingFlags = edgeIteratorState.getFlags();
            EdgeIterator iterBase = GraphHopperApplication.edgeExplorer.setBaseNode(queryResult.getClosestNode());
            List<Edge> edges = new ArrayList<>();
            List<EdgeIterator> edgeIteratorList = new ArrayList<>();
            while (iterBase.next()) {
                edgeIteratorList.add(iterBase);
                Edge edge = new Edge();
                edge.setId(iterBase.getEdge());
                edge.setAdjustNode(iterBase.getAdjNode());
                edge.setBaseNode(iterBase.getBaseNode());
                edge.setDistance(iterBase.getDistance());
                System.out.println(encoder.getSpeed(iterBase.getFlags()));
                //save speed
                GraphHopperApplication.hashMapSpeed.put(iterBase.getEdge(), encoder.getSpeed(iterBase.getFlags()));
                // set speed
//        edge.setFlags(encoder.setSpeed(existingFlags, encoder.getSpeed(existingFlags)/8/report.getLevel()));
                switch (rating.getTrafficStatus()) {
                    case 1:
                        iterBase.setFlags(encoder.setSpeed(existingFlags, 20));
                        edge.setSpeed(20);
                        break;
                    case 2:
                        iterBase.setFlags(encoder.setSpeed(existingFlags, 10));
                        edge.setSpeed(10);
                        break;
                    case 3:
                        iterBase.setFlags(encoder.setSpeed(existingFlags, 5));
                        edge.setSpeed(5);
                        break;
                    case 4:
                        iterBase.setFlags(encoder.setSpeed(existingFlags, 0));
                        edge.setSpeed(0);
                        break;
                }
                System.out.println(encoder.getSpeed(iterBase.getFlags()));
                System.out.println("aaaaaaaaaa: " + iterBase.getEdge());
                listIDEdge.add(iterBase.getEdge());
                edge.setPolyline(getPolylineBetween2Nodes(iterBase.getBaseNode(), iterBase.getAdjNode()));
                edges.add(edge);
            }
            for(int i=0; i<listIDEdge.size(); i++) {
                TimerManagement timerManagement = new TimerManagement(new Timer());
//                timerManagement = setupTimer(timerManagement, iterBase, encoder, existingFlags);
                int finalI = i;
                if(GraphHopperApplication.hashMapDelay.get(listIDEdge.get(finalI)) != null) {
                    GraphHopperApplication.hashMapDelay.get(listIDEdge.get(finalI)).getTimer().cancel();
                }
                GraphHopperApplication.hashMapDelay.put(listIDEdge.get(finalI), timerManagement);
//                if (GraphHopperApplication.hashMapDelay.get(listIDEdge.get(finalI))) lay ra roi remove. sau khi lay thi huy timer cua no

                timerManagement.getTimer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        timerManagement.setInterval(timerManagement.getInterval() - 1);
                        System.out.println("id: " + listIDEdge.get(finalI) + " / time left: " + timerManagement.getInterval() + "\n");
                        if (timerManagement.getInterval() == 0) {
                            System.out.println("id: " + listIDEdge.get(finalI) + " finish!");
                            timerManagement.getTimer().cancel();
                            GraphHopperApplication.hashMapDelay.remove(getKeyFromValue(GraphHopperApplication.hashMapDelay, timerManagement));
                            edgeIteratorList.get(finalI).setFlags(encoder.setSpeed(edgeIteratorList.get(finalI).getFlags(), GraphHopperApplication.hashMapSpeed.get(edgeIteratorList.get(finalI).getEdge())));
                            ratingDAO.updateEdge(edges.get(finalI), 0);
                            System.out.println(encoder.getSpeed(edgeIteratorList.get(finalI).getFlags()));
                            GraphHopperApplication.hashMapSpeed.remove(listIDEdge.get(finalI));
                        }
                    }
                }, DELAY, PERIOD);
            }
            rating.getPlace().setEdges(edges);
        }
        return rating;
    }

    private Integer getKeyFromValue(HashMap<Integer, TimerManagement> hashMap, TimerManagement timerManagement) {
        Set<Map.Entry<Integer, TimerManagement>> set = hashMap.entrySet();
        for (Map.Entry<Integer, TimerManagement> entry : set) {
            if (timerManagement.equals(entry.getValue())) {
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

    public Response insertEdge(Rating rating) {
        rating = setupRating(rating);
        if (rating == null) {
            Response response = new Response();
            response.setCode(500);
            response.setMessage("Failed");
            return response;
        }
        return ratingDAO.insertAllEdges(rating);
    }
}
