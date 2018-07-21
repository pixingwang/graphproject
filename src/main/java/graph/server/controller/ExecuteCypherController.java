package graph.server.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import graph.server.model.ExtendByIdModel;
import graph.server.service.GraphEmbeddedDriverService;
import graph.server.tools.UpdateSaveOld;



@RestController
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ExecuteCypherController {

	@Autowired
	GraphEmbeddedDriverService graphEmbeddedDriverService;
	@Autowired
	UpdateSaveOld uOld;
	
	
	public static Map<String, List<Map<String, Object>>> saveOld = new HashMap<String, List<Map<String, Object>>>();// 保存查询结果
	
	//查询参数容器(id集合)
	private static Map<String, Object> params = new HashMap<String, Object>();
	
	/**
	 * 嵌入式执行cypher
	 * @param name
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/searchByCypher",method=RequestMethod.POST)//执行cypher
	@ResponseBody
	public Map<String, List<Map<String, Object>>>  searchByCypher(@Param("name") String name) throws IOException {
		Map<String,Object> params=new HashMap<>();
		saveOld = graphEmbeddedDriverService.executeNeo4jCypher(name,params);
		return saveOld;
	}

	/**
	 * 嵌入式全局查询节点
	 * @param name
	 * @param property
	 * @return
	 * @throws IOException
	 */
	/*@RequestMapping(value = "/searchNodeByParam", method = RequestMethod.POST) // 通过id扩展相应节点
	@ResponseBody
	public Map<String, List<Map<String, Object>>> extendById(@RequestParam("name") String name, @RequestParam("property") String property) throws IOException {
		params = LuceneQuery.executeQuery(property, name);
		String cypher = "start n=node({id}) return n";
		saveOld = graphEmbeddedDriverService.executeNeo4jCypher(name,params);
		String cypher="MATCH (n) WHERE n."+property+" contains \""+name+"\" return n";
		saveOld = searchByCypher(cypher);
		
		return saveOld;
	}*/

	@RequestMapping(value = "/searchNodeByParam", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<Map<String, Object>>> extendById(@RequestParam("name") String name, @RequestParam("property") String property) throws IOException {
		
		String cypher="MATCH (n) WHERE n."+property+" CONTAINS \""+name+"\" RETURN n";
		saveOld = searchByCypher(cypher);
		return saveOld;
	}
	/**
	 * 嵌入式扩展节点
	 * @param test
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/extendById", method = RequestMethod.POST, produces = { "application/json;charset=utf-8" }) // 通过id扩展相应节点
	@ResponseBody
	public Map<String, List<Map<String, Object>>> extendById(@RequestBody ExtendByIdModel test) throws IOException {
		
		saveOld.clear();
		saveOld = uOld.update(test.getNodes(),test.getLinks());
		saveOld = graphEmbeddedDriverService.extendById(test.getId(), saveOld);
		System.out.println(saveOld);
		return saveOld;
	}
	
}
