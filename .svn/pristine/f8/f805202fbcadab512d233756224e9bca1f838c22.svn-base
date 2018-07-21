package graph.server.importdata;

import org.neo4j.graphdb.RelationshipType;

/**
 * 定义图数据库一类关系数据结构，规定起始节点的序号属性为“start”，末端节点序号标签为“end”，均为Long 支持基本类型
 * @author dx
 *
 */
public class BatchRelationshipData extends BatchDatastructure{
	
//	关系类型
	private RelationshipType RelationshipTypes;
//	起始节点和结束节点字段
	private String start,end;
//  设置开始数据位置或者结束点数据位置
	private int startindex,endindex;

	/**
	 * 获取起始节点id字段
	 * @return
	 */
	public String getStart() {
		return start;
	}


	/**
	 * 获取结束节点id字段
	 * @return
	 */
	public String getEnd() {
		return end;
	}


	/**
	 * 设置起始起点id字段
	 * @param start
	 */
	public void setStart(String start) {
		this.start = start;
	}


	/**
	 * 设置结束节点id字段
	 * @param end
	 */
	public void setEnd(String end) {
		this.end = end;
	}


	public RelationshipType getRelationshipTypes() {
		return RelationshipTypes;
	}


	/**
	 * 设置关系的类型
	 * @param RelationshiptypeString
	 */
	public void setRelationshipTypes(String  RelationshiptypeString) {
		RelationshipTypes=RelationshipType.withName(RelationshiptypeString);

	}


	public int getStartindex() {
		return startindex;
	}


	public int getEndindex() {
		return endindex;
	}


	public void setStartindex(int startindex) {
		this.startindex = startindex;
	}


	public void setEndindex(int endindex) {
		this.endindex = endindex;
	}

}
