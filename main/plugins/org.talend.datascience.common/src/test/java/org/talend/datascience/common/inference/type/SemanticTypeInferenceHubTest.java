package org.talend.datascience.common.inference.type;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.talend.datascience.common.inference.SemanticTypeInferenceHub;

import com.fasterxml.jackson.core.JsonParseException;

public class SemanticTypeInferenceHubTest {
	SemanticTypeInferenceHub inferHub = new SemanticTypeInferenceHub();

	@Test
	public void testInferTypesInputStream() {
		try {

			String schemaInJson = inferHub.inferDataTypes(getClass()
					.getResourceAsStream("data10RecordsNoType.json"));
			Assert.assertEquals(
					IOUtils.readLines(
							getClass().getResourceAsStream(
									"data10RecordsAssertNoType.json")).get(0),
					schemaInJson);

			// --- infer data types given type
			schemaInJson = inferHub.inferDataTypes(getClass()
					.getResourceAsStream("data10Records.json"));
			Assert.assertEquals(
					IOUtils.readLines(
							getClass().getResourceAsStream(
									"data10RecordsAssert.json")).get(0),
					schemaInJson);

			schemaInJson = inferHub.inferDataTypes(getClass()
					.getResourceAsStream("alldata.json"));
			Assert.assertEquals(
					IOUtils.readLines(
							getClass()
									.getResourceAsStream("alldataAssert.json"))
							.get(0), schemaInJson);
			// Assert valid and invalid given null values over non-null values.
			schemaInJson = inferHub.inferDataTypes(getClass()
					.getResourceAsStream("nullOverNoneNull.json"));
			Assert.assertEquals(
					IOUtils.readLines(
							getClass().getResourceAsStream(
									"nullOverNonNullAssert.json")).get(0),
					schemaInJson);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInferSemanticTypesInputStream() throws JsonParseException,
			IOException {
		String schemaInJson = inferHub.inferSemanticTypes(getClass()
				.getResourceAsStream("data10Records.json"));
		Assert.assertEquals(
				IOUtils.readLines(
						getClass().getResourceAsStream(
								"data10SemanticAssert.json")).get(0),
				schemaInJson);
	}

	@Test
	public void testComputeColumnQuality() throws JsonParseException,
			IOException {
		String schemaInJson = inferHub
				.computeColumnQuality(IOUtils
						.readLines(
								getClass().getResourceAsStream(
										"data10SchemaOnly.json")).get(0));
		Assert.assertEquals(
				IOUtils.readLines(
						getClass().getResourceAsStream(
								"data10ComputeQuality.json")).get(0),
				schemaInJson);
	}
}
