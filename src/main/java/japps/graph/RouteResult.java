package japps.graph;

import java.util.ArrayList;
import java.util.List;

public class RouteResult {
    private double cost = 0;
    private List<String> points = new ArrayList<String>();
    
    protected RouteResult(List<String> points, double cost) {
        this.points = points;
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public List<String> getPoints() {
        return points;
    }
}
