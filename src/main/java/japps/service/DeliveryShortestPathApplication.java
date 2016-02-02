package japps.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import japps.service.health.TemplateHealthCheck;
import japps.service.resources.DeliveryShortestPathResource;
import japps.service.resources.MapsResource;

public class DeliveryShortestPathApplication extends Application<DeliveryShortestPathConfiguration> {
    public static void main(String[] args) throws Exception {
        new DeliveryShortestPathApplication().run(args);
    }

    @Override
    public String getName() {
        return "delivery-shortest-path";
    }

    @Override
    public void initialize(Bootstrap<DeliveryShortestPathConfiguration> bootstrap) {
    }

    @Override
    public void run(DeliveryShortestPathConfiguration configuration,
                    Environment environment) {
        final MapsResource mapsResource = new MapsResource();
        
        final TemplateHealthCheck healthCheck =
            new TemplateHealthCheck(configuration.getTemplate());
        //environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(mapsResource);
    }

}
