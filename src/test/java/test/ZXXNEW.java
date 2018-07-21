package test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ZXXNEW
{
	@Context
    public GraphDatabaseService db;
    
    
public ZXXNEW(GraphDatabaseService graphdb) {
		this.db=graphdb;
	}

    public static void main(String args[]){
    	GraphDatabaseService graphdb=new GraphDatabaseFactory().newEmbeddedDatabase(new File("D:\\neo4jdatabase-923 -test"));
        ZXXNEW tmp=new ZXXNEW(graphdb);
        tmp.circle(3);

    }

    public Stream<path> circle( int depth) 
    {
    	//�����б�
    	List<List<String>> sortlist=new ArrayList<>();
    	
		CsvWriter w=new CsvWriter("D://result.csv", '-', Charset.forName("UTF-8"));
    	
		Tranverse tran=new Tranverse(db);
    	
		Label l=Label.label("C");
		ResourceIterator<Node> nodes;
		
		long start=new Date().getTime();
		
		try(Transaction tr=db.beginTx()){
		nodes= db.findNodes(l);

			//�漰��N��ȥ���ڵ��ǣ����뵹������㼯
		while(nodes.hasNext()){
			Node n=nodes.next();
			System.out.println(n.getId());
			tran.search(n, RelationshipType.withName("ZDB"), Direction.OUTGOING, depth);
			Iterator<Node> node= tran.getNodes();
			Iterator<Relationship> rel=tran.getRelationships();
			int nodesize=tran.NodeIteratorsize;
			int relsize=tran.RelationshipIteratorsize;
			
//			if(relinuse.size()>30) {//TODO ���˴���
			//��¼�ڵ������
			List<String>ll=new ArrayList<>();
			//������ǰ�ڵ���ھ�
			ll.add(String.valueOf(nodesize));
			ll.add("节点数");
			ll.add(String.valueOf(n.getId()));
			ll.add("号节点");
//			while(node.hasNext()) {
//				ll.add(String.valueOf(node.next().getId()));
//			}
			
			//TODO ���б�
			sortlist.add(ll);
			
//			try {
//				w.writeRecord(ll.toArray(new String[ll.size()]));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

//			}
		}
		
		w.close();
			}
		
		//TODO ��������
		sortlist.sort( new Comparator<List<String>>(){
			@Override
			public int compare(List<String> o1, List<String> o2) {
				if(Integer.valueOf((String) o1.get(0))>Integer.valueOf((String) o2.get(0)))
					return 1;
				else if(Integer.valueOf((String) o1.get(0))<Integer.valueOf((String) o2.get(0))) {
					return -1;
				}
				return 0;
			}});
		
		//д�� 
		CsvWriter csv=new CsvWriter("D://hhh.csv", '-', Charset.forName("UTF-8"));
		for (int i=sortlist.size()-1;i>=sortlist.size()-10000;i--) {
			try {
				csv.writeRecord(sortlist.get(i).toArray(new String[sortlist.get(i).size()]));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		csv.close();
		
		System.out.println((new Date().getTime())-start);
		return null;
    	//return t.traverse(N.get(0)).stream().map(path::new);
    }
    
 
    //�Խڵ���ʽ���·��
    public class path{
//    	public Path path;
    	public List<Object> list;
    	public path(Path path){
    		Iterator<Node> nodes=path.nodes().iterator();
    		List<Object> li=new ArrayList<>();
    		while(nodes.hasNext()){
    			li.add(nodes.next().getId());
    		}
    			this.list=li;
    	}
    }
    
//	//�ڵ�����ж�
//		public class nodeevaluator implements Evaluator{
//
//			private Map<Long,Long> nodeinuse;
//			private int count=0;
//			
//		    public nodeevaluator(Map<Long, Long> relinuse) {
//				this.nodeinuse=relinuse;
//			}
//			
//			@Override
//			public Evaluation evaluate(Path path) {
//				
//				if(path.length()<1) {
//					count=0;
//					nodeinuse.put(path.startNode().getId(), (long) 1);
//					return Evaluation.EXCLUDE_AND_CONTINUE;
//					}
//
//				
//				if(!hasnotcircle(path)||nodeinuse.containsKey(path.endNode().getId()))
//					return Evaluation.EXCLUDE_AND_PRUNE;
//				
//				nodeinuse.put(path.endNode().getId(), (long)1);//���·�
//
//				if(nodeinuse.size()>count) {
//					count=nodeinuse.size();
//					return Evaluation.INCLUDE_AND_CONTINUE;
//				}
//				
//				return Evaluation.EXCLUDE_AND_CONTINUE;
//			}
//		}
//
//		//�ж��Ƿ��ظ��л�
//		boolean hasnotcircle(Path path){
//
//			Iterator<Node> nodes=path.nodes().iterator();
//			long lastid=path.endNode().getId();
//			
//			while(nodes.hasNext()){
//				Node n=nodes.next();
//				if(nodes.hasNext()&&n.getId()==lastid){
//					return false;
//					}
//			}
//			
//			return true;
//		}
}
