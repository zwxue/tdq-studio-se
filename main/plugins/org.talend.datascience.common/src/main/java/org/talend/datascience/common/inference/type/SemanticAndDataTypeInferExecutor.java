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

import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;
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
	
	public void setSemanticProperties(String ddPath,String kwPath,Mode searchMode){
		semanticInferExecutor.setDdPath(ddPath);
		semanticInferExecutor.setKwPath(kwPath);
		semanticInferExecutor.setSemanticRecognizerMode(searchMode);
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
		// The column type bean instance is same from data type executor to
		// semantic executor.
		semanticInferExecutor.getResults();// This step is required to get
											// semantic information and populate
											// to bean.
		return dataTypeInferExecutor.getResults();// Bean instance is same from
													// data type exectutor to
													// semantic type executor.
	}

	@Override
	public boolean init() {
		boolean isInited = dataTypeInferExecutor.init();
		if (isInited) {
			isInited = semanticInferExecutor.init();
		}
		return isInited;
	}

	@Override
	protected boolean initColumnTypeBean(ColumnTypeBean bean, int colIdx) {
		ColumnTypeBean newBean = new ColumnTypeBean();
		newBean.setColumnIdx(colIdx);
		// Use same bean instance for both data type and semantic type executor.
		boolean isInited = dataTypeInferExecutor.initColumnTypeBean(newBean,
				colIdx);
		if (isInited) {
			isInited = semanticInferExecutor
					.initColumnTypeBean(newBean, colIdx);
		}
		typeToCountBean.add(newBean);
		return isInited;
	}

}
