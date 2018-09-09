/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.http;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.http.cli.ImportCommand;
import com.graphhopper.http.resources.FindPathsAPI;
import com.graphhopper.http.resources.PersonAPI;
import com.graphhopper.http.resources.ReportAPI;
import com.graphhopper.http.resources.RootResource;
import com.graphhopper.reader.osm.OSMFileHeader;
import com.graphhopper.reader.osm.OSMReader;
import com.graphhopper.routing.AlternativeRoute;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.QueryGraph;
import com.graphhopper.routing.util.*;
import com.graphhopper.routing.weighting.ShortestWeighting;
import com.graphhopper.storage.*;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.LocationIndexTree;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.Parameters;
import control.PersonControl;
import io.dropwizard.Application;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import model.TimerManagement;
import model.dao.*;
import model.entity.Device;
import model.entity.Edge;
import model.entity.Place;
import model.entity.Rating;
import model.person.Account;
import model.person.Fullname;
import model.person.Person;
import model.person.Status;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public final class GraphHopperApplication extends Application<GraphHopperServerConfiguration> {
    public static HashMap<Integer, TimerManagement> hashMapDelay = new HashMap<>();
    public static HashMap<Integer, Double> hashMapSpeed = new HashMap<>();
    public static GraphHopper graphHopper;
    public static void main(String[] args) throws Exception {
        new GraphHopperApplication().run(args);

        graphHopper = new GraphHopper().forServer();
        graphHopper.setCHEnabled(false);
//        graphHopper.getLMFactoryDecorator().setEnabled(false);
        FlagEncoder encoder = new CarFlagEncoder();
        EncodingManager em = new EncodingManager(encoder);
//        graphHopper.setEncodingManager(em);
        graphHopper.load("E:\\graphhopper\\[asia_vietnam].osm-gh");

        GraphStorage graphStorage = graphHopper.getGraphHopperStorage();

        Graph graphBase = ((GraphHopperStorage) graphStorage).getBaseGraph();
        LocationIndex index = new LocationIndexTree(graphBase, new RAMDirectory("E:\\graphhopper\\[asia_vietnam].osm-gh", true));
        if (!index.loadExisting())
            throw new IllegalStateException("location index cannot be loaded!");
        QueryResult fromQR = index.findClosest(21.006018, 105.822773, EdgeFilter.ALL_EDGES);
        QueryResult toQR = index.findClosest(21.008743, 105.851526, EdgeFilter.ALL_EDGES);
//        System.out.println(fromQR.getClosestNode() +" - " + toQR.getClosestNode());
//        QueryGraph queryGraph = new QueryGraph((Graph) graphStorage);
//        queryGraph.lookup(fromQR, toQR);
        AlternativeRoute alternativeRoute = new AlternativeRoute(graphBase, new ShortestWeighting(encoder), TraversalMode.EDGE_BASED_2DIR);
        List<Path> paths = alternativeRoute.calcPaths(fromQR.getClosestNode(), toQR.getClosestNode());
//        for (Path path : paths) {
//            System.out.println(path.toDetailsString());
//            System.out.println(path.calcNodes());
//            System.out.println(path.calcEdges());
//        }
    }

    @Override
    public void initialize(Bootstrap<GraphHopperServerConfiguration> bootstrap) {
        bootstrap.addBundle(new GraphHopperBundle());
        bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/maps/", "index.html"));
        bootstrap.addCommand(new ImportCommand(bootstrap.getObjectMapper()));
        bootstrap.addBundle(hibernateBundle);
    }

    /**
     * Hibernate bundle.
     */
    private final HibernateBundle<GraphHopperServerConfiguration> hibernateBundle
            = new HibernateBundle<GraphHopperServerConfiguration>(
            Status.class, Fullname.class, Account.class, Person.class, Device.class, Edge.class, Place.class, Rating.class
    ) {
        @Override
        public DataSourceFactory getDataSourceFactory(
                GraphHopperServerConfiguration configuration
        ) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public void run(GraphHopperServerConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new RootResource());
//        environment.servlets().addFilter("cors", CORSFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "*");
//        environment.servlets().addFilter("ipfilter", new IPFilter(configuration.getGraphHopperConfiguration().get("jetty.whiteips", ""), configuration.getGraphHopperConfiguration().get("jetty.blackips", ""))).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "*");
        environment.jersey().register(new FindPathsAPI());
        environment.jersey().register(new ReportAPI());
        final FullnameDAO fullnameDAO
                = new FullnameDAO(hibernateBundle.getSessionFactory());
        final StatusDAO statusDAO
                = new StatusDAO(hibernateBundle.getSessionFactory());
        final AccountDAO accountDAO
                = new AccountDAO(hibernateBundle.getSessionFactory());
        final PersonDAO personDAO
                = new PersonDAO(hibernateBundle.getSessionFactory());
        final DeviceDAO deviceDAO
                = new DeviceDAO(hibernateBundle.getSessionFactory());
        final EdgeDAO edgeDAO
                = new EdgeDAO(hibernateBundle.getSessionFactory());
        final RatingDAO ratingDAO
                = new RatingDAO(hibernateBundle.getSessionFactory());
        PersonControl personControl = new PersonControl(fullnameDAO, statusDAO, accountDAO, personDAO, deviceDAO, edgeDAO, ratingDAO);
        environment.jersey().register(new PersonAPI(personControl));
    }
}
