package org.talend.dataprofiler.ecos.service;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class EcosystemServiceTest {

	@Test
	public void testGetCategoryList() {
		List list;
		try {
			list = EcosystemService.getCategoryList();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Object object = (Object) iterator.next();
				System.out.println(object);
				
			}
			assertNotNull(list);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		
	}

}
