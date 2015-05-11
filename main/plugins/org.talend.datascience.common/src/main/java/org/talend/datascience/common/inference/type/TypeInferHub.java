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
package org.talend.datascience.common.inference.type;

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
 * example). this class hold one instance of {{@link TypeInferExecutor}
 * 
 * @author zhao
 *
 */
public class TypeInferHub {
	private TypeInferExecutor inferExectutor = new TypeInferExecutor();

	private String jsonRecordPath = "records";
	private String jsonColumnPath = "columns";
	private String id = "id";

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
	public String inferTypes(File jsonFile) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(jsonFile);
		String types = inferTypes(fileInputStream);
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
	public String inferTypes(String jsonString) throws IOException {
		ByteArrayInputStream jsonStream = new ByteArrayInputStream(
				jsonString.getBytes("UTF-8"));
		String types = inferTypes(jsonStream);
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
	public String inferTypes(InputStream jsonStream) throws JsonParseException,
			IOException {
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser jParser = jsonFactory.createParser(jsonStream);
		JsonToken current = jParser.nextToken();
		if (current != JsonToken.START_OBJECT) {
			return null;
		}

		// Column names (id from source json)
		List<String> columnNames = null;

		boolean isTypeInferExecutorInited = false;
		ObjectMapper mapper = new ObjectMapper();
		while (jParser.nextToken() != JsonToken.END_OBJECT) {
			String fieldname = jParser.getCurrentName();
			current = jParser.nextToken();
			if (jsonColumnPath.equals(fieldname) && columnNames == null) {
				// get column names from source json.
				if (current == JsonToken.START_ARRAY) {
					// For each of the records in the array
					while (jParser.nextToken() != JsonToken.END_ARRAY) {
						JsonNode node = mapper.readTree(jParser);
						if (columnNames == null) {
							columnNames = new ArrayList<String>();
						}
						columnNames.add(node.get(id).asText());
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
						if (!isTypeInferExecutorInited) {
							isTypeInferExecutorInited = true;
							inferExectutor.init(record);
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
		List<Map<String, Long>> results = inferExectutor.getResults();

		String jsonResult = getJsonResult(results, jsonFactory, columnNames);
		return jsonResult;
	}

	private String getJsonResult(List<Map<String, Long>> results,
			JsonFactory jsonFactory, List<String> columnNames) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		JsonGenerator jGenerator = jsonFactory.createGenerator(output,
				JsonEncoding.UTF8);

		jGenerator.writeStartObject();// root object start
		jGenerator.writeArrayFieldStart("column");// column array start
		int colIdx = 0;
		for (Map<String, Long> colMap : results) {
			jGenerator.writeStartObject();// Column start
			// Write fixed metadata
			jGenerator.writeStringField("id", columnNames.get(colIdx));
			colIdx++;
			jGenerator.writeStringField("name", "");
			// Write types
			ValueComparator bvc = new ValueComparator(colMap);
			Map<String, Long> sortMap = new TreeMap<String, Long>(bvc);
			sortMap.putAll(colMap);
			Iterator<String> typeKeyInterator = sortMap.keySet().iterator();
			String suggestType = null;
			jGenerator.writeArrayFieldStart("types"); // Type array start
			int typeCountsWithoutEmpty = 0;
			long validCount = 0;
			long emptyCount = 0;
			long totalCount = 0;
			while (typeKeyInterator.hasNext()) {
				String nextType = typeKeyInterator.next();
				Long count = sortMap.get(nextType);
				if (count == null || count == 0) {
					// Ignore empty count
					continue;
				}
				jGenerator.writeStartObject();// Type object start.
				if (!TypeInferenceUtils.TYPE_EMPTY.equals(nextType)) {
					if (suggestType == null) {
						suggestType = nextType;
						validCount = count;
					}
					typeCountsWithoutEmpty++;
				} else {
					emptyCount = count;
				}
				totalCount += count;
				jGenerator.writeNumberField(nextType, count);
				jGenerator.writeEndObject();// Type object end.
			}
			if (suggestType == null) {
				// If empty type only
				suggestType = TypeInferenceUtils.TYPE_STRING;
			}

			String type = TypeInferenceUtils.TYPE_STRING;
			if (typeCountsWithoutEmpty == 1) {
				// set type value as suggest type.
				type = suggestType;
			}
			jGenerator.writeEndArray();// Type array end
			jGenerator.writeStringField("type", type);
			jGenerator.writeStringField("suggested type", suggestType);
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

class ValueComparator implements Comparator<String> {

	Map<String, Long> base;

	public ValueComparator(Map<String, Long> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(String a, String b) {
		return base.get(b).compareTo(base.get(a));
	}
}