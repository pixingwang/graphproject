package graph.server.service;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import graph.server.model.*;



@Mapper
public interface AdminService {
	@Select("SELECT * FROM `graphHub`.`user` where user_name = #{user_name} and password = #{password}")
    UserModel findByNameAndPassword(UserModel admin);

    @Delete("DELETE FROM `graphHub`.`graphub` WHERE graphub_id  = #{graphub_id}")
    int deleteById(int graphub_id);
    
    @Select("SELECT * FROM `graphHub`.`graphub` where user_Id = ${_parameter}")
    @Results({
    	//查询关联对象
    	@Result(property="neo4j",
    			column="neo4j_id",
    			one=@One(select="graph.server.service.AdminService.findNeo4jByNeo4j_id")),
    	
    	@Result(property="graphX",
    			column="graphX_id",
    			one=@One(select="graph.server.service.AdminService.findGraphXByGraphX_ID")),
    	@Result(property="neo4jLocal",
				column="embedded_neo4j_id",
				one=@One(select="graph.server.service.AdminService.findNeo4jLocalByEmbedded_neo4j_id"))
    })
    List<GraphubModel> findGraphubListByUserID(long user_Id);
    
    
    @Select("SELECT * FROM `graphHub`.`neo4j` where neo4j_id = ${_parameter}")
    Neo4jModel findNeo4jByNeo4j_id(long neo4j_id);
    @Select("SELECT * FROM `graphHub`.`graphx` where graphX_id = ${_parameter}")
    GraphXModel findGraphXByGraphX_ID(long graphX_id);
    @Select("SELECT * FROM `graphHub`.`neo4jlocal` where embedded_neo4j_id = ${_parameter}")
    Neo4jLocalModel findNeo4jLocalByEmbedded_neo4j_id(long embedded_neo4j_id);
    
    @Select("SELECT * FROM `graphHub`.`graphub` where graphub_id = #{_parameter}")
    @Results({
    	//查询关联对象
    	@Result(property="neo4j",
    			column="neo4j_id",
    			one=@One(select="graph.server.service.AdminService.findNeo4jByNeo4j_id")),
    	
    	@Result(property="graphX",
    			column="graphX_id",
    			one=@One(select="graph.server.service.AdminService.findGraphXByGraphX_ID")),
    	@Result(property="neo4jLocal",
				column="embedded_neo4j_id",
				one=@One(select="graph.server.service.AdminService.findNeo4jLocalByEmbedded_neo4j_id"))
    })
	GraphubModel findGraphubListByGraphubID(Integer graphub_id);
	
	@Select("SELECT * FROM `graphHub`.`graphub` where user_Id = #{_parameter} and state = 1")
    @Results({
    	//查询关联对象
    	@Result(property="neo4j",
    			column="neo4j_id",
    			one=@One(select="graph.server.service.AdminService.findNeo4jByNeo4j_id")),
    	
    	@Result(property="graphX",
    			column="graphX_id",
    			one=@One(select="graph.server.service.AdminService.findGraphXByGraphX_ID")),
    	@Result(property="neo4jLocal",
				column="embedded_neo4j_id",
				one=@One(select="graph.server.service.AdminService.findNeo4jLocalByEmbedded_neo4j_id"))
    })
	GraphubModel findGraphubActiveByUser_ID(long user_Id);

    @Update("UPDATE `graphHub`.`graphub` SET `graphub_name` = #{graphub_name}, `user_Id` = #{user_Id}, `neo4j_id` = #{neo4j.neo4j_id}, `graphX_id` = #{graphX.graphX_id}, `embedded_neo4j_id` = #{neo4jLocal.embedded_neo4j_id} WHERE `graphub_id` = #{graphub_id};")
    int updateGraphubById(GraphubModel graphub);
    
    @Insert("INSERT INTO `graphHub`.`graphub` (`graphub_id`, `graphub_name`, `user_Id`, `neo4j_id`, `graphX_id`,`embedded_neo4j_id`, `graphub_createtime`, `state`) VALUES (null, #{graphub_name}, #{user_Id}, #{neo4j.neo4j_id}, #{graphX.graphX_id}, #{neo4jLocal.embedded_neo4j_id}, #{graphub_createtime}, #{state});")
    int insertGraphub(GraphubModel graphub);
    
