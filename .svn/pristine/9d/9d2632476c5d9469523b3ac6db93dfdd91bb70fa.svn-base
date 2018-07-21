package graph.server.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;


/**
 * 社区发现算法---标签传播算法工具
 * @author dx
 * 
 */
public class CommunityLPA {

	//接受图数据库系统服务
	private GraphDatabaseService db;
	//初始化线程池的默认线程数量
	private int DEFAULT_POOL_THREADS = Runtime.getRuntime().availableProcessors() * 2;
	private static ThreadPoolExecutor threadpool;
	private  double decaynum;
	
	public  CommunityLPA(GraphDatabaseService db) {
		this.db=db;
    	int threadsize=DEFAULT_POOL_THREADS;//线程池大小
    	int queuesize=DEFAULT_POOL_THREADS*25;//默认任务队列大小
    	//建立初始化线程池
		this.threadpool=  new ThreadPoolExecutor(threadsize/2, threadsize, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queuesize), new CallerBlocksPolicy());
	}
	
	/**
	 * 这是一个社区发现算法工具---基于LPA标签传播算法
	 * @param iteratortimes  迭代次数
	 * @param Nodelabel   迭代节点类型
	 * @param partitionKey  分类属性
	 * @param Reltype   迭代关系类型
	 * @param nodeweight  迭代节点权重属性  默认1
	 * @param relweight   迭代关系权重属性  默认1
	 * @param direction   迭代关系方向    OUTGOING INCOMING BOTH
	 * @param batchSize   批处理数量
	 * @param decaynum    衰减系数
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void LPA(int iteratortimes,String Nodelabel,String Reltype,String partitionKey,String nodeweight,String relweight,String direction,int batchSize,double decaynum) throws InterruptedException, ExecutionException {
		Label nodelabel=Label.label(Nodelabel);//初始化标签
		RelationshipType reltyppe=RelationshipType.withName(Reltype);//初始化关系类型
		Direction direct=Direction.valueOf(direction);//初始化传播方向
		
		//changesum 每次迭代节点变化次数，nodesum 总节点数目 ，countflag 允许统计节点总数
		int changesum=0,nodesum=0;
		boolean countflag=false;this.decaynum=decaynum;
		
//		迭代开始
		for(int i = 0; i < iteratortimes; i++) {
		//一次性批处理节点数目，作为一次future任务
        List<Node> batch = null;
        List<Future<Integer>> futures = new ArrayList<>();
        
		//开始一次事务
		try(Transaction tx=db.beginTx()){
			Iterator<Node> nodes= db.findNodes(nodelabel);
			
			while(nodes.hasNext()) {
			
			if(!countflag) nodesum++;//数数总数.
			
			Node node=nodes.next();
				
            if (batch == null) {
                batch = new ArrayList<>(batchSize);
            }
            
            batch.add(node);
            
            //整理完毕一批次节点之后加入Future中准备进行处理
            if (batch.size() == batchSize) {
                futures.add(clusterBatch(batch, partitionKey, reltyppe, direct, nodeweight,relweight));
                batch = null;
            }
			
			}
			
			//扫尾没处理完毕的节点
            if (batch != null) {
                futures.add(clusterBatch(batch, partitionKey, reltyppe, direct, nodeweight,relweight));
            }
            tx.success();
		}
		
//		启动future任务
        for (Future<Integer> future : futures) {
        	while(!future.isDone());
        	changesum+=future.get().intValue();
        }
		
        System.out.println("总节点数目" +nodesum+"  更改分区数目  "+changesum+"  ");
        if(changesum==0) break;
        else {changesum=0;countflag=true;}
		}
		
//		任务结束之后关闭线程池
		threadpool.shutdown();

	}

    /**批量加入future中
     * @param batch  批量节点列表
     * @param partitionKey  设定分区关键字
     * @param relationshipType 传播关系类型 
     * @param direction   传播方向
     * @param nodeweight  节点权重属性名称
     * @param relweight   关系权重属性名称
     * @return  Future任务
     */
    private Future<Integer> clusterBatch(List<Node> batch, String partitionKey, RelationshipType relationshipType, Direction direction, String nodeweight, String relweight) {
 
    	Future<Integer> future= threadpool.submit(new Callable<Integer>(){
        	    int changesum=0;//改变分区的节点数目
        	
				@Override
				public Integer call() throws Exception {
					
					try(Transaction tx=db.beginTx()){
						//遍历batch节点列表
						for(int i=0;i<batch.size();i++) {
							
							Node node=batch.get(i);
				            Map<Object, List<Double>> votes = new HashMap<>();
				            
				            //遍历周围的关系获取周边节点
				            for (Relationship rel :
				                    relationshipType == null
				                            ? node.getRelationships(direction)
				                            : node.getRelationships(relationshipType, direction)) {
				                Node other = rel.getOtherNode(node);
				                Object partition = partition(other, partitionKey);
				                double weight = weight(rel, relweight) * weight(other, nodeweight)*decaynum;//计算权重影响  0.8 衰减因子
				               
				                vote(votes, partition, weight);
				            }
	
				            Object originalPartition = partition(node, partitionKey);
				            Object partition = originalPartition;
				            double weight = 0.0d;
				            double finalweight=0;//最后确定的权重
//				            遍历投票结果得出最终节点归属
				            for (Map.Entry<Object, List<Double>> entry : votes.entrySet()) {
				            	
				            	double entryweight=0;
				            	double maxweight=0;
				            	for(Double a:entry.getValue()) {
				            		entryweight+=a;
				            		if(a>maxweight) maxweight=a;
				            		}
				            	
				                if (weight < entryweight) {
				                    weight = entryweight;
				                    partition = entry.getKey();
				                    finalweight=maxweight;
				                }
				            }
//				            如果原始的分区改变了则更改当前分区和权重   节点变更计数器加1
				            if (!String.valueOf(partition).equals(String.valueOf(originalPartition))) {
				                node.setProperty(partitionKey, partition);
				                node.setProperty(nodeweight, finalweight);//建立分区影响权重
				                changesum++; //如果改变了分区则计数加一
				                }

						}
						
						tx.success();
					}
					
					return changesum;
				}
				});
    	
        return future;
    }
    
    //添加新进程被拒绝时候调用停顿后重新提交
    static class CallerBlocksPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                // block caller for 100ns
                LockSupport.parkNanos(100);
                try {
                    // submit again
                    executor.submit(r).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    //获取社区分类
    private Object partition(Node node, String partitionKey) {
        Object partition = node.getProperty(partitionKey, null);
        return partition == null ? node.getId() : partition;
    }
    
    //获取节点和关系的权重
    private double weight(PropertyContainer container, String propertyKey) {
        if (propertyKey != null) {
            Object propertyValue = container.getProperty(propertyKey, null);
            if (propertyValue instanceof Number) {
                return ((Number) propertyValue).doubleValue();
            }
        }
        return 1.0d;
    }
    //给中心点投票
    private void vote(Map<Object,List< Double>> votes, Object partition, double weight) {
        List<Double> currentWeight = votes.getOrDefault(partition, new ArrayList<>());
//        double newWeight = currentWeight + weight;
//        double newWeight = currentWeight>weight?currentWeight:weight;//选择新的
        currentWeight.add(weight);
        votes.put(partition, currentWeight);
         
    }
    
}
