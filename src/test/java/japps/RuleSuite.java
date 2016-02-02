package japps;

import java.io.File;
import java.io.IOException;

import org.junit.*;
import org.junit.rules.ExternalResource;
import org.junit.runners.Suite;
import org.neo4j.io.fs.FileUtils;

import japps.graph.Neo4jGraphServiceTest;
import japps.service.resources.MapsResourceStatesTest;

import org.junit.runner.RunWith;

@RunWith( Suite.class )
@Suite.SuiteClasses( { 
    Neo4jGraphServiceTest.class,
    MapsResourceStatesTest.class,
    MapsResourceStatesTest.class
} )
public class RuleSuite {
    
    @ClassRule
    public static ExternalResource testRule = new ExternalResource(){
        @Override
        protected void before() throws Throwable{
            try {
                FileUtils.deleteRecursively(new File("db"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    };
}
