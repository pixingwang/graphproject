package graph.server.relationDBsearch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import graph.server.service.RelationDatabaseinstanceService;


/**
 * 执行所有的数据库增删改查操作
 * @author stalley
 *
 */
@Service
public class SqlSearch {
	@Autowired
	RelationDatabaseinstanceService rdbinstance;
	
	
	/**
	 * 执行增删改的sql
	 * @return int 影响数据库的行数
	 */
	public int executeUpdate(String sql,Object...params){
		Connection conn = null;
		
		/* * 1：有效的防止sql注入，但不是绝对的安全，
		 * 2：提高数据库的综合效率 
		 *   可以将sql语句的结构保存到数据库缓存中，当再次执行该结构的sql语句就可以直接执行缓存中的sql
		 *   从而提高数据库的效率*/
		 
		PreparedStatement pstm = null;
		try {
			//调用DBHelper的方法链接数据
			conn = rdbinstance.getMysql().getConnection();
			//找个车，负责向数据发送SQL语句
			pstm = conn.prepareStatement(sql);
			
			//给sql语句中的占位符赋值
			for(int i = 0 ; i < params.length ; i++){
				pstm.setObject(i+1, params[i]);
			}
			//发送执行sql语句
			int i = pstm.executeUpdate(); //这里的sql就不能再试sql语句的结构体，必须具体到某一个sql语句
			//影响数据库的行数
			return i;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e);
		}
		finally{
			rdbinstance.closeMysql(null, pstm, conn);
		}
	}
	
