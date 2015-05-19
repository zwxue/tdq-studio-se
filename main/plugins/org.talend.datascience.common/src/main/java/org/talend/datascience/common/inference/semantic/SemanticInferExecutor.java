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

	private Map<Integer, CategoryRecognizer> columnIdxToCategoryRecognizer = new HashMap<Integer, CategoryRecognizer>();
	CategoryRecognizerBuilder builder;
	// TODO where do we get the lucene index.
	private String ddPath = "/home/zhao/Talend/codebase/GIT/tdq-siq/org.talend.dataquality.semantic/luceneIdx/dictionary";
	private String kwPath = "/home/zhao/Talend/codebase/GIT/tdq-siq/org.talend.dataquality.semantic/luceneIdx/keyword";
	private Mode semanticRecognizerMode = Mode.LUCENE;

	public void setDdPath(String ddPath) {
		this.ddPath = ddPath;
	}

	public void setKwPath(String kwPath) {
		this.kwPath = kwPath;
	}

	public void setSemanticRecognizerMode(Mode semanticRecognizerMode) {
		this.semanticRecognizerMode = semanticRecognizerMode;
	}

	public SemanticInferExecutor() {
		builder = CategoryRecognizerBuilder.newBuilder();
		builder.setMode(semanticRecognizerMode);
	}

	@Override
	public void initInRecordLevel() {
		typeToCountBean.clear();
		columnIdxToCategoryRecognizer.clear();
	}

	@Override
	public void initEachColumn(Integer colIdx,ColumnTypeBean typeBean) {
		CategoryRecognizer recognizer = builder.ddPath(ddPath).kwPath(kwPath)
				.build();
		columnIdxToCategoryRecognizer.put(colIdx, recognizer);
		ColumnTypeBean bean = typeBean;
		if(bean == null){
			bean = new ColumnTypeBean();
		}
		bean.setColumnIdx(colIdx);
		typeToCountBean.add(bean);

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
			bean.setColumnIdx(colIdx);
			for (Category semCategory : result) {
				bean.putSemanticNameToCount(semCategory.getCategoryName(),
						semCategory.getCount());
			}
		}
		return typeToCountBean;
	}
}
