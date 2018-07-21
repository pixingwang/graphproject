package graph.server.importdata;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Values;

public class Mythread extends Thread {

	Driver driver=null;
	String cypher=null;
	int no=-1;
	
	public Mythread(Driver driver,String cypher,int no){
		this.driver=driver;
		this.cypher=cypher;
		this.no=no;
	}
	
	public void run(){
		try(Session session=driver.session()){
			session.run(cypher,Values.parameters("no",no));
			}
	}
}
