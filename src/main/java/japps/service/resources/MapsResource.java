package japps.service.resources;

import com.codahale.metrics.annotation.Timed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicLong;

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
}