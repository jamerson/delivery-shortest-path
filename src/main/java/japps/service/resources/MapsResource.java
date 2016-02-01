package japps.service.resources;

import com.codahale.metrics.annotation.Timed;

import japps.graph.AbstractGraphService;
import japps.graph.GraphServiceFactory;
import japps.graph.RouteResult;
import japps.service.api.Route;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/maps")
@Produces(MediaType.APPLICATION_JSON)
public class MapsResource {

    public MapsResource(String template, String defaultName) {
    }

    @POST
    @Timed
    @Path("/{name}")
    public Response postMap(@PathParam("name") @NotEmpty String mapName, String map) {
        Logger logger = LoggerFactory.getLogger("japps.service.resources.MapsResource");
        logger.debug(mapName);
        logger.debug(map);
        
        return Response.status(Response.Status.CREATED).build();
    }
    
    @GET
    @Timed
    @Path("/{name}/find")
    public Response findPath(@PathParam("name") @NotEmpty String mapName, @QueryParam("start") String startPoint, @QueryParam("end") String endPoint, @QueryParam("auto") double autonomy, @QueryParam("fuel") double fuelPrice) {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        
        RouteResult result = service.findRoute(mapName, startPoint, endPoint, autonomy, fuelPrice);
        
        if(result != null) {
            Route route = new Route(StringUtils.join(result.getPoints(),' '), result.getCost());
            return Response.ok().entity(route).build();
        }
        
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}