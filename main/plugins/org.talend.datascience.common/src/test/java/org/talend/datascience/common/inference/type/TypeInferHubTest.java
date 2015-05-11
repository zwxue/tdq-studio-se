package org.talend.datascience.common.inference.type;

import java.io.IOException;

import org.junit.Test;

public class TypeInferHubTest {
	TypeInferHub inferHub = new TypeInferHub();

	@Test
	public void testInferTypesInputStream() {
		try {
			String schemaInJson = inferHub.inferTypes(getClass().getResourceAsStream("alldata.json"));
			System.out.println(schemaInJson);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
