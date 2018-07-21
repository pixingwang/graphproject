package graph.server.graphsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.neo4j.cypher.internal.compiler.v3_2.commands.expressions.PathImpl;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.QueryExecutionException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import graph.server.GraphHub;



/**
 * 
 * @author SRF
 *
 */

@Component
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ExecuteCypher {

	@Autowired
	GraphHub graphHub;
	
	private static final int MAX_NUMS_OF_NODES = 300;
	private static final int MAX_NUMS_OF_RELS = 300;
	
	private List<Map<String,Object>> subGraphList = new ArrayList<Map<String,Object>>();
	
	private Set<Node> allNodes = new HashSet<>();//用来存储结果中的所有节点对象
	private Set<Relationship> allRels = new HashSet<>();//用来存储结果中的所有关系对象
	
	private Set<Node> visitedNodes = new HashSet<>();//用来存储查找子图时已经访问过的节点
	
	
	public Map<String,List<Map<String,Object>>> searchByCypher(String cypher,Map<String,Object> parameters) throws IOException{
		GraphDatabaseService graphDb=graphHub.getEmbeddeddriver().getGraphDatabaseService();
		
		allNodes.clear();
		allRels.clear();
		visitedNodes.clear();
		subGraphList.clear();
		System.out.println("--------------------------开始-----------------------------------------");
		
		Map<String,List<Map<String,Object>>> result_map = new HashMap<>();
		
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> links = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> allRowsData = new ArrayList<Map<String,Object>>();//总结果的图视图
		List<Map<String,Object>> listData = new ArrayList<Map<String,Object>>();//列表展示时的数据，包括图视图的数据和表视图的数据
		List<Map<String,Object>> columnNameList = new ArrayList<Map<String,Object>>();//表视图的表头
		
		List<Map<String,Object>> errors = new ArrayList<Map<String,Object>>();
		
		//{allRowsData:[myRow,myRow,myRow,...]}
		//{columnNameList:[{"columnName":[列名1,列名2,列名3,列名4,...]}]}
		//{listData:[{rowNo:no,rowAbstract:摘要,rowLinks:[{}...],rowNodes:[{}...],rowData:[myRow],columnNames:[]},{},{},...]}
		
		try ( Transaction tx = graphDb.beginTx()){
			Result result = graphDb.execute(cypher, parameters);
			
			List<String> columns = result.columns();
			System.out.println("列名："+columns.toString());
			System.out.println("列的个数："+columns.size());
			
			Map<String,Object> map = new HashMap<>();
			map.put("columnName", columns);
			columnNameList.add(map);
			
			while (result.hasNext()){
				Map<String, Object> row = result.next();//一行的数据，可能有多列
				//myRow：{节点别名(列名):{节点属性集，也是集合},关系别名(列名):{关系属性集，也是集合},路径别名(列名):[{起始节点的属性集},{关系的属性集},{终止节点的属性集},,,{起始节点的属性集},{关系的属性集},{终止节点的属性集}],其它值(列名):对应的值......}
				//用来存储总结果表视图的一行
				Map<String, Object> myRow = new HashMap<>();
				
				Map<String,Object> myListRow = new HashMap<>();//用来存储列表查看的一行
				List<Map<String,Object>> rowNodes = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> rowLinks = new ArrayList<Map<String,Object>>();
				StringBuilder rowAbstract = new StringBuilder();
				
				for (String columnName : result.columns()){//对一行的每列进行操作
					if(row.get(columnName)==null) 
						continue;//查询属性时可能存在某个对象没有该属性 所以跳过 否则会报错
					String curClass = row.get(columnName).getClass().getSimpleName();//获取当前列的数据类型
					Object curObject = row.get(columnName);
					switch(curClass) {
						case "NodeProxy":searchByCypherForNodes(nodes,(Node)curObject,links,myRow,columnName,rowNodes,rowAbstract);break;//当前列为节点类型
						case "RelationshipProxy":searchByCypherForRels(links,(Relationship)curObject,nodes,myRow,columnName,rowNodes,rowLinks,rowAbstract);break;//当前列为关系类型
						case "PathImpl":searchByCypherForPath1(links,nodes,(PathImpl)curObject,myRow,columnName,rowNodes,rowLinks,rowAbstract);break;//当前列为路径类型
						default:searchByCypherForProperties(curObject,myRow,columnName);//其它，可以为属性值等，可能为：Long,String,Integer等类型
					}
		        }
				
				allRowsData.add(myRow);
				
				List<Map<String,Object>> listMyRow = new ArrayList<>();
				listMyRow.add(myRow);
				
				myListRow.put("rowNo", listData.size()+1);
				myListRow.put("rowData", listMyRow);
				myListRow.put("columnName", columns);
				
				myListRow.put("rowAbstract", rowAbstract.length()==0?null:rowAbstract.substring(0, rowAbstract.length()-1));
				
				numberingRels(rowLinks, rowNodes);
				myListRow.put("links", rowLinks);
				myListRow.put("nodes", rowNodes);
				
				if(links.size()!=0 || nodes.size() !=0)
					listData.add(myListRow);
				
				if(allNodes.size()>MAX_NUMS_OF_NODES) {
//					break;
				}
		    }
			
		}catch(QueryExecutionException e) {
			
			e.printStackTrace();
			System.out.println(e.getMessage());
			
			Map<String,Object> myError = new HashMap<>();
			myError.put("myError", e.getMessage());
			errors.add(myError);
			
		}
		numberingRels(links, nodes);
		
/*		Map<String,Object> myAllListRow = new HashMap<>();//用来存储总结果在列表查看中的一行
		
		myAllListRow.put("rowNo", 1);
		myAllListRow.put("rowData", allRowsData);
		myAllListRow.put("columnName", columnNameList.size()>0?columnNameList.get(0).get("columnName"):null);
		myAllListRow.put("links", links);
		myAllListRow.put("nodes", nodes);
		myAllListRow.put("rowAbstract", "全部结果");
		if(links.size()!=0 || nodes.size() !=0)
			listData.add(0, myAllListRow);*/
		
		result_map.put("nodes",nodes);
		result_map.put("links",links);
		result_map.put("allRowsData", allRowsData);
		result_map.put("columnNameList", columnNameList);
		result_map.put("listData", listData);
//		result_map.put("subGraphList", subGraphList);
		result_map.put("errors", errors);
		System.out.println("--------------------------结束-----------------------------------------");
		System.out.println("nodes:"+allNodes.size()+" rels:"+allRels.size());
		return result_map;
		
	}
	
	/**
	 * 根据cypher获取节点
	 * @param curNodesList
	 * @param addNode
	 * @param columnSize
	 * @param curRelsList
	 * @param myRow 
	 * @param columnName 
	 * @param rowNodes 
	 * @param rowAbstract 
	 */
	private void searchByCypherForNodes(List<Map<String,Object>> curNodesList,Node addNode,
			List<Map<String,Object>> curRelsList, Map<String, Object> myRow, String columnName, List<Map<String, Object>> rowNodes, StringBuilder rowAbstract){
		
		Map<String, Object> myNode = new HashMap<>();
		
		myRow.put(columnName, addNode.getAllProperties());//表视图一行中的节点的属性的列
		
		/*boolean idExisted1 = nodeExisted(addNode, curNodesList);
		if(idExisted1) return;
		
		allNodes.add(addNode);*/
		
		myNode.put("id", addNode.getId());
		myNode.put("label", getNodeLabel(addNode.getLabels().iterator()));
		
		int count = 1;String str = null;
		Map<String,Object> nodeProperties = addNode.getAllProperties();
		for(Map.Entry<String, Object> entry:nodeProperties.entrySet()) {
			myNode.put(entry.getKey(), entry.getValue());
			
			if(count>0 && entry.getValue().getClass().getSimpleName().equals("String")) {
				String str1 = (String) entry.getValue();
				String reg = "[\\u4e00-\\u9fa5]+";
				
				if(str1.matches(reg)) {//有汉字
					count--;
					str = str1.substring(0, str1.length()>3?3:str1.length());
				}
			}
		}
		
		boolean rowNodeIdExisted = nodeExisted(addNode, rowNodes);//每一行对应的图视图
		if(!rowNodeIdExisted) {
			Map<String, Object> myListNode = new HashMap<>(myNode);
			rowNodes.add(myListNode);
			if(str!=null) {
				rowAbstract.append(str);
				rowAbstract.append("_");
			}
		}
		
		boolean idExisted1 = nodeExisted(addNode, curNodesList);
		if(!idExisted1) {
			allNodes.add(addNode);
			curNodesList.add(myNode);
		}
		
//		if(columnSize == 1) {//如果只查询节点，则同时查找节点之间的关系
/*			long curNodeId = addNode.getId();
			Iterator<Relationship> attachRels = addNode.getRelationships().iterator();
			while(attachRels.hasNext()) {
				Map<String, Object> myRel = new HashMap<>();
				
				Relationship rs = attachRels.next();
				
				boolean relExisted = relExisted(rs, curRelsList);
				if(relExisted) continue;
				
				Node tempNode = curNodeId == rs.getStartNodeId() ? rs.getEndNode():rs.getStartNode();
				
				boolean idExisted = nodeExisted(tempNode, curNodesList);
				if(!idExisted) continue;
				
				allRels.add(rs);
				
				myRel.put("id", rs.getId());
				myRel.put("label", rs.getType().name().toString());
				myRel.put("source",rs.getStartNodeId());
				myRel.put("target", rs.getEndNodeId());
				
				Map<String,Object> relProperties = rs.getAllProperties();
				for(Map.Entry<String, Object> entry : relProperties.entrySet()) {
					myRel.put(entry.getKey(), entry.getValue());
				}
				curRelsList.add(myRel);
			}*/
//		}
	}
	


	/**
	 * 根据cypher获取关系
	 * @param curRelsList
	 * @param addRel
	 * @param curNodesList
	 * @param myRow 
	 * @param columnName 
	 * @param rowLinks 
	 * @param rowNodes 
	 * @param rowAbstract 
	 */
	private void searchByCypherForRels(List<Map<String,Object>> curRelsList, Relationship addRel, 
			List<Map<String,Object>> curNodesList, Map<String, Object> myRow, String columnName, 
			List<Map<String,Object>> rowNodes, List<Map<String,Object>> rowLinks, StringBuilder rowAbstract) {
		
		Map<String,Object> myRel = new HashMap<>();
		
		myRow.put(columnName, addRel.getAllProperties());//表视图一行中的关系的属性的列
		
		/*boolean relExisted = relExisted(addRel, curRelsList);
		if(relExisted) return;
		
		allRels.add(addRel);*/
		
		myRel.put("id", addRel.getId());
		myRel.put("label", addRel.getType().name().toString());
		myRel.put("source",addRel.getStartNodeId());
		myRel.put("target", addRel.getEndNodeId());
		
		int count = 1;String str = null;
		Map<String,Object> relProperties = addRel.getAllProperties();
		for(Map.Entry<String, Object> entry:relProperties.entrySet()) {
			myRel.put(entry.getKey(), entry.getValue());
			
			if(count>0 && entry.getValue().getClass().getSimpleName().equals("String")) {
				String str1 = (String) entry.getValue();
				String reg = "[\\u4e00-\\u9fa5]+";
				
				if(str1.matches(reg)) {//有汉字
					count--;
					str = str1.substring(0, str1.length()>3?3:str1.length());
				}
			}
			
		}
//		curRelsList.add(myRel);
		
		boolean relExisted = relExisted(addRel, curRelsList);
		if(!relExisted) {
			allRels.add(addRel);
			curRelsList.add(myRel);
		}
		
		
		boolean rowLinkIdExisted = relExisted(addRel, rowLinks);
		if(!rowLinkIdExisted) {
			Map<String,Object> myListRel = new HashMap<>(myRel);
			rowLinks.add(myListRel);
			if(str!=null) {
				rowAbstract.append(str);
				rowAbstract.append("_");
			}
		}
		
		Node[] nodeArrays = addRel.getNodes();
		int count1=1;String str_ = null;
		for(Node n:nodeArrays) {
			Map<String,Object> myNode = new HashMap<String, Object>();
			
/*			boolean idExisted = nodeExisted(n, curNodesList);
			if(idExisted) continue;
			
			allNodes.add(n);*/
			
			myNode.put("id", n.getId());
			myNode.put("label", getNodeLabel(n.getLabels().iterator()));
			
			Map<String,Object> nodeProperties = n.getAllProperties();
			for(Map.Entry<String, Object> entry:nodeProperties.entrySet()) {
				myNode.put(entry.getKey(), entry.getValue());
				
				if(count1>0 && entry.getValue().getClass().getSimpleName().equals("String")) {
					String str1 = (String) entry.getValue();
					String reg = "[\\u4e00-\\u9fa5]+";
					
					if(str1.matches(reg)) {//有汉字
						count1--;
						str_ = str1.substring(0, str1.length()>3?3:str1.length());
					}
				}
			}
//			curNodesList.add(myNode);
			
			boolean rowNodeIdExisted = nodeExisted(n, rowNodes);
			if(!rowNodeIdExisted) {
				Map<String, Object> myListNode = new HashMap<>(myNode);
				rowNodes.add(myListNode);
				
				if(count1==0) {
					if(str_!=null) {
						rowAbstract.append(str_);
						rowAbstract.append("_");
						str_=null;
					}
				}
			}
			
				
			boolean idExisted = nodeExisted(n, curNodesList);
			if(!relExisted && !idExisted) {
				allNodes.add(n);
				curNodesList.add(myNode);
			}
			
		}
		
	}
	
	/**
	 * 根据cypher获取路径
	 * @param curRelsList
	 * @param curNodesList
	 * @param addPath
	 * @param myRow 
	 * @param columnName 
	 * @param rowLinks 
	 * @param rowNodes 
	 * @param rowAbstract 
	 */
	private void searchByCypherForPath1(List<Map<String, Object>> curRelsList, List<Map<String, Object>> curNodesList,
			PathImpl addPath, Map<String, Object> myRow, String columnName, List<Map<String, Object>> rowNodes, 
			List<Map<String, Object>> rowLinks, StringBuilder rowAbstract) {
		
		//System.out.println(addPath.toString());
		
		Iterator<Relationship> rels = addPath.relationships().iterator();
		List<Map<String,Object>> pathList = new ArrayList<>();//一条路径包含多条关系和多个节点
		
		int count=1;String str = null;
		while(rels.hasNext()) {
			Map<String,Object> myRel = new HashMap<String, Object>();
			
			Relationship rel = rels.next();
			
			pathList.add(rel.getStartNode().getAllProperties());
			pathList.add(rel.getAllProperties());
			pathList.add(rel.getEndNode().getAllProperties());
			
	/*		boolean relExisted = relExisted(rel, curRelsList); 
			if(relExisted) continue;
			
			allRels.add(rel);*/
			
			myRel.put("id", rel.getId());
			myRel.put("label", rel.getType().name().toString());
			myRel.put("source",rel.getStartNodeId());
			myRel.put("target", rel.getEndNodeId());
			
			Map<String,Object> relProperties = rel.getAllProperties();
			for(Map.Entry<String, Object> entry:relProperties.entrySet()) {
				myRel.put(entry.getKey(), entry.getValue());
				
				if(count>0 && entry.getValue().getClass().getSimpleName().equals("String")) {
					String str1 = (String) entry.getValue();
					String reg = "[\\u4e00-\\u9fa5]+";
					
					if(str1.matches(reg)) {//有汉字
						count--;
						str = str1.substring(0, str1.length()>3?3:str1.length());
					}
				}
				
			}
			
			boolean relExisted = relExisted(rel, curRelsList); 
			if(!relExisted) {
				allRels.add(rel);
				curRelsList.add(myRel);
			}
			
			boolean rowLinkIdExisted = relExisted(rel, rowLinks);
			if(!rowLinkIdExisted) {
				Map<String,Object> myListRel = new HashMap<>(myRel);
				rowLinks.add(myListRel);
				
				if(count==0 && str!=null) {
					rowAbstract.append(str);
					rowAbstract.append("_");
				}
			}
		}
		myRow.put(columnName, pathList);//表视图的一行中的路径的列；{列名:[{起始节点的属性集},{关系的属性集},{终止节点的属性集},,,{起始节点的属性集},{关系的属性集},{终止节点的属性集}]}
		
		Iterator<Node> nodes = addPath.nodes().iterator();
		int count1=1;String str_ = null;
		while(nodes.hasNext()) {
			
			Map<String,Object> myNode = new HashMap<String, Object>();
			
			Node n = nodes.next();
			
			/*boolean idExisted = nodeExisted(n,curNodesList);
			if(idExisted) continue;
			
			allNodes.add(n);*/
			
			myNode.put("id", n.getId());
			myNode.put("label", getNodeLabel(n.getLabels().iterator()));
			
			Map<String,Object> nodeProperties = n.getAllProperties();
			for(Map.Entry<String, Object> entry:nodeProperties.entrySet()) {
				myNode.put(entry.getKey(), entry.getValue());
				
				if(count1>0 && entry.getValue().getClass().getSimpleName().equals("String")) {
					String str1 = (String) entry.getValue();
					String reg = "[\\u4e00-\\u9fa5]+";
					
					if(str1.matches(reg)) {//有汉字
						count1--;
						str_ = str1.substring(0, str1.length()>3?3:str1.length());
					}
				}
				
			}
			
			boolean idExisted = nodeExisted(n,curNodesList);
			if(!idExisted) {
				allNodes.add(n);
				curNodesList.add(myNode);
			}
			
			boolean rowNodeIdExisted = nodeExisted(n, rowNodes);//每一行对应的图视图
			if(!rowNodeIdExisted) {
				Map<String, Object> myListNode = new HashMap<>(myNode);
				rowNodes.add(myListNode);
				
				if(count1==0) {
					if(str_!=null) {
						rowAbstract.append(str_);
						rowAbstract.append("_");
						str_=null;
					}
				}
			}
		}
	}
	
	
	/**
	 * 根据cypher获取路径
	 * @param curRelsList
	 * @param curNodesList
	 * @param addPath
	 * @param myRow 
	 * @param columnName 
	 */
	private void searchByCypherForPath(List<Map<String, Object>> curRelsList, List<Map<String, Object>> curNodesList,
			PathImpl addPath, Map<String, Object> myRow, String columnName) {
		
		
		System.out.println(addPath.toString());
		
		Iterator<Relationship> rels = addPath.relationships().iterator();
		System.out.println("添加一条路径");
		
		List<Map<String,Object>> pathList = new ArrayList<>();//一条路径包含多条关系和多个节点
		while(rels.hasNext()) {
			Map<String,Object> myRel = new HashMap<String, Object>();
			
			Relationship rel = rels.next();
			
			System.out.println("当前关系:"+rel.toString());
			
			pathList.add(rel.getStartNode().getAllProperties());
			pathList.add(rel.getAllProperties());
			pathList.add(rel.getEndNode().getAllProperties());
			
			boolean relExisted = relExisted(rel, curRelsList); 
			if(relExisted) continue;
			
			allRels.add(rel);
			
			myRel.put("id", rel.getId());
			myRel.put("label", rel.getType().name().toString());
			myRel.put("source",rel.getStartNodeId());
			myRel.put("target", rel.getEndNodeId());
			
			Map<String,Object> relProperties = rel.getAllProperties();
			for(Map.Entry<String, Object> entry:relProperties.entrySet()) {
				myRel.put(entry.getKey(), entry.getValue());
			}
			curRelsList.add(myRel);
			
			Node[] nodeArrays = rel.getNodes();
			for(Node n:nodeArrays) {
				
				Map<String,Object> myNode = new HashMap<String, Object>();
				
				boolean idExisted = nodeExisted(n,curNodesList);
				if(idExisted) continue;
				
				allNodes.add(n);
				
				myNode.put("id", n.getId());
				myNode.put("label", getNodeLabel(n.getLabels().iterator()));
				
				Map<String,Object> nodeProperties = n.getAllProperties();
				for(Map.Entry<String, Object> entry:nodeProperties.entrySet()) {
					myNode.put(entry.getKey(), entry.getValue());
				}
				
				curNodesList.add(myNode);
			}
		}
		myRow.put(columnName, pathList);//表视图的一行中的路径的列；{列名:[{起始节点的属性集},{关系的属性集},{终止节点的属性集},,,{起始节点的属性集},{关系的属性集},{终止节点的属性集}]}
		
	}
	
	/**
	 * 根据cypher获取查询的属性或者其它数值，比如count(*)的值等
	 * @param addProperties
	 * @param columnName
	 * @param curOthers
	 * @param myRow 
	 * @param columnName2 
	 */
	private void searchByCypherForProperties(Object addProperties, 
			Map<String, Object> myRow, String columnName) {
		
		myRow.put(columnName, addProperties);//表视图一行中属性或者其它数值的列
		
	}

	/**
	 * 广度优先遍历
	 * 根据一个节点获取其所在子图的所有节点
	 * @param addNode
	 */
	private void getAllNodesOfSubgraphBySingleNode(Node addNode){
		Set<Node> curSubNodes = new HashSet<>();//存储当前子图的所有节点
		
		Queue<Node> q = new LinkedList<>();//定义遍历队列
		
		if(visitedNodes.contains(addNode)) {/*System.out.println(addNode.getId()+":节点已存在其它已被遍历的子图中");*/return;}//如果当前节点已存在其它已被遍历的子图中
		
		q.add(addNode);//把当前节点放入队列
		
		while(!q.isEmpty()) {
			Node headNode = q.poll();//访问队首节点
			
			curSubNodes.add(headNode);//队首节点加入子图节点集
			visitedNodes.add(headNode);//当前节点标记为已访问
			
			if(!headNode.hasRelationship()) { /*System.out.println(headNode.getId()+"是孤立节点");*/continue;}//队首节点是孤立节点，访问队列的下个节点
			Iterator<Relationship> attachRels = headNode.getRelationships().iterator();//获取队首节点的所有关系
			while(attachRels.hasNext()) {
				Relationship rel = attachRels.next();
				
				if(allRels.isEmpty()) continue;
				
				if(!allRels.isEmpty() && !allRels.contains(rel)) { 
//					System.out.println("关系集中没有："+rel.getId());
					continue;
				}
				
				Node otherNode = rel.getOtherNode(headNode);
				
				if((allNodes.contains(otherNode)) && (!visitedNodes.contains(otherNode))) {
					q.add(otherNode);
				}
				
			}
		}
//		System.out.println("----------------------子图开始----------------------");
//		System.out.println("当前子图的节点个数："+curSubNodes.size());
//		for(Node n:curSubNodes) {
//			System.out.println(n.getId()+"-----"+n.getLabels().toString());
//		}
//		System.out.println("----------------------子图结束----------------------");
		
		getSubGraphByNodes(curSubNodes);
		
	}
	
	/**
	 * 根据子图的节点集获取子图
	 * @param curSubNodes
	 */
	private void getSubGraphByNodes(Set<Node> curSubNodes) {
		
		Map<String,Object> mySubGraph = new HashMap<>();//一个子图
		
		List<Map<String,Object>> subNodes = new ArrayList<Map<String,Object>>();//一个子图中的节点集
		List<Map<String,Object>> subLinks = new ArrayList<Map<String,Object>>();//一个子图中的关系集
		
		for(Node n:curSubNodes) {
			getSubGraph(subNodes, n, subLinks);
		}
		
		numberingRels(subLinks, subNodes);
		
		mySubGraph.put("links", subLinks);//当前子图的关系集放入子图
		mySubGraph.put("nodes", subNodes);//当前子图的节点集放入子图
		mySubGraph.put("subGraphNo", subGraphList.size()+1);//当前子图的编号
		
		subGraphList.add(mySubGraph);//当前子图放入子图集
	}
	
	/**
	 * 根据子图中的一个节点获取与其相关联的关系和节点
	 * @param curNodesList
	 * @param addNode
	 * @param curRelsList
	 */
	private void getSubGraph(List<Map<String,Object>> curNodesList,Node addNode,
			List<Map<String,Object>> curRelsList){
		
		Map<String, Object> myNode = new HashMap<>();
		
		boolean idExisted1 = nodeExisted(addNode, curNodesList);
		if(idExisted1) return;
		
		myNode.put("id", addNode.getId());
		myNode.put("label", getNodeLabel(addNode.getLabels().iterator()));
		
		Map<String,Object> nodeProperties = addNode.getAllProperties();
		for(Map.Entry<String, Object> entry:nodeProperties.entrySet()) {
			myNode.put(entry.getKey(), entry.getValue());
		}
		curNodesList.add(myNode);
		
		long curNodeId = addNode.getId();
		Iterator<Relationship> attachRels = addNode.getRelationships().iterator();
		while(attachRels.hasNext()) {
			Map<String, Object> myRel = new HashMap<>();
			
			Relationship rs = attachRels.next();
			
			if(allRels.isEmpty()) continue;//关系集为空时  当前子图无关系
			
			if(!allRels.isEmpty() && !allRels.contains(rs)) continue;//关系集不为空 且 关系集中没有当前关系，则不添加此关系
			
			boolean relExisted = relExisted(rs, curRelsList);
			if(relExisted) continue;
			
			Node tempNode = curNodeId == rs.getStartNodeId() ? rs.getEndNode():rs.getStartNode();
			
			boolean idExisted = nodeExisted(tempNode, curNodesList);
			if(!idExisted) continue;
			
			myRel.put("id", rs.getId());
			myRel.put("label", rs.getType().name().toString());
			myRel.put("source",rs.getStartNodeId());
			myRel.put("target", rs.getEndNodeId());
			
			Map<String,Object> relProperties = rs.getAllProperties();
			for(Map.Entry<String, Object> entry : relProperties.entrySet()) {
				myRel.put(entry.getKey(), entry.getValue());
			}
			curRelsList.add(myRel);
		}
	}

	/**
	 * 判断要添加的节点是否已经存在
	 * @param addNode
	 * @param curNodesList
	 * @return
	 */
	public boolean nodeExisted(Node addNode,List<Map<String,Object>> curNodesList) {
		
		long curNodeId = addNode.getId();
		for(Map<String,Object> m:curNodesList) {
			if(m.get("id").equals(curNodeId)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断要添加的关系是否已经存在
	 * @param addRel
	 * @param curRelsList
	 * @return
	 */
	public boolean relExisted(Relationship addRel,List<Map<String,Object>> curRelsList) {
		
		long curRelId = addRel.getId();
		for(Map<String,Object> r: curRelsList) {
			if(r.get("id").equals(curRelId)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取节点的label
	 * 由于一个节点有多个label，前台展示时只展示特定的label
	 * @param it
	 * @return
	 */
	public String getNodeLabel(Iterator<Label> it) {
		
		List<Label> labels =new ArrayList<>();
		while(it.hasNext()) {
			Label l = it.next();
			labels.add(l);
		}
		
		return labels.toString().substring(1, labels.toString().length()-1);
	}
	/**
	 * 给两个节点之间的关系编号
	 * @param numLinks
	 * @param numNodes
	 */
	public void numberingRels(List<Map<String,Object>> numLinks,List<Map<String,Object>> numNodes) {
		for (Map<String, Object> rel : numLinks) {
			long source=getIndex((long) rel.get("source"), numNodes);
			long target=getIndex((long) rel.get("target"), numNodes);
			rel.put("source", source);
			rel.put("target", target);
			rel.put("no", 0);
			rel.put("sum", 0);
		}
		/**
		 * (0)对于每一条关系r，初始化no,sum均为0，这一步在上面已完成
		 * (1)在与其它关系对比前，首先判断no是否为0
		 * 		若为0，表明该关系r是相同首尾节点所有关系中最先被遍历的，则no赋1
		 * 		若不为0，表明与该关系r相同首尾节点所有关系均已被编号，对no不做改变
		 * (2)然后开始遍历其它所有除本身以外的关系r1
		 * 		同样的 若n为0	则表明与r1相同首尾节点的关系第一次被遍历  no自加1后 赋值给r1的no 此时r为该所有关系中首个被遍历
		 * 		若n不为0		表明与r1相同首尾节点的关系编号no完成，但sum还未赋值
		 * 		sum++ 计算与关系r相同首尾节点的关系数目
		 * (3)内层遍历结束 将sum赋值给r 至此 完成一条关系r的sum和no值，完成与关系r相同首尾节点的编号no
		 * 对于下一条关系，重复(1)~(3)，直到所有关系被遍历完
		 */
		for (Map<String, Object> rel : numLinks) {
			
			long source=(long) rel.get("source");
			long target=(long) rel.get("target");
			
			int no=1,sum=1;
			long relId = (long) rel.get("id");
			
			if((int)rel.get("no")==0) {//此处如果no为0，说明该关系在所有首尾相同的关系中第一个被遍历
				rel.put("no", 1);//
			}
			
			for(int j=numLinks.size()-1;j>=0;j--){
				boolean hasa=numLinks.get(j).get("source").equals(source) && numLinks.get(j).get("target").equals(target);
				boolean hasb=numLinks.get(j).get("source").equals(target) && numLinks.get(j).get("target").equals(source);
				
				if(hasa || hasb) {
					if((long)numLinks.get(j).get("id")!=relId) {
						
						if((int)numLinks.get(j).get("no")==0) {
							no++;
							numLinks.get(j).put("no", no);
						}
						
						sum++;
					}
				}
				
				rel.put("sum",sum);
			}
			
			
		}
	}
	/**
	 * 获取节点的索引 以得到target和source
	 * @param nodeId
	 * @param nodes
	 * @return 
	 */
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
