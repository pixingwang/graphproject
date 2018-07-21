package graph.server.importdata;

import java.io.IOException;
import java.util.Date;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.web.multipart.MultipartFile;

public class DBfileService {
	
//	@Autowired
//	Session session;
	//数据库import文件夹目录
	String neo4jdbpath;
	//数据库import文件夹目录
	String username;	
	//数据库import文件夹目录
	String password;	
	
	
//	@Autowired
//	DBfileRepository dbfilerepository;

    //增加数据进入数据库
	public String UpdateDB(MultipartFile mutipartfile )throws IllegalStateException, IOException{
		
//		//缓存到当前工作目录下
//		String path=System.getProperty("user.dir")+"\\"+mutipartfile.getOriginalFilename();
//		File file=new File(path);
//		mutipartfile.transferTo(file);
//		
//		Csv2import csvimport=new Csv2import();
		long start=new Date().getTime();		
//		//读取csv文件并转换文件到目标数据库目录
//		csvimport.readCsv(path, neo4jdbpath);
		//建立索引
//		session.query("CREATE INDEX ON :P(code)", new HashedMap<>());
//		session.query("CREATE INDEX ON :C(code)", new HashedMap<>());
//		long mid=(new Date().getTime()-start)/1000;
//		System.out.println("插入用时  "+mid/3600+"时"+(mid/60)%60+"分"+mid%60+"秒");

//		dbfilerepository.CreateCindex();
//		dbfilerepository.CreatePindex();
		
		String cypher[]=cypher();
		System.out.println();
		//和数据库进行连接
		final Driver driver=GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic(/*username*/"neo4j", /*password*/"123456"));
		try(Session session=driver.session()){
			session.run("create index on :P(code)");
			session.run("create index on :C(code)");
			session.run("LOAD CSV WITH HEADERS FROM 'file:///target.csv' AS csvLine merge (p:P {code:csvLine.FROM_CUST_CODE}) set p.no=csvLine.FROM_CUST_NO set p.name=csvLine.FROM_CUST_NAME merge(q:P{code:csvLine.TO_CUST_CODE}) set q.no=csvLine.TO_CUST_NO set q.name=csvLine.TO_CUST_NAME with csvLine,p,q foreach(n in case when csvLine.REL_TYPE='DB'then [1] else [] end |create (p)-[r:DB{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q)) foreach(n in case when csvLine.REL_TYPE='JY'then [1] else [] end |create (p)-[r:JY{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q)) foreach(n in case when csvLine.REL_TYPE in ['FR','GD','CW','DS','JS','ZJL','KZRGL'] then [1] else [] end |create (p)-[r:KZ{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q)) foreach(n in case when csvLine.REL_TYPE in ['GL','QT']then [1] else [] end |create (p)-[r:QT{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q)) ");
			}
		long mid=(new Date().getTime()-start)/1000;
		System.out.println("插入用时  "+mid/3600+"时"+(mid/60)%60+"分"+mid%60+"秒");
		
		
		for(int i=0;i<4;i++){
		long a=new Date().getTime();
		try(Session session=driver.session()){
				session.run(cypher[i]);
				session.close();
				}
		long b=(new Date().getTime()-start)/1000;
		System.out.println("本次加载用时  "+mid/3600+"时"+(mid/60)%60+"分"+mid%60+"秒");
		}
		
		
//		//执行数据库读入操作
//		Transaction tx=session.beginTransaction();
//		try{
//			tx.run(cypher[0]);
//		}catch (Exception e) {
//			tx.rollback();
//		}
//		session.clear();

//		tx=session.beginTransaction();
//		try{
//			dbfilerepository.LoadC2P();
//		}catch (Exception e) {
//			tx.rollback();
//		}
//		tx.rollback();
//		session.clear();
//
//		tx=session.beginTransaction();
//		try{
//			dbfilerepository.LoadP2C();
//		}catch (Exception e) {
//			tx.rollback();
//		}
//		tx.rollback();
//		session.clear();
//		
//		tx=session.beginTransaction();
//		try{
//			dbfilerepository.LoadP2P();
//		}catch (Exception e) {
//			tx.rollback();
//		}
//		tx.rollback();
//		session.clear();

		long end=(new Date().getTime()-start)/1000;
		System.out.println("总共用时  "+end/3600+"时"+(end/60)%60+"分"+end%60+"秒");	
		
//		file.delete();
//		session.query("DROP INDEX ON :P(code)", null);
//		session.query("DROP INDEX ON :C(code)", null);
		return "数据库已更新";
	}
	
	
    public String[] cypher(){
    	String Cypher[]=new String[4];
    	String type[][]={{"P","C"},{"C","P"}};
    	int num=0;
    		for(int j=0;j<2;j++){
    			for(int k=0;k<2;k++){
    				Cypher[num++]="USING PERIODIC COMMIT 1000  LOAD CSV WITH HEADERS FROM 'file:///"+/*type[j][0].charAt(0)*/"C"+'2'+/*type[j][k].charAt(0)*/"P"+".csv' AS csvLine "
    		    	        +"merge (p:"+type[j][0]+" {code:csvLine.FROM_CUST_CODE}) "
    		    			+"on match set p.no=csvLine.FROM_CUST_NO,set p.name=csvLine.FROM_CUST_NAME "
    		    	        +"on create set p.no=csvLine.FROM_CUST_NO,set p.name=csvLine.FROM_CUST_NAME "
    		    	        +"merge(q:"+type[j][k]+"{code:csvLine.TO_CUST_CODE}) "
    		    			+"on match set q.no=csvLine.TO_CUST_NO,q.name=csvLine.TO_CUST_NAME "
    		    	        +"on create set q.no=csvLine.TO_CUST_NO,q.name=csvLine.TO_CUST_NAME "
//    		    	        +"with csvLine,p,q "
//    		    			+"foreach(n in case when csvLine.REL_TYPE='DB'then [1] else [] end |"
//    		    	        +"create (p)-[r:DB{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q)) "
//    		    			+"foreach(n in case when csvLine.REL_TYPE='JY'then [1] else [] end |"
//    		    	        +"create (p)-[r:JY{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q)) "
//    		    			+"foreach(n in case when csvLine.REL_TYPE in ['FR','GD','CW','DS','JS','ZJL','KZRGL'] then [1] else [] end |"
//    		    	        +"create (p)-[r:KZ{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q)) "
//    		    			+"foreach(n in case when csvLine.REL_TYPE in ['GL','QT']then [1] else [] end |"
//    		    	        +"create (p)-[r:QT{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q)) "
                            ;
    			}
    		}

    	return Cypher;
    }

    //减少数据进入数据库
	public String DeleteDB() {
		String cypher="MATCH (n) DETACH DELETE n";
//		session.query(cypher, new HashMap<>());
		return "已删除";
	}
	
}