	@Select("SELECT * FROM  `graphHub`.`neo4j` WHERE (neo4j_name IN (SELECT  distinct neo4j_name FROM `graphHub`.`neo4j` WHERE `user_Id` = #{_parameter}))")
	List<Neo4jModel> findAllNeo4j(long user_Id);
	
	@Select("SELECT * FROM  `graphHub`.`graphx` WHERE (graphX_name IN (SELECT  distinct graphX_name FROM `graphHub`.`graphx` WHERE `user_Id` = #{_parameter}))")
	List<GraphXModel> findAllGraphX(long user_Id);
	
	@Select("SELECT * FROM  `graphHub`.`neo4jlocal` WHERE (embedded_neo4j_name IN (SELECT  distinct embedded_neo4j_name FROM `graphHub`.`neo4jlocal` WHERE `user_Id` = #{_parameter}))")
	List<Neo4jLocalModel> findAllNeo4jLocal(long user_Id);
	
	//状态化1
	@Update("UPDATE `graphHub`.`graphub` SET `state` = 1 WHERE graphub_id = #{graphub_id} AND user_Id = #{user_Id}")
	int GraphubStateChangeTo1(GraphubModel graphub);
	
	//状态归0
	@Update("UPDATE `graphHub`.`graphub` SET `state` = 0 WHERE user_Id = #{_parameter}")
	int GraphubStateChangeTo0(long user_Id);
	
	@Select("SELECT state FROM `graphHub`.`graphub` WHERE graphub_id = #{graphub_id} AND user_Id = #{user_Id}")
	int GraphubState1Or0(GraphubModel graphub);
	
	@Select("SELECT * FROM `graphHub`.`neo4jlocal` where embedded_neo4j_path = #{embedded_neo4j_path} and user_Id = #{user_Id}")
    Neo4jLocalModel findNeo4jLocalByEmbedded_neo4j_path(Neo4jLocalModel neo4jLocal);
	//注册
	@Insert("INSERT INTO `graphHub`.`user` (`user_id`,`password`, `user_name`, `email`, `role`) VALUES (null, #{password}, #{user_name}, #{email}, #{role});")
	int intsertUser(UserModel user); 
	
	@Select("SELECT * FROM `graphHub`.`graphub` where user_Id = #{user_Id} and embedded_neo4j_id=#{neo4jLocal.embedded_neo4j_id}")
    @Results({
    	//查询关联对象
    	@Result(property="neo4j",
    			column="neo4j_id",
    			one=@One(select="graph.server.service.AdminService.findNeo4jByNeo4j_id")),
    	
    	@Result(property="graphX",
    			column="graphX_id",
    			one=@One(select="graph.server.service.AdminService.findGraphXByGraphX_ID")),
    	@Result(property="neo4jLocal",
				column="embedded_neo4j_id",
				one=@One(select="graph.server.service.AdminService.findNeo4jLocalByEmbedded_neo4j_id"))
    })
	GraphubModel findGraphubConnectByPath(GraphubModel graphub);
	/**
	 * Neo4j平台的增删该查
	 */
	@Select("SELECT * FROM `graphHub`.`neo4j` where user_Id = ${_parameter}")
	List<Neo4jModel> findNeo4jListByUserID(long user_Id);
	
	//Neo4j本地路径查询
	@Select("SELECT * FROM `graphHub`.`neo4jlocal` where user_Id = ${_parameter}")
	List<Neo4jLocalModel> findNeo4jListLocalByUserID(long user_Id);
	
    @Delete("DELETE FROM `graphHub`.`neo4j` WHERE neo4j_id  = #{neo4j_id}")
    int deleteNeo4jById(int neo4j_id);
    
    //Neo4j本地删除
    @Delete("DELETE FROM `graphHub`.`neo4jlocal` WHERE embedded_neo4j_id  = #{embedded_neo4j_id}")
    int deleteNeo4jLocalById(int embedded_neo4j_id);
    
