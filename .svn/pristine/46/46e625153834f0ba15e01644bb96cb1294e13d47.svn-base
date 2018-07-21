package graph.server.importdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import graph.server.GraphHub;
import graph.server.Enum.Datapropertytype;
import graph.server.config.GraphHubConfiguration;
import graph.server.tools.FilterImport;
import graph.server.tools.FilterImport.filter;

/**
 * 通过cypher 批量导入数据
 * @author dx
 *
 */
@Component
@Scope("prototype")
public class BatchfromstreamCypher {
	
	@Autowired
	GraphHubConfiguration graphHubConfiguration;
	
	//默认设置
	private String split="\001";
	private Charset charset=Charset.forName("utf-8");
	private boolean header=false;
	private boolean updatetype=true;
	private HashMap<Integer,List<FilterImport.filter>> filters;

	
	@Autowired
	GraphHub graphHub;
	
	// 增加节点进入数据库
	/**
	 * 根据输入条件进行更新图数据库 
	 * 
	 * @param databasepath   图数据库的路径
	 * @param filepath    文件存放位置  支持本地磁盘路径和hadoop路径
	 * @param data   当前导入数据的数据结构
	 * @param datatype   当前导入数据的类型
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public String UpdateNode( String filepath,BatchDatastructure data ) throws IllegalStateException, IOException, URISyntaxException {
		
		//如果是全量更新则删除数据库文件
		if(!updatetype) Deletedatabase();
		
		GraphDatabaseService graph=graphHub.getEmbeddeddriver().getGraphDatabaseService();
		
		
		long starttime = System.currentTimeMillis();

		//获取当前路径文件夹下方所有文件流
		Sourcedata sd=new Sourcedata();
		List<BufferedReader> readerlist=sd.getOpenSourceStreamfromdisk(filepath, charset);
		if(readerlist==null)
		readerlist=sd.getOpenSourceStreamfromhadoop(filepath, charset);
		
		for (int i = 0; i < readerlist.size(); i++) {

			//依次使用流
			BufferedReader reader = readerlist.get(i);
				insertnode( graph,reader, data, split);
			//停止流
			reader.close();
		}

//		// 插入结束
//		inserter.shutdown();

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("总共用时  " + end / 3600 + "时" + (end / 60) % 60 + "分" + end % 60
				+ "秒");

		return "数据库已更新";
	}
	
	// 增加关系进入数据库
	/**
	 * 根据输入条件进行更新图数据库 
	 * 
	 * @param databasepath   图数据库的路径
	 * @param filepath    文件存放位置  支持本地磁盘路径和hadoop路径
	 * @param data   当前导入数据的数据结构
	 * @param datatype   当前导入数据的类型
	 * @param  startlabels  起始节点标签
	 * @param  endlabels  结束节点标签
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public String UpdateRel( String filepath,BatchDatastructure data ,Label[] startlabels,Label[] endlabels) throws IllegalStateException, IOException, URISyntaxException {		
		

		//如果是全量更新则删除数据库文件
		if(!updatetype) Deletedatabase();
		
		GraphDatabaseService graph=graphHub.getEmbeddeddriver().getGraphDatabaseService();
		
		long starttime = System.currentTimeMillis();

		//获取当前路径文件夹下方所有文件流
		Sourcedata sd=new Sourcedata();
		List<BufferedReader> readerlist=sd.getOpenSourceStreamfromdisk(filepath, charset);
		if(readerlist==null)
		readerlist=sd.getOpenSourceStreamfromhadoop(filepath, charset);
		
		for (int i = 0; i < readerlist.size(); i++) {

			//依次使用流
			BufferedReader reader = readerlist.get(i);
			insertrelationship(graph,reader, data, split,startlabels,endlabels);
			//停止流
			reader.close();
		}

//		// 插入结束
//		inserter.shutdown();

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("总共用时  " + end / 3600 + "时" + (end / 60) % 60 + "分" + end % 60
				+ "秒");

		return "数据库已更新";
	}

	
	/**
	 * 通用插入节点方法
	 * @param 图数据库实例
	 * @param reader  读取字符流
	 * @param data   节点结构体
	 * @param split   分隔符字符串
	 */
	private void insertnode(GraphDatabaseService graph, BufferedReader reader,BatchDatastructure data,String split) {
		
		long starttime = System.currentTimeMillis();
		Iterator<Entry<String, Datapropertytype>> entries= data.getstructure().entrySet().iterator();
		List<String> propertylist=new ArrayList<String>();
		List<Datapropertytype> types=new ArrayList<Datapropertytype>();
		Entry<String, Datapropertytype> entry;
		String key=((BatchNodeData)data).getKey();//节点关键字
		Label labels[]=((BatchNodeData)data).getNodeLabels();
		

		//记录id或关键字所在的位置
		int idindex=((BatchNodeData)data).getKeyindex(),index=-1;
		
		while(entries.hasNext()) {
			index++;
			entry=entries.next();
			
		   propertylist.add(entry.getKey());
		   types.add(entry.getValue());

		}
		
		List<Integer> exclude=data.getExcludecolumn();
		
		int count=0;
		
		//迭代添加属性
		try(Transaction tx=graph.beginTx()){
			//是否包含表头
			if(header) reader.readLine();
			
			String line=reader.readLine();
			while(line!=null) {
				boolean filterboolean=true;//决定是否保持掉本次记录 默认为保留 true
				String linearray[]=line.split(split,-1);
				line=reader.readLine();
				
				//预览筛选
				for (int i = 0; i <=index; i++) {
					//筛选
					if(filters!=null&&filters.containsKey(i)) {
						for(filter f:filters.get(i)) {
							if(!f.compare(linearray[i]))
								filterboolean=false;
						}
					}
				}
				
				if(!filterboolean) continue;
				
				Node tmp=findfirstnode(graph, labels, key, linearray[idindex]);
				if(tmp==null)
					tmp=graph.createNode(labels);

				try {
				for (int i = 0; i < linearray.length; i++) {
					
					if(exclude!=null&&exclude.contains(i))
						continue;

					tmp.setProperty(propertylist.get(i), changetype(types.get(i),linearray[i]));
					
				}	
				}catch(Exception e) {
					System.out.println("转换属性存在问题第"+count+"行");
					e.printStackTrace();
					continue;
				}
				
				count++;

			}

			tx.success();
		} catch (IOException e) {
			System.out.println("文件问题或空文件");
			e.printStackTrace();
		}
		
		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入节点数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");
		
	}
	
	
	
