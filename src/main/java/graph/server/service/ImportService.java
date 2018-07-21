package graph.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import graph.server.GraphHub;
import graph.server.Enum.Datatype;
import graph.server.importdata.BatchNodeData;
import graph.server.importdata.BatchRelationshipData;
import graph.server.importdata.Batchfromstream;
import graph.server.importdata.BatchfromstreamCypher;
import graph.server.importdata.ImportSet;
import graph.server.importdata.Sourcedata;
import graph.server.tools.FilterImport;

/**
 * @author dx
 *专门提供数据导入服务
 */
@Service
public class ImportService {

	@Autowired
	Sourcedata sourcedata;
	@Autowired
	Batchfromstream batchfromstream;
	@Autowired
	BatchfromstreamCypher batchfromstreamcypher;
	@Autowired
	GraphHub graphHub;
	@Autowired
	FilterImport filterimport;
	
	@Value("${Neo4jDatabasePath}")
	String Neo4jDatabasePath;
	
	/**
	 * 从本地文本路径中获取预览
	 * @param path  文件所在文件夹路径
	 * @param encoding  编码格式
	 * @param separator  分隔符
	 * @return  前五行列表
	 */
	public List<String[]> getpreviewtext(String path,String encoding,String separator){
		
		List<BufferedReader> listbr= sourcedata.getOpenSourceStreamfromdisk(path, Charset.forName(encoding));
		List<String[]> results=new ArrayList<String[]>();
		
		for(BufferedReader br:listbr) {
			
			try {
				String line=br.readLine();
				
				while(line!=null) {
					results.add(line.split(separator));
					
					if(results.size()==5)
						break;
					
					line=br.readLine();
				}
				
				if(results.size()==5)
					break;

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		for(BufferedReader br:listbr) {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return results;
	}

	/**从文本中插入节点  数据格式来源于importset
	 * @param importset
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws InterruptedException 
	 * @throws URISyntaxException 
	 */
	public void ImportNodefromtxt(ImportSet importset) throws IllegalStateException, IOException, InterruptedException, URISyntaxException {
		
		BatchNodeData n=new BatchNodeData();//定义导入结构体
		//定义结构体内部内容
		n.setExcludecolumn(importset.getExcludecolumn());
		n.setKeyindex(importset.getKeyindex());
		n.setNodeLabels(importset.getNodelabels());
		n.setstructure(importset.getStructure());
		
		if(!importset.getLoadtype()) {
			graphHub.getEmbeddeddriver().getGraphDatabaseService().shutdown();
		
		//定义文本导入格式
		batchfromstream.setCharset(Charset.forName(importset.getEncoding()));
		batchfromstream.SetInserter(Neo4jDatabasePath);
		batchfromstream.setSplit(importset.getSeparator());
		batchfromstream.setHeader(importset.getHeader());
		batchfromstream.setUpdatetype(importset.getUpdatetype());
		batchfromstream.setFilters(filterimport.explain(importset.getFilter()));
		//导入节点
		batchfromstream.UpdateDB(Neo4jDatabasePath, importset.getPath(), n, Datatype.Node);
		//建立索引
		if(importset.getIndex()!=null)
		for(Entry<String, String> entry:importset.getIndex().entrySet())
		batchfromstream.Createindex(entry.getKey(), entry.getValue());
		//建立约束
		if(importset.getConstraint()!=null)
		for(Entry<String, String> entry:importset.getConstraint().entrySet())
		batchfromstream.Createconstraint(entry.getKey(), entry.getValue());
		//关闭插入器
		batchfromstream.CloseInserter();
		
		graphHub.getEmbeddeddriver().configure();
		}else {
		//设置关键字列属性名称
		n.setKey(importset.getKey());
		//定义文本导入格式
		batchfromstreamcypher.setCharset(Charset.forName(importset.getEncoding()));
		batchfromstreamcypher.setSplit(importset.getSeparator());
		batchfromstreamcypher.setHeader(importset.getHeader());
		batchfromstreamcypher.setUpdatetype(importset.getUpdatetype());
		batchfromstreamcypher.setFilters(filterimport.explain(importset.getFilter()));
		
		//导入节点
		batchfromstreamcypher.UpdateNode(importset.getPath(), n);
		//建立索引
		if(importset.getIndex()!=null)
		for(Entry<String, String> entry:importset.getIndex().entrySet())
			batchfromstreamcypher.Createindex(entry.getKey(), entry.getValue());
		//建立约束
		if(importset.getConstraint()!=null)
		for(Entry<String, String> entry:importset.getConstraint().entrySet())
			batchfromstreamcypher.Createconstraint(entry.getKey(), entry.getValue());
		}
		
	}
	
	/**从文本中插入关系  数据格式来源于importset
	 * @param importset
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws URISyntaxException 
	 */
	public void ImportRelfromtxt(ImportSet importset) throws IllegalStateException, IOException, URISyntaxException {
		BatchRelationshipData r=new BatchRelationshipData();//定义导入结构体
		//定义结构体内部内容
		r.setExcludecolumn(importset.getExcludecolumn());
		r.setStartindex(importset.getStartindex());
		r.setEndindex(importset.getEndindex());
		r.setRelationshipTypes(importset.getRelationshiptypes());
		r.setstructure(importset.getStructure());
		
		if(!importset.getLoadtype()) {
			graphHub.getEmbeddeddriver().close();
	
		//定义文本导入格式
		batchfromstream.setCharset(Charset.forName(importset.getEncoding()));
		batchfromstream.SetInserter(Neo4jDatabasePath);
		batchfromstream.setSplit(importset.getSeparator());
		batchfromstream.setHeader(importset.getHeader());
		batchfromstream.setUpdatetype(importset.getUpdatetype());
		batchfromstream.setFilters(filterimport.explain(importset.getFilter()));
		//导入节点
		batchfromstream.UpdateDB(Neo4jDatabasePath, importset.getPath(), r, Datatype.Relationship);
		
//		//建立索引
//		for(Entry<String, String> entry:importset.getIndex().entrySet())
//		batchfromstream.Createindex(entry.getKey(), entry.getValue());
//		//建立约束
//		for(Entry<String, String> entry:importset.getConstraint().entrySet())
//		batchfromstream.Createconstraint(entry.getKey(), entry.getValue());
		//关闭插入器
		batchfromstream.CloseInserter();
		
		graphHub.getEmbeddeddriver().configure();}
		else {
			//定义开始结束节点的属性名称
			r.setStart(importset.getStart());
			r.setEnd(importset.getEnd());
			//定义文本导入格式
			batchfromstreamcypher.setCharset(Charset.forName(importset.getEncoding()));
			batchfromstreamcypher.setSplit(importset.getSeparator());
			batchfromstreamcypher.setHeader(importset.getHeader());
			batchfromstreamcypher.setUpdatetype(importset.getUpdatetype());
			batchfromstreamcypher.setFilters(filterimport.explain(importset.getFilter()));

			//导入节点
			batchfromstreamcypher.UpdateRel(importset.getPath(), r,importset.getStartlabels(),importset.getEndlabels());
			
//			//建立索引
//			for(Entry<String, String> entry:importset.getIndex().entrySet())
//				batchfromstreamcypher.Createindex(entry.getKey(), entry.getValue());
//			//建立约束
//			for(Entry<String, String> entry:importset.getConstraint().entrySet())
//				batchfromstreamcypher.Createconstraint(entry.getKey(), entry.getValue());
		}
	}
	

	
}
