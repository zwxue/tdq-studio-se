package org.talend.datascience.common.inference.type;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class TypeInferHubTest {
	TypeInferHub inferHub = new TypeInferHub();

	@Test
	public void testInferTypesInputStream() {
		try {
			String schemaInJson = inferHub.inferTypes(getClass()
					.getResourceAsStream("alldata.json"));
			Assert.assertEquals(
					IOUtils.readLines(
							getClass()
									.getResourceAsStream("alldataAssert.json"))
							.get(0), schemaInJson);
			// Assert valid and invalid given null values over non-null values.
			schemaInJson = inferHub.inferTypes(getClass().getResourceAsStream(
					"nullOverNoneNull.json"));
			Assert.assertEquals(
					IOUtils.readLines(
							getClass().getResourceAsStream(
									"nullOverNonNullAssert.json")).get(0),
					schemaInJson);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
