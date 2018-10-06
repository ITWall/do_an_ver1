package control;

import com.graphhopper.http.GraphHopperApplication;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import model.Report;
import model.response.Response;
import model.TimerManagement;

import java.util.*;

public class ReportControl {
    private int delay = 0;
    private int period = 1000;

    public Response receiveReport(Report report){
        Response resultReport = new Response();
        QueryResult qrReport = GraphHopperApplication.graphHopper.getLocationIndex().findClosest(report.getLatLng().getLatitude(), report.getLatLng().getLongitude(),EdgeFilter.ALL_EDGES);
        if(!qrReport.isValid()){
            resultReport.setCode(101);
            resultReport.setMessage("Cannot find nearby location " + report.getLatLng().getLatitude()+","+report.getLatLng().getLongitude());
            return resultReport;
        }
        EdgeIteratorState edge = qrReport.getClosestEdge();
        FlagEncoder encoder = new CarFlagEncoder();
        EncodingManager em = new EncodingManager(encoder);
        long existingFlags = edge.getFlags();
        //save speed
        GraphHopperApplication.hashMapSpeed.put(edge.getEdge(), encoder.getSpeed(existingFlags));
        // set speed
//        edge.setFlags(encoder.setSpeed(existingFlags, encoder.getSpeed(existingFlags)/8/report.getLevel()));
        switch (report.getLevel()){
            case 1:
                edge.setFlags(encoder.setSpeed(existingFlags, 20));
                break;
            case 2:
                edge.setFlags(encoder.setSpeed(existingFlags, 10));
                break;
            case 3:
                edge.setFlags(encoder.setSpeed(existingFlags, 5));
                break;
            case 4:
                edge.setFlags(encoder.setSpeed(existingFlags, 0));
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
//                    GraphHopperApplication.hashMapDelay.remove(getKeyFromValue(GraphHopperApplication.hashMapDelay, timerManagement));
//                    System.out.println("hashmap_size: " + GraphHopperApplication.hashMapDelay.size());
                }
            }
        }, delay, period);
//        GraphHopperApplication.hashMapDelay.put(edge.getEdge(), timerManagement);
        resultReport.setCode(200);
        resultReport.setMessage("Success");
        return resultReport;
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
}
