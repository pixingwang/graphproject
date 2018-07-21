package graph.server.service;



import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.types.Relationship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import graph.server.GraphHub;
import graph.server.repository.Neo4jBoltRepository;

import graph.server.tools.ToD3FormatFromBoltDriver;



/**
 * @author dx
 *对图进行操作服务
 */
@Service
public class GraphBoltDriverService {

	@Autowired 
	Neo4jBoltRepository neo4jBoltRepository;
	@Autowired
	ToD3FormatFromBoltDriver td;

	
	@Transactional(readOnly=true)//通过节点Id扩展节点
	public Map<String,List<Map<String, Object>>> extendById(Long id,Map<String,List<Map<String, Object>>> old) {
		Collection<Relationship> result=neo4jBoltRepository.extendById(id);
		System.out.println(result);
		return td.toD3FormatByExtend(result,old,id);
	}
	
	/**
	 * 通过服务式获取数据库详细信息(节点标签，关系类型，属性)
	 * 
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<Map<String, Object>>> databaseDetails() throws IOException {
		Map<String, List<Map<String, Object>>> details = new HashMap<>();
		/*---------------------------------------数据库详细信息------------------------------------------------------------------------ */
		details = neo4jBoltRepository.databaseDetails();
		return details;
	}

	/**
	 * 获取服务式相关信息
	 * 
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public Map<String, Object> databaseInfo() throws IOException, URISyntaxException {
		Map<String, Object> sysInfo = new HashMap<>();
		/*---------------------------------------数据库信息------------------------------------------------------------------------ */
		sysInfo = neo4jBoltRepository.databaseInfo();
		return sysInfo;
	}

	/**
	 * 获取服务式schema
	 * 
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<Map<String, Object>>> getSchema() throws IOException {

		Map<String, List<Map<String, Object>>> schema = new HashMap<>();
		/*---------------------------------------Schema信息------------------------------------------------------------------------ */
		schema = neo4jBoltRepository.getSchema();
		return schema;
	}
	
	/**
	 * 服务式方式导入数据-插入关系时获取所有节点标签和对应属性
	 * @return
	 * @throws IOException 
	 */
	public Map<String,List<String>> getAllNodeLabelAndReferProperty() throws IOException {
		Map<String,List<String>> allNodeLabelAndReferProperty=new HashMap<>();
		
		allNodeLabelAndReferProperty=neo4jBoltRepository.getAllNodeLabelAndReferProperty();
		return allNodeLabelAndReferProperty;
	}

}
