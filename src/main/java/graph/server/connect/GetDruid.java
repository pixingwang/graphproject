package graph.server.connect;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSourceFactory;
/**
 * @author dx
 *获取关系数据库链接实体
 */
@Component
public class GetDruid {

	private static DataSource instance ;

	
	public  void InitDruid(){
		Properties properties = new Properties();
	    try {
	    	properties.load(new FileInputStream("src/main/resources/application_druid.properties"));
			instance = DruidDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  DataSource getInstance(){
		if(instance==null) {
			InitDruid();
		}
		return instance;
	}
	
	/**
	 * 释放资源
	 * @param rs
	 * @param stm
	 * @param conn
	 */
	public  void close(ResultSet rs,Statement stm,Connection conn){
		
		try {
			if(rs != null){
				rs.close();
				rs = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(stm != null && !stm.isClosed()){
				stm.close();
				stm = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if(conn != null && !conn.isClosed()){
				conn.close();
				conn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
