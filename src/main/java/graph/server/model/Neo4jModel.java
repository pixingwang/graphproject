package graph.server.model;



import java.util.Date;

public class Neo4jModel {

	private long neo4j_id; // neo4j的ID
	private String neo4j_name; // neo4j的名称
	private String neo4j_ip; // neo4j的IP
	private String neo4j_port; // neo4j的端口
	private long user_Id; // 平台登陆用户ID
	private String neo4j_user; // neo4j的登陆的用户名
	private String neo4j_password; // neo4j的登陆的密码
	private String neo4j_editon; // neo4j的版本
	private Date neo4j_createtime; //neo4j的创建时间
	
	public Date getNeo4j_createtime() {
		
		return neo4j_createtime;
	}

	public void setNeo4j_createtime(Date neo4j_createtime) {
		this.neo4j_createtime = neo4j_createtime;
	}

	public long getNeo4j_id() {
		return neo4j_id;
	}

	public void setNeo4j_id(long neo4j_id) {
		this.neo4j_id = neo4j_id;
	}

	public String getNeo4j_name() {
		return neo4j_name;
	}

	public void setNeo4j_name(String neo4j_name) {
		this.neo4j_name = neo4j_name;
	}

	public String getNeo4j_ip() {
		return neo4j_ip;
	}

	public void setNeo4j_ip(String neo4j_ip) {
		this.neo4j_ip = neo4j_ip;
	}

	public String getNeo4j_port() {
		return neo4j_port;
	}

	public void setNeo4j_port(String neo4j_port) {
		this.neo4j_port = neo4j_port;
	}

	public long getUser_Id() {
		return user_Id;
	}

	public void setUser_Id(long user_Id) {
		this.user_Id = user_Id;
	}

	public String getNeo4j_user() {
		return neo4j_user;
	}

	public void setNeo4j_user(String neo4j_user) {
		this.neo4j_user = neo4j_user;
	}

	public String getNeo4j_password() {
		return neo4j_password;
	}

	public void setNeo4j_password(String neo4j_password) {
		this.neo4j_password = neo4j_password;
	}

	public String getNeo4j_editon() {
		return neo4j_editon;
	}

	public void setNeo4j_editon(String neo4j_editon) {
		this.neo4j_editon = neo4j_editon;
	}

	@Override
	public String toString() {
		return "Neo4j [neo4j_id=" + neo4j_id + ", neo4j_name=" + neo4j_name + ", neo4j_ip=" + neo4j_ip + ", neo4j_port="
				+ neo4j_port + ", user_Id=" + user_Id + ", neo4j_user=" + neo4j_user + ", neo4j_password="
				+ neo4j_password + ", neo4j_editon=" + neo4j_editon + ", neo4j_createtime=" + neo4j_createtime + "]";
	}



}
