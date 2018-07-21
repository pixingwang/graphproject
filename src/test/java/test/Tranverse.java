package test;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;


public class Tranverse {

    public GraphDatabaseService db;
    public Iterator<Node> NodeIterator;
    public Iterator<Relationship> RelationshipIterator;
    public int NodeIteratorsize;
    public int RelationshipIteratorsize;
    
 public Tranverse(GraphDatabaseService graphdb) {
		this.db=graphdb;
	}    
//    public void getNearNodes(Node node,RelationshipType rel,Direction dir,int depth) 
//    {
// 	
//    	//�Ѿ�ʹ�õĽڵ���߹�ϵ
//    	Map<Long, Node> inuse=new HashMap<Long, Node>();
//
//		TraversalDescription t= db.traversalDescription()
//								.depthFirst()
//								.uniqueness(Uniqueness.NONE)
//								.relationships(rel,dir)
//								.evaluator(Evaluators.toDepth(depth))
//								.evaluator(new nodeevaluator(inuse));
//		
//		Iterator<Path>p= t.traverse(node).iterator();
//    	//����������Ϣ
//		while(p.hasNext()) {
//			p.next();
//		}
//		
//		NodeIterator=inuse.values().iterator();
//		
//    }
    
    public void search(Node node,RelationshipType rel,Direction dir,int depth) 
    {
 	
    	//�Ѿ�ʹ�õĽڵ���߹�ϵ
    	Map<Long, Relationship> relinuse=new HashMap<Long, Relationship>();
    	Map<Long, Node> nodeinuse=new HashMap<Long, Node>();

		TraversalDescription t= db.traversalDescription()
								.breadthFirst()
								.uniqueness(Uniqueness.NONE)
								.relationships(rel,dir)
								.evaluator(Evaluators.toDepth(depth))
								.evaluator(new relevaluator(relinuse,nodeinuse));
		
		Iterator<Path> p= t.traverse(node).iterator();
    	//����������Ϣ
		while(p.hasNext()) {
			p.next();
		}
		NodeIteratorsize=nodeinuse.size();
		RelationshipIteratorsize=relinuse.size();
		NodeIterator=nodeinuse.values().iterator();
		RelationshipIterator=relinuse.values().iterator();

    }
 

	  //��ϵ�����ж�
	  	public class relevaluator implements Evaluator{
	
	  		private Map<Long,Relationship> relinuse;
	  		Map<Long, Node> nodeinuse;
	  		private int count=0;
	  		
	  	    public relevaluator(Map<Long, Relationship> relinuse, Map<Long, Node> nodeinuse) {
	  			this.relinuse=relinuse;
	  			this.nodeinuse=nodeinuse;
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
	  			
	  			if(relinuse.size()>count) {
	  				count=relinuse.size();
	  				//������ϵʱ����л��������ڵ�
	  				if(!nodeinuse.containsKey(path.endNode().getId()))
	  				   nodeinuse.put(path.endNode().getId(),path.endNode());
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
	  	
//	//�ڵ�����ж�
//		public class nodeevaluator implements Evaluator{
//
//			private Map<Long,Node> nodeinuse;
//			private int count=0;
//			
//		    public nodeevaluator(Map<Long, Node> inuse) {
//				this.nodeinuse=inuse;
//			}
//			
//			@Override
//			public Evaluation evaluate(Path path) {
//
//				if(path.length()<1) {
//					count=0;
//					nodeinuse.put(path.startNode().getId(),path.startNode());
//					return Evaluation.EXCLUDE_AND_CONTINUE;
//					}
//
//				
//				if(!hasnotcircle(path)||nodeinuse.containsKey(path.endNode().getId()))
//					return Evaluation.EXCLUDE_AND_PRUNE;
//				
//				nodeinuse.put(path.endNode().getId(), path.endNode());//���·�
//
//				if(nodeinuse.size()>count) {
//					count=nodeinuse.size();
//					return Evaluation.INCLUDE_AND_CONTINUE;
//				}
//				
//				return Evaluation.EXCLUDE_AND_CONTINUE;
//			}
//		}

		//�ж��Ƿ��ظ��л�
		boolean hasnotcircle(Path path){

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
