package com.briup.smart.env.util;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class JdbcUtils {
	
	private static DataSource dataSource;
	
	static {
		Properties properties = new Properties();
		
		InputStream inStream = JdbcUtils.class.getClassLoader().getResourceAsStream("druid.properties");
		
		try {
			properties.load(inStream);
			
			dataSource = DruidDataSourceFactory.createDataSource(properties);
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
	public static void close(ResultSet rs,Statement stmt,Connection conn) {
		if(rs!=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(stmt!=null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(conn!=null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(Statement stmt,Connection conn) {
		close(null, stmt, conn);
	}
	
	//DDL DML
	public static int executeUpdate(String sql) {
		
		int rows = 0;
		
		Connection conn = null;
		Statement stmt = null;
		try {
			
			conn = getConnection();
			
			stmt = conn.createStatement();
			
			rows = stmt.executeUpdate(sql);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		
		return rows;
		
	}
	
	
	public static <T> T queryForObject(String sql,Function<ResultSet,T> f) {
		
		
		T obj = null;
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			conn = getConnection();
			
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			//rs ---> obj
			obj = f.apply(rs);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs,stmt, conn);
		}
		
		return obj;
		
	}
	
	public static <T> T queryForOne(String sql,Class<T> clazz) {
		return queryForList(sql,clazz).get(0);
	}
	
	public static <T> List<T> queryForList(String sql,Class<T> clazz) {
		List<T> list = new ArrayList<>();
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			conn = getConnection();
			
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			List<TypeAndName> parseReslut = parse(clazz);
			T obj = null;
			while(rs.next()) {
				obj = clazz.newInstance();
				for(TypeAndName typeAndName:parseReslut) {
					
					String type = typeAndName.type;
					
					String name = typeAndName.name;
					
					if("Integer".equals(type)) {
						Integer value = rs.getInt(name);
						typeAndName.invokeSet(obj, Integer.class, value);
					}
					else if("String".equals(type)) {
						String value = rs.getString(name);
						typeAndName.invokeSet(obj, String.class, value);
					}
					else if("int".equals(type)) {
						int value = rs.getInt(name);
						typeAndName.invokeSet(obj, int.class, value);
					}
					//...
					
				}
				
				list.add(obj);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs,stmt, conn);
		}
		
		return list;
	}
	
	private static <T> List<TypeAndName> parse(Class<T> c){
		List<TypeAndName> list = new ArrayList<>();
		
		Field[] fields = c.getDeclaredFields();
		for(Field f:fields) {
			String type = f.getType().getSimpleName();
			String name = f.getName();
			list.add(new TypeAndName(type, name));
		}
		return list;
	}
	
	private static class TypeAndName{
		String type;
		String name;
		
		public TypeAndName(String type, String name) {
			this.type = type;
			this.name = name;
		}
		
		public void invokeSet(Object obj,Class<?> c,Object value) {
			try {
				Method m = obj.getClass().getMethod("set"+initCap(name), c);
				m.invoke(obj, value);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		public String initCap(String name) {
			return name.substring(0, 1).toUpperCase()+name.substring(1);
		}
		
	}
	
}
