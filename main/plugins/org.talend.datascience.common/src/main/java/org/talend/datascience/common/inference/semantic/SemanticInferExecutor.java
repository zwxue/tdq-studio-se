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
package org.talend.datascience.common.inference.semantic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.dataquality.semantic.recognizer.Category;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;
import org.talend.datascience.common.inference.type.AbstractInferExecutor;
import org.talend.datascience.common.inference.type.ColumnTypeBean;

/**
 * Semantic type infer executor.
 * 
 * @author zhao
 *
 */
public class SemanticInferExecutor extends AbstractInferExecutor {
	private static Logger log = Logger.getLogger(SemanticInferExecutor.class);
	private Map<Integer, CategoryRecognizer> columnIdxToCategoryRecognizer = new HashMap<Integer, CategoryRecognizer>();
	CategoryRecognizerBuilder builder;
	private String ddPath = null;
	private String kwPath = null;
	private Mode semanticRecognizerMode = Mode.LUCENE;
	// Threshold of handle to be run . since the semantic inferring will require
	// more time than expected , we only want to run the handle method on a
	// sample with small size.
	private int handleThreshold = 100;
	
	private int currentHandleCursor = 1;

	public void setDdPath(String ddPath) {
		this.ddPath = ddPath;
	}

	public void setKwPath(String kwPath) {
		this.kwPath = kwPath;
	}

	public void setSemanticRecognizerMode(Mode semanticRecognizerMode) {
		this.semanticRecognizerMode = semanticRecognizerMode;
	}
	
	public void setHandleThreshold(int handleThreshold) {
		this.handleThreshold = handleThreshold;
	}

	public SemanticInferExecutor() {
		builder = CategoryRecognizerBuilder.newBuilder();
		builder.setMode(semanticRecognizerMode);
	}

	@Override
	public boolean init() {
		currentHandleCursor=1;
		columnIdxToCategoryRecognizer.clear();
		return super.init();
	}

	@Override
	public boolean initColumnTypeBean(ColumnTypeBean typeBean, int colIdx) {
		if (ddPath == null || kwPath == null) {
			log.error("dictionary path and key word path is not specified.");
			return false;
		}
		CategoryRecognizer recognizer = builder.ddPath(ddPath).kwPath(kwPath)
				.build();
		columnIdxToCategoryRecognizer.put(colIdx, recognizer);
		ColumnTypeBean bean = typeBean;
		if (bean == null) {
			bean = new ColumnTypeBean();
			bean.setColumnIdx(colIdx);
		}
		typeToCountBean.add(bean);
		return true;
	}

	@Override
	public boolean handle(String[] record) {
		boolean isHanded = true;
		if(currentHandleCursor<handleThreshold){
			isHanded= super.handle(record);
			currentHandleCursor++;
		}
		return isHanded;
	}

	@Override
	public void handleColumn(String column, int colIdx) {
		columnIdxToCategoryRecognizer.get(colIdx).process(column);
	}

	@Override
	public boolean end() {
		for (CategoryRecognizer rn : columnIdxToCategoryRecognizer.values()) {
			rn.reset();
		}
		return true;
	}

	@Override
	public List<ColumnTypeBean> getResults() {
		Iterator<Integer> colIdxIterator = columnIdxToCategoryRecognizer
				.keySet().iterator();
		while (colIdxIterator.hasNext()) {
			Integer colIdx = colIdxIterator.next();
			Collection<Category> result = columnIdxToCategoryRecognizer.get(
					colIdx).getResult();
			ColumnTypeBean bean = typeToCountBean.get(colIdx);
			for (Category semCategory : result) {
				bean.putSemanticNameToCount(semCategory.getCategoryName(),
						semCategory.getCount());
			}
		}
		return typeToCountBean;
	}

}
