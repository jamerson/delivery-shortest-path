package japps.graph;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractGraphService implements GraphServiceInterface {

    private Pattern linePattern = Pattern.compile("([A-Z] [A-Z] [0-9])");
    private Pattern routePattern = Pattern.compile("([A-Z]) ([A-Z]) ([0-9])");
    
    public boolean load(String mapName, String map) {
        
        if(beforeLoad(mapName)) {
            Matcher lineMatcher = linePattern.matcher(map);
            Matcher routeMatcher = null;
            String line = null;
            String startPoint = null;
            String endPoint = null;
            double distance = 0;
            while(lineMatcher.find()) {
                line = lineMatcher.group();
                routeMatcher = routePattern.matcher(line);
                if(routeMatcher.find()) {
                    startPoint = routeMatcher.group(1);
                    endPoint = routeMatcher.group(2);
                    distance = Double.parseDouble(routeMatcher.group(3));
                    loadRoute(startPoint, endPoint, distance);
                } else {
                    return false;
                }
            }
            if(line == null) {
                return false;
            }
            
            return afterLoad();
        } else {
            return false;
        }
    }
    
    abstract boolean beforeLoad(String mapName);
    abstract boolean loadRoute(String startPoint, String endPoint, double distance);
    abstract boolean afterLoad();
    public abstract RouteResult findRoute(String mapName, String startPoint, String endPoint, double autonomy, double fuelPrice);

}