	/**
	 * 通用插入关系方法
	 * @param 图数据库实例
	 * @param reader  读取字符流
	 * @param data   节点结构体
	 * @param split   分隔符字符串
	 * @param startlabels 起始节点标签
	 * @param endlabels 结束节点标签
	 */
	private void insertrelationship(GraphDatabaseService graph, BufferedReader reader,BatchDatastructure data,String split,
			Label[] startlabels,Label[] endlabels){

		long starttime = System.currentTimeMillis();
		
		Iterator<Entry<String, Datapropertytype>> entries= data.getstructure().entrySet().iterator();
		List<String> propertylist=new ArrayList<String>();
		List<Datapropertytype> types=new ArrayList<Datapropertytype>();
		Entry<String, Datapropertytype> entry;
		
		RelationshipType reltype=((BatchRelationshipData)data).getRelationshipTypes();
		//起始节点和结束节点关键字名字
		String startpropertyname=((BatchRelationshipData)data).getStart();
		String endpropertyname=((BatchRelationshipData)data).getEnd();

		int startindex=((BatchRelationshipData)data).getStartindex(),
				endindex=((BatchRelationshipData)data).getEndindex(),index=-1;
		
		while(entries.hasNext()) {
			index++;
			entry=entries.next();
			
			propertylist.add(entry.getKey());
			types.add(entry.getValue());
			
		}
		
		
		int count = 0;
		List<Integer> exclude=data.getExcludecolumn();
		
		try (Transaction tx=graph.beginTx()){
			//是否包含表头
			if(header) reader.readLine();
			
			String line=reader.readLine();
			while (line !=null) {
				boolean filterboolean=true;//决定是否保持掉本次记录 默认为保留 true
				String linearray[]=line.split(split,-1);
				line=reader.readLine();
				
				//预览筛选
				for (int i = 0; i <=index; i++) {
					//筛选
					if(filters!=null&&filters.containsKey(i)) {
						for(filter f:filters.get(i)) {
							if(f.compare(linearray[i]))
								filterboolean=false;
						}
					}
				}
				
				if(!filterboolean) continue;
				
				Node startnode=findfirstnode(graph, startlabels, startpropertyname, changetype(types.get(startindex),linearray[startindex]));
				Node endnode=findfirstnode(graph, endlabels, endpropertyname, changetype(types.get(endindex),linearray[endindex]));
				
//				有一个没有找到则跳过进行下一轮插入
				if(startnode==null||endnode==null)
				continue;
				
				Relationship rel=startnode.createRelationshipTo(endnode,reltype);
				
				try {
					for (int i = 0; i < linearray.length; i++) {
						
						if(exclude!=null&&exclude.contains(i))
							continue;
						
						if(i==startindex||i==endindex) 
							continue;

						rel.setProperty(propertylist.get(i), changetype(types.get(i),linearray[i]));
					}
				} catch (Exception e) {
					System.out.println("转换属性存在问题第"+count+"行");
					e.printStackTrace();
					continue;
					
				}

				count++;
			}
			
			tx.success();
		} catch (Exception e) {
			System.out.println("文件问题或空文件");
			e.printStackTrace();
		}

		long endtime = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入关系数目：" + count + "插入用时  " + endtime / 3600 + "时" + (endtime / 60)
				% 60 + "分" + endtime % 60 + "秒");

	}
	
	
	void debug(String s) {
		System.out.println(new Date().toLocaleString() + " >>> " + s);
	}

	void error(String s) {
		System.err.println(new Date().toLocaleString() + " >>> " + s);
	}
	
	private Object changetype(Datapropertytype datapropertytype,String str){
		Object value;
		try {
		switch (datapropertytype) {
		case String:
			if(!(str.equals("null")||str.equals(""))) value=str;
			else value=" ";
			break;
		case Long:
			value=Long.parseLong(str);
			break;
		case Integer:
			value=Integer.parseInt(str);
			break;
		case Float:
			value=Float.parseFloat(str);
			break;
		case Double:
			value=Double.parseDouble(str);
			break;
		case Boolean:
			value=Boolean.parseBoolean(str);
			break;
		default:
			value=" ";
			break;
		}
		}catch (Exception e) {
		switch(datapropertytype) {
		case String:
			value=" ";
			break;
		case Long:
		case Integer:
			value=0;
			break;
		case Float:
		case Double:
			value=0.0;
			break;
		case Boolean:
			value=true;
			break;
		default:
			value=" ";
			break;
		}
		}
		
		return value;
	}
	
	//更新前删除数据库
	/**
	 * 格式化图数据库
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public void Deletedatabase() throws IOException, URISyntaxException {
		System.out.println(111);
		graphHub.getEmbeddeddriver().getGraphDatabaseService().shutdown();
		System.out.println(graphHubConfiguration.getEmbbeded_databasePath());
		FileUtils.deleteRecursively(new File(new URI(graphHubConfiguration.getEmbbeded_databasePath())));
		graphHub.getEmbeddeddriver().configure();;
	
		/*GraphDatabaseService graph=graphinstarcesservice.getNeo4j();
		
		try(Transaction tx=graph.beginTx()){
			graph.execute("match (a) detach delete a");
			tx.success();
		}
		*/
	}
	
	
	/**
	 * 创建一个索引
	 * @param label  标签类型
	 * @param property  属性名称
	 * @throws IOException 
	 */
	public void Createindex(String label,String property ) throws IOException {
		GraphDatabaseService graph=graphHub.getEmbeddeddriver().getGraphDatabaseService();

		try (Transaction tx=graph.beginTx()){
		graph.schema().indexFor(Label.label(label)).on(property).create();
		tx.success();
		}catch (Exception e) {
			System.out.println("创建索引错误");
		}
	}
	
	/**
	 * 创建一个约束
	 * @param label  标签类型
	 * @param property  属性名称
	 * @throws IOException 
	 */
	public void Createconstraint(String label,String property ) throws IOException {
		GraphDatabaseService graph=graphHub.getEmbeddeddriver().getGraphDatabaseService();
		
		try (Transaction tx=graph.beginTx()){
			graph.schema().constraintFor(Label.label(label)).assertPropertyIsUnique(property).create();
			tx.success();
			}catch (Exception e) {
			System.out.println("创建约束错误");
		}
	}
	
	/**
	 * 获取默认的流分隔符
	 * @return
	 */
	public String getSplit() {
		return split;
	}
	
	/**
	 * 获取默认的字符编码方式
	 * @return
	 */
	public Charset getCharset() {
		return charset;
	}
	
	/**
	 * 设定默认的分隔符
	 * @param split
	 */
	public void setSplit(String split) {
		this.split = split;
	}
	
	/**
	 * 设定默认的文件编码方式
	 * @param charset
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}


	/**是否包含表头
	 * @return
	 */
	public boolean getHeader() {
		return header;
	}


	/**更新类型是否是全量更新
	 * @return
	 */
	public boolean getUpdatetype() {
		return updatetype;
	}


	/**设定是否包含表头
	 * @param header
	 */
	public void setHeader(boolean header) {
		this.header = header;
	}


	/**设定更新是否增量更新  增量true  全量  false
	 * @param updatetype
	 */
	public void setUpdatetype(boolean updatetype) {
		this.updatetype = updatetype;
	}


	public HashMap<Integer, List<FilterImport.filter>> getFilters() {
		return filters;
	}


	public void setFilters(HashMap<Integer, List<FilterImport.filter>> filters) {
		this.filters = filters;
	}

//根据标签和关键字寻找第一个匹配的节点
	public Node findfirstnode(GraphDatabaseService graph,Label[] labels,String key,Object value) {
		Iterator<Node>iterator=graph.findNodes(labels[0], key, value);
		Node tmp=null;
		while(iterator.hasNext()) {
			 tmp=iterator.next();
//			 遍历标签，不包含则重置 并且验证下一个标签
		for (int i = 1; i < labels.length; i++) {
			if(!tmp.hasLabel(labels[i])) {
				tmp=null;
				break;
				}
		}
		 
		}
		
		return tmp;
		
	}
}
