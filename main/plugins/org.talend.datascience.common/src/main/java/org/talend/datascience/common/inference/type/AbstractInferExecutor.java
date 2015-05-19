package org.talend.datascience.common.inference.type;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInferExecutor {
	protected List<ColumnTypeBean> typeToCountBean = new ArrayList<ColumnTypeBean>();
	/**
	 * init the temporary collection for type inferring. this method should only be called once.
	 * 
	 * @return true if initialized successfully, false otherwise.
	 */
	public  boolean init(String[] record){
		initInRecordLevel();
		for (int idx = 0; idx < record.length; idx++) {
			initEachColumn(idx, null);
		}
		return true;
	}
	
	
	protected abstract  void initInRecordLevel() ;


	/**
	 * Initialize the executor of column one by one given a column index and column type bean. 
	 * @param colIdx
	 * @param bean the bean that is used to init the exectutor, or null if want a new instance.
	 */
	protected abstract void initEachColumn(Integer colIdx,ColumnTypeBean bean);

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
			handleColumn(column, colIdx);
			colIdx++;
		}
		return true;
	}

	protected abstract void handleColumn(String column, int colIdx);
	
	public abstract boolean end();
	
	/**
	 * Get the inferring result, this method should be called once and only once
	 * after {{@link #handle(String[])} method .
	 * 
	 * @return the inference result for all columns in the list, each column will has a map of <type,freq> structure.
	 */
	public abstract List<ColumnTypeBean> getResults() ;
}
