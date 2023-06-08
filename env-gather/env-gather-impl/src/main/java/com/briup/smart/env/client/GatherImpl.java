package com.briup.smart.env.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import com.briup.smart.env.constans.EnvConstans;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.BackupImpl;

public class GatherImpl implements Gather, PropertiesAware, EnvConstans {
	private String filePath;

	@Override
	public Collection<Environment> gather() throws Exception {
//		read from data-file-simple by using BufferedReader
//		data example 100|101|2|16|1|3|5d606f7802|1|1516323596029
		FileInputStream fis = null;
		fis = new FileInputStream(filePath);
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(fis));
		String str = null;
		ArrayList<Environment> list = new ArrayList<Environment>();
		while ((str = br.readLine()) != null) {
			String[] split = str.split("\\|");
			if (split.length == 9) {
				Environment environment = new Environment();
				environment.setSrcId(split[0]);
				environment.setDesId(split[1]);
				environment.setDevId(split[2]);
				environment.setSersorAddress(split[3]);
				environment.setCount(Integer.parseInt(split[4]));
				environment.setCmd(split[5]);
				environment.setStatus(Integer.parseInt(split[7]));
				environment.setGather_date(new Timestamp(Long.parseLong(split[8])));
				switch (Integer.parseInt(split[3])) {
				case 16:
					environment.setName("温度");
					environment.setData((Integer.parseInt(split[6].substring(0, 4), 16) * (0.00268127F)) - 46.85F);
					list.add(environment);
					environment = copy(environment);
					environment.setName("湿度");
					environment.setData((Integer.parseInt(split[6].substring(4, 8), 16) * 0.00190735F) - 6);
					break;
				case 256:
					environment.setName("光照强度");
					environment.setData(Integer.parseInt(split[6].substring(0, 4), 16));
					break;
				case 1280:
					environment.setName("二氧化碳");
					environment.setData(Integer.parseInt(split[6].substring(0, 4), 16));
					break;
				}
				list.add(environment);
			}
		}
		if (br != null)
			br.close();
		if (fis != null)
			fis.close();
//		BackupImpl.load()
		BackupImpl backupImpl = new BackupImpl();
		@SuppressWarnings("unchecked")
		ArrayList<Environment> backup = (ArrayList<Environment>) backupImpl.load("data-file-backup",
				BackupImpl.LOAD_UNREMOVE);
		list = (ArrayList<Environment>) list.subList(backup.size(), list.size() + 1);
//		BackupImpl.store()
		backupImpl.appendList("data-file-backup", list);

		return list;
	}

	public <T> T copy(T t) throws Exception {
		@SuppressWarnings("unchecked")
		Class<T> c = (Class<T>) t.getClass();
		T copy = c.newInstance();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			if (!Modifier.toString(field.getModifiers()).contains("static")
					&& !Modifier.toString(field.getModifiers()).contains("final")) {
				field.setAccessible(true);
				field.set(copy, field.get(t));
			}
		}
		return copy;
	}

	@Override
	public void init(Properties properties) throws Exception {
		filePath = properties.getProperty(EnvConstans.MODULE_GATHER_FILE_PATH);
	}
}
