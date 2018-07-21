package graph.server.importdata;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.session.Session;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.csvreader.CsvReader;


@Service
public class DBfileServicebatch {
	
	@Autowired
	Session session;
//	@Autowired
//	DBfileRepository dbfilerepository;
	
	//数据库文件夹目录
	String neo4jdbpath;
	//neo4j目录
	String neo4jpath;
	
    //增加数据进入数据库
	public String UpdateDB(MultipartFile mutipartfile )throws IllegalStateException, IOException{
		
		
		//缓存到当前工作目录下
		String path=System.getProperty("user.dir")+"\\"+mutipartfile.getOriginalFilename();
		File file=new File(path);
		mutipartfile.transferTo(file);
		
		Process proc=Runtime.getRuntime().exec("cmd /c "+neo4jpath+"\\stop.bat",null,new File(neo4jpath));
		try {
			proc.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		proc.destroy();
		

		boolean index;
			
		GraphDatabaseService batchDb =new GraphDatabaseFactory().newEmbeddedDatabase(new File(neo4jdbpath).getAbsoluteFile());
		try(Transaction tx= batchDb.beginTx()){
		batchDb.schema().awaitIndexesOnline(10, TimeUnit.SECONDS);
		}
        

		try(Transaction tx=batchDb.beginTx()){
			index=batchDb.schema().getIndexes()!=null;
		}
		
        batchDb.shutdown();
        
		final Map<String, String> config=new HashMap<>();
		 config.put("cache_type", "none"); 
		 config.put("use_memory_mapped_buffers", "true");
		 config.put("dbms.memory.heap.max_size", "10g");
		BatchInserter inserter=BatchInserters.inserter(new File(neo4jdbpath).getAbsoluteFile(),config);
		
		Label person= Label.label("P");
		Label company=Label.label("C");
		Label account=Label.label("Ac");
		
		if(!index){
		inserter.createDeferredSchemaIndex(person).on("code").create();
		inserter.createDeferredSchemaIndex(company).on("code").create();
		inserter.createDeferredSchemaIndex(account).on("code").create();
		}
		
		Map<String,Object> properties1 =new HashMap<>();
		Map<String,Object> properties2 =new HashMap<>();
		Map<String,Object> properties3 =new HashMap<>();
		String R;
		
		long starttime=new Date().getTime();
		
		try{
//			ArrayList<String[]> csvList=new ArrayList<String[]>();

			CsvReader reader=new CsvReader(path, ',',Charset.forName("UTF-8"));
			//放弃第一行信息
			reader.readHeaders();
			
			int count=0;
			while(reader.readRecord()){
				String[] tmp=reader.getValues();
				Long start=Long.parseLong(tmp[3]);
				Long end=Long.parseLong(tmp[7]);
				count++;
				properties1.put("code", tmp[4]);
				properties1.put("name", tmp[5]);
				properties1.put("no",start.toString());
				
				properties2.put("code", tmp[8]);
				properties2.put("name", tmp[9]);
				properties2.put("no", end.toString());
				
				properties3.put("data_date",tmp[0]);
				properties3.put("rela_from", tmp[12]);
				properties3.put("money", Float.parseFloat(tmp[13]));
				properties3.put("proportion", tmp[11]);
				properties3.put("rel_type_cn", tmp[2]);
				
				
				if(inserter.nodeExists(start))
				inserter.setNodeProperties(start ,properties1);
				else
				inserter.createNode(start ,properties1, tmp[6].charAt(0)=='P'?person:company);
				
				
				if(inserter.nodeExists(end))
				inserter.setNodeProperties(end ,properties2);
				else
				inserter.createNode(end , properties2, tmp[10].charAt(0)=='P'?person:company);
				
				switch(tmp[1]){
					case "JY":
					case "DB":
						R=tmp[1];
						break;
					case "QT":
					case "GL":
						R="QT";
						break;
					default:
						R= "KZ";
					}
				RelationshipType rel=RelationshipType.withName(R);
				inserter.createRelationship(start, end, rel, properties3);
				
				if(count%1000000==0){
					System.out.println(count);
//					csvList.clear();
				}
				
				}
			

			//输出剩余信息
			System.out.println(count+" 正在结束插入");
			long mid=(new Date().getTime()-starttime)/1000;
			System.out.println("插入用时  "+mid/3600+"时"+(mid/60)%60+"分"+mid%60+"秒");
			//插入结束	
			inserter.shutdown();
			//输出总数
			System.out.println(count);		
			//读取结束
			reader.close();

		}catch(Exception err){
			System.out.println(err);
		}
		
		long end=(new Date().getTime()-starttime)/1000;
		System.out.println("总共用时  "+end/3600+"时"+(end/60)%60+"分"+end%60+"秒");
		
		//启动服务
		proc=Runtime.getRuntime().exec("cmd /c "+neo4jpath+"\\start.bat",null,new File(neo4jpath));
        try {
			proc.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        proc.destroy();
		file.delete();
		return "数据库已更新";		
	}
	
}
