package graph.server.importdata;

import java.io.BufferedReader;
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

import org.neo4j.graphdb.Label;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import graph.server.Enum.Datapropertytype;
import graph.server.Enum.Datatype;
import graph.server.tools.FilterImport;
import graph.server.tools.FilterImport.filter;


/**
 * 以流的形式读取数据，默认的分隔符是"\001" 默认的文件编码格式是 utf-8
 * @author dx
 *
 */
@Component
@Scope("prototype")
public class Batchfromstream {

	//默认设置
	private String split="\001";
	private Charset charset=Charset.forName("utf-8");
	private BatchInserter inserter;
	private boolean header=false;
	private boolean updatetype=true;
	private HashMap<Integer,List<FilterImport.filter>> filters;
	
	// 增加数据进入数据库
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
	 */
	public String UpdateDB(String databasepath, String filepath,BatchDatastructure data ,Datatype datatype) throws IllegalStateException, IOException {
		//如果是全量更新则删除数据库文件
		if(!updatetype) Deletedatabase(databasepath);
		
		long starttime = System.currentTimeMillis();

		//获取当前路径文件夹下方所有文件流
		Sourcedata sd=new Sourcedata();
		List<BufferedReader> readerlist=sd.getOpenSourceStreamfromdisk(filepath, charset);
		if(readerlist==null)
		readerlist=sd.getOpenSourceStreamfromhadoop(filepath, charset);
		
		for (int i = 0; i < readerlist.size(); i++) {

			//依次使用流
			BufferedReader reader = readerlist.get(i);
			switch (datatype) {
			case Node:
				insertnode(inserter, reader, data, split);
				break;
			case Relationship:
				insertrelationship(inserter, reader,  data, split);
				break;
			}
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
	 * @param inserter  插入接口
	 * @param reader  读取字符流
	 * @param data   节点结构体
	 * @param split   分隔符字符串
	 */
	private void insertnode(BatchInserter inserter, BufferedReader reader,BatchDatastructure data,String split) {
		
		long starttime = System.currentTimeMillis();
		
		Iterator<Entry<String, Datapropertytype>> entries= data.getstructure().entrySet().iterator();
		List<String> propertylist=new ArrayList<String>();
		List<Datapropertytype> types=new ArrayList<Datapropertytype>();
		Entry<String, Datapropertytype> entry;
		
		long id=-1;
		//记录id所在的位置
		int idindex=((BatchNodeData)data).getKeyindex(),index=-1;
		
		while(entries.hasNext()) {
			index++;
			entry=entries.next();
			
		   propertylist.add(entry.getKey());
		   types.add(entry.getValue());
	
		}
		
		List<Integer> exclude=data.getExcludecolumn();
		
	    Map<String, Object> properties = new HashMap<>(); 
		int count=0;
		
		//迭代添加属性
		try {
			//是否包含表头
			if(header) reader.readLine();
			
			String line=reader.readLine();
			while(line!=null) {
				boolean filterboolean=false;//决定是否保持掉本次记录 默认为删除
				String linearray[]=line.split(split,-1);
				line=reader.readLine();
				
				try {
				for (int i = 0; i <=index; i++) {
					
					if(exclude!=null&&exclude.contains(i))
						continue;
					
					if(i==idindex) {
						id=(long) changetype(types.get(i), linearray[i]);
						continue;
					}
					//筛选
					if(filters!=null&&filters.containsKey(i)) {
						for(filter f:filters.get(i)) {
							if(f.compare(linearray[i]))
								filterboolean=true;
						}
					}
					
					properties.put(propertylist.get(i), changetype(types.get(i),linearray[i]));
					
				}	
				}catch(Exception e) {
					System.out.println("转换属性存在问题第"+count+"行");
					e.printStackTrace();
					continue;
				}
				
				if(!filterboolean)
					continue;
				
				if (inserter.nodeExists(id))
					inserter.setNodeProperties(id, properties);
				else
					inserter.createNode(id, properties, ((BatchNodeData)data).getNodeLabels());
				
				count++;

			}

		} catch (IOException e) {
			System.out.println("文件问题或空文件");
			e.printStackTrace();
		}
		
		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入节点数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");
		
	}
	
	
	
	/**
	 * @param inserter
	 * @param reader
	 * @param data
	 * @param split
	 */
	private void insertrelationship(BatchInserter inserter, BufferedReader reader,BatchDatastructure data,String split){

		Map<String, Object> properties = new HashMap<>();

		long starttime = System.currentTimeMillis();
		
		Iterator<Entry<String, Datapropertytype>> entries= data.getstructure().entrySet().iterator();
		List<String> propertylist=new ArrayList<String>();
		List<Datapropertytype> types=new ArrayList<Datapropertytype>();
		Entry<String, Datapropertytype> entry;

		long startid=-1,endid=-1;
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
		
		try {
			//是否包含表头
			if(header) reader.readLine();
			
			String line=reader.readLine();
			while (line !=null) {
				boolean filterboolean=false;//决定是否保持掉本次记录 默认为删除
				String linearray[]=line.split(split,-1);
				line=reader.readLine();

				try {
					for (int i = 0; i <= index; i++) {
						
						if(exclude!=null&&exclude.contains(i))
							continue;
						
						if(i==startindex) {
							startid=(long) changetype(types.get(i),linearray[i]);
							continue;
						}
						if(i==endindex) {
							endid=(long) changetype(types.get(i),linearray[i]);
							continue;
						} 
						
						//筛选
						if(filters!=null&&filters.containsKey(i)) {
							for(filter f:filters.get(i)) {
								if(f.compare(linearray[i]))
									filterboolean=true;
							}
						}
						
						properties.put(propertylist.get(i), changetype(types.get(i),linearray[i]));

					}
				} catch (Exception e) {
					System.out.println("转换属性存在问题第"+count+"行");
					e.printStackTrace();
					continue;
					
				}

//				if(startid==endid) continue;//去除自己担保 TODO ????

				if(!filterboolean)
					continue;
				
				inserter.createRelationship(startid,endid,((BatchRelationshipData)data).getRelationshipTypes(), properties);

				count++;
			}
		} catch (Exception e) {
			System.out.println("文件问题或空文件");
			e.printStackTrace();
		}

		long end = (System.currentTimeMillis() - starttime) / 1000;
		debug("本次共插入关系数目：" + count + "插入用时  " + end / 3600 + "时" + (end / 60)
				% 60 + "分" + end % 60 + "秒");

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
	 */
	public void Deletedatabase(String Neo4jDatabasePath) {
		
		File databasefile=new File(Neo4jDatabasePath);
		File files[]=databasefile.listFiles();
		
		//清空数据库文件
		for(File f:files){
			f.delete();
		}
	}
	
	/**
	 * 输入地址设置插入工具
	 * @param databasepath
	 */
	public void SetInserter(String databasepath) {
		final Map<String, String> config = new HashMap<>();
		config.put("cache_type", "none");
		config.put("use_memory_mapped_buffers", "true");
		config.put("dbms.memory.heap.max_size", "10g");
	    try {
			inserter = BatchInserters.inserter(
					new File(databasepath).getAbsoluteFile(), config);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
	}
	
	/**
	 * 操作完毕之后最后关闭插入工具
	 */
	public void CloseInserter() {
		this.inserter.shutdown();
	}
	
	/**
	 * 创建一个索引
	 * @param label  标签类型
	 * @param property  属性名称
	 */
	public void Createindex(String label,String property ) {
		try {
		this.inserter.createDeferredSchemaIndex(Label.label(label)).on(property).create();
		}catch (Exception e) {
			System.out.println("创建索引错误");
		}
	}
	
	/**
	 * 创建一个约束
	 * @param label  标签类型
	 * @param property  属性名称
	 */
	public void Createconstraint(String label,String property ) {
		try {
		this.inserter.createDeferredConstraint(Label.label(label)).assertPropertyIsUnique(property).create();
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


	/**获取过滤器
	 * @return
	 */
	public HashMap<Integer, List<FilterImport.filter>> getFilters() {
		return filters;
	}


	/**设置过滤器
	 * @param filter
	 */
	public void setFilters(HashMap<Integer, List<FilterImport.filter>> filter) {
		this.filters = filter;
	}
	
	
	
}
