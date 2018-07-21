package graph.server.importdata;

import java.util.Date;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

public class LoadcsvThread {

//	//数据库import文件夹目录
//	@Value("${Neo4jDBPath}")
//	String neo4jdbpath;
	//数据库import文件夹目录
	String username;	
	//数据库import文件夹目录
	String password;	
    	
    //增加数据进入数据库
	public boolean Load(String type,int sum ,String filename) throws InterruptedException {
		//读取相应文件类型的第几号文件
		int typeid=-1;
		boolean success=false;
		
		String cypher[]=cypher();
		//和数据库进行连接
		final Driver driver=GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic(/*username*/"neo4j", /*password*/"123456"));
		try(Session session=driver.session()){
			session.run("CREATE INDEX ON :P(code)");
			session.run("CREATE INDEX ON :C(code)");
//			session.run("CREATE INDEX ON :Ac(code)");
			session.run("CREATE CONSTRAINT ON (c:Ac) ASSERT c.code IS UNIQUE");
			}
		
		switch(type){
		case "P": typeid=0;break;
		case "C": typeid=1;break;
		case "JY": typeid=2;break;
		case "KZ": typeid=3;break;
		case "DB": typeid=4;break;
		case "QT": typeid=5;break;
		}
		System.out.println(typeid);
		for(int i=sum;i>=0;i--){
			long a=new Date().getTime();
		try(Session session=driver.session()){
			System.out.println(cypher[typeid].replaceFirst("filename", filename+Integer.toString(i)));
				session.run(cypher[typeid].replaceFirst("filename", filename+Integer.toString(i)));
				}
		long b=(new Date().getTime()-a)/1000;
		System.out.println("本次关系加载用时  "+b/3600+"时"+(b/60)%60+"分"+b%60+"秒");
		}
		success=true;
		driver.close();
		
		return success;
		 
		  
	}
	
	
    public String[] cypher(){
    	String Cypher[]=new String[6];
		Cypher[0]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"merge (p:Ac {code:csvLine.FROM_CUST_CODE}) "
    			+"on match set p.no=csvLine.FROM_CUST_NO,p.name=csvLine.FROM_CUST_NAME "
    	        +"on create set p.no=csvLine.FROM_CUST_NO,p.name=csvLine.FROM_CUST_NAME,p:P "
    	        +"merge (q:Ac {code:csvLine.TO_CUST_CODE}) "
    			+"on match set q.no=csvLine.TO_CUST_NO,q.name=csvLine.TO_CUST_NAME "
    	        +"on create set q.no=csvLine.TO_CUST_NO,q.name=csvLine.TO_CUST_NAME,q:C ";
		
		Cypher[1]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"merge (p:Ac {code:csvLine.FROM_CUST_CODE}) "
    			+"on match set p.no=csvLine.FROM_CUST_NO,p.name=csvLine.FROM_CUST_NAME "
    	        +"on create set p.no=csvLine.FROM_CUST_NO,p.name=csvLine.FROM_CUST_NAME,p:C ";
		
		Cypher[2]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"match (p:Ac {code:csvLine.FROM_CUST_CODE}) "
    			+"match (q:Ac {code:csvLine.TO_CUST_CODE}) "
    	        +"create (p)-[r:JY{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q) ";

		Cypher[3]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"match (p:Ac {code:csvLine.FROM_CUST_CODE}) "
    			+"match (q:Ac {code:csvLine.TO_CUST_CODE}) "
    	        +"create (p)-[r:KZ{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q) ";

		Cypher[4]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"match (p:Ac {code:csvLine.FROM_CUST_CODE}) "
    			+"match (q:Ac {code:csvLine.TO_CUST_CODE}) "
    	        +"create (p)-[r:DB{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q) ";

		Cypher[5]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"match (p:Ac {code:csvLine.FROM_CUST_CODE}) "
    			+"match (q:Ac {code:csvLine.TO_CUST_CODE}) "
    	        +"create (p)-[r:QT{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q) ";
    	return Cypher;
    }
}
