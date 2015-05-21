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
import java.util.Map;

/**
 * Type inference executor which provide several methods computing the types.<br>
 * The suggested usage of this class is the following call sequence:<br>
 * 1. {{@link #init()}, called once.<br>
 * 2. {{@link #handle(String[])} , called as many iterations as required.<br>
 * 3. {{@link #getResults()} , called once.<br>
 * 
 * <b>Important note:</b> This class is <b>NOT</b> thread safe.
 *
 * @author zhao
 * @author fhuaulme
 */
public class DataTypeInferExecutor extends AbstractInferExecutor {


	private static String getTypeName(String value) {
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
	 */
	@Override
	public boolean init() {
		return super.init();
	}


	@Override
	protected void handleColumn(String column, int colIdx) {
		String typeName = getTypeName(column);
		final ColumnTypeBean columnTypeBean = typeToCountBean.get(colIdx);
		final Map<String, Long> countMap = columnTypeBean.getTypeToCountMap();
		final Long previousCount = countMap.get(typeName) == null ? 0
				: countMap.get(typeName);
		countMap.put(typeName, previousCount + 1);
	}

	@Override
	protected boolean initColumnTypeBean(ColumnTypeBean bean, int colIdx) {
		ColumnTypeBean typeBean = bean;
		if (typeBean == null) {
			// create a map for each column.
			typeBean = new ColumnTypeBean();
			typeBean.setColumnIdx(colIdx);
		}
		typeToCountBean.add(typeBean);
		for (String type : TypeInferenceUtils.TYPES) {
			typeBean.putTypeToCount(type, 0l);
		}
		return true;
	}

	@Override
	public boolean end() {
		return true;
	}

	/**
	 * Get the inferring result, this method should be called once and only once
	 * after {{@link #handle(String[])} method.
	 * 
	 * @return A map for <b>each</b> column. Each map contains the type
	 *         occurrence count.
	 */
	public List<ColumnTypeBean> getResults() {
		return typeToCountBean;
	}
}
