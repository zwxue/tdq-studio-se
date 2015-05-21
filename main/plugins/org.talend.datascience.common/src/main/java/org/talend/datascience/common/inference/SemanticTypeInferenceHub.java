// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.datascience.common.inference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.talend.datascience.common.inference.semantic.SemanticInferExecutor;
import org.talend.datascience.common.inference.type.AbstractInferExecutor;
import org.talend.datascience.common.inference.type.ColumnTypeBean;
import org.talend.datascience.common.inference.type.DataTypeInferExecutor;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A interface processing input and output data with specific format (json for
 * example). this class hold one instance of {{@link DataTypeInferExecutor}
 * 
 * @author zhao
 *
 */
public class SemanticTypeInferenceHub {

	private String jsonRecordPath = "records";
	private String jsonColumnPath = "columns";
	private String id = "id";
	private String type = "type";
	private String typeOccurrences = "types";
	protected static final String TYPE_NAME = "name";
	protected static final String OCCUR = "occurrences";
	private static final String SEMANTICS = "semantics";
	public void setJsonRecordPath(String jsonRecordPath) {
		this.jsonRecordPath = jsonRecordPath;
	}

	/**
	 * Inferring types given a file.
	 * 
	 * @param jsonFile
	 *            json file.
	 * @return types guessed in json format, this method will return null if
	 *         exception occurs.
	 * @throws IOException
	 */
	public String inferDataTypes(File jsonFile) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(jsonFile);
		String types = inferDataTypes(fileInputStream);
		fileInputStream.close();
		return types;
	}

	/**
	 * Inferring types given a string content.
	 * 
	 * @param jsonString
	 *            content in json string.
	 * @return inferring result in json format.
	 * @throws IOException
	 */
	public String inferDataTypes(String jsonString) throws IOException {
		ByteArrayInputStream jsonStream = new ByteArrayInputStream(
				jsonString.getBytes("UTF-8"));
		String types = inferDataTypes(jsonStream);
		jsonStream.close();
		return types;
	}

	/**
	 * Inferring types given an json input stream
	 * 
	 * @param jsonStream
	 * @return types guessed in json format, the format is defined from
	 *         https://jira.talendforge.org/browse/TDQ-9914 . This method will
	 *         return null if exception occurs.
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public String inferDataTypes(InputStream jsonStream)
			throws JsonParseException, IOException {
		AbstractInferExecutor inferExectutor = new DataTypeInferExecutor();

		String jsonResult = infer(jsonStream, inferExectutor,
				InferTypeEnum.DataType);
		return jsonResult;
	}

	private String infer(InputStream jsonStream,
			AbstractInferExecutor inferExectutor, InferTypeEnum inferType)
			throws JsonParseException, IOException {
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser jParser = jsonFactory.createParser(jsonStream);
		JsonToken current = jParser.nextToken();
		if (current != JsonToken.START_OBJECT) {
			return null;
		}

		// Column names (id from source json)
		List<String> columnNames = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		inferExectutor.init();
		ObjectMapper mapper = new ObjectMapper();
		while (jParser.nextToken() != JsonToken.END_OBJECT) {
			String fieldname = jParser.getCurrentName();
			current = jParser.nextToken();
			if (jsonColumnPath.equals(fieldname) && columnNames.isEmpty()) {
				// get column names from source json.
				if (current == JsonToken.START_ARRAY) {
					// For each of the records in the array
					while (jParser.nextToken() != JsonToken.END_ARRAY) {
						JsonNode node = mapper.readTree(jParser);
						columnNames.add(node.get(id).asText());
						types.add(node.get(type).asText());
					}
				}
			} else if (jsonRecordPath.equals(fieldname)) {
				// For each of the records in the array
				// read the record into a tree model,
				// this moves the parsing position to the end of it
				if (current == JsonToken.START_ARRAY) {
					// For each of the records in the array
					while (jParser.nextToken() != JsonToken.END_ARRAY) {
						JsonNode node = mapper.readTree(jParser);
						String[] record = new String[node.size()];
						int fieldIdx = 0;
						Iterator<String> fieldIt = node.fieldNames();
						while (fieldIt.hasNext()) {
							String fieldName = fieldIt.next();
							JsonNode fieldNode = node.get(fieldName);
							String fieldValue = fieldNode != null ? fieldNode
									.asText() : "";
							record[fieldIdx] = fieldValue;
							fieldIdx++;
						}
						inferExectutor.handle(record);
					}
				}

				// And now we have random access to everything in the
				// object
			} else {
				jParser.skipChildren();

			}
		}

		jParser.close();

		// getJson string
		List<ColumnTypeBean> results = inferExectutor.getResults();

		String jsonResult = getJsonResult(results, jsonFactory, columnNames,
				types, inferType);
		return jsonResult;
	}

	/**
	 * Guess the semantic type given an json input stream
	 * 
	 * @param jsonStream
	 * @return semantic name guessed in json format, the format is defined from
	 *         https://jira.talendforge.org/browse/TDQ-10318 .
	 * @throws {@link IOException}
	 * @throws {@link JsonParseException}
	 */
	public String inferSemanticTypes(InputStream jsonStream)
			throws JsonParseException, IOException {
		AbstractInferExecutor inferExectutor = new SemanticInferExecutor();
		String jsonResult = infer(jsonStream, inferExectutor,
				InferTypeEnum.Semantic);
		return jsonResult;
	}

	/**
	 * Compute the column quality (count, valid , invalid and empty ) given the
	 * data type, use need to specify type , if no type specified the valid and
	 * invalid count will be computed according to suggested type.
	 * 
	 * @param dataTypesInJson
	 *            the data types come from the output of {
	 *            {@link #inferDataTypes(InputStream)}.
	 * @return column quality, the format is defined from
	 *         https://jira.talendforge.org/browse/TDQ-10318 . This method will
	 *         return null if exception occurs.
	 * @throws IOException
	 * @throws JsonParseException
	 */
	public String computeColumnQuality(String dataTypesInJson)
			throws JsonParseException, IOException {
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser jParser = jsonFactory.createParser(dataTypesInJson);
		JsonToken current = jParser.nextToken();
		if (current != JsonToken.START_OBJECT) {
			return null;
		}

		// Column names (id from source json)
		List<ColumnTypeBean> typeBeans = new ArrayList<ColumnTypeBean>();
		List<String> columnNames = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		ObjectMapper mapper = new ObjectMapper();
		TypeOccuConsumer typeOccurConsumer = new TypeOccuConsumer();
		while (jParser.nextToken() != JsonToken.END_OBJECT) {
			String fieldname = jParser.getCurrentName();
			current = jParser.nextToken();
			if (jsonColumnPath.equals(fieldname) && types.isEmpty()) {
				// get column names from source json.
				if (current == JsonToken.START_ARRAY) {
					// For each of the records in the array
					int colIdx = 0;
					while (jParser.nextToken() != JsonToken.END_ARRAY) {
						JsonNode node = mapper.readTree(jParser);
						columnNames.add(node.get(id).asText());
						types.add(node.get(type).asText());
						ColumnTypeBean typeBean = new ColumnTypeBean();
						typeBeans.add(typeBean);
						typeBean.setColumnIdx(colIdx++);
						typeBean.setType(node.get(type).asText());
						typeBean.setId(node.get(id).asText());
						typeOccurConsumer.setTypeBean(typeBean);
						node.get(typeOccurrences).forEach(typeOccurConsumer);
						;
					}
				}
			} else {
				jParser.skipChildren();
			}
		}
		jParser.close();
		String jsonResult = getJsonResult(typeBeans, jsonFactory, columnNames,
				types, InferTypeEnum.DataType);
		return jsonResult;
	}

	private String getJsonResult(List<ColumnTypeBean> results,
			JsonFactory jsonFactory, List<String> columnNames,
			List<String> types, InferTypeEnum inferType) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		JsonGenerator jGenerator = jsonFactory.createGenerator(output,
				JsonEncoding.UTF8);

		jGenerator.writeStartObject();// root object start
		jGenerator.writeArrayFieldStart(jsonColumnPath);// column array start
		int colIdx = 0;
		String typeValue = null;
		for (ColumnTypeBean colMap : results) {
			jGenerator.writeStartObject();// Column start
			// Write fixed metadata
			jGenerator.writeStringField(
					id,
					columnNames.isEmpty() ? StringUtils.EMPTY : columnNames
							.get(colIdx));
			typeValue = types.isEmpty() ? StringUtils.EMPTY : types.get(colIdx);
			jGenerator.writeStringField(this.type, typeValue);
			colIdx++;
			jGenerator.writeStringField("name", "");
			Map<String, Long> typeToCountMap = null;
			if (inferType == InferTypeEnum.DataType) {
				// Write types
				ValueComparator bvc = new ValueComparator(colMap);
				typeToCountMap = new TreeMap<String, Long>(bvc);
				typeToCountMap.putAll(colMap.getTypeToCountMap());
			} else {
				typeToCountMap = colMap.getSemanticNameToCountMap();
			}
			Iterator<String> typeKeyInterator = typeToCountMap.keySet()
					.iterator();
			String suggestType = null;
			if (inferType == InferTypeEnum.DataType) {
				jGenerator.writeArrayFieldStart(this.typeOccurrences); // Type array start
			} else {
				jGenerator.writeArrayFieldStart(SEMANTICS); // Type array
																// start
			}
			long validCount = 0;
			long emptyCount = 0;
			long totalCount = 0;
			while (typeKeyInterator.hasNext()) {
				String nextType = typeKeyInterator.next();
				Long count = typeToCountMap.get(nextType);
				if (count == null || count == 0) {
					// Ignore empty count
					continue;
				}
				jGenerator.writeStartObject();// Type object start.
				if (inferType == InferTypeEnum.DataType) {
					// Only when inferring data types, computing the suggested
					// type , valid and invalid count etc.
					if (!TypeInferenceUtils.TYPE_EMPTY.equals(nextType)) {
						// get suggested type
						if (suggestType == null) {
							suggestType = nextType;
							if (StringUtils.isEmpty(typeValue)) {
								// If the type is not given , valid count will
								// be
								// the count from suggested type.
								validCount = count;
							}
						}
						// get valid count
						if (!StringUtils.isEmpty(typeValue)
								&& StringUtils.equalsIgnoreCase(typeValue, nextType)) {
							validCount = count;
						}
					} else {
						emptyCount = count;
					}
					totalCount += count;
				}
				jGenerator.writeStringField(TYPE_NAME, nextType);
				jGenerator.writeNumberField(OCCUR, count);
				jGenerator.writeEndObject();// Type object end.
			}
			if (suggestType == null) {
				// If empty type only
				suggestType = TypeInferenceUtils.TYPE_STRING;
			}

			jGenerator.writeEndArray();// Type array end
			jGenerator.writeStringField("suggestedType", suggestType);
			// Stats.
			jGenerator.writeObjectFieldStart("statistics");// Statistics object
															// start
			jGenerator.writeNumberField("count", totalCount);
			jGenerator.writeNumberField("valid", validCount);
			jGenerator.writeNumberField("invalid", totalCount - validCount
					- emptyCount);
			jGenerator.writeNumberField("empty", emptyCount);
			jGenerator.writeEndObject();// Statistics object end

			jGenerator.writeEndObject();// Column end
		}
		jGenerator.writeEndArray();// column array end
		jGenerator.writeEndObject();// root object end
		jGenerator.close();
		String jsonResult = output.toString();
		output.close();
		return jsonResult;
	}

}

class TypeOccuConsumer implements Consumer<JsonNode> {

	private ColumnTypeBean typeBean = null;

	public void setTypeBean(ColumnTypeBean typeBean) {
		this.typeBean = typeBean;
	}

	@Override
	public void accept(JsonNode node) {
		String typeName = node.get(SemanticTypeInferenceHub.TYPE_NAME).asText();
		Long occurs = node.get(SemanticTypeInferenceHub.OCCUR).asLong();
		typeBean.putTypeToCount(typeName, occurs);
	}

}

enum InferTypeEnum {
	DataType, Semantic
}

class ValueComparator implements Comparator<String> {

	ColumnTypeBean base;

	public ValueComparator(ColumnTypeBean base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(String a, String b) {
		return base.getDataTypeCount(b).compareTo(base.getDataTypeCount(a));
	}
}