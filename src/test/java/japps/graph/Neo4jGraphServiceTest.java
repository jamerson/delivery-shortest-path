package japps.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Neo4jGraphServiceTest {
    
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
    
    public void testNegativeDistance() {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        String map = "A B -1\\n";
        assertFalse(service.load("testNegativeDistance",map));
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
    
    @Test
    public void testDuplicateEntry() {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        String map = "A B 10\\nA B 15\\n";
        assertTrue(service.load("testDuplicateEntry",map));
        
        RouteResult result = service.findRoute("testDuplicateEntry", "A", "B", 1, 1);
        assertEquals(
                10, 
                result.getCost(),
                0.01);
        
    }
}
