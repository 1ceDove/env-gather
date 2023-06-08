package com.briup.smart.env.server;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.briup.smart.env.entity.Environment;

public class ServerImpl implements Server {
	private static final int ServerSocketPort = 9999;
	private static final int ShutdownServerPort = 8888;
	private boolean flag = true;
	private ServerSocket server = null;
	private ServerSocket shutdownServer = null;
	private Socket socket = null;
	private Socket shutdownSocket = null;
	private ObjectInputStream in = null;

	@Override
	public void reciver() throws Exception {
		startReciverServer();
	}

	@Override
	public void shutdown() throws Exception {
		startShutdownServer();
	}

	private void startReciverServer() throws Exception {
		// 创建server socket
		// 与client 9999通信
		// IO 反序列化 ->list
		// DBstore 入库
		// 多线程
		server = new ServerSocket(ServerSocketPort);
		System.out.println("Port number: " + server.getLocalPort());
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
		while (flag) {
			socket = server.accept();
			System.out.println("Here comes client");
			fixedThreadPool.execute(() -> {
				try {
					in = new ObjectInputStream(socket.getInputStream());
					@SuppressWarnings("unchecked")
					List<Environment> list = (List<Environment>) in.readObject();
					new DBStoreImpl().saveDB(list);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (in != null)
							in.close();
						if (socket != null)
							socket.close();
						if (server != null)
							server.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		fixedThreadPool.shutdown();
	}

	private void startShutdownServer() throws Exception {
		new Thread(() -> {
			try {
				shutdownServer = new ServerSocket(ShutdownServerPort);
				shutdownSocket = shutdownServer.accept();
				this.flag = false;
				server.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (shutdownSocket != null)
						shutdownSocket.close();
					if (shutdownServer != null)
						shutdownServer.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
}
