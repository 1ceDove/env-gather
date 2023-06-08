package com.briup.smart.env.client;

import java.util.Collection;

import org.junit.Test;

import com.briup.smart.env.entity.Environment;

public class GatherImplTest {

	@Test
	public void testGather() throws Exception {
		Collection<Environment> list = new GatherImpl().gather();
		list.forEach(i -> System.out.println(i));
		System.out.println(list.size());
	}
}
