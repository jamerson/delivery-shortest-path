package japps.service.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Route {
    private String route = null;
    private String cost = null;
    
    public void setRoute(String route) {
        this.route = route;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    
    
    public Route() {}
    
    public Route(String route, String cost) {
        this.route = route;
        this.cost = cost;
    }

    public String getRoute() {
        return route;
    }

    public String getCost() {
        return cost;
    }
}
