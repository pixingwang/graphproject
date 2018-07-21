package graph.server.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.stereotype.Service;



@Service
public class MapUtilForBoltDriver {

	public Map<String, Object> map(Node c){
		Map<String, Object> result = new HashMap<String, Object>();
	
		result=c.asMap();
		
		List<String> nodeLabel=new ArrayList<>();
		Iterator<String> labels=c.labels().iterator();

		while(labels.hasNext()) {
			
			String label=labels.next();
			//System.out.println(label.name());
			nodeLabel.add(label);
		}
		
		result.put("label",nodeLabel.toString().substring(1, nodeLabel.toString().length()-1));
		result.put("id", c.id());
		//System.out.println(result);
	return result;
	}

	
	public Map<String, Object> map(String key1, Object value1, String key2, Object value2,Relationship r) {
		Map<String, Object> result = new HashMap<String, Object>(2);
		result.put(key1, value1);
		result.put(key2, value2);
		
		result.put("label",r.type());
		result.put("id",r.id());
		
		return result;
		
	}
	public Map<String, List<Map<String, Object>>> map(String key1, List<Map<String, Object>> value1, String key2, List<Map<String, Object>> value2) {
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>(2);
		result.put(key1, value1);
		result.put(key2, value2);
		return result;
	}
	
	
	public int getIndex(Map<String, Object> node,List<Map<String, Object>> nodes) {
		int index=-1;
		int length=nodes.size();
		for(int i=0;i<length;i++) {
			
			if(node.get("id").toString().equals(nodes.get(i).get("id").toString())){
				index=i;
				break;
			}
			
			
		}
		return index;
	}
	public Map<String,List<Map<String, Object>>> filterForTotal(Map<String,List<Map<String, Object>>> result,String min,String max){
		
		double mymin=Double.parseDouble(min);
		double mymax=Double.parseDouble(max);
		List<Map<String,Object>> rels=result.get("links");
		
		
		//筛选不符合条件的边
		for(int i=0;i<rels.size();i++) {
			double total=(double)rels.get(i).get("total");
			if(total<mymin||total>mymax) {
				result.get("links").remove(i);
				i--;
			}
		}
		rels=result.get("links");
		
		List<Map<String,Object>> nodes=new ArrayList<>();
		Map<String,Object> startNode,endNode;
		//筛选不符合条件的节点
		Iterator<Map<String, Object>> r=rels.iterator();
		int j=0;
		while(r.hasNext()) {
			Map<String,Object> myr=r.next();
			startNode=result.get("nodes").get((int)myr.get("source"));
			endNode=result.get("nodes").get((int)myr.get("target"));
			
			int source,target;
			
			if(nodes.indexOf(startNode)==-1) {
				nodes.add(startNode);
				source=nodes.size()-1;
			}else {
				source=nodes.indexOf(startNode);
			}
			
			if(nodes.indexOf(endNode)==-1) {
				nodes.add(endNode);
				target=nodes.size()-1;
			}else {
				target=nodes.indexOf(endNode);
			}
			rels.get(j).replace("source", source);
			rels.get(j).replace("target", target);
			j++;
		}
		
		result.replace("nodes", nodes);
		result.replace("links", rels);
		
		return result;
		
	}
	
	public  String createCypherforDBCircle(int n) {//通过输入框的数据创建不同的cypher语句
		String []node=new String[n+1];
		for(int i=1;i<=n;i++) {
			String name="n";
			name=name+i;
			node[i]=name;
		}
	
		StringBuilder cypher=new StringBuilder("match circle=(n0)");
		for(int i=1;i<=n;i++) {
			cypher.append("-[:DB]->("+node[i]+")");
		}
		int length=node.length;
		cypher.append(" where n0="+node[length-1]);
		
		for(int i=0;i<n-1;i++) {
			String name="n";
			name=name+i;
			for(int j=i+1;j<n;j++) {
				cypher.append(" and "+name+"<>"+node[j]);
			}
		}
		
		cypher.append(" return circle  ");
		return cypher.toString();
	}
	
	public  String createCypherforJYChain(Long id,int n,String minJYJE, String maxJYJE, String minJYRQ,String maxJYRQ) {//通过输入框的数据创建不同的cypher语句
		String []rel=new String[n+1];
		for(int i=1;i<=n;i++) {
			String name="n";
			name=name+i;
			rel[i]=name;
		}
	
		StringBuilder cypher=new StringBuilder("match JYChain=(n0:CK) ");
		for(int i=1;i<=n;i++) {
			cypher.append("-["+rel[i]+":JY]->(:CK)");
		}
		
		cypher.append(" where id(n0)="+id);
		
		for(int i=1;i<=n;i++){
			cypher.append(" and "+rel[i]+".jyje>toInteger("+minJYJE+") and "+rel[i]+".jyje<toInteger("+maxJYJE+") and toInteger("+rel[i]+".jyrq)>toInteger("+minJYRQ+") and toInteger("+rel[i]+".jyrq)<toInteger("+maxJYRQ+") ");
		}
		
		cypher.append(" return JYChain ");
		return cypher.toString();
	}

}
