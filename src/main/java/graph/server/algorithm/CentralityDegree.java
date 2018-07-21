package graph.server.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;


/**
 * 中心性统计工具
 * @author dx
 *
 */
public class CentralityDegree {

    public GraphDatabaseService db;
    public Iterator<Node> NodeIterator;
    public Iterator<Relationship> RelationshipIterator;
    public int NodeIteratorsize;
    public int RelationshipIteratorsize;
    Map<String,Integer> info;
    List<List<Relationship>> listrelationship;
    
    
public CentralityDegree(GraphDatabaseService graphdb) {
		this.db=graphdb;
	}    
 
    /**
     * 中心性统计方法
     * @param node  起始节点
     * @param rel   统计关系类型
     * @param dir   统计关系方向
     * @param depth 统计节点最深深度
     * @return  所有符合并且不重复的路径流
     */
    public Stream<Path> search(Node node,RelationshipType rel,Direction dir,int depth) 
    {
 	
    	//已经使用的节点或者关系
    	Map<Long, Relationship> relinuse=new HashMap<Long, Relationship>();
    	Map<Long, Node> nodeinuse=new HashMap<Long, Node>();
    	info=new HashMap<>();
    	//初始化储存关系列表3层
    	List<List<Relationship>> listrels=new ArrayList<List<Relationship>>();
    	for(int i=0;i<3;i++)
    		listrels.add(new ArrayList<>());

		TraversalDescription t= db.traversalDescription()
								.breadthFirst()
								.uniqueness(Uniqueness.NONE)
								.relationships(rel,dir)
								.evaluator(Evaluators.toDepth(depth))
								.evaluator(new relevaluator(relinuse,nodeinuse,listrels,info));
		
		Iterator<Path> p=t.traverse(node).stream().iterator();
    	//遍历所有信息
		List<Path> listpath=new ArrayList<Path>();
		while(p.hasNext()) {
			listpath.add(p.next());
		}
		NodeIteratorsize=nodeinuse.size();
		RelationshipIteratorsize=relinuse.size();
		NodeIterator=nodeinuse.values().iterator();
		RelationshipIterator=relinuse.values().iterator();
		
		return listpath.stream();

    }
 

	  //关系遍历判断
	  	public class relevaluator implements Evaluator{
	
	  		private Map<Long,Relationship> relinuse;
	  		private Map<Long, Node> nodeinuse;
	  		private Map<String,Integer> info;
	  		private int count=0;
	  		private List<List<Relationship>> listrels;
	  		
	  	    public relevaluator(Map<Long, Relationship> relinuse, Map<Long, Node> nodeinuse,List<List<Relationship>> listrels,Map<String, Integer> info) {
	  			this.relinuse=relinuse;
	  			this.nodeinuse=nodeinuse;
	  			this.info=info;
	  			this.listrels=listrels;
	  		}
	  		
	  		@Override
	  		public Evaluation evaluate(Path path) {
					
	  			if(path.length()<1){
	  				nodeinuse.put(path.startNode().getId(), path.startNode());
	  				count=0;
	  				return Evaluation.EXCLUDE_AND_CONTINUE;			
	  				}
	  			
	  			if(relinuse.containsKey(path.lastRelationship().getId()))
	  			   return Evaluation.EXCLUDE_AND_PRUNE;
	  			
	  			relinuse.put(path.lastRelationship().getId(), path.lastRelationship());
	  			//增加层级关系
	  			listrels.get(path.length()-1).add(path.lastRelationship());
	  			
	  			if(relinuse.size()>count) {
	  				count=relinuse.size();
	  				//新增关系时候才有机会新增节点
	  				if(!nodeinuse.containsKey(path.endNode().getId())) { 
	  				   nodeinuse.put(path.endNode().getId(),path.endNode());
	  				   info.put("depth"+path.length(), info.getOrDefault("depth"+path.length(),0)+1);

	  				   if(path.length()>info.get("maxlength")) info.put("maxlength", path.length());
	  				   }
	  				   
	  				if(!hasnotcircle(path))
	  					return Evaluation.INCLUDE_AND_PRUNE;
	  				else 
	  			        return Evaluation.INCLUDE_AND_CONTINUE;
	  			   }
	
	  			return Evaluation.EXCLUDE_AND_CONTINUE;
	  		}
	  		
	  	}
    
	  	public Iterator<Node> getNodes(){
	  		return NodeIterator;
	  	}
	  	public Iterator<Relationship>getRelationships(){
	  		return RelationshipIterator;
	  	}
	  	public int getNodeIteratrosize() {
	  		return NodeIteratorsize;
	  	}
	  	public int getReltionshipIteratrosize() {
	  		return RelationshipIteratorsize;
	  	}
	  	public int getdepth1() {
	  		return info.get("depth1");
	  	}
	  	public int getdepth2() {
	  		return info.get("depth2");
	  	}
	  	public int getdepth3() {
	  		return info.get("depth3");
	  	}
	  	public int getmaxlength() {
	  		return info.get("maxlength");
	  	}
	  	public List<List<Relationship>> getListRelationship() {
	  		return listrelationship;
	  	}
	  	
		//判断是否重复有环
		private boolean hasnotcircle(Path path){

			Iterator<Node> nodes=path.nodes().iterator();
			long lastid=path.endNode().getId();
			
			while(nodes.hasNext()){
				Node n=nodes.next();
				if(nodes.hasNext()&&n.getId()==lastid){
					return false;
					}
			}
			return true;
		}
}
