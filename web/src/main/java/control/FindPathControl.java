package control;

import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.http.GraphHopperApplication;
import com.graphhopper.routing.AlternativeRoute;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.*;
import com.graphhopper.routing.weighting.FastestWeighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.*;
import com.graphhopper.util.details.PathDetailsBuilderFactory;
import model.Instruction;
import model.LatLng;
import model.ResultPath;
import model.google.Geocoding;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FindPathControl {
    private String startPoint;
    private String endPoint;
    private Graph graph;
    private String fromLocation;
    private String toLocation;
    private static final int MAX_PATH = 10;

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public FindPathControl() {
        graph = GraphHopperApplication.graphHopper.getGraphHopperStorage().getBaseGraph();
    }

    public ResultPath findPathByPoint(){
        ResultPath resultPath = new ResultPath();
        String start[] = startPoint.split(",");
        Double start_lat = Double.valueOf(start[0]);
        Double start_lng = Double.valueOf(start[1]);
        LatLng startLatlng = new LatLng(start_lat, start_lng);
        String end[] = endPoint.split(",");
        Double end_lat = Double.valueOf(end[0]);
        Double end_lng = Double.valueOf(end[1]);
        LatLng endLatlng = new LatLng(end_lat, end_lng);
        resultPath.setStartPoint(startLatlng);
        resultPath.setEndPoint(endLatlng);
        resultPath.setInstructionList(getAllInstructions(startLatlng, endLatlng));
        return resultPath;
    }

    public ResultPath findPathByLocation(){
        ResultPath resultPath = new ResultPath();
        LatLng startLatlng = null;
        LatLng endLatlng = null;
        try {
            startLatlng = getLatlnFromLocation(fromLocation);
            endLatlng = getLatlnFromLocation(toLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultPath.setStartPoint(startLatlng);
        resultPath.setEndPoint(endLatlng);
        resultPath.setInstructionList(getAllInstructions(startLatlng, endLatlng));
        return resultPath;
    }

    private List<Instruction> getAllInstructions(LatLng start, LatLng end){
        GHRequest req = new GHRequest(start.getLatitude(),start.getLongitude(), end.getLatitude(), end.getLongitude()).
                setLocale(Locale.US);
        FlagEncoder encoder = new CarFlagEncoder();
        EncodingManager em = new EncodingManager(encoder);
        QueryResult qrStart = GraphHopperApplication.graphHopper.getLocationIndex().findClosest(start.getLatitude(), start.getLongitude(), EdgeFilter.ALL_EDGES);
        System.out.println(qrStart.getClosestNode());
        QueryResult qrEnd = GraphHopperApplication.graphHopper.getLocationIndex().findClosest(end.getLatitude(), end.getLongitude(), EdgeFilter.ALL_EDGES);
        System.out.println(qrEnd.getClosestNode());
        //change speed
        QueryResult qrChanged = GraphHopperApplication.graphHopper.getLocationIndex().findClosest(21.02821367297054, 105.81239605275682, EdgeFilter.ALL_EDGES);
        if(!qrChanged.isValid())
            throw new RuntimeException("Cannot find nearby location " + 21.02821367297054+","+105.81239605275682);
        EdgeIteratorState edge = qrChanged.getClosestEdge();
        // use existing flags to reuse access information
        long existingFlags = edge.getFlags();
        System.out.println("Speed: " + encoder.getSpeed(existingFlags));
        // set speed
//        edge.setFlags(encoder.setSpeed(existingFlags, encoder.getSpeed(existingFlags)/8));
        //======================
        AlternativeRoute alternativeRoute = new AlternativeRoute(graph, new FastestWeighting(encoder), TraversalMode.EDGE_BASED_2DIR);
        alternativeRoute.setMaxPaths(MAX_PATH);
        List<Path> paths = alternativeRoute.calcPaths(qrStart.getClosestNode(), qrEnd.getClosestNode());
        HintsMap hints = req.getHints();
        boolean tmpCalcPoints = hints.getBool(Parameters.Routing.CALC_POINTS, true);
        double wayPointMaxDistance = hints.getDouble(Parameters.Routing.WAY_POINT_MAX_DISTANCE, 1d);
        DouglasPeucker peucker = new DouglasPeucker().setMaxDistance(wayPointMaxDistance);
        boolean tmpEnableInstructions = hints.getBool(Parameters.Routing.INSTRUCTIONS, true);
        PathDetailsBuilderFactory pathBuilderFactory = new PathDetailsBuilderFactory();
        TranslationMap trMap = new TranslationMap().doImport();
        Translation tr = trMap.getWithFallBack(Locale.US);
        PathMerger pathMerger = new PathMerger().
                setCalcPoints(tmpCalcPoints).
                setDouglasPeucker(peucker).
                setEnableInstructions(tmpEnableInstructions).
                setPathDetailsBuilders(pathBuilderFactory, req.getPathDetails()).
                setSimplifyResponse(wayPointMaxDistance > 0);
        ArrayList<Path> pathToMerge = new ArrayList<Path>();
        List<Instruction> instructions = new ArrayList<Instruction>();
        for(Path path: paths){
            Instruction instruction = new Instruction();
            pathToMerge.add(path);
            PathWrapper altResponse = new PathWrapper();
            pathMerger.doWork(altResponse, pathToMerge, tr);
            instruction.setDistance(altResponse.getDistance());
            instruction.setTime(altResponse.getTime());
            PointList pointList = altResponse.getPoints();
            for(int i=0; i<pointList.getSize(); i++){
//                System.out.println("coordinateList.add(new LatLong(" + pointList.getLat(i)+ ", "+ pointList.getLon(i)+"));");
//                System.out.println(pointList.getLat(i) + ", " + pointList.getLon(i));
            }
//            System.out.println("==============================");
            instruction.setPolyline(PolylineManager.encodePolyline(altResponse.getPoints()));
//            System.out.println("distance sdfsadf: " + PolylineManager.encodePolyline(altResponse.getPoints()));
//            System.out.println(altResponse.getPoints());
            pathToMerge.clear();
            instructions.add(instruction);
        }
        return instructions;
    }

    public LatLng getLatlnFromLocation(String location) throws IOException {
        LatLng latLng = new LatLng();
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl("https://maps.googleapis.com/maps/api/geocode/").
                addConverterFactory(JacksonConverterFactory.create()).
                build();
        GeoLocationService geoLocationService = retrofit.create(GeoLocationService.class);
        Response<Geocoding> geocodingResponse = geoLocationService.getAddressInfo(location, "AIzaSyD2CmgRkseL7s8m3RtFN2kvriXgCzObzoM").execute();
        latLng.setLatitude(geocodingResponse.body().getResults().get(0).getGeometry().getLocation().getLat());
        latLng.setLongitude(geocodingResponse.body().getResults().get(0).getGeometry().getLocation().getLng());
        return latLng;
    }
}
