package japps.graph;

public class GraphServiceFactory {
    private static Neo4jGraphService instance = new Neo4jGraphService();
    
    public static AbstractGraphService getGraphService() {
        return instance;
    }
}
