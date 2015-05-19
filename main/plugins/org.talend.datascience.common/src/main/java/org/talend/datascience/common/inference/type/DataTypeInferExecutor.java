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

import java.util.List;

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
public class DataTypeInferExecutor extends AbstractInferExecutor {
	

	public DataTypeInferExecutor() {
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
	public List<ColumnTypeBean> inferTypes(List<String[]> records) {
		// Init the result container
		if (records == null || records.isEmpty() || records.get(0) == null
				|| records.get(0).length == 0) {
			// Input data are empty
			return typeToCountBean;
		}
		init(records.get(0));

		// Loop the data set , do inferring.
		for (String[] record : records) {// Record
			handle(record);
		}

		return typeToCountBean;
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

	protected void initInRecordLevel() {
		typeToCountBean.clear();
	}

	@Override
	protected void initEachColumn(Integer idx, ColumnTypeBean bean) {
		// create a map for each column.
		ColumnTypeBean typeBean = bean;
		if (typeBean == null) {
			typeBean = new ColumnTypeBean();
		}
		typeBean.setColumnIdx(idx);
		for (String type : TypeInferenceUtils.TYPES) {
			typeBean.putTypeToCount(type, 0l);
		}
		typeToCountBean.add(typeBean);
	}

	@Override
	protected void handleColumn(String column, int colIdx) {
		String typeName = getTypeName(column);
		typeToCountBean.get(colIdx).putTypeToCount(typeName,
				typeToCountBean.get(colIdx).getDataTypeCount(typeName) + 1);

	}

	public boolean end() {
		return true;
	}

	@Override
	public List<ColumnTypeBean> getResults() {
		return typeToCountBean;
	}
}
