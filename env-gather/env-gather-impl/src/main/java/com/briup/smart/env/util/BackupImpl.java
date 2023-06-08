package com.briup.smart.env.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.briup.smart.env.entity.Environment;

public class BackupImpl implements Backup {
	private String filePath = "./src/main/resources/";

	public BackupImpl() {
	}

	public BackupImpl(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public Object load(String fileName, boolean del) throws Exception {
		FileInputStream fis = null;
		fis = new FileInputStream(filePath + fileName);
		ObjectInputStream ois = null;
		ois = new ObjectInputStream(fis);
		Object obj = ois.readObject();
		close(ois);
		close(fis);
		if (del)
			new File(filePath + fileName).delete();
		return obj;
	}

	@Override
	public void store(String fileName, Object obj, boolean append) throws Exception {
		FileOutputStream fos = null;
		fos = new FileOutputStream(filePath + fileName, append);
		ObjectOutputStream oos = null;
		oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		oos.flush();
		close(oos);
		close(fos);
	}

	@SuppressWarnings("unchecked")
	public void appendList(String fileName, ArrayList<Environment> list) throws Exception {
		store(fileName, ((ArrayList<Environment>) load(fileName, BackupImpl.LOAD_UNREMOVE)).addAll(list),
				BackupImpl.STORE_OVERRIDE);
	}

	private void close(OutputStream os) throws Exception {
		if (os != null)
			os.close();
	}

	private void close(InputStream is) throws Exception {
		if (is != null)
			is.close();
	}
}
