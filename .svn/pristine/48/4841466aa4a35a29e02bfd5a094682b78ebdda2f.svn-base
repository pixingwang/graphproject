package graph.server.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import graph.server.connect.GetDruid;

/**
 * @author dx
 *获取关系数据库对象实体
 */
@Service
public class RelationDatabaseinstanceService {
	@Autowired
	GetDruid mysql;
	

	public DataSource getMysql() {
		
		return mysql.getInstance();
	}
	
	public void closeMysql(ResultSet rs,Statement stm,Connection conn){
		mysql.close(rs, stm, conn);
	}
}
