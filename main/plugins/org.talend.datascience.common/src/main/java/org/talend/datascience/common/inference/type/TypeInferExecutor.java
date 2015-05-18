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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.dataquality.semantic.recognizer.Category;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;

/**
 * Type inference executor which provide several methods computing the types.<br>
 * The suggested usage of this class is the following call sequence:<br>
 * 1. {{@link #init(String[])}, called once.<br>
 * 2. {{@link #handle(String[])} , called as many iterations as required.<br>
 * 3. {{@link #getResults()} , called once.<br>
 * 
 * @author zhao
 *
 */
class TypeInferExecutor {
	private List<Map<String, Long>> typeToCountList = null;
	private List<Map<String, Long>> semanticNameToCountList = null;

	private CategoryRecognizer categoryRecognizer = null;

	private String ddPath = null, kwPath = null;

	public TypeInferExecutor() {
		typeToCountList = new ArrayList<Map<String, Long>>();
		CategoryRecognizerBuilder b = CategoryRecognizerBuilder.newBuilder();
		b.setMode(Mode.LUCENE);
		// TODO where do we get the lucene index.
		ddPath = "/home/zhao/Talend/codebase/GIT/tdq-siq/org.talend.dataquality.semantic/luceneIdx/dictionary";
		kwPath = "/home/zhao/Talend/codebase/GIT/tdq-siq/org.talend.dataquality.semantic/luceneIdx/keyword";
		categoryRecognizer = b.ddPath(ddPath).kwPath(kwPath).build();
	}

	/**
	 * Inferring the types given a list of records.
	 * 
	 * @param records
	 *            a list of records which one record is represented as a string
	 *            array.
	 * @return a list of inferred type result. each item(map> in the list refer
	 *         to a column, from which the key is data type name and value is
	 *         the frequency.
	 */
	public List<Map<String, Long>> inferTypes(List<String[]> records) {
		// Init the result container
		if (records == null || records.isEmpty() || records.get(0) == null
				|| records.get(0).length == 0) {
			// Input data are empty
			return typeToCountList;
		}
		init(records.get(0));

		// Loop the data set , do inferring.
		for (String[] record : records) {// Record
			handle(record);
		}

		return typeToCountList;
	}

	private String getTypeName(String value) {
		if (TypeInferenceUtils.isEmpty(value)) {
			// 1. detect empty
			return TypeInferenceUtils.TYPE_EMPTY;
		} else if (TypeInferenceUtils.isBoolean(value)) {
			// 2. detect boolean
			return TypeInferenceUtils.TYPE_BOOL;
		} else if (TypeInferenceUtils.isChar(value)) {
			// 3. detect char
			return TypeInferenceUtils.TYPE_CHAR;
		} else if (TypeInferenceUtils.isInteger(value)) {
			// 4. detect integer
			return TypeInferenceUtils.TYPE_INTEGER;
		} else if (TypeInferenceUtils.isDouble(value)) {
			// 5. detect double
			return TypeInferenceUtils.TYPE_DOUBLE;
		} else if (TypeInferenceUtils.isAlphString(value)) {
			// 6. detect alph string
			return TypeInferenceUtils.TYPE_STRING;
		} else if (TypeInferenceUtils.isDate(value)) {
			// 7. detect date
			return TypeInferenceUtils.TYPE_DATE;
		}
		// will return string when no matching
		return TypeInferenceUtils.TYPE_STRING;
	}

	/**
	 * init the temporary collection for type inferring.
	 * 
	 * @return true if initialized successfully, false otherwise.
	 */
	public boolean init(String[] record) {
		typeToCountList.clear();
		semanticNameToCountList.clear();
		for (int idx = 0; idx < record.length; idx++) {
			// create a map for each column.
			Map<String, Long> columnMap = new HashMap<String, Long>();
			for (String type : TypeInferenceUtils.TYPES) {
				columnMap.put(type, 0l);
			}
			typeToCountList.add(columnMap);
			
		}
		categoryRecognizer.prepare();
		return true;
	}

	/**
	 * Inferring types record by record.
	 * 
	 * @param record
	 *            for which the data type is guessed.
	 * @return true if inferred successfully, false otherwise.
	 */
	public boolean handle(String[] record) {
		int colIdx = 0;
		for (String column : record) {// Column
			String typeName = getTypeName(column);
			typeToCountList.get(colIdx).put(typeName,
					typeToCountList.get(colIdx).get(typeName) + 1);
			// Get semantic name
			categoryRecognizer.process(column);
			colIdx++;
		}
		return true;
	}

	public boolean end() {
		categoryRecognizer.reset();
		return true;
	}

	/**
	 * Get the inferring result, this method should be called once and only once
	 * after {{@link #handle(String[])} method .
	 * 
	 * @return
	 */
	public List<Map<String, Long>> getResults() {
		return typeToCountList;
	}

	private String getSemanticCategory() {
		Collection<Category> result = categoryRecognizer.getResult();
		String semanticName = null;
		if(result!=null&&!result.isEmpty()){
//			semanticName=result.toArray()[0].getCategoryName();
		}
					
		return semanticName;
	}

}
