package japps.graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;

public class Neo4jGraphService extends AbstractGraphService {
    
    Map<String, String> config = new HashMap<String, String>();
    String dbFileName = "db";
    File dbFile = new File(dbFileName);
    Label pointLabel = DynamicLabel.label( "Point" );
    String mapName = null;
    RelationshipType routeRelationshipType = DynamicRelationshipType.withName( "ROUTE" );
    private static GraphDatabaseService graphDb = null;
    private static Neo4jGraphService instance = new Neo4jGraphService();
    
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
    
    public synchronized static Neo4jGraphService getInstance() {
        return instance;
    }
    
    private Neo4jGraphService() {
        File dbFile = new File(dbFileName);
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbFile);
        registerShutdownHook( graphDb );
        try ( Transaction tx = graphDb.beginTx() ) {
            graphDb.schema()
                    .constraintFor( pointLabel )
                    .assertPropertyIsUnique( "name" )
                    .create();
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
            String queryString = "MERGE (n:Point {name: {name}, map: {map}}) RETURN n";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put( "name", nodeName );
            parameters.put( "map", mapName );
            resultIterator = graphDb.execute( queryString, parameters ).columnAs( "n" );
            result = resultIterator.next();
            tx.success();
        }
        return result;
    }
    
    private Node getNode(String map, String name) {
        Result result = graphDb.execute( "MATCH (n:Point {name: '" + name + "', map:'" + map + "'}) RETURN n" );
        Node node = null;
        Map<String,Object> row = null;
        while(result.hasNext()) {
            row = result.next();
            node = (Node)row.get("n");
        }
        
        return node;
    }

    @Override
    public boolean loadRoute(String startPoint, String endPoint, double distance) {
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
    public boolean afterLoad() {
        return true;
    }
    
    public RouteResult findRoute(String mapName, String startPoint, String endPoint, double autonomy, double fuelPrice) {
        WeightedPath path = null;
        RouteResult result = null;
        
        try ( Transaction tx = graphDb.beginTx() )
        {
            Iterator<Node> nodes = graphDb.getAllNodes().iterator();
            
            while(nodes.hasNext()) {
                Node item = nodes.next();
                System.out.println(item.getProperty("name"));
            }
            
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
            result = new RouteResult(points, path.weight());
            
            tx.success();
        }
        
        return result;
    }
    
}
