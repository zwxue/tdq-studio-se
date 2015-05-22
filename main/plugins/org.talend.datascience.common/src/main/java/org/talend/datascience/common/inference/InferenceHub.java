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

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;
import org.talend.datascience.common.inference.semantic.SemanticInferExecutor;
import org.talend.datascience.common.inference.type.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

enum InferTypeEnum {
    DataType,
    Semantic,
    All
}

/**
 * A interface processing input and output data with specific format (json for example).
 * 
 * @author zhao
 *
 */
public class InferenceHub {

    protected static final String TYPE_NAME = "name";

    protected static final String OCCUR = "occurrences";

    private static final String SEMANTICS = "semantics";

    private static final String SUGGEST_TYPE = "suggestedType";

    private static final String STATS = "statistics";

    private static final String COUNT = "count";

    private static final String VALIDE = "valid";

    private static final String INVALIDE = "invalid";

    private static final String EMPTY = "empty";

    private String jsonRecordPath = "records";

    private String jsonColumnPath = "columns";

    private String id = "id";

    private String type = "type";

    private String typeOccurrences = "types";

    private String fieldName = "name";

    public void setJsonRecordPath(String jsonRecordPath) {
        this.jsonRecordPath = jsonRecordPath;
    }

    /**
     * Inferring types given a file.
     * 
     * @param jsonFile json file.
     * @return types guessed in json format, this method will return null if exception occurs.
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
     * @param jsonString content in json string.
     * @return inferring result in json format.
     * @throws IOException
     */
    public String inferDataTypes(String jsonString) throws IOException {
        ByteArrayInputStream jsonStream = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));
        String types = inferDataTypes(jsonStream);
        jsonStream.close();
        return types;
    }

    /**
     * Inferring types given an json input stream
     * 
     * @param jsonStream
     * @return types guessed in json format, the format is defined from https://jira.talendforge.org/browse/TDQ-9914 .
     * This method will return null if exception occurs.
     * @throws JsonParseException
     * @throws IOException
     */
    public String inferDataTypes(InputStream jsonStream) throws JsonParseException, IOException {
        AbstractInferExecutor inferExectutor = new DataTypeInferExecutor();

        String jsonResult = infer(jsonStream, inferExectutor, InferTypeEnum.DataType);
        return jsonResult;
    }

    private String infer(InputStream jsonStream, AbstractInferExecutor inferExectutor, InferTypeEnum inferType)
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
                            String fieldValue = fieldNode != null ? fieldNode.asText() : "";
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

        String jsonResult = getJsonResult(results, jsonFactory, columnNames, types, inferType);
        return jsonResult;
    }

    /**
     * Guess the semantic type given an json input stream
     * 
     * @param jsonStream
     * @return semantic name guessed in json format, the format is defined from
     * https://jira.talendforge.org/browse/TDQ-10318 .
     * @throws {@link IOException}
     * @throws {@link JsonParseException}
     */
    public String inferSemanticTypes(InputStream jsonStream, String ddPath, String kwPath, Mode searchMode)
            throws JsonParseException, IOException {
        SemanticInferExecutor inferExectutor = new SemanticInferExecutor();
        inferExectutor.setDdPath(ddPath);
        inferExectutor.setKwPath(kwPath);
        inferExectutor.setSemanticRecognizerMode(searchMode);
        String jsonResult = infer(jsonStream, inferExectutor, InferTypeEnum.Semantic);
        return jsonResult;
    }

    /**
     * Get the data type and semantics together
     * 
     * @param jsonStream input to be inferred from.
     * @param ddPath semantic repository dictionary path
     * @param kwPath semantic repository key word path
     * @param searchMode search mode , either {@link Mode#ELASTIC_SEARCH} or {@link Mode#LUCENE}.
     * @return schema together with data type and semantic.
     * @throws IOException
     * @throws JsonParseException
     */
    public String inferDataTypeAndSemantics(InputStream jsonStream, String ddPath, String kwPath, Mode searchMode)
            throws JsonParseException, IOException {
        SemanticAndDataTypeInferExecutor inferExectutor = new SemanticAndDataTypeInferExecutor();
        inferExectutor.setSemanticProperties(ddPath, kwPath, searchMode);
        String jsonResult = infer(jsonStream, inferExectutor, InferTypeEnum.All);
        return jsonResult;
    }

    /**
     * Compute the column quality (count, valid , invalid and empty ) given the data type, use need to specify type , if
     * no type specified the valid and invalid count will be computed according to suggested type.
     * 
     * @param dataTypesInJson the data types come from the output of { {@link #inferDataTypes(InputStream)}.
     * @return column quality, the format is defined from https://jira.talendforge.org/browse/TDQ-10318.
     * @throws IOException
     * @throws JsonParseException
     */
    public String computeColumnQuality(String dataTypesInJson) throws JsonParseException, IOException {
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
                    }
                }
            } else {
                jParser.skipChildren();
            }
        }
        jParser.close();
        String jsonResult = getJsonResult(typeBeans, jsonFactory, columnNames, types, InferTypeEnum.DataType);
        return jsonResult;
    }

    private String getJsonResult(List<ColumnTypeBean> results, JsonFactory jsonFactory, List<String> columnNames,
            List<String> types, InferTypeEnum inferType) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator jGenerator = jsonFactory.createGenerator(output, JsonEncoding.UTF8);

        jGenerator.writeStartObject();// root object start
        jGenerator.writeArrayFieldStart(jsonColumnPath);// column array start
        int colIdx = 0;
        String typeValue = null;
        for (ColumnTypeBean columnTypeBean : results) {
            jGenerator.writeStartObject();// Column start
            // Write fixed metadata
            jGenerator.writeStringField(id, columnNames.isEmpty() ? StringUtils.EMPTY : columnNames.get(colIdx));
            typeValue = types.isEmpty() ? StringUtils.EMPTY : types.get(colIdx);
            jGenerator.writeStringField(this.type, typeValue);
            colIdx++;
            jGenerator.writeStringField(fieldName, "");
            if (inferType == InferTypeEnum.DataType) {
                writeTypesAndOccurrences(jGenerator, typeValue, columnTypeBean);
            } else if (inferType == InferTypeEnum.Semantic) {
                writeSemanticOccurrences(jGenerator, columnTypeBean);
            } else {
                // Infer both data type and semantic type.
                writeTypesAndOccurrences(jGenerator, typeValue, columnTypeBean);
                writeSemanticOccurrences(jGenerator, columnTypeBean);
            }
            jGenerator.writeEndObject();// Column end
        }
        jGenerator.writeEndArray();// column array end
        jGenerator.writeEndObject();// root object end
        jGenerator.close();
        String jsonResult = output.toString();
        output.close();
        return jsonResult;
    }

    private void writeSemanticOccurrences(JsonGenerator jGenerator, ColumnTypeBean columnTypeBean) throws IOException {
        Map<String, Long> typeToCountMap = columnTypeBean.getSemanticNameToCountMap();
        Iterator<String> typeKeyInterator = typeToCountMap.keySet().iterator();

        jGenerator.writeArrayFieldStart(SEMANTICS); // Type array
        while (typeKeyInterator.hasNext()) {
            String nextType = typeKeyInterator.next();
            Long count = typeToCountMap.get(nextType);
            if (count == null || count == 0) {
                // Ignore empty count
                continue;
            }
            jGenerator.writeStartObject();// Type object start.
            jGenerator.writeStringField(TYPE_NAME, nextType);
            jGenerator.writeNumberField(OCCUR, count);
            jGenerator.writeEndObject();// Type object end.
        } // start
        jGenerator.writeEndArray();// Type array end
    }

    private void writeTypesAndOccurrences(JsonGenerator jGenerator, String typeValue, ColumnTypeBean columnTypeBean)
            throws IOException {
        // Write types
        ValueComparator bvc = new ValueComparator(columnTypeBean);
        Map<String, Long> typeToCountMap = new TreeMap<String, Long>(bvc);
        typeToCountMap.putAll(columnTypeBean.getTypeToCountMap());
        Iterator<String> typeKeyInterator = typeToCountMap.keySet().iterator();

        // Type array start
        jGenerator.writeArrayFieldStart(this.typeOccurrences);
        while (typeKeyInterator.hasNext()) {
            String nextType = typeKeyInterator.next();
            Long count = typeToCountMap.get(nextType);
            if (count == null || count == 0) {
                // Ignore empty count
                continue;
            }
            // Only when inferring data types, computing the suggested
            // type , valid and invalid count etc.
            writeDataTypeOccurrences(jGenerator, columnTypeBean, nextType, count, typeValue);
        }
        jGenerator.writeEndArray();// Type array end
        if (columnTypeBean.getSuggestedType() == null) {
            // If empty type only
            columnTypeBean.setSuggestedType(TypeInferenceUtils.TYPE_STRING);
        }

        jGenerator.writeStringField(SUGGEST_TYPE, columnTypeBean.getSuggestedType());
        // Stats.
        jGenerator.writeObjectFieldStart(STATS);// Statistics object
                                                // start
        jGenerator.writeNumberField(COUNT, columnTypeBean.getCount());
        jGenerator.writeNumberField(VALIDE, columnTypeBean.getValidCount());
        jGenerator.writeNumberField(INVALIDE,
                columnTypeBean.getCount() - columnTypeBean.getValidCount() - columnTypeBean.getEmpties());
        jGenerator.writeNumberField(EMPTY, columnTypeBean.getEmpties());
        jGenerator.writeEndObject();// Statistics object end
    }

    private void writeDataTypeOccurrences(JsonGenerator jGenerator, ColumnTypeBean columnTypeBean, String nextType, long count,
            String typeValue) throws IOException {
        jGenerator.writeStartObject();// Type object start.

        if (!TypeInferenceUtils.TYPE_EMPTY.equals(nextType)) {
            // get suggested type
            if (columnTypeBean.getSuggestedType() == null) {
                columnTypeBean.setSuggestedType(nextType);
                if (StringUtils.isEmpty(typeValue)) {
                    // If the type is not given , valid count will
                    // be
                    // the count from suggested type.
                    columnTypeBean.setValidCount(count);
                }
            }
            // get valid count
            if (!StringUtils.isEmpty(typeValue) && StringUtils.equalsIgnoreCase(typeValue, nextType)) {
                columnTypeBean.setValidCount(count);
            }
        } else {
            columnTypeBean.setEmpties(count);
        }
        columnTypeBean.setCount(columnTypeBean.getCount() + count);
        jGenerator.writeStringField(TYPE_NAME, nextType);
        jGenerator.writeNumberField(OCCUR, count);
        jGenerator.writeEndObject();// Type object end.
    }

}

class TypeOccuConsumer implements Consumer<JsonNode> {

    private ColumnTypeBean typeBean = null;

    public void setTypeBean(ColumnTypeBean typeBean) {
        this.typeBean = typeBean;
    }

    public void accept(JsonNode node) {
        String typeName = node.get(InferenceHub.TYPE_NAME).asText();
        Long occurs = node.get(InferenceHub.OCCUR).asLong();
        typeBean.putTypeToCount(typeName, occurs);
    }

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