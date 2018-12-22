package control;

import com.graphhopper.GHRequest;
import com.graphhopper.PathWrapper;
import com.graphhopper.http.GraphHopperApplication;
import com.graphhopper.routing.AlternativeRoute;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.util.*;
import com.graphhopper.routing.weighting.FastestWeighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.*;
import com.graphhopper.util.details.PathDetailsBuilderFactory;
import model.Instruction;
import model.LatLng;
import model.ResultPath;
import model.entity.Edge;
import model.entity.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PathControl {
    private Graph graph;
    private static final int MAX_PATH = 30;

    public PathControl() {
        graph = GraphHopperApplication.graphHopper.getGraphHopperStorage().getBaseGraph();
    }

    public List<Path> findPath(Path pathRequest){
        GHRequest req = new GHRequest(pathRequest.getStartLat(),pathRequest.getStartLon(), pathRequest.getEndLat(), pathRequest.getEndLon()).
                setLocale(Locale.US);
        FlagEncoder encoder = new CarFlagEncoder();
        EncodingManager em = new EncodingManager(encoder);
        QueryResult qrStart = GraphHopperApplication.graphHopper.getLocationIndex().findClosest(pathRequest.getStartLat(),pathRequest.getStartLon(), EdgeFilter.ALL_EDGES);
        QueryResult qrEnd = GraphHopperApplication.graphHopper.getLocationIndex().findClosest(pathRequest.getEndLat(), pathRequest.getEndLon(), EdgeFilter.ALL_EDGES);
        AlternativeRoute alternativeRoute = new AlternativeRoute(graph, new FastestWeighting(encoder), TraversalMode.EDGE_BASED_2DIR);
        alternativeRoute.setMaxPaths(MAX_PATH);
        List<com.graphhopper.routing.Path> paths = alternativeRoute.calcPaths(qrStart.getClosestNode(), qrEnd.getClosestNode());
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
        ArrayList<com.graphhopper.routing.Path> pathToMerge = new ArrayList<com.graphhopper.routing.Path>();
        List<Path> listPathFound = new ArrayList<>();
        for(com.graphhopper.routing.Path pathFound: paths){
            pathToMerge.add(pathFound);
            PathWrapper altResponse = new PathWrapper();
            pathMerger.doWork(altResponse, pathToMerge, tr);
            Path path = new Path();
            path.setStartLat(pathRequest.getStartLat());
            path.setStartLon(pathRequest.getStartLon());
            path.setEndLat(pathRequest.getEndLat());
            path.setEndLon(pathRequest.getEndLon());
            path.setDistance(altResponse.getDistance());
            path.setMovingTime(altResponse.getTime());
//            PointList pointList = altResponse.getPoints();
//            for(int i=0; i<pointList.getSize(); i++){
//                System.out.println("coordinateList.add(new LatLong(" + pointList.getLat(i)+ ", "+ pointList.getLon(i)+"));");
//                System.out.println(pointList.getLat(i) + ", " + pointList.getLon(i));
//            }
//            System.out.println("==============================");
            path.setPolyline(PolylineManager.encodePolyline(altResponse.getPoints()));
            List<EdgeIteratorState> edgeIteratorStates = pathFound.calcEdges();
            List<Edge> edges = new ArrayList<>();
            for (EdgeIteratorState e: edgeIteratorStates) {
                Edge edge = new Edge();
                long existingFlags = e.getFlags();
                edge.setId(e.getEdge());
                edge.setBaseNode(e.getBaseNode());
                edge.setAdjustNode(e.getAdjNode());
                edge.setTrafficStatus(0);
                edge.setDistance(e.getDistance());
                edge.setSpeed(encoder.getSpeed(existingFlags));
                edge.setPolyline(getPolylineBetween2Nodes(e.getBaseNode(), e.getAdjNode()));
                edges.add(edge);
            }
            path.setEdges(edges);
//            System.out.println("distance sdfsadf: " + PolylineManager.encodePolyline(altResponse.getPoints()));
//            System.out.println(altResponse.getPoints());
            listPathFound.add(path);
            pathToMerge.clear();
        }
        return listPathFound;
    }

    private String getPolylineBetween2Nodes(int baseNode, int adjNode) {
        FlagEncoder encoder = new CarFlagEncoder();
        EncodingManager em = new EncodingManager(encoder);
        Graph graph = GraphHopperApplication.graphHopper.getGraphHopperStorage().getBaseGraph();
//        List<Path> paths = alternativeRoute.calcPaths(baseNode, adjNode);
        com.graphhopper.routing.Path path = new Dijkstra(graph, new FastestWeighting(encoder), TraversalMode.EDGE_BASED_2DIR).calcPath(baseNode, adjNode);
        return PolylineManager.encodePolyline(path.calcPoints());
    }
}
