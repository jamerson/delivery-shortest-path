package japps.service.resources;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import io.dropwizard.testing.junit.ResourceTestRule;
import japps.service.api.Route;

public class MapsResourceStatesTest {
    
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new MapsResource())
            .build();
    
    private static final String states = "RN PB 70\\n"
    + "RN CE 70\\n"
    + "PB PE 70\\n"
    + "PB CE 80\\n"
    + "PE CE 80\\n"
    + "PE PI 80\\n"
    + "PE BA 70\\n"
    + "BA PI 60\\n"
    + "BA TO 70\\n"
    + "BA GO 80\\n"
    + "GO TO 60\\n"
    + "TO PI 90\\n"
    + "TO MA 50\\n"
    + "MA PA 60\\n"
    + "PA AP 60\\n"
    + "PA RR 80\\n"
    + "PA MA 10\\n"
    + "AM AC 60\\n"
    + "AM RR 60\\n"
    + "AM RO 70\\n"
    + "RO MT 60\\n"
    + "MT TO 60\\n"
    + "MT GO 50\\n"
    + "GO MS 70\\n"
    + "MS MT 70\\n"
    + "GO DF 50\\n"
    + "GO MG 30\\n"
    + "MG BA 30\\n"
    + "BA ES 80\\n"
    + "BA SE 80\\n"
    + "SE AL 80\\n"
    + "AL PE 70\\n"
    + "PI MA 40\\n"
    + "PA MT 40\\n"
    + "MG MS 90\\n"
    + "MS SP 60\\n"
    + "MS PR 70\\n"
    + "PR SP 50\\n"
    + "PR SC 50\\n"
    + "SC RS 50\\n"
    + "SP MG 40\\n"
    + "SP RJ 80\\n"
    + "RJ MG 60\\n"
    + "RJ ES 80\\n"
    + "ES MG 60\\n"
    + "BA AL 80\\n"
    + "CE PI 40\\n";
    
    @BeforeClass
    public static void loadMap() {
        Response response = resources.client().
                target("/maps/states").request().
                post(Entity.text(states));
    }
    
    @Test
    public void testFindRouteRN_RS() {
        Route response = resources.client().target("/maps/states/query_route?start=RN&end=RS&auto=10&fuel=2.5").request().get(Route.class);
        
        assertEquals(
                "RN CE PI BA MG SP PR SC RS", 
                response.getRoute());
    }
    
    @Test
    public void testFindRouteRN_MS() {
        Route response = resources.client().target("/maps/states/query_route?start=RN&end=MS&auto=10&fuel=2.5").request().get(Route.class);
        
        assertEquals(
                "RN CE PI MA PA MT MS", 
                response.getRoute());
    }
    
    @Test
    public void testFindRouteAP_AC() {
        Route response = resources.client().target("/maps/states/query_route?start=AP&end=AC&auto=10&fuel=2.5").request().get(Route.class);
        
        assertEquals(
                "AP PA RR AM AC",
                response.getRoute());
    }
}
