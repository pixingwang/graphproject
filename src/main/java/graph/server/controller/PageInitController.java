package graph.server.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import graph.server.GraphHub;
import graph.server.model.GraphubModel;
import graph.server.model.Neo4jModel;
import graph.server.service.GraphEmbeddedDriverService;

import net.sf.json.JSONObject;


/**
 * @author 王瑶
 *
 */
/**
 * @author 王瑶
 *
 */
/**
 * @author 王瑶
 *
 */
/**
 * @author 王瑶
 *
 */
@RestController
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class PageInitController {
	@Autowired
	GraphEmbeddedDriverService pageInit;
	@Autowired
	GraphHub graphHub;
	
	
	/**
	 * 获取数据库详细信息(节点标签，关系类型，属性)
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/getDB_details",method=RequestMethod.POST)
	@ResponseBody
	public String  getDB_details() throws IOException  {
		
		Map<String, List<Map<String, Object>>> result=pageInit.databaseDetails();
		JSONObject json = JSONObject.fromObject(result);
		System.out.println("getDB_details："+json);
		
		return json.toString();

	}
	
	/**
	 * 获取数据库相关信息
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	@RequestMapping(value="/getDB_info",method=RequestMethod.POST)
	@ResponseBody
	public String  getDB_info() throws IOException, URISyntaxException {
	
		Map<String, Object> result=pageInit.databaseInfo();
		JSONObject json = JSONObject.fromObject(result);
		System.out.println("getDB_info:"+json);
		
		return json.toString();
		
	}
	
	/**
	 * 获取数据库模式Schema
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/getDB_schema",method=RequestMethod.POST)//获取数据库schema
	@ResponseBody
	public String  getDB_schema() throws IOException {
		Map<String, List<Map<String, Object>>> result=pageInit.getSchema();
		System.out.println(result.get("nodes"));
		JSONObject json = JSONObject.fromObject(result);
		System.out.println("getDB_schema:"+json);
		
		return json.toString();

	}
	
	@RequestMapping(value="/init",method=RequestMethod.POST)//获取数据库schema
	@ResponseBody
	public String  init(@RequestParam("path") String path)  {
		System.out.println(path);
		Neo4jModel neo4j=new Neo4jModel();
		neo4j.setNeo4j_user("neo4j");
		neo4j.setNeo4j_password("123456");
		GraphubModel graphub=new GraphubModel();
		graphub.setNeo4j(neo4j);
		
		graphHub.init(graphub);
		Driver driver=graphHub.getBoltDriver().getDriver();
		System.out.println(driver.hashCode());
		
		try (Session session=driver.session()){
			try (Transaction tx=session.beginTransaction()){
				StatementResult result=tx.run("call dbms.components");
				System.out.println(result.list().toString());
				StatementResult result2=tx.run("call db.schema");
				System.out.println(result2.list().toString());
				
			}
		}
		
		System.out.println(1);
		return "ok";

	}
	
	
	
}
