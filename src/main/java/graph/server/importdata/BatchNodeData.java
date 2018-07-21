package graph.server.importdata;

import org.neo4j.graphdb.Label;

/**
 * 定义节点一类节点数据结构,规定节点id的属性为“id”,类型为Long  支持基本类型
 * @author dx
 *
 */
public class BatchNodeData extends BatchDatastructure{

//	节点标签列
	private Label[] Nodelabels;
//	id字段
	private String key;
//关键字或者id位置	
	private int keyindex;

	/**
	 * 获取id字段
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 设置id字段
	 * @param id
	 */
	public void setKey(String id) {
		this.key = id;
	}

	public Label[] getNodeLabels() {
		return Nodelabels;
	}

	/**
	 * 设计节点的标签类型可以有多个
	 * @param NodelabelStrings
	 */
	public void setNodeLabels(String ... NodelabelStrings) {
		
		Nodelabels=new Label[NodelabelStrings.length];
		
		for(int i=0;i<NodelabelStrings.length;i++)
			Nodelabels[i]=Label.label(NodelabelStrings[i]);
		
	}

	public int getKeyindex() {
		return keyindex;
	}

	public void setKeyindex(int keyindex) {
		this.keyindex = keyindex;
	}
	
}