	@Select("SELECT * FROM `graphHub`.`neo4j` where neo4j_id = #{_parameter}")
	Neo4jModel findNeo4jListByNeo4jID(Integer neo4j_id);
	
	//Neo4j本地修改
	@Select("SELECT * FROM `graphHub`.`neo4jlocal` where embedded_neo4j_id = #{_parameter}")
	Neo4jLocalModel findNeo4jListLocalByNeo4jID(Integer embedded_neo4j_id);
	
    @Update("UPDATE `graphHub`.`neo4j` SET `neo4j_name` = #{neo4j_name}, `neo4j_ip` = #{neo4j_ip}, `neo4j_port` = #{neo4j_port}, `user_Id` = #{user_Id}, `neo4j_user` = #{neo4j_user}, `neo4j_password` = #{neo4j_password}, `neo4j_editon` = #{neo4j_editon} WHERE `neo4j_id` = #{neo4j_id};")
    int updateNeo4jById(Neo4jModel neo4j);
    
    //Neo4j本地修改更新
    @Update("UPDATE `graphHub`.`neo4jlocal` SET `embedded_neo4j_name` = #{embedded_neo4j_name}, `user_Id` = #{user_Id}, `embedded_neo4j_path` = #{embedded_neo4j_path} WHERE `embedded_neo4j_id` = #{embedded_neo4j_id};")
    int updateNeo4jLocalById(Neo4jLocalModel neo4jLocal);
    
    @Insert("INSERT INTO `graphHub`.`neo4j` (`neo4j_id`,`neo4j_name`, `neo4j_ip`, `neo4j_port`, `user_Id`, `neo4j_user`,`neo4j_password`,`neo4j_editon`,`neo4j_createtime`) VALUES (null, #{neo4j_name}, #{neo4j_ip}, #{neo4j_port}, #{user_Id}, #{neo4j_user},#{neo4j_password},#{neo4j_editon},#{neo4j_createtime});")
    int insertNeo4j(Neo4jModel neo4j);
    
    //Neo4j本地添加
    @Insert("INSERT INTO `graphHub`.`neo4jlocal` (`embedded_neo4j_id`,`embedded_neo4j_name`, `user_Id`,`embedded_neo4j_path`,`embedded_neo4j_createtime`) VALUES (null, #{embedded_neo4j_name}, #{user_Id}, #{embedded_neo4j_path},#{embedded_neo4j_createtime});")
    int insertNeo4jLocal(Neo4jLocalModel neo4jLocal);
    
	/**
	 * GraphX平台的增删该查
	 */
	@Select("SELECT * FROM `graphHub`.`graphx` where user_Id = ${_parameter}")
	List<GraphXModel> findGraphXListByUserID(long user_Id);
	
    @Delete("DELETE FROM `graphHub`.`graphx` WHERE graphX_id  = #{graphX_id}")
    int deleteGraphXById(int graphX_id);
    
	@Select("SELECT * FROM `graphHub`.`graphx` where graphX_id = #{_parameter}")
	GraphXModel findGraphXListBygraphXID(Integer graphX_id);
	
    @Update("UPDATE `graphHub`.`graphx` SET `graphX_name` = #{graphX_name}, `graphX_ip` = #{graphX_ip}, `graphX_port` = #{graphX_port}, `user_Id` = #{user_Id}, `graphX_version` = #{graphX_version}, `graphX_algorithm` = #{graphX_algorithm}, `graphX_operation` = #{graphX_operation} WHERE `graphX_id` = #{graphX_id};")
    int updateGraphXById(GraphXModel graphX);
    
    @Insert("INSERT INTO `graphHub`.`graphx` (`graphX_id`,`graphX_name`, `graphX_ip`, `graphX_port`, `user_Id`, `graphX_version`,`graphX_algorithm`,`graphX_operation`,`graphX_createtime`) VALUES (null, #{graphX_name}, #{graphX_ip}, #{graphX_port}, #{user_Id}, #{graphX_version},#{graphX_algorithm},#{graphX_operation},#{graphX_createtime});")
    int insertGraphX(GraphXModel graphX);
	
	

}
