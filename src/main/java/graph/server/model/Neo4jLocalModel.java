package graph.server.model;

import java.util.Date;

public class Neo4jLocalModel {

	private long embedded_neo4j_id; // neo4j的ID
	private String embedded_neo4j_name; // neo4j的名称
	private long user_Id; // 平台登陆用户ID
	private String embedded_neo4j_path; // neo4j的登陆的用户名
	private Date embedded_neo4j_createtime; // neo4j的创建时间

	public long getEmbedded_neo4j_id() {
		return embedded_neo4j_id;
	}

	public void setEmbedded_neo4j_id(long embedded_neo4j_id) {
		this.embedded_neo4j_id = embedded_neo4j_id;
	}

	public String getEmbedded_neo4j_name() {
		return embedded_neo4j_name;
	}

	public void setEmbedded_neo4j_name(String embedded_neo4j_name) {
		this.embedded_neo4j_name = embedded_neo4j_name;
	}

	public long getUser_Id() {
		return user_Id;
	}

	public void setUser_Id(long user_Id) {
		this.user_Id = user_Id;
	}

	public String getEmbedded_neo4j_path() {
		return embedded_neo4j_path;
	}

	public void setEmbedded_neo4j_path(String embedded_neo4j_path) {
		this.embedded_neo4j_path = embedded_neo4j_path;
	}

	public Date getEmbedded_neo4j_createtime() {
		return embedded_neo4j_createtime;
	}

	public void setEmbedded_neo4j_createtime(Date embedded_neo4j_createtime) {
		this.embedded_neo4j_createtime = embedded_neo4j_createtime;
	}

	@Override
	public String toString() {
		return "Neo4jLocal [embedded_neo4j_id=" + embedded_neo4j_id + ", embedded_neo4j_name=" + embedded_neo4j_name
				+ ", user_Id=" + user_Id + ", embedded_neo4j_path=" + embedded_neo4j_path
				+ ", embedded_neo4j_createtime=" + embedded_neo4j_createtime + "]";
	}

}
