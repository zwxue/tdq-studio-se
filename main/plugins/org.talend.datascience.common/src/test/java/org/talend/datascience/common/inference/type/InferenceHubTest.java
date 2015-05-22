package org.talend.datascience.common.inference.type;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;
import org.talend.datascience.common.inference.InferenceHub;
import org.talend.datascience.common.inference.semantic.SemanticInferExecutorTest;

import com.fasterxml.jackson.core.JsonParseException;

public class InferenceHubTest {
	InferenceHub inferHub = new InferenceHub();

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

	/**
	 * This test is ignored for the time being because the dictionary path and
	 * key word path is hard coded, they should be replaced later by elastic
	 * search server.
	 * 
	 * @throws JsonParseException
	 * @throws IOException
	 */
	@Ignore
	public void testInferSemanticTypesInputStream() throws JsonParseException,
			IOException {
		String schemaInJson = inferHub.inferSemanticTypes(getClass()
				.getResourceAsStream("data10Records.json"),
				SemanticInferExecutorTest.ddPath,
				SemanticInferExecutorTest.kwPath, Mode.LUCENE);
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

	/**
	 * This test is ignored for the time being because the dictionary path and
	 * key word path is hard coded, they should be replaced later by elastic
	 * search server.
	 * 
	 * @throws JsonParseException
	 * @throws IOException
	 */
	@Ignore
	public void testInferDataTypeAndSemantic() throws JsonParseException,
			IOException {
		String schemaInJson = inferHub.inferDataTypeAndSemantics(getClass()
				.getResourceAsStream("data10Records.json"),
				SemanticInferExecutorTest.ddPath,
				SemanticInferExecutorTest.kwPath, Mode.LUCENE);
		Assert.assertEquals(
				IOUtils.readLines(
						getClass().getResourceAsStream(
								"data10DataTypeAndSemanticAssert.json")).get(0),
				schemaInJson);
	}
}
