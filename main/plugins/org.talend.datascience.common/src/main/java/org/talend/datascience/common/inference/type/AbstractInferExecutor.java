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
import java.util.List;

/**
 * An abstract class for the attributes and methods common in data type and
 * semantic category inferring.
 * 
 * @author mzhao
 *
 */
public abstract class AbstractInferExecutor {
	protected List<ColumnTypeBean> typeToCountBean = new ArrayList<ColumnTypeBean>();

	/**
	 * init the temporary collection for type inferring. this method should only
	 * be called once.
	 * 
	 * @return true if initialized successfully, false otherwise.
	 */
	public boolean init(){
		typeToCountBean.clear();
		return true;
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
	public List<ColumnTypeBean> handle(Iterable<String[]> records) {
		// Init the result container
		if (records == null) {
			// Input data are empty
			return typeToCountBean;
		}
		// Loop the data set , do inferring.
		for (String[] record : records) { // Record
			if (record != null && record.length > 0) {
				handle(record);
			}
		}
		return typeToCountBean;
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
			// Initialize the executor of column one by one on the fly.
			resize(record.length);
			handleColumn(column, colIdx);
			colIdx++;
		}
		return true;
	}

	private void resize(int size) {
		if (typeToCountBean.size() < (size - 1)) {
			for (int idx = 0; idx < size; idx++) {
				initColumnTypeBean(null, idx);
			}
		}
	}
	

	/**
	 * Initialize a new column type bean given column index.
	 * 
	 * @param bean
	 *            a instance bean already created, or null if creating a new
	 *            bean.
	 * @return true if initialized correctly , false otherwise.
	 */
	protected abstract boolean initColumnTypeBean(ColumnTypeBean bean,
			int colIdx);

	/**
	 * Handle (infer data type or semantic name) one column value by one.
	 * 
	 * @param columnValue
	 *            the column value.
	 * @param colIdx
	 *            column index
	 */
	protected abstract void handleColumn(String columnValue, int colIdx);

	public abstract boolean end();

	/**
	 * Get the inferring result, this method should be called once and only once
	 * after {{@link #handle(String[])} method .
	 * 
	 * @return the inference result for all columns in the list, each column
	 *         will has a map of <type,freq> structure.
	 */
	public abstract List<ColumnTypeBean> getResults();
}
