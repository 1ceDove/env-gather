package com.briup.smart.env.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.JdbcUtils;

/**
 * 
 * @author 刘斌
 *
 */
public class DBStoreImpl implements DBStore {
	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	int s = 0;

	@Override
	public void saveDB(Collection<Environment> c) throws Exception {
		connection = JdbcUtils.getConnection();
		connection.setAutoCommit(false);
		c.forEach(i -> {
			try {
				String sql = "insert into env_detail__" + getDate(i.getGather_date())
						+ "(name,src_id,des_id,dev_id,sensor_address,count,cmd,data,status,gather_date) values(?,?,?,?,?,?,?,?,?,?)";
				preparedStatement = connection.prepareStatement(sql);

				preparedStatement.setString(1, i.getName());
				preparedStatement.setString(2, i.getSrcId());
				preparedStatement.setString(3, i.getDesId());
				preparedStatement.setString(4, i.getDevId());
				preparedStatement.setString(5, i.getSersorAddress());
				preparedStatement.setInt(6, i.getCount());
				preparedStatement.setString(7, i.getCmd());
				preparedStatement.setFloat(8, i.getData());
				preparedStatement.setInt(9, i.getStatus());
				preparedStatement.setTimestamp(10, i.getGather_date());

				preparedStatement.addBatch();
				s++;
				if (s == 50) {
					preparedStatement.executeBatch();
					s = 0;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		preparedStatement.executeBatch();
		connection.commit();
		close();
	}

	public int getDate(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public void close() throws Exception {
		if (preparedStatement != null)
			preparedStatement.close();
		if (connection != null)
			connection.close();
	}
}
