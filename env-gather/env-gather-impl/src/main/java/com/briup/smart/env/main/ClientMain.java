package com.briup.smart.env.main;

import com.briup.smart.env.ConfigurationImpl;
import com.briup.smart.env.client.ClientImpl;
import com.briup.smart.env.client.GatherImpl;

//客户端入口类
public class ClientMain {
	public static void main(String[] args) {
		ConfigurationImpl.getInstance();
		try {
			new ClientImpl().send(new GatherImpl().gather());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
