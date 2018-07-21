package graph.server.service;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import graph.server.GraphHub;
import graph.server.repository.Neo4jEmbeddedRepository;



/**
 * @author 王瑶
 *
 */
@Service
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class IndexPageService {
	@Autowired
	GraphHub graphHub;
	@Autowired
	Neo4jEmbeddedRepository neo4jEmbeddedRepository;
	
	/**
	 * 获取数据库详细信息(节点标签，关系类型，属性)
	 * @return 
	 * @throws IOException 
	 */
	public Map<String, List<Map<String, Object>>> databaseDetails() throws IOException {
		Map<String, List<Map<String, Object>>> details = new HashMap<>();
		details=neo4jEmbeddedRepository.databaseDetails();
		return details;
	}

	/**
	 * 获取数据库相关信息
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public Map<String, Object> databaseInfo() throws IOException, URISyntaxException {
		Map<String, Object> sysInfo = new HashMap<>();
		sysInfo=neo4jEmbeddedRepository.databaseInfo();
		return sysInfo;
	}

	/**
	 * 获取数据库schema
	 * @return
	 * @throws IOException 
	 */
	public Map<String, List<Map<String, Object>>> getSchema() throws IOException {
		Map<String, List<Map<String, Object>>> schema = new HashMap<>();
		schema=neo4jEmbeddedRepository.getSchema();
		return schema;
	}


}
