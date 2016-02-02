package japps.service.resources;

import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import io.dropwizard.testing.junit.ResourceTestRule;
import japps.service.api.Route;

public class MapsResourceTest {
    
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new MapsResource())
            .build();
    
    private final String simpleMap = "A B 10";
    private final String incorrectInputOneNode = "A 10";
    private final String spMap = "A B 10\\nB D 15\\nA C 20\\nC D 30\\nB E 50\\nD E 30";

    @Test
    public void testCreateSimpleMap() {
        Response response = resources.client().
                target("/maps/simpleMap").request().
                post(Entity.text(simpleMap));
        
        assertEquals(
                Response.Status.CREATED.getStatusCode(), 
                response.getStatus());
    }
    
    @Test
    public void testCreateSimpleMapIncorrectInputOneNode() {
        Response response = resources.client().
                target("/maps/incorrectInputOneNode").request().
                post(Entity.text(incorrectInputOneNode));
        
        assertEquals(
                Response.Status.BAD_REQUEST.getStatusCode(), 
                response.getStatus());
    }
    
    @Test
    public void testFindInSimpleMap() {
        Route response = resources.client().target("/maps/simpleMap/query_route?start=A&end=B&auto=10&fuel=2.5").request().get(Route.class);
        
        assertEquals(
                "A B", 
                response.getRoute());
        assertEquals("2.5", response.getCost());
    }
    
    @Test
    public void testValidExample() {
        Response response = resources.client().
                target("/maps/sp").request().
                post(Entity.text(spMap));
        
        assertEquals(
                Response.Status.CREATED.getStatusCode(), 
                response.getStatus());
        
        Route result = resources.client().target("/maps/sp/query_route?start=A&end=D&auto=10&fuel=2.5").request().get(Route.class);
        
        assertEquals(
                "A B D", 
                result.getRoute());
        assertEquals("6.25", result.getCost());
    }
}