/*public int count(String sql,Object...params){
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			//调用DBHelper的方法链接数据
			conn = DBHelper.getConnection();
			//找个车，负责向数据发送SQL语句
			pstm = conn.prepareStatement(sql);
			//给sql语句中的占位符赋值
			for(int i = 0 ; i < params.length ; i++){
				pstm.setObject(i+1, params[i]);
			}
			//发送执行sql语句
			rs = pstm.executeQuery(); //这里的sql就不能再试sql语句的结构体，必须具体到某一个sql语句
			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e);
		}
		finally{
			DBHelper.close(rs, pstm, null);
		}
		return 0;
	}*/
	
	
	/**
	 * 执行查询的sql语句
	 */
	public List<Map<String,Object>> executeQuery(String sql,Object...params){
		//集合保存查询结果
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		Connection conn = null;
		PreparedStatement pstm = null;
		//结果集，负责获取查询结果
		ResultSet rs = null;
		try {
			//使用DBHelper链接数据库
			conn =  rdbinstance.getMysql().getConnection();
			//实例化PreparedStatement对象
			pstm = conn.prepareStatement(sql);
			//设置参数
			for(int i = 0 ; i < params.length ; i++){
				pstm.setObject(i+1, params[i]);
			}
			//发送并执行sql语句
			rs = pstm.executeQuery();
			
			//获取结果集的元数据对象
			ResultSetMetaData rsmd = rs.getMetaData();

			//从元数据中获取结果集列的数量
			int count = rsmd.getColumnCount();
			
			//创建一个String类型泛型集合，用来保存结果集中的列名
			List<String> columns = new ArrayList<String>(count);
			//创建一个String类型集合，用来保存结果集中对应列的类型名
			List<String> columnsTypeName=new ArrayList<String>(count);
			
			for(int i = 0 ; i< count ;i++){
				columns.add(rsmd.getColumnLabel(i+1));
				columnsTypeName.add(rsmd.getColumnTypeName(i+1));
			}
			//System.out.println(columns+"--"+columnsTypeName);
			
			/*//从缓存中获取set 方法
			List<SetBean> setList = CacheBean.cache.get(clazz.getName());
			if(setList == null){
				setList = new ArrayList<SetBean>();
				Method [] ms = clazz.getMethods();
				//筛选所有的set方法
				for(Method m : ms){
					if(m.getName().startsWith("set")){
						SetBean s = new SetBean();
						s.setSetMethod(m);
						s.setParameterType(m.getParameterTypes()[0]);
						setList.add(s);
					}
				}
				CacheBean.cache.put(clazz.getName(), setList);
			}*/
			
			
			//从结果集中获取数据
			//判断有没有下一条数据
			while(rs.next()){
			
				//通过字节码创建对象
				Map<String,Object> t = new HashMap<>();
			
				for(int i = 0 ; i< count ;i++){
					
					String columnsName=columns.get(i);
					String type=columnsTypeName.get(i);
				
					if(type.equals("CHAR")||type.equals("VARCHAR")||type.equals("TEXT")) {
						t.put(columnsName, rs.getString(columnsName));
					}
					else if(type.equals("INTEGER")||type.equals("BIGINT")||type.equals("MEDUIMINT")||type.equals("SMALLINT")){
						t.put(columnsName, rs.getInt(columnsName));
					}
					else if(type.equals("FLOAT")) {
						t.put(columnsName,rs.getFloat(columnsName));
					}
					else if(type.equals("DOUBLE")) {
						t.put(columnsName,rs.getDouble(columnsName));
					}
					else if(type.equals("LONG")) {
						t.put(columnsName,rs.getLong(columnsName));
					}
					else if(type.equals("DATE")) {
						t.put(columnsName,rs.getDate(columnsName));
					}
					
				}
				
				data.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e);
		}
		finally{
			rdbinstance.closeMysql(rs, pstm, conn);
		}
		return data;
	} 
	public int insertReturnGenerateKey(String sql,Object...params){
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Connection conn=null;
		try {
			conn=rdbinstance.getMysql().getConnection();
			//找个车，负责向数据发送SQL语句
			pstm =  conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			
			//给sql语句中的占位符赋值
			for(int i = 0 ; i < params.length ; i++){
				pstm.setObject(i+1, params[i]);
			}
			//发送执行sql语句
			pstm.executeUpdate(); //这里的sql就不能再试sql语句的结构体，必须具体到某一个sql语句
			
			rs = pstm.getGeneratedKeys();
			if(rs.next()){
				return rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e);
		}
		finally{
			rdbinstance.closeMysql(rs, pstm, conn);
		}
		return 0;
	}
	
	
	
	public static void main(String[] args) {
		SqlSearch dao = new SqlSearch();
	
		
		/*List<Map<String,Object>> data=dao.executeQuery(sql1);
		System.out.println(data);
		System.out.println(data.get(0).get("records"));*/
		
		String sql = "select KIND,count(*) as count from pattern_graph GROUP BY KIND";
		List<Map<String,Object>> data=dao.executeQuery(sql);
		System.out.println(data);
		String sql1="select max(skip) as count from dkffzyg";
		List<Map<String,Object>> data1=dao.executeQuery(sql1);
		System.out.println(data1);
		
		/*int i = dao.executeUpdate("insert into userinfo (username,password) value(?,?)","karen","123456");
		int j = dao.executeUpdate("delete from userinfo where id = ? ",2);
		int k = dao.executeUpdate("update userinfo set password = ?","karen123");
		
		System.out.println(i);
		System.out.println(j);
		System.out.println(k);*/
		
//		List<Map<String,Object>> data = dao.executeQuery("select username as uname , password as pwd from userinfo");
//		
//		System.out.println(data);
//		String sql = "select * from userInfo";
		
//		List<User> userList = dao.executeQuery(sql, User.class);
//		
//		for(User u : userList){
//			System.out.println(u.getId() + "\t" + u.getUsername() + "\t" + u.getPassword()); 
//		}
//		
//		String sql = "select id,orderNo,cusName from orderInfo";
//		List<Order> orderList = dao.executeQuery(sql, Order.class);
//		for(Order o : orderList){
//			System.out.println(o.getId() + "\t" + o.getOrderNo() + "\t" + o.getCusName());
		}
		
	}

