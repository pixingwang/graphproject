package graph.server.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import graph.server.relationDBsearch.SqlSearch;


/**
 * @author dx
 *执行SQL语句服务
 */
@Service
public class SqlSearchService {
	@Autowired
	SqlSearch sqlsearch;
	
	public SqlSearch getSqlsearch() {
		return sqlsearch;
	}

	public int Update(String sql,Object...params) {
		return sqlsearch.executeUpdate(sql, params);
	}

	public List<Map<String,Object>> Query(String sql,Object...params){
		return sqlsearch.executeQuery(sql, params);
	}
	
	public int insertReturnGenerateKey(String sql,Object...params){
		return sqlsearch.insertReturnGenerateKey(sql, params);
	}
}
