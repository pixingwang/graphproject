package graph.server.importdata;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.neo4j.graphdb.Label;
import org.neo4j.ogm.annotation.Property;
import org.springframework.web.bind.annotation.RequestMapping;

import graph.server.Enum.Datapropertytype;

public class ImportSet  {

//	//数据源类型  文本  true  关系数据库 false 
//	private boolean datasource;
	//节点标签列
	private String[] Nodelabels;
	//节点关键字段 命名  id 或者关键字	
	private String key;
	//	关系类型 字符名
	private String Relationshiptypes;
	//	起始节点和结束节点字段id  或者关键字
	private String start,end;
	//设置是否是节点节点为true，关系为false
	private boolean type;
	
	//节点关键字所在位置
	private int keyindex;
	//起始节点和结束节点所在位置
	private int startindex,endindex;
	
	//文本路径
	private String path;
	//编码格式
	private String encoding;
	//文本分隔符
	private String separator;
	//是否含有表头 是true 否false
	private boolean header;

	//数据源地址
	private String address;
	//数据源端口
	private String port;
	//数据库类型
	private String databasetype;
	//数据库名字
	private String databasename;
	//用户名
	private String username;
	//密码 
	private String password;
	//表名
	private String table;
	
	//索引属性  键值对形式
	private LinkedHashMap<String,String> index;
	//约束属性  键值对形式
	private LinkedHashMap<String,String> constraint;
	//导入模式是否事务模式  事务模式true 非事务模式false
	private boolean loadtype;
	//更新类型  true是增量,false是全量更新
	private boolean updatetype;
	//筛选过滤规则 字符串数组 
	private String[] filter;
	
	//cypher导入的标签  事务模式导入时候所需节点和关系标签
	private String[] startlabels,endlabels;
	
	// 结构链表  按顺序  属性名字  ：属性类型   <string ,boolean,float,double,integer,long>
	public LinkedHashMap<String, String> structure ; 
	//不需要的列号  数组
	public List<Integer> excludecolumn ;
	
	/**获取编码格式
	 * @return
	 */
	public String getEncoding() {
		return encoding;
	}


	/**
	 * 获取结束节点id字段
	 * @return
	 */
	public String getEnd() {
		return end;
	}


	/**
	 * 获取id字段
	 * @return
	 */
	public String getKey() {
		return key;
	}


	public String[] getNodelabels() {
		return Nodelabels;
	}


	public String getPath() {
		return path;
	}


	public String getRelationshiptypes() {
		return Relationshiptypes;
	}
	
	public String getSeparator() {
		return separator;
	}
	/**
	 * 获取起始节点id字段
	 * @return
	 */
	public String getStart() {
		return start;
	}
	public boolean getType() {
		return type;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * 设置结束节点id字段
	 * @param end
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * 设置id字段
	 * @param id
	 */
	public void setKey(String id) {
		this.key = id;
	}

	/**
	 * 设计节点的标签类型可以有多个
	 * @param Nodelabels
	 */
	public void setNodelabels(String [] Nodelabels) {
		
		this.Nodelabels=Nodelabels;
		
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 设置关系的类型
	 * @param RelationshiptypeString
	 */
	public void setRelationshiptypes(String  Relationshiptypes) {
		this.Relationshiptypes=Relationshiptypes;

	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * 设置起始起点id字段
	 * @param start
	 */
	public void setStart(String start) {
		this.start = start;
	}

	public void setType(boolean type) {
		this.type = type;
	}


//	public boolean getDatasource() {
//		return datasource;
//	}


	public boolean getHeader() {
		return header;
	}


	public String getAddress() {
		return address;
	}


	public String getPort() {
		return port;
	}


	public String getDatabasetype() {
		return databasetype;
	}


	public String getDatabasename() {
		return databasename;
	}


	public String getUsername() {
		return username;
	}


	public String getPassword() {
		return password;
	}


	public String getTable() {
		return table;
	}


	public LinkedHashMap<String, String> getIndex() {
		return index;
	}


	public LinkedHashMap<String, String> getConstraint() {
		return constraint;
	}


	public boolean getLoadtype() {
		return loadtype;
	}


	public boolean getUpdatetype() {
		return updatetype;
	}


	public String[] getFilter() {
		return filter;
	}


//	public void setDatasource(boolean datasource) {
//		this.datasource = datasource;
//	}


	public void setHeader(boolean header) {
		this.header = header;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public void setPort(String port) {
		this.port = port;
	}


	public void setDatabasetype(String databasetype) {
		this.databasetype = databasetype;
	}


	public void setDatabasename(String databasename) {
		this.databasename = databasename;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public void setTable(String table) {
		this.table = table;
	}


	public void setIndex(LinkedHashMap<String, String> index) {
		this.index = index;
	}


	public void setConstraint(LinkedHashMap<String, String> constraint) {
		this.constraint = constraint;
	}


	public void setLoadtype(boolean loadtype) {
		this.loadtype = loadtype;
	}


	public void setUpdatetype(boolean updatetype) {
		this.updatetype = updatetype;
	}


	public void setFilter(String[] filter) {
		this.filter = filter;
	}


	public Label[] getStartlabels() {
		Label tmp[]=new Label[startlabels.length];
		for(int i=0;i<tmp.length;i++) {
			tmp[i]=Label.label(startlabels[i]);
		}
		
		return tmp;
	}


	public Label[] getEndlabels() {
		
		Label tmp[]=new Label[endlabels.length];
		for(int i=0;i<tmp.length;i++) {
			tmp[i]=Label.label(endlabels[i]);
		}
		
		return tmp;
	}


	public void setStartlabel(String[] startlabel) {
		this.startlabels = startlabel;
	}


	public void setEndlabel(String[] endlabel) {
		this.endlabels = endlabel;
	}


	public LinkedHashMap<String, Datapropertytype> getStructure() {
		
		LinkedHashMap<String, Datapropertytype> struct=new LinkedHashMap<>();
		
		for(Entry<String, String> entry:structure.entrySet())
		switch(entry.getValue().toLowerCase()) {
		case "string":struct.put(entry.getKey(), Datapropertytype.String);break;
		case "boolean":struct.put(entry.getKey(), Datapropertytype.Boolean);break;
		case "float":struct.put(entry.getKey(), Datapropertytype.Float);break;
		case "double":struct.put(entry.getKey(), Datapropertytype.Double);break;
		case "long":struct.put(entry.getKey(), Datapropertytype.Long);break;
		case "integer":struct.put(entry.getKey(), Datapropertytype.Integer);break;
		}
		
		return struct;
	}


	public List<Integer> getExcludecolumn() {
		return excludecolumn;
	}


	public void setStructure(LinkedHashMap<String, String> structure) {
		this.structure = structure;
	}


	public void setExcludecolumn(List<Integer> excludecolumn) {
		this.excludecolumn = excludecolumn;
	}
	
	public int getKeyindex() {
		return keyindex;
	}


	public int getStartindex() {
		return startindex;
	}


	public void setKeyindex(int keyindex) {
		this.keyindex = keyindex;
	}


	public void setStartindex(int startindex) {
		this.startindex = startindex;
	}


	public void setStartlabels(String[] startlabels) {
		this.startlabels = startlabels;
	}


	public int getEndindex() {
		return endindex;
	}


	public void setEndindex(int endindex) {
		this.endindex = endindex;
	}
}
