package japps.graph;

import org.junit.Test;

import static org.junit.Assert.*;

public class Neo4jGraphServiceTest {
    
//    @Test
//    public void testValidateInputOneLine() {
//        Neo4jGraphService service = Neo4jGraphService.getInstance();
//        String map = "A B 10\\n";
//        service.load("testValidateInputOneLine", map);
//        assertTrue(true);
//    }
//    
//    @Test
//    public void testValidateInputOneLineBigDistance() {
//        Neo4jGraphService service = Neo4jGraphService.getInstance();
//        String map = "A B 100\\n";
//        assertTrue(service.load("testValidateInputOneLineBigDistance",map));
//    }
//    
//    @Test
//    public void testValidateInputOneIncorrectLine() {
//        Neo4jGraphService service = Neo4jGraphService.getInstance();
//        String map = "A 10\\n";
//        assertFalse(service.load("testValidateInputOneIncorrectLine",map));
//    }
    
    @Test
    public void testValidExample() {
        Neo4jGraphService service = Neo4jGraphService.getInstance();
        String map = "A B 10\\nB D 15\\nA C 20\\nC D 30\\nB E 50\\nD E 30";
        assertTrue(service.load("testValidExample",map));
        
        RouteResult result = service.findRoute("testValidExample", "A", "D", 10, 2.5);
        assertEquals("A",result.getPoints().get(0));
        assertEquals("B",result.getPoints().get(1));
        assertEquals("D",result.getPoints().get(2));
        //assertEquals(6.25, result.getCost());
    }
}
