package graph.server.repository;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import graph.server.GraphHub;
import graph.server.tools.GetFileSize;




@Component
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Neo4jBoltRepository {

	@Autowired
	GraphHub graphHub;
	@Autowired
	GetFileSize getFileSize;
	
	/**
	 * 通过BoltDriver扩展节点
	 * @param id 待扩展节点id
	 * @return neo4j.driver.v1.types.Relationship集合
	 */
	public Collection<Relationship> extendById(Long id) {
		Driver driver=graphHub.getBoltDriver().getDriver();
		Map<String,Object> parameters=new HashMap<>();
		Set<Relationship> results=new HashSet<>();
		
		parameters.put("id",id);
		try (Session session=driver.session()){
			try (Transaction tx=session.beginTransaction()){
				
				
				
				
			}
		}
		
		
		return null;
	}

	public Map<String, List<Map<String, Object>>> databaseDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> databaseInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, List<Map<String, Object>>> getSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, List<String>> getAllNodeLabelAndReferProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
	
}
