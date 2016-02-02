package japps.service.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import japps.graph.AbstractGraphService;
import japps.graph.GraphServiceFactory;
import japps.graph.RouteResult;
import japps.service.api.Route;

@Path("/maps")
@Produces(MediaType.APPLICATION_JSON)
public class MapsResource {

    public MapsResource() {
    }

    @POST
    @Timed
    @Path("/{name}")
    public Response postMap(@PathParam("name") @NotEmpty String mapName, @NotEmpty String map) {
        Logger logger = LoggerFactory.getLogger("japps.service.resources.MapsResource");
        logger.debug(mapName);
        logger.debug(map);
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        
        if(service.load(mapName, map)) {
            return Response.status(Response.Status.CREATED).build();
        }
        
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @GET
    @Timed
    @Path("/{name}/query_route")
    public Response findPath(@PathParam("name") @NotEmpty String mapName, @QueryParam("start") @NotEmpty String startPoint, @QueryParam("end") @NotEmpty String endPoint, @QueryParam("auto") @NotEmpty String autonomy, @QueryParam("fuel") @NotEmpty String fuelPrice) {
        AbstractGraphService service = GraphServiceFactory.getGraphService();
        
        double autonomyNumber = 0;
        double fuelPriceNumber = 0;
        try {
            autonomyNumber = Double.parseDouble(autonomy);
            fuelPriceNumber = Double.parseDouble(fuelPrice);
        } catch(NumberFormatException ex) {
            return Response.status(Response.Status.BAD_REQUEST).build(); 
        }
        
        RouteResult result = service.findRoute(mapName, startPoint, endPoint, autonomyNumber, fuelPriceNumber);
        
        if(result != null) {
            Route route = new Route(StringUtils.join(result.getPoints(),' '), Double.toString(result.getCost()));
            return Response.ok().entity(route).build();
        }
        
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}