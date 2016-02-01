//package japps;
//
//import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.factory.GraphDatabaseFactory;
//
//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
//
//import java.io.File;
//
///**
// * Unit test for simple App.
// */
//public class AppTest 
//    extends TestCase
//{
//    /**
//     * Create the test case
//     *
//     * @param testName name of the test case
//     */
//    public AppTest( String testName )
//    {
//        super( testName );
//    }
//
//    /**
//     * @return the suite of tests being tested
//     */
//    public static Test suite()
//    {
//        return new TestSuite( AppTest.class );
//    }
//    
//    private static void registerShutdownHook( final GraphDatabaseService graphDb )
//    {
//        // Registers a shutdown hook for the Neo4j instance so that it
//        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
//        // running application).
//        Runtime.getRuntime().addShutdownHook( new Thread()
//        {
//            @Override
//            public void run()
//            {
//                graphDb.shutdown();
//            }
//        } );
//    }
//
//    /**
//     * Rigourous Test :-)
//     */
//    public void testApp()
//    {
//    	File dbFile = new File("db");
//    	GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbFile);
//    	registerShutdownHook( graphDb );
//        assertTrue( true );
//    }
//}
