package com.briup.smart.env.shutdown;

import java.net.Socket;

public class ShutdownClient {
	private static final String host = "127.0.0.1";
	private static final int port = 8888;

	public static void main(String[] args) {
		try {
			new ShutdownClient().shutdown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void shutdown() throws Exception {
		Socket socket = null;
		socket = new Socket(host, port);
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
