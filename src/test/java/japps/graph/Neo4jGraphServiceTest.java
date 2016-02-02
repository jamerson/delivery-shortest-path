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
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        String map = "A B 10\\nB D 15\\nA C 20\\nC D 30\\nB E 50\\nD E 30";
        assertTrue(service.load("testValidExample",map));
        
        RouteResult result = service.findRoute("testValidExample", "A", "D", 10, 2.5);
        assertEquals("A",result.getPoints().get(0));
        assertEquals("B",result.getPoints().get(1));
        assertEquals("D",result.getPoints().get(2));
        //assertEquals(6.25, result.getCost());
    }
}
