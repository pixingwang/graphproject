package test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.kernel.impl.util.dbstructure.GraphDbStructureGuide;
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

/**
 * This is an example showing how you could expose Neo4j's full text indexes as
 * two procedures - one for updating indexes, and one for querying by label and
 * the lucene query language.
 */
public class DB
{
    // This field declares that we need a GraphDatabaseService
    // as context when any procedure in this class is invoked
    @Context
    public GraphDatabaseService db;
    
    // This gives us a log instance that outputs messages to the
    // standard log, normally found under `data/log/console.log`
    @Context
    public Log log;
    
public DB(GraphDatabaseService graphdb) {
		this.db=graphdb;
	}

/*    // Only static fields and @Context-annotated fields are allowed in
    // Procedure classes. This static field is the configuration we use
    // to create full-text indexes.
    private static final Map<String,String> FULL_TEXT =
            stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext" );

    // This field declares that we need a GraphDatabaseService
    // as context when any procedure in this class is invoked
    @Context
    public GraphDatabaseService db;
    
    // This gives us a log instance that outputs messages to the
    // standard log, normally found under `data/log/console.log`
    @Context
    public Log log;

    *//**
     * This declares the first of two procedures in this class - a
     * procedure that performs queries in a legacy index.
     *
     * It returns a Stream of Records, where records are
     * specified per procedure. This particular procedure returns
     * a stream of {@link SearchHit} records.
     *
     * The arguments to this procedure are annotated with the
     * {@link Name} annotation and define the position, name
     * and type of arguments required to invoke this procedure.
     * There is a limited set of types you can use for arguments,
     * these are as follows:
     *
     * <ul>
     *     <li>{@link String}</li>
     *     <li>{@link Long} or {@code long}</li>
     *     <li>{@link Double} or {@code double}</li>
     *     <li>{@link Number}</li>
     *     <li>{@link Boolean} or {@code boolean}</li>
     *     <li>{@link java.util.Map} with key {@link String} and value {@link Object}</li>
     *     <li>{@link java.util.List} of elements of any valid argument type, including {@link java.util.List}</li>
     *     <li>{@link Object}, meaning any of the valid argument types</li>
     * </ul>
     *
     * @param label the label name to query by
     * @param query the lucene query, for instance `name:Brook*` to
     *              search by property `name` and find any value starting
     *              with `Brook`. Please refer to the Lucene Query Parser
     *              documentation for full available syntax.
     * @return the nodes found by the query
     *//*
    // TODO: This is here as a workaround, because index().forNodes() is not read-only
    @Procedure(value = "example.search", mode = Mode.WRITE)
    @Description("Execute lucene query in the given index, return found nodes")
    public Stream<SearchHit> search( @Name("label") String label,
                                     @Name("query") String query )
    {
        String index = indexName( label );
        
        // Avoid creating the index, if it's not there we won't be
        // finding anything anyway!
        if( !db.index().existsForNodes( index ))
        {
            // Just to show how you'd do logging
            log.debug( "Skipping index query since index does not exist: `%s`", index );
            return Stream.empty();
        }

        // If there is an index, do a lookup and convert the result
        // to our output record.
        return db.index()
                .forNodes( index )
                .query( query )
                .stream()
                .map( SearchHit::new );
        
    }

    *//**
     * This is the second procedure defined in this class, it is used to update the
     * index with nodes that should be queryable. You can send the same node multiple
     * times, if it already exists in the index the index will be updated to match
     * the current state of the node.
     *
     * This procedure works largely the same as {@link #search(String, String)},
     * with two notable differences. One, it is annotated with {@link Mode}.WRITE,
     * which is <i>required</i> if you want to perform updates to the graph in your
     * procedure.
     *
     * Two, it returns {@code void} rather than a stream. This is simply a short-hand
     * for saying our procedure always returns an empty stream of empty records.
     *
     * @param nodeId the id of the node to index
     * @param propKeys a list of property keys to index, only the ones the node
     *                 actually contains will be added
     *//*
    @Procedure(value = "example.index", mode=Mode.SCHEMA)
    @Description("For the node with the given node-id, add properties for the provided keys to index per label")
    public void index( @Name("nodeId") long nodeId,
                       @Name("properties") List<String> propKeys )
    {
        Node node = db.getNodeById( nodeId );

        // Load all properties for the node once and in bulk,
        // the resulting set will only contain those properties in `propKeys`
        // that the node actually contains.
        Set<Map.Entry<String,Object>> properties =
                node.getProperties( propKeys.toArray( new String[0] ) ).entrySet();

        // Index every label (this is just as an example, we could filter which labels to index)
        for ( Label label : node.getLabels() )
        {
            Index<Node> index = db.index().forNodes( indexName( label.name() ), FULL_TEXT );

            // In case the node is indexed before, remove all occurrences of it so
            // we don't get old or duplicated data
            index.remove( node );

            // And then index all the properties
            for ( Map.Entry<String,Object> property : properties )
            {
                index.add( node, property.getKey(), property.getValue() );
            }
        }
    }


    *//**
     * This is the output record for our search procedure. All procedures
     * that return results return them as a Stream of Records, where the
     * records are defined like this one - customized to fit what the procedure
     * is returning.
     *
     * These classes can only have public non-final fields, and the fields must
     * be one of the following types:
     *
     * <ul>
     *     <li>{@link String}</li>
     *     <li>{@link Long} or {@code long}</li>
     *     <li>{@link Double} or {@code double}</li>
     *     <li>{@link Number}</li>
     *     <li>{@link Boolean} or {@code boolean}</li>
     *     <li>{@link org.neo4j.graphdb.Node}</li>
     *     <li>{@link org.neo4j.graphdb.Relationship}</li>
     *     <li>{@link org.neo4j.graphdb.Path}</li>
     *     <li>{@link java.util.Map} with key {@link String} and value {@link Object}</li>
     *     <li>{@link java.util.List} of elements of any valid field type, including {@link java.util.List}</li>
     *     <li>{@link Object}, meaning any of the valid field types</li>
     * </ul>
     *//*
    public static class SearchHit
    {
        // This records contain a single field named 'nodeId'
        public long nodeId;

        public SearchHit( Node node )
        {
            this.nodeId = node.getId();
        }
    }

    private String indexName( String label )
    {
        return "label-" + label;
    }
 */   
    public static void main(String args[]){
    	GraphDatabaseService graphdb=new GraphDatabaseFactory().newEmbeddedDatabase(new File("D://neo4jdatabase-923"));
        DB tmp=new DB(graphdb);
        tmp.circle(9999);

    }
    
