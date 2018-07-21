package graph.server.importdata;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

public class Loadcsv {

//	//数据库import文件夹目录
//	@Value("${Neo4jDBPath}")
//	String neo4jdbpath;
	//数据库import文件夹目录
	String username;	
	//数据库import文件夹目录
	String password;	

    //增加数据进入数据库
	public boolean Load(String type,int no,String filename ) {	
		int typeid=-1;
		boolean success=false;
		
		String cypher[]=cypher();
		System.out.println();
		//和数据库进行连接
		final Driver driver=GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic(username, password));
		try(Session session=driver.session()){
			session.run("CREATE INDEX ON :P(code,name)");
			session.run("CREATE INDEX ON :C(code,name)");
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
		
		for(int i=0;i<no;i++)
		try(Session session=driver.session()){
				session.run(cypher[typeid].replaceFirst("filename", filename+Integer.toString(i)));
				}
		success=true;
		driver.close();
		
		return success;
	}
	
	
    public String[] cypher(){
    	String Cypher[]=new String[6];
		Cypher[0]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"merge (p:Ac {code:csvLine.FROM_CUST_CODE}) "
    			+"on match set p.no=csvLine.FROM_CUST_NO,set p.name=csvLine.FROM_CUST_NAME "
    	        +"on create set p.no=csvLine.FROM_CUST_NO,set p.name=csvLine.FROM_CUST_NAME,p:P ";
		
		Cypher[1]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"merge (p:Ac {code:csvLine.FROM_CUST_CODE}) "
    			+"on match set p.no=csvLine.FROM_CUST_NO,set p.name=csvLine.FROM_CUST_NAME "
    	        +"on create set p.no=csvLine.FROM_CUST_NO,set p.name=csvLine.FROM_CUST_NAME,p:C ";
		
		Cypher[2]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"match (p:Ac {code:toint(csvLine.FROM_CUST_CODE)}) "
    			+"match (q:Ac {code:toint(csvLine.TO_CUST_CODE)}) "
    	        +"create (p)-[r:JY{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q) ";

		Cypher[3]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"match (p:Ac {code:toint(csvLine.FROM_CUST_CODE)}) "
    			+"match (q:Ac {code:toint(csvLine.TO_CUST_CODE)}) "
    	        +"create (p)-[r:JY{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q) ";

		Cypher[4]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"match (p:Ac {code:toint(csvLine.FROM_CUST_CODE)}) "
    			+"match (q:Ac {code:toint(csvLine.TO_CUST_CODE)}) "
    	        +"create (p)-[r:JY{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q) ";

		Cypher[5]="USING PERIODIC COMMIT 10000  LOAD CSV WITH HEADERS FROM 'file:///filename.csv' AS csvLine "
    	        +"match (p:Ac {code:toint(csvLine.FROM_CUST_CODE)}) "
    			+"match (q:Ac {code:toint(csvLine.TO_CUST_CODE)}) "
    	        +"create (p)-[r:JY{rela_from:csvLine.RELA_FROM,proportion:csvLine.PROPORTION,money:toFloat(csvLine.MONEY),data_date:csvLine.DATA_DATE,rel_type_cn:csvLine.REL_TYPE_CN}]->(q) ";
    	return Cypher;
    }

}
