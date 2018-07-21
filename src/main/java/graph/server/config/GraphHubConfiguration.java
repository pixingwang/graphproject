package graph.server.config;

import org.springframework.stereotype.Component;

import graph.server.model.*;

@Component
public class GraphHubConfiguration {
	private String uri_neo4j;
	private String username_neo4j ;
	private String password_neo4j ;
	private String embbeded_databasePath ;

	public void setConfiguration(GraphubModel graphub) {
		if(graphub.getNeo4j()==null) {
			uri_neo4j = "bolt://localhost:7687";
			username_neo4j = "neo4j";
			password_neo4j = "neo4j";
		}else {
			this.uri_neo4j = graphub.getNeo4j().getNeo4j_ip()+":"+graphub.getNeo4j().getNeo4j_port();
			this.username_neo4j = graphub.getNeo4j().getNeo4j_user();
			this.password_neo4j = graphub.getNeo4j().getNeo4j_password();
		}
		
		if(graphub.getNeo4jLocal()==null) {
			embbeded_databasePath="";
		}else {
			this.embbeded_databasePath=graphub.getNeo4jLocal().getEmbedded_neo4j_path();
		}
		System.out.println(toString());
	}

	public String getUri_neo4j() {
		return uri_neo4j;
	}

	public String getUsername_neo4j() {
		return username_neo4j;
	}

	public String getPassword_neo4j() {
		return password_neo4j;
	}

	public String getEmbbeded_databasePath() {
		return embbeded_databasePath;
	}

	public void setEmbbeded_databasePath(String embbeded_databasePath) {
		this.embbeded_databasePath = embbeded_databasePath;
	}
	@Override
	public String toString() {
		return "GraphHubConfiguration [uri_neo4j=" + uri_neo4j + ", username_neo4j=" + username_neo4j + ", password_neo4j=" + password_neo4j
				+ ", embbeded_databasePath=" + embbeded_databasePath+"]";
	}
}