package com.briup.smart.env;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;

public class ConfigurationImpl implements Configuration {
	private static final ConfigurationImpl CONFIG = new ConfigurationImpl();
	private static Map<String, Object> map = new HashMap<String, Object>();
	private static Properties properties = new Properties();

	static {
		try {
			handler(parseXml(new File("./src/main/resources/conf.xml")));
			initModule();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ConfigurationImpl() {
		// TODO Auto-generated constructor stub
	}

	public static Configuration getInstance() {
		return CONFIG;
	}

	public static void initModule() throws Exception {
		for (Object moduleObject : map.values()) {
			if (moduleObject instanceof PropertiesAware) {
				((PropertiesAware) moduleObject).init(properties);
			}
			if (moduleObject instanceof ConfigurationAware) {
				((ConfigurationAware) moduleObject).setConfiguration(CONFIG);
			}
		}
	}

	@Override
	public Log getLogger() throws Exception {
		// TODO Auto-generated method stub
		return (Log) map.get(ModuleName.LOGGER.name());
	}

	@Override
	public Server getServer() throws Exception {
		// TODO Auto-generated method stub
		return (Server) map.get(ModuleName.SERVER.name());
	}

	@Override
	public Client getClient() throws Exception {
		// TODO Auto-generated method stub
		return (Client) map.get(ModuleName.CLIENT.name());
	}

	@Override
	public DBStore getDbStore() throws Exception {
		// TODO Auto-generated method stub
		return (DBStore) map.get(ModuleName.DBSTORE.name());
	}

	@Override
	public Gather getGather() throws Exception {
		// TODO Auto-generated method stub
		return (Gather) map.get(ModuleName.GATHER.name());
	}

	@Override
	public Backup getBackup() throws Exception {
		// TODO Auto-generated method stub
		return (Backup) map.get(ModuleName.BACKUP.name());
	}

	private enum ModuleName {
		GATHER, CLIENT, SERVER, DBSTORE, BACKUP, LOGGER
	}

	public static Document parseXml(File file) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	public static void handler(Document document) throws Exception {
		Element root = document.getRootElement();
		for (Iterator<Element> iterator = root.elementIterator(); iterator.hasNext();) {
			Element element = iterator.next();
			String modulName = element.getName();
			String className = element.attribute("class").getValue();
			map.put(modulName, Class.forName(className).newInstance());
			for (Iterator<Element> iterator2 = element.elementIterator(); iterator2.hasNext();) {
				Element element2 = iterator2.next();
				String childName = element2.getName();
				String childText = element2.getText();
				properties.put(childName, childText);
			}
		}
	}
}
