package graph.server.tools;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import graph.server.GraphHub;
import graph.server.repository.Neo4jBoltRepository;







@Service
public class ToD3FormatFromBoltDriver {
	
	@Autowired
	MapUtilForBoltDriver mapUtil;
	@Autowired
	GraphHub graphHub;
	@Autowired
	Neo4jBoltRepository neo4jBoltRepository;
	
	public Map<String, List<Map<String, Object>>> toD3FormatByrel(Collection<Relationship> relationship) {
		List<Map<String, Object>> nodes = new ArrayList<>();
		List<Map<String, Object>> rels = new ArrayList<>();	
		
		return toD3Format(relationship, nodes, rels);	
	}
	
	public Map<String, List<Map<String, Object>>> toD3FormatBynode(Collection<Node> node) {
		List<Map<String, Object>> nodes = new ArrayList<>();
		List<Map<String, Object>> rels = new ArrayList<>();	
		Iterator<Node> result = node.iterator();
		
		while (result.hasNext()) {
			Node mynode=result.next();
			nodes.add(mapUtil.map(mynode));
			}	
	
		return mapUtil.map("nodes", nodes, "links", rels);
	}
	
	public Map<String, List<Map<String, Object>>> toD3FormatByExtend(Collection<Relationship> relationship,Map<String,List<Map<String, Object>>> old,Long id) {
		
		List<Map<String, Object>> nodes = old.get("nodes");
		List<Map<String, Object>> rels =  old.get("links");
		
		
		Map<String,Object> extendNode=new HashMap<>();
		
		extendNode.put("id", id);
		
		int i=mapUtil.getIndex(extendNode, nodes);
		
		for(int j=0;j<=rels.size()-1;j++) {
			if((int)rels.get(j).get("source")==i||(int)rels.get(j).get("target")==i) {
				rels.remove(j);
				j--;
			}
		}
		
		return toD3Format(relationship, nodes, rels);
		
	}
	
	private Map<String, List<Map<String, Object>>> toD3Format(Collection<Relationship> relationship,List<Map<String, Object>> nodes,List<Map<String, Object>> rels){

		Iterator<Relationship> result = relationship.iterator();
		
		
			while (result.hasNext()) {
				
				Relationship C=result.next();
				
				int source,target,index;
				Map<String, Object>  startNode=new HashMap<>();
				Map<String, Object> endNode=new HashMap<>();
				Map<String, Object> a=new HashMap<>();
				int sum=1;int no=1;
				boolean flagsum=true,flagno=true;
				int flag=0;
				
				Node StartNode,EndNode;
				
				/*startNode=mapUtil.map(StartNode);
				endNode=mapUtil.map(EndNode);*/
				
				
				index=mapUtil.getIndex(startNode, nodes);
					if(index==-1) {
						nodes.add(startNode);
						source=nodes.size()-1;
					}else {
						source=index;
					}
				index=mapUtil.getIndex(endNode, nodes);
					if(index==-1) {
						nodes.add(endNode);
						target=nodes.size()-1;
					}else {
						target=index;
					}
					
				a=mapUtil.map("source", source, "target", target,C);

				for(int j=rels.size()-1;j>=0;j--){
					boolean hasa=(int)rels.get(j).get("source")==source&&(int)rels.get(j).get("target")==target;
					boolean hasb=(int)rels.get(j).get("source")==target&&(int)rels.get(j).get("target")==source;
					
					
					if(flag==0) {
						if(hasa||hasb){
							if(flagsum){
							sum=(int)rels.get(j).get("sum")+1;
							flagsum=false;
							}
							
							if(flagno){
							 no=(int)rels.get(j).get("no")+1;
							 flagno=false;
							}	
							rels.get(j).replace("sum", sum);	
						}
					}
				}
				a.put("sum",sum);
				a.put("no",no);
				rels.add(a);
			}
				
			/*Iterator<Map<String,Object>> shownode=nodes.iterator();
			while(shownode.hasNext()) {
				Map<String,Object> sMap=shownode.next();
				System.out.println(sMap);
			}
			
			Iterator<Map<String,Object>> showlink=rels.iterator();
			while(showlink.hasNext()) {
				Map<String,Object> sMap=showlink.next();
				System.out.println(sMap);
			}
		*/
			/*System.out.println(nodes.size());
			System.out.println(rels.size());*/
		
		
		
		
		return mapUtil.map("nodes", nodes, "links", rels);	
	}
	
	
	public Map<String, List<Map<String, Object>>> toD3FormatByExtendInput(Collection<Relationship> relationship,Map<String, List<Map<String, Object>>> old, Long id) {
		List<Map<String, Object>> nodes = old.get("nodes");
		List<Map<String, Object>> rels =  old.get("links");
		Map<String, List<Map<String, Object>>> result;
		
		Map<String,Object> extendNode=new HashMap<>();
		
		extendNode.put("id", id);
		
		int i=mapUtil.getIndex(extendNode, nodes);
		
		for(int j=0;j<=rels.size()-1;j++) {
			if((int)rels.get(j).get("source")==i) {
				rels.remove(j);
				j--;
			}
		}
		
		List<Long> relsId=new ArrayList<>();
		Iterator<Map<String,Object>> relsIterator=rels.iterator();
		while(relsIterator.hasNext()) {
			Map<String,Object> sMap=relsIterator.next();
			relsId.add((Long)sMap.get("id"));
		}
		//System.out.println(relsId);
		Iterator<Relationship> relationshipIterator=relationship.iterator();
		List<Relationship> delList=new ArrayList<>();
		while(relationshipIterator.hasNext()) {
			Relationship rr=relationshipIterator.next();
			//System.out.println(rr.getId().toString());
			if(relsId.contains(rr.id())) {
				delList.add(rr);
			}
		}
		relationship.removeAll(delList);
		
		result=toD3Format(relationship, nodes, rels);
		
		
		List<Map<String, Object>> new_rels=new ArrayList<>();
		
		Iterator<Map<String,Object>> link=result.get("links").iterator();
		
		while(link.hasNext()) {
			
			int source,target;
			int sum=1;int no=1;
			boolean flagsum=true,flagno=true;
			int flag=0;
			Map<String,Object> l=link.next();
			source=(int)l.get("source");
			target=(int)l.get("target");
			for(int j=new_rels.size()-1;j>=0;j--){
				boolean hasa=(int)new_rels.get(j).get("source")==source&&(int)new_rels.get(j).get("target")==target;
				boolean hasb=(int)new_rels.get(j).get("source")==target&&(int)new_rels.get(j).get("target")==source;
				
				
				if(flag==0) {
					if(hasa||hasb){
						if(flagsum){
						sum=(int)new_rels.get(j).get("sum")+1;
						flagsum=false;
						}
						
						if(flagno){
						 no=(int)new_rels.get(j).get("no")+1;
						 flagno=false;
						}	
						new_rels.get(j).replace("sum", sum);	
					}
				}
			}
			l.replace("sum",sum);
			l.replace("no",no);
			new_rels.add(l);
		}
		
	/*	Iterator<Map<String,Object>> shownode=result.get("nodes").iterator();
		while(shownode.hasNext()) {
			Map<String,Object> sMap=shownode.next();
			System.out.println(sMap);
		}
		
		Iterator<Map<String,Object>> showlink=new_rels.iterator();
		while(showlink.hasNext()) {
			Map<String,Object> sMap=showlink.next();
			System.out.println(sMap);
		}
		*/
		result.replace("links", new_rels);
		
		
		
		return result;
		
	}
	

}
