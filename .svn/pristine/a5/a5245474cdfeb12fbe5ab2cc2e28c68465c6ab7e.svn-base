package graph.server.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import graph.server.graphsearch.ExecuteCypher;
import graph.server.repository.Neo4jEmbeddedRepository;
import graph.server.tools.ToD3FormatFromEmbeddedDriver;

/**
 * @author dx
 *图查询服务
 *
 */
@Service
public class GraphEmbeddedDriverService {
	@Autowired
	ExecuteCypher  executecypher;
	@Autowired
	Neo4jEmbeddedRepository neo4jEmbeddedRepository;
	@Autowired
	ToD3FormatFromEmbeddedDriver td;



	/**
	 * 通过嵌入式方式执行cypher
	 * @param cypher
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<Map<String, Object>>> executeNeo4jCypher(String cypher,Map<String,Object> parameters) throws IOException {
		return executecypher.searchByCypher(cypher,parameters);
	}
	
	
	/**
	 * 通过嵌入式方式扩展节点
	 * @param id
	 * @param old
	 * @return
	 * @throws IOException
	 */
	public Map<String,List<Map<String, Object>>> extendById(Long id,Map<String,List<Map<String, Object>>> old) throws IOException {
		Collection<Relationship> result=neo4jEmbeddedRepository.extendById(id);
		return td.toD3FormatByExtend(result,old,id);
	}

	/**
	 * 通过嵌入式方式全局查询节点
	 * @param param
	 * @param property
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<Map<String, Object>>> searchNodeByParam(String param, String property) throws IOException {
		Collection<Node> result=neo4jEmbeddedRepository.searchNodeByParam(param,property);
		return td.toD3FormatBynode(result);
	}
	/**
	 * 通过嵌入式获取数据库详细信息(节点标签，关系类型，属性)
	 * 
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<Map<String, Object>>> databaseDetails() throws IOException {
		Map<String, List<Map<String, Object>>> details = new HashMap<>();
		/*---------------------------------------数据库详细信息------------------------------------------------------------------------ */
		details = neo4jEmbeddedRepository.databaseDetails();
		return details;
	}

	/**
	 * 获取数据库相关信息
	 * 
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public Map<String, Object> databaseInfo() throws IOException, URISyntaxException {
		Map<String, Object> sysInfo = new HashMap<>();
		/*---------------------------------------数据库信息------------------------------------------------------------------------ */
		sysInfo = neo4jEmbeddedRepository.databaseInfo();
		return sysInfo;
	}

	/**
	 * 获取数据库schema
	 * 
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<Map<String, Object>>> getSchema() throws IOException {

		Map<String, List<Map<String, Object>>> schema = new HashMap<>();
		/*---------------------------------------Schema信息------------------------------------------------------------------------ */
		schema = neo4jEmbeddedRepository.getSchema();
		return schema;
	}
	
	/**
	 * 嵌入式方式导入数据-插入关系时获取所有节点标签和对应属性
	 * @return
	 * @throws IOException 
	 */
	public Map<String,List<String>> getAllNodeLabelAndReferProperty() throws IOException {
		Map<String,List<String>> allNodeLabelAndReferProperty=new HashMap<>();
		
		allNodeLabelAndReferProperty=neo4jEmbeddedRepository.getAllNodeLabelAndReferProperty();
		return allNodeLabelAndReferProperty;
	}
}
