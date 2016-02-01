package japps.service.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Route {
    private String route = null;
    private double cost = 0;
    
    public Route(String route, double cost) {
        this.route = route;
        this.cost = cost;
    }

    @JsonProperty
    public String getRoute() {
        return route;
    }

    @JsonProperty
    public double getCost() {
        return cost;
    }
}