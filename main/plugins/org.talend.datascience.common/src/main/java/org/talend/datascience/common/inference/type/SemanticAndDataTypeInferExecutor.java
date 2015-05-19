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

import org.talend.datascience.common.inference.semantic.SemanticInferExecutor;

/**
 * Semantic and data type infer executor.
 * 
 * @author zhao
 *
 */
public class SemanticAndDataTypeInferExecutor extends AbstractInferExecutor {

	SemanticInferExecutor semanticInferExecutor = null;
	DataTypeInferExecutor dataTypeInferExecutor = null;

	public SemanticAndDataTypeInferExecutor() {
		semanticInferExecutor = new SemanticInferExecutor();
		dataTypeInferExecutor = new DataTypeInferExecutor();
	}

	@Override
	protected void handleColumn(String column, int colIdx) {
		dataTypeInferExecutor.handleColumn(column, colIdx);
		semanticInferExecutor.handleColumn(column, colIdx);
	}

	@Override
	public boolean end() {
		boolean isEnded = dataTypeInferExecutor.end();
		if (isEnded) {
			isEnded = semanticInferExecutor.end();
		}
		return isEnded;
	}

	@Override
	public List<ColumnTypeBean> getResults() {
		// The column type bean instaince is same from data type executor to
		// semantic executor.
		return dataTypeInferExecutor.getResults();
	}

	@Override
	protected void initInRecordLevel() {
		dataTypeInferExecutor.initInRecordLevel();
		semanticInferExecutor.initInRecordLevel();
	}

	@Override
	protected void initEachColumn(Integer colIdx, ColumnTypeBean bean) {
		ColumnTypeBean newBean = new ColumnTypeBean();
		dataTypeInferExecutor.initEachColumn(colIdx, newBean);
		semanticInferExecutor.initEachColumn(colIdx, newBean);
	}

}
