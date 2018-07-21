package graph.server.connect;



import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;
import org.neo4j.ogm.exception.ConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graph.server.config.GraphHubConfiguration;


@Component
public class BoltDriver {
	
	@Autowired
	GraphHubConfiguration configuration;
	
	private Driver boltDriver;
	
	public Driver getDriver() {
		initializeDriver();
		return boltDriver;
	}
    public synchronized void close() {
        if (boltDriver != null) {
            try {
            	System.out.println("Shutting down Bolt driver {} "+boltDriver);
                boltDriver.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
	
    private void initializeDriver() {
        try {
            if (configuration.getUsername_neo4j() != null) {
                AuthToken authToken = AuthTokens.basic(configuration.getUsername_neo4j(), configuration.getPassword_neo4j());
                boltDriver = createDriver(configuration, authToken);
            } else {
                try {
                    boltDriver = createDriver(configuration, AuthTokens.none());
                } catch (ServiceUnavailableException e) {
                    throw new ConnectionException("Could not create driver instance.", e);
                }
                System.out.println("Bolt Driver credentials not supplied");
            }
        } catch (ServiceUnavailableException e) {
            throw new ConnectionException("Could not create driver instance", e);
        }
    }
	
	private Driver createDriver(GraphHubConfiguration config, AuthToken authToken) {
		boltDriver=GraphDatabase.driver(config.getUri_neo4j(), authToken);
        return boltDriver;
    }

	

}
