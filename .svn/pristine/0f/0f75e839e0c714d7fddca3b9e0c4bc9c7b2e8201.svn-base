package graph.server.repository;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.cypher.internal.frontend.v2_3.perty.recipe.Pretty.nest;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import graph.server.GraphHub;
import graph.server.tools.GetFileSize;



/**
 * @author 王瑶
 *
 */
@Component
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Neo4jEmbeddedRepository {
	@Autowired
	GraphHub graphHub;
	@Autowired
	GetFileSize getFileSize;
	/**
	 * 嵌入式方式扩展节点
	 * @param id 待扩展节点id
	 * @return
	 * @throws IOException
	 */
	public Collection<Relationship> extendById(Long id) throws IOException{
		GraphDatabaseService graphDb=graphHub.getEmbeddeddriver().getGraphDatabaseService();
		
		Map<String,Object> parameters=new HashMap<>();
		Set<Relationship> results=new HashSet<>();
		
		parameters.put("id",id);
		Result result=graphDb.execute("MATCH (n)-[r]-(p) WHERE id(n)={id} RETURN n,r,p", parameters);
		Iterator<Relationship> rels=result.columnAs("r");
		
		while(rels.hasNext()) {
			Relationship relationship=rels.next();
			results.add(relationship);
		}

		return results;
	}

	/**
	 * 嵌入式通过节点属性查询节点
	 * @param param 模糊查询的属性值
	 * @param property 节点的属性
	 * @return
	 * @throws IOException
	 */
	public Collection<Node> searchNodeByParam(String param, String property) throws IOException {
		GraphDatabaseService graphDb=graphHub.getEmbeddeddriver().getGraphDatabaseService();
		
		Map<String,Object> parameters=new HashMap<>();
		Set<Node> results=new HashSet<>();
		
		parameters.put("property",property);
		parameters.put("param", param);
		String cypher="MATCH (n) WHERE n."+property+" contains {param} return n";
		System.out.println(cypher);
		Result result=graphDb.execute(cypher, parameters);
		Iterator<Node> nodes=result.columnAs("n");
		
		while(nodes.hasNext()) {
			Node node=nodes.next();
			results.add(node);
		}
		return results;
	}
	
	/**
	 * 通过嵌入式获取数据库详细信息(节点标签，关系类型，属性)
	 * @return 
	 * @throws IOException 
	 */
	public Map<String, List<Map<String, Object>>> databaseDetails() throws IOException {
		GraphDatabaseService graphDb=graphHub.getEmbeddeddriver().getGraphDatabaseService();
		Map<String, List<Map<String, Object>>> details = new HashMap<>();
		try (Transaction tx =graphDb.beginTx()) {
			
			System.out.println("节点标签，关系类型，属性信息:");
			// 节点标签
			// System.out.println("节点标签有:");
			Iterator<Label> labelsItor = graphDb.getAllLabels().iterator();
			List<Map<String, Object>> labels = new ArrayList<>();
			while (labelsItor.hasNext()) {
				Map<String, Object> map = new HashMap<>();
				Label myLabel = labelsItor.next();

				Long sum = 0L;
				Iterator<Node> nodes = graphDb.findNodes(myLabel);
				while (nodes.hasNext()) {
					nodes.next();
					sum++;
				}
				map.put("labelname", myLabel.toString());
				map.put("count", sum);
				labels.add(map);

			}

			// 关系类型
			// System.out.println("关系类型有:");
			Iterator<RelationshipType> relationshipTypes = graphDb.getAllRelationshipTypes().iterator();
			List<Map<String, Object>> relTypes = new ArrayList<>();
			while (relationshipTypes.hasNext()) {
				Map<String, Object> map = new HashMap<>();
				String myRelType = relationshipTypes.next().name();
				String cy = "match p=()-[:" + myRelType + "]->() return count(p) as count";
				map.put("typename", myRelType);
				map.put("count", graphDb.execute(cy).columnAs("count").next());
				relTypes.add(map);
				// System.out.println(myRelType+"--"+graphDb.execute(cy).columnAs("count").next());
			}

			// 属性信息
			// System.out.println("属性有:");

			Result result = graphDb.execute("CALL db.propertyKeys() YIELD propertyKey AS prop\r\n" + "MATCH (n)\r\n"
					+ "WHERE n[prop] IS NOT NULL RETURN  prop,count(n) as numNodes");
			List<Map<String, Object>> propertys = new ArrayList<>();
			while (result.hasNext()) {
				Map<String, Object> map = result.next();
				propertys.add(map);
			}
			// System.out.println(propertys.size());
			details.put("labels", labels);
			details.put("relTypes", relTypes);
			details.put("propertys", propertys);
			

		}
		return details;
	}
	
	/**
	 * 通过嵌入式方式获取数据库相关信息
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public Map<String, Object> databaseInfo() throws IOException, URISyntaxException {
		GraphDatabaseService graphDb=graphHub.getEmbeddeddriver().getGraphDatabaseService();
		Map<String, Object> sysInfo = new HashMap<>();
		/*---------------------------------------数据库信息------------------------------------------------------------------------ */
		try (Transaction tx = graphDb.beginTx()) {
			System.out.println("数据库信息:");
			final long total = getFileSize.getSize();
			String myDbinfo = "call dbms.components";
			Result result = graphDb.execute(myDbinfo);
			Map<String, Object> map = result.next();

			sysInfo.put("versions", map.get("versions"));
			sysInfo.put("name", map.get("name"));
			sysInfo.put("edition", map.get("edition"));
			sysInfo.put("size", String.format("%.2f", total / (1024.00 * 1024)) + "M");

			/*String username = (String) graphDb.execute("call dbms.security.listUsers").next().get("username");
			sysInfo.put("username", username);*/

		}
		return sysInfo;
	}
	/**
	 * 通过嵌入式方式获取数据库schema
	 * @return
	 * @throws IOException 
	 */
	public Map<String, List<Map<String, Object>>> getSchema() throws IOException {
		GraphDatabaseService graphDb=graphHub.getEmbeddeddriver().getGraphDatabaseService();
		Map<String, List<Map<String, Object>>> schema = new HashMap<>();
		/*---------------------------------------Schema信息------------------------------------------------------------------------ */
		try (Transaction tx = graphDb.beginTx()) {
			String cy = "call db.schema";
			Result result = graphDb.execute(cy);
			//System.out.println(result.next().get("nodes"));
			Map<String,Object> result_map=result.next();
			
			List<Node> myNodeList = (List<Node>) result_map.get("nodes");
			List<Relationship> myRelList = (List<Relationship>)result_map.get("relationships");
			List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
			
			for (Node node : myNodeList) { 
				Map<String, Object> myNode = new HashMap<>();
				myNode.put("id", node.getId());
				myNode.put("label", node.getLabels().iterator().next().toString());
				myNode.put("properties", node.getAllProperties());
				nodes.add(myNode);
			}
			
			List<Map<String, Object>> links = new ArrayList<Map<String, Object>>();
			for (Relationship rel : myRelList) {
				int sum=1;int no=1;
				boolean flagsum=true,flagno=true;
				int flag=0;
				int source=getIndex(rel.getStartNodeId(), nodes);
				int target=getIndex(rel.getEndNodeId(), nodes);
				
				Map<String, Object> myrel = new HashMap<>();
				myrel.put("id", rel.getId());
				myrel.put("source", source);
				myrel.put("target", target);
				myrel.put("label", rel.getType().name());
				myrel.put("properties", rel.getAllProperties());
				for(int j=links.size()-1;j>=0;j--){
					boolean hasa=(int)links.get(j).get("source")==source&&(int)links.get(j).get("target")==target;
					boolean hasb=(int)links.get(j).get("source")==target&&(int)links.get(j).get("target")==source;
					if(flag==0) {
						if(hasa||hasb){
							if(flagsum){
								sum=(int)links.get(j).get("sum")+1;
								flagsum=false;
							}
							
							if(flagno){
								 no=(int)links.get(j).get("no")+1;
								 flagno=false;
							}	
							links.get(j).replace("sum", sum);	
						}
					}
				}
				myrel.put("sum",sum);
				myrel.put("no",no);
				
				links.add(myrel);
			}

			schema.put("nodes", nodes);
			schema.put("links", links);

		}
		return schema;
	}
	
	/**
	 * 嵌入式方式导入数据-插入关系时获取所有节点标签和对应属性
	 * @return
	 * @throws IOException 
	 */
	public Map<String, List<String>> getAllNodeLabelAndReferProperty() throws IOException {
		GraphDatabaseService graphDb=graphHub.getEmbeddeddriver().getGraphDatabaseService();
		Map<String,List<String>> allNodeLabelAndReferProperty=new HashMap<>();
		try (Transaction tx = graphDb.beginTx()) {
			
			Iterator<Label> labels=graphDb.getAllLabelsInUse().iterator();
			List<String> label_property=new ArrayList<>();
			while(labels.hasNext()) {
				Label label=labels.next();
				Result result=graphDb.execute("MATCH (N:"+label+") RETURN KEYS(N) AS PROP");
				label_property=(List<String>)result.next().get("PROP");
				System.out.println(label_property);
				allNodeLabelAndReferProperty.put(label.name(),label_property);
			}
			
		}
		
		return allNodeLabelAndReferProperty;
	}
	
	public int getIndex(Long nodeId,List<Map<String, Object>> nodes) {
		int index=-1;
		int length=nodes.size();
		for(int i=0;i<length;i++) {
			if(nodeId.toString().equals(nodes.get(i).get("id").toString())){
				index=i;
				break;
			}
		}
		return index;
	}

	
}