    private enum Rels implements RelationshipType{
    	R,ZDB
    }
    

    public Stream<path> circle( Number depth) 
    {
 	
    	//�Ѿ�ʹ�õĽڵ���߹�ϵ
    	Map<Long, Long> relinuse=new HashMap<Long, Long>();
    	Map<String, Long> hashtime=new HashMap<String, Long>();
    	relinuse.put((long)-1,(long)1);
    	hashtime.put("times",(long)0);
    	hashtime.put("round", (long)1);

    	//������ʹ�õĽڵ���߹�ϵ ��bignodeinuse ����ʹ��//TODO
    	List<String> tobeinuse=new ArrayList<>();//TODO
    	List<Long> pathlist=new ArrayList<>();
    	
		CsvWriter w=new CsvWriter("D://result.csv", '-', Charset.forName("UTF-8"));
    	
		CsvWriter x=new CsvWriter("D://result666.csv", ',', Charset.forName("UTF-8"));
		
		TraversalDescription t= db.traversalDescription()
								.depthFirst()
								.uniqueness(Uniqueness.NONE)
								.relationships(Rels.ZDB,Direction.OUTGOING)
								.evaluator(Evaluators.toDepth(depth.intValue()))
								.evaluator(new newnodeevaluator(relinuse,hashtime));
    	
		Label l=Label.label("C");
		ResourceIterator<Node> nodes;
		List<Long> N = new ArrayList<>();
		
		long start=new Date().getTime();
		
		try(Transaction tr=db.beginTx()){
		nodes= db.findNodes(l);
		System.out.println("node!");
		while(nodes.hasNext()){
			N.add(nodes.next().getId());
			}
	
		long aa=0;
		//���ӱ���
		int increase=10;
		//��ʼ˳�����
		while(!N.isEmpty()){
		
			List<Node> LN=new ArrayList<>();
			for(int i=0;i<N.size();i++)
				LN.add(db.getNodeById(N.get(i)));
			
			Iterator<Path> p= t.traverse(LN).iterator();
			
			System.out.println(p.hasNext()+"    "+relinuse.size()+"  ");
//			System.out.println("�ٶ�1    "+relinuse.get((long)1)+" "+relinuse.get((long)1)/(new Date().getTime()-start)*1000);
//			CsvWriter w=new CsvWriter("E://dx//result.csv", '-', Charset.forName("UTF-8"));
			List<String> ll=new ArrayList<>();//TODO
			
			while(p.hasNext()) {
				System.out.println(aa++);
				//ȡ��·����Ϣ//TODO
				Path path=p.next();//TODO
				if(aa%100==0)
				System.out.println("id   " +path.startNode().getId());
				Iterator<Node> nn= path.nodes().iterator();//TODO
				//ȡ��ĩβ��Ϣ//TODO
				long end=path.endNode().getId();//TODO
				boolean flag=true;//TODO
				
				while (nn.hasNext()) {
					Node tmp=nn.next();
					
					if(end!=tmp.getId()&&flag) continue; //TODO
					flag=false;//TODO
					ll.add(String.valueOf(tmp.getId()));
				}
				
				try {
					w.writeRecord(ll.toArray(new String[ll.size()]));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ll.clear();
			}
			
			System.out.println(666);
			Iterator<Long> inuse=relinuse.keySet().iterator();
			while(inuse.hasNext())
			if(N.contains(inuse.next()))
			N.remove(inuse.next());

//			if(hashtime.get("end")<starttime+1*1000){
//				N.remove(i);
//			}
			System.out.println("   剩余节点数量"+N.size()+" 循环次数"+(increase-9)+"  倍数"+hashtime.get("round"));

			//ÿ��ѭ�����֮����������
			hashtime.put("round", Math.round(hashtime.get("round")*1.5*(increase++)/(10.0)));
			
			if(increase==18)
				break;
		}
		w.close();
		}

		System.out.println(N.size());
		for(int i=0;i<N.size();i++){
		tobeinuse.add(String.valueOf(N.get(i)));
		}

		String a[]=tobeinuse.toArray(new String[tobeinuse.size()]);
		try {
			x.writeRecord(a);
			x.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
    
//��ϵ�����ж�
	public class relevaluator implements Evaluator{

		private Map<Long,Long> relinuse;
		
	    public relevaluator(Map<Long, Long> relinuse) {
			this.relinuse=relinuse;
		}
		
		@Override
		public Evaluation evaluate(Path path) {

			if(path.length()<2){
				if(path.length()==0)
					return Evaluation.EXCLUDE_AND_CONTINUE;
				
				relinuse.put(path.lastRelationship().getId(), (long)1);
				return Evaluation.EXCLUDE_AND_CONTINUE;				
				}
			
			if(relinuse.containsKey(path.lastRelationship().getId())||!isunique(path))
			   return Evaluation.EXCLUDE_AND_PRUNE;
								
			if(path.startNode().getId()==path.endNode().getId())
			   return Evaluation.INCLUDE_AND_PRUNE;

			return Evaluation.EXCLUDE_AND_CONTINUE;
		}
		
	}

	//�ڵ�����ж�
		public class nodeevaluator implements Evaluator{

			private Map<Long,Long> nodeinuse;
			private Map<String,Long> hashtime;
		    public nodeevaluator(Map<Long, Long> relinuse,Map<String,Long> hashtime) {
				this.nodeinuse=relinuse;
				this.hashtime=hashtime;
			}
			
			@Override
			public Evaluation evaluate(Path path) {
				
				if(path.length()<2){
					if(path.length()==0) {
						hashtime.put("times", (long) 0);
						return Evaluation.EXCLUDE_AND_CONTINUE;
						}

					nodeinuse.put(path.startNode().getId(), (long)1);//���·�
					if(nodeinuse.containsKey(path.endNode().getId()))
						return Evaluation.EXCLUDE_AND_PRUNE;
					
					hashtime.put("times", hashtime.get("times")+1);
					return Evaluation.EXCLUDE_AND_CONTINUE;
					}

				if(nodeinuse.containsKey(path.endNode().getId())) 
				return Evaluation.EXCLUDE_AND_PRUNE;
				
				hashtime.put("times", hashtime.get("times")+1);
				
				if(hashtime.get("times")>=hashtime.get("round")*1000000){
					return Evaluation.EXCLUDE_AND_PRUNE;
					}
				
				if(!isunique(path))
					return Evaluation.EXCLUDE_AND_PRUNE;
				
				if( path.startNode().getId()!=path.endNode().getId()){
					if(nodeinuse.containsKey(path.endNode().getId()))
					return Evaluation.EXCLUDE_AND_PRUNE;
					}
				else{
				   return Evaluation.INCLUDE_AND_PRUNE;}
				

				return Evaluation.EXCLUDE_AND_CONTINUE;
			}
			
		}

		//�ڵ�����ж�2
		public class newnodeevaluator implements Evaluator{
			private Map<Long,Long> nodeinuse;
			private Map<String,Long> hashtime;
			private long startid=0;
		    public newnodeevaluator(Map<Long, Long> relinuse,Map<String,Long> hashtime) {
		    	this.nodeinuse=relinuse;
		    	this.hashtime=hashtime;
			}
			
			@Override
			public Evaluation evaluate(Path path) {
			
				if(path.length()<1) {
					startid=path.nodes().iterator().next().getId();
					nodeinuse.put(startid,(long)1);
					hashtime.put("times", (long)0);
					return Evaluation.EXCLUDE_AND_CONTINUE;
					}
				
//				if(nodeinuse.containsKey(path.endNode().getId())&&(path.startNode().getId()!=path.endNode().getId()))
//					return Evaluation.EXCLUDE_AND_PRUNE;
				
				System.out.println(hashtime.get("times"));
				hashtime.put("times", hashtime.get("times")+1);
				
				if(hashtime.get("times")>hashtime.get("round")*10000) {
					nodeinuse.remove(startid);
					return Evaluation.EXCLUDE_AND_PRUNE;
					}
				
				if( path.startNode().getId()>path.endNode().getId()||!isunique(path))
					return Evaluation.EXCLUDE_AND_PRUNE;

				if(path.startNode().getId()==path.endNode().getId())
				   return Evaluation.INCLUDE_AND_PRUNE;
				
				return Evaluation.EXCLUDE_AND_CONTINUE;
			}
			
		}

		//���ӽڵ�����ж�
		public class bignodeevaluator implements Evaluator{

			private Map<Long,Long> nodeinuse;//�ѷ��ʱ��Լ�id��Ļ��Ľڵ����Լ�Ϊ����
			private Map<Long,Long> alreadyinuse;//�ѷ������л��Ļ��Ľڵ�
			private List<Long> tobeinuse ;//��ǰ·�����ѷ��ʱ��Լ�id��Ļ��Ľڵ�
			private List<Long> pathlist;//·���б�ڵ�id
			private GraphDatabaseService db;
			private CsvWriter w;
		    public bignodeevaluator(Map<Long, Long> relinuse,Map<Long, Long> alreadyinuse,List<Long> pathlist,List<Long> tobeinuse,GraphDatabaseService db,CsvWriter w) {
				this.nodeinuse=relinuse;
				this.tobeinuse=tobeinuse;
				this.pathlist=pathlist;
				this.alreadyinuse=alreadyinuse;
				this.db=db;
				this.w=w;
			}
			
			@Override
			public Evaluation evaluate(Path path) {
				
				if(path.length()<2){
					//����Ϊ0��·��ֻ��һ���ڵ�ֱ�Ӽ���
					if(path.length()==0)
						return Evaluation.EXCLUDE_AND_CONTINUE;
					
					if(nodeinuse.containsKey(path.startNode().getId())||nodeinuse.containsKey(path.endNode().getId()))
						return Evaluation.EXCLUDE_AND_PRUNE;
					
					if(pathlist.size()>0&&pathlist.get(0)!=path.startNode().getId()){
						for(int i=pathlist.size()-1;i>=0;i--){
							
							TraversalDescription t= db.traversalDescription()
									.depthFirst()
									.uniqueness(Uniqueness.NONE)
									.relationships(Rels.R,Direction.OUTGOING)
									.evaluator(Evaluators.toDepth(99999))
									.evaluator(new nodeevaluator(nodeinuse,null));
							
							Node N;
							try(Transaction tr=db.beginTx()){
								
							N= db.getNodeById(pathlist.get(i));

							Iterator<Path> p= t.traverse(N).iterator();
//							System.out.println("�ٶ�1    "+relinuse.get((long)1)+" "+relinuse.get((long)1)/(new Date().getTime()-start)*1000);
							
//							CsvWriter w=new CsvWriter("E://dx//result.csv", '-', Charset.forName("UTF-8"));
							List<String> ll=new ArrayList<>();//TODO
							
							long aa=0;
							
							while(p.hasNext()) {
//								if(aa%100==0)
//								System.out.println("�ٶ�2    "+relinuse.get((long) 1)+" "+relinuse.get((long)1)/(new Date().getTime()-start)*1000);
								System.out.println(aa++);
								//ȡ��·����Ϣ//TODO
								Path ppp=p.next();//TODO
								Iterator<Node> nn= ppp.nodes().iterator();//TODO
								//ȡ��ĩβ��Ϣ//TODO
								long end=ppp.endNode().getId();//TODO
								boolean flag=true;//TODO
								
								while (nn.hasNext()) {
									Node tmp=nn.next();
									if(end!=tmp.getId()&&flag) continue; //TODO
									flag=false;//TODO
									ll.add(String.valueOf(tmp.getId()));
								}
								
								try {
									w.writeRecord(ll.toArray(new String[ll.size()]));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								ll.clear();
							}
							}
							
							nodeinuse.put(pathlist.get(i),(long)1);
							
							pathlist.remove((int)i);
							}
					}
					
					
					//���Ԥ���ѷ�����Ͻڵ�����е�·����Ϣ
					pathlist.clear();
					//���¼�¼�µ�·����¼
					pathlist.add(path.startNode().getId());
					pathlist.add(path.endNode().getId());
					
					return Evaluation.EXCLUDE_AND_CONTINUE;
					}
			if(nodeinuse.containsKey(path.endNode().getId()))
				return Evaluation.EXCLUDE_AND_PRUNE;
//				Iterator<Node> nnn=path.nodes().iterator();
//				while(nnn.hasNext()){
//					System.out.print(nnn.next().getId()+"-");
//				}
//				System.out.print("  path1 "+path.length()+"\n");
//				for(long s:pathlist){
//					System.out.print(s+"-");
//				}
//				System.out.print(" path2  "+pathlist.size()+"\n");
				
				long startno=path.startNode().getId(),endno=path.endNode().getId();

//				if(pathlist.size()<path.length()){
//					if(pathlist.indexOf(endno)<0)
//					pathlist.add(endno);
//					}
				
				//��ȡ�����ڶ����ڵ�id
				Iterator<Node> reverse=path.reverseNodes().iterator();
				reverse.next();
				//��ȡ��ǰ·�������ڶ����ڵ�id���˳�·����¼tobeinuse�е��ѱ����ڵ���Ϣ
				long lastsecond=reverse.next().getId();
				//һ�������·���仯����ʽ ���û�о�������·������
				//��������л����߻��˵���� ·������С�ڵ�����һ�ε�·������				
				if(pathlist.size()>=path.length()+1) {
					for(int i=pathlist.size()-1;i>pathlist.indexOf(lastsecond);i--){
						
						TraversalDescription t= db.traversalDescription()
								.depthFirst()
								.uniqueness(Uniqueness.NONE)
								.relationships(Rels.R,Direction.OUTGOING)
								.evaluator(Evaluators.toDepth(99999))
								.evaluator(new nodeevaluator(nodeinuse,null));
						
						Node N;
						try(Transaction tr=db.beginTx()){
							
						N= db.getNodeById(pathlist.get(i));

						Iterator<Path> p= t.traverse(N).iterator();
//						System.out.println("�ٶ�1    "+relinuse.get((long)1)+" "+relinuse.get((long)1)/(new Date().getTime()-start)*1000);
						
//						CsvWriter w=new CsvWriter("E://dx//result.csv", '-', Charset.forName("UTF-8"));
						List<String> ll=new ArrayList<>();//TODO
						
						long aa=0;
						
						while(p.hasNext()) {
//							if(aa%100==0)
//							System.out.println("�ٶ�2    "+relinuse.get((long) 1)+" "+relinuse.get((long)1)/(new Date().getTime()-start)*1000);
							System.out.println(aa++);
							//ȡ��·����Ϣ//TODO
							Path ppp=p.next();//TODO
							Iterator<Node> nn= ppp.nodes().iterator();//TODO
							//ȡ��ĩβ��Ϣ//TODO
							long end=ppp.endNode().getId();//TODO
							boolean flag=true;//TODO
							
							while (nn.hasNext()) {
								Node tmp=nn.next();
								if(end!=tmp.getId()&&flag) continue; //TODO
								flag=false;//TODO
								ll.add(String.valueOf(tmp.getId()));
							}
							
							try {
								w.writeRecord(ll.toArray(new String[ll.size()]));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							ll.clear();
						}
						}
						
						nodeinuse.put(pathlist.get(i),(long)1);
						
						pathlist.remove((int)i);
						}
				}
				if(!pathlist.contains(endno))
				pathlist.add(endno);

				if(nodeinuse.containsKey(path.endNode().getId()))
					return Evaluation.EXCLUDE_AND_PRUNE;
				
				//�Ķ������Ƕ�������Ҫ��¼��ȥ 
				if(!isunique(path)){
					return Evaluation.EXCLUDE_AND_PRUNE;
					}
				
				return Evaluation.EXCLUDE_AND_CONTINUE;
			}
		}
		
	//�ж��Ƿ��ظ�����
	boolean isunique(Path path){

		Iterator<Node> nodes=path.nodes().iterator();
		long lastid=path.endNode().getId();
		
//		Map<Long, Node> tmp = new HashMap<Long, Node>();		
//		while(nodes.hasNext()){
//			Node n=nodes.next();
//			if(tmp.containsKey(n.getId())&&nodes.hasNext())
//			return false;
//					
//			tmp.put(n.getId(), n);
//		}
		
		nodes.next();
		while(nodes.hasNext()){
			Node n=nodes.next();
			if(nodes.hasNext()&&n.getId()==lastid){
				return false;}
		}
		
		return true;
	}
	
	//�ж��Ƿ��ظ��л�
	boolean hasnotcircel(Path path){

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
