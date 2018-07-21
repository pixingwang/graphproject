package graph.server.importdata;

import java.util.LinkedHashMap;
import java.util.List;

import graph.server.Enum.Datapropertytype;

/**
 * 每个结构化数据中的属性定义
 * @author dx
 *
 */
public class BatchDatastructure {
// 结构链表
public LinkedHashMap<String, Datapropertytype> structure ; 
//不需要的列号
public List<Integer> excludecolumn ;

/**
 * 获取不需要的列号
 * @return
 */
public List<Integer> getExcludecolumn() {
	return excludecolumn;
}

/**设置不需要的列号
 * @param excludecolumn
 */
public void setExcludecolumn(List<Integer> excludecolumn) {
	this.excludecolumn = excludecolumn;
}

/**
 * 设置节点的属性类型，设置属性---类型的键值对反映节点的属性结构类型，id默认为节点的编号  或者
 * 设置关系的属性类型，设置属性---类型的键值对反映节点的属性结构类型，source，target默认为关系的起始节点编号
 * @param structure
 */
public void setstructure(LinkedHashMap<String, Datapropertytype> structure) {
	this.structure = structure;
}

/**
 * 获取结构映射
 * @return
 */
public LinkedHashMap<String, Datapropertytype> getstructure() {
	return structure;
}

}
