package japps.service.resources;

import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import io.dropwizard.testing.junit.ResourceTestRule;

public class MapsResourceTest {
    
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new MapsResource())
            .build();
    
    private final String simpleMap = "A B 10";
    private final String incorrectInputOneNode = "A 10";

    @Test
    public void testCreateSimpleMap() {
        Response response = resources.client().
                target("/maps/simpleMap").request().
                post(Entity.text(simpleMap));
        
        assertEquals(
                Response.Status.CREATED.getStatusCode(), 
                response.getStatus());
        assertTrue(true);
    }
    
    @Test
    public void testCreateSimpleMapIncorrectInputOneNode() {
        Response response = resources.client().
                target("/maps/incorrectInputOneNode").request().
                post(Entity.text(incorrectInputOneNode));
        
        assertEquals(
                Response.Status.BAD_REQUEST.getStatusCode(), 
                response.getStatus());
        assertTrue(true);
    }
}
