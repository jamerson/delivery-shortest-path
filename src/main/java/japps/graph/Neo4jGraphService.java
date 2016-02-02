package japps.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jGraphService extends AbstractGraphService {
    String dbFileName = "db";
    File dbFile = new File(dbFileName);
    Label pointLabel = DynamicLabel.label( "Point" );
    String mapName = null;
    RelationshipType routeRelationshipType = DynamicRelationshipType.withName( "ROUTE" );
    private static GraphDatabaseService graphDb = null;
    
    private static void registerShutdownHook( final GraphDatabaseService graphDb ) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
    
    protected Neo4jGraphService() {
        File dbFile = new File(dbFileName);
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbFile);
        registerShutdownHook( graphDb );
        try ( Transaction tx = graphDb.beginTx() ) {
            //check if the db already has the constraint created
            if(!graphDb.schema().getConstraints(pointLabel).iterator().hasNext()) {
                graphDb.schema()
                .constraintFor( pointLabel )
                .assertPropertyIsUnique( "map_point" )
                .create();
            }

            tx.success();
        }
    }

    @Override
    public boolean beforeLoad(String mapName) {
        this.mapName = mapName;
        return true;
    }
    
    private Node createNode(String nodeName) {
        Node result = null;
        ResourceIterator<Node> resultIterator = null;
        try ( Transaction tx = graphDb.beginTx() )
        {
            String queryString = "MERGE (n:Point {name: {name}, map_point: {map_point}}) RETURN n";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put( "name", nodeName);
            parameters.put( "map_point", String.format("%1$s_%2$s", mapName, nodeName));
            resultIterator = graphDb.execute( queryString, parameters ).columnAs( "n" );
            result = resultIterator.next();
            tx.success();
        }
        return result;
    }
    
    Node getNode(String map, String nodeName) {
        String queryString = "MATCH (n:Point {map_point: {map_point}}) RETURN n";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put( "map_point", String.format("%1$s_%2$s", mapName, nodeName));
        Result result = graphDb.execute( queryString, parameters );
        Node node = null;
        Map<String,Object> row = null;
        while(result.hasNext()) {
            row = result.next();
            node = (Node)row.get("n");
        }
        
        return node;
    }

    @Override
    boolean loadRoute(String startPoint, String endPoint, double distance) {
        try ( Transaction tx = graphDb.beginTx() )
        {
            //creating the nodes
            Node startNode = createNode(startPoint);
            Node endNode = createNode(endPoint);
    
            //creating the relationship
            Relationship relationship = startNode.createRelationshipTo(endNode, routeRelationshipType);
            relationship.setProperty("distance", distance );
            
            tx.success();
        }
        
        return true;
    }

    @Override
    boolean afterLoad() {
        return true;
    }
    
    public RouteResult findRoute(String mapName, String startPoint, String endPoint, double autonomy, double fuelPrice) {
        WeightedPath path = null;
        RouteResult result = null;
        
        if(startPoint.equals(endPoint)) {
            return null;
        }
        
        try ( Transaction tx = graphDb.beginTx() )
        {            
            Node startNode = getNode(mapName, startPoint);
            Node endNode = getNode(mapName, endPoint);
            PathFinder<WeightedPath> finder = GraphAlgoFactory.dijkstra(
                    PathExpanders.forTypeAndDirection(
                            routeRelationshipType, Direction.BOTH ), 
                            CommonEvaluators.doubleCostEvaluator("distance") );
            path = finder.findSinglePath(startNode, endNode);
            if(path == null) {
                return null;
            }
            
            Iterator<Node> nodeIterator = path.nodes().iterator();
            ArrayList<String> points = new ArrayList<String>();
            while(nodeIterator.hasNext()) {
                points.add((String)nodeIterator.next().getProperty("name"));
            }
            double cost = (path.weight() / autonomy) * fuelPrice;
            result = new RouteResult(points, cost);
            
            tx.success();
        }
        
        return result;
    }
    
}
