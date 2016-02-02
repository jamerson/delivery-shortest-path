package japps.graph;

import org.junit.Test;
import org.neo4j.io.fs.FileUtils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;

public class Neo4jGraphServiceTest {
    
    @BeforeClass
    public static void cleanTestDatabase()
    {
        try {
            FileUtils.deleteRecursively(new File("db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testValidateInputOneLine() {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        String map = "A B 10\\n";
        service.load("testValidateInputOneLine", map);
        assertTrue(true);
    }
    
    @Test
    public void testValidateInputOneLineBigDistance() {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        String map = "A B 100\\n";
        assertTrue(service.load("testValidateInputOneLineBigDistance",map));
    }
    
    @Test
    public void testValidateInputOneIncorrectLine() {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        String map = "A 10\\n";
        assertFalse(service.load("testValidateInputOneIncorrectLine",map));
    }
    
    @Test
    public void testValidExample() {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        String map = "A B 10\\nB D 15\\nA C 20\\nC D 30\\nB E 50\\nD E 30";
        assertTrue(service.load("testValidExample",map));
        
        RouteResult result = service.findRoute("testValidExample", "A", "D", 10, 2.5);
        assertEquals("A",result.getPoints().get(0));
        assertEquals("B",result.getPoints().get(1));
        assertEquals("D",result.getPoints().get(2));
        assertEquals(
                6.25, 
                result.getCost(),
                0.01);
    }
    
    @Test
    public void testUnreachablePath() {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        String map = "A B 10\\nD E 30";
        assertTrue(service.load("testUnreachablePath",map));
        
        RouteResult result = service.findRoute("testValidExample", "A", "D", 10, 2.5);
        assertNull(result);
    }
    
    public void testInexistentMap() {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        
        RouteResult result = service.findRoute("testInexistentMap", "A", "D", 10, 2.5);
        assertNull(result);
    }
    
    @Test
    public void testSameStartAndEndPoints() {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        String map = "A B 10\\nB D 15\\nA C 20\\nC D 30\\nB E 50\\nD E 30";
        assertTrue(service.load("testSameStartAndEndPoints",map));
        
        RouteResult result = service.findRoute("testSameStartAndEndPoints", "D", "D", 10, 2.5);
        assertNull(result);
    }
}
