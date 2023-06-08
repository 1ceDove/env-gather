package com.briup.smart.env.client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.PropertiesAware;

public class ClientImpl implements Client, PropertiesAware {
	private static final String host = "127.0.0.1";
	private static final int port = 9999;

	@Override
	public void send(Collection<Environment> c) throws Exception {
		// host port
		// socket
		// 对象流 序列化
		if (c == null || c.size() < 0) {
			System.out.println("ERROR: Collection do not exist");
			return;
		}
		Socket socket = null;
		ObjectOutputStream oos = null;
		try {
			socket = new Socket(host, port);
			// 3.IO 对象流 序列化
			oos = new ObjectOutputStream(socket.getOutputStream());

			oos.writeObject(c);
			oos.flush();

		} finally {
			if (oos != null) {
				oos.close();
			}
			if (socket != null) {
				socket.close();
			}
		}
	}

	@Override
	public void init(Properties properties) throws Exception {

	}
}
