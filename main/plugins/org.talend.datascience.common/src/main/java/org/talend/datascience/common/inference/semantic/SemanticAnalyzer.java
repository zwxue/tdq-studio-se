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
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;

/**
 * Semantic type infer executor.
 */
public class SemanticAnalyzer implements Analyzer<SemanticType> {

	private final ResizableList<SemanticType> results = new ResizableList<SemanticType>(
			SemanticType.class);

	private final Map<Integer, CategoryRecognizer> columnIdxToCategoryRecognizer = new HashMap<Integer, CategoryRecognizer>();

	private Mode semanticRecognizerMode = Mode.LUCENE;

	private String host = "localhost";
	private int port = 9300;
	private String clusterName = "elasticsearch";

	private String ddPath = null;

	private String kwPath = null;

	private CategoryRecognizerBuilder builder;

	// Threshold of handle to be run . since the semantic inferring will require
	// more time than expected , we only want to run the handle method on a
	// sample with small size.
	private int handleThreshold = 100;

	private int currentHandleCursor = 1;

	public SemanticAnalyzer() {
		builder = CategoryRecognizerBuilder.newBuilder();
		builder.setMode(semanticRecognizerMode);
	}

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

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public void init() {
		currentHandleCursor = 1;
		columnIdxToCategoryRecognizer.clear();
		results.clear();
		// init semantic builder
		if (Mode.ELASTIC_SEARCH == semanticRecognizerMode) {
			builder = builder.host(host).port(port).cluster(clusterName);
		} else {
			// Lucene mode
			builder = builder.ddPath(ddPath).kwPath(kwPath);
		}
		builder.setMode(semanticRecognizerMode);
	}

	public boolean analyze(String... record) {
		results.resize(record.length);
		resizeCategoryRecognizer(record);

		boolean isHanded = true;
		if (currentHandleCursor <= handleThreshold) {
			for (int i = 0; i < record.length; i++) {
				columnIdxToCategoryRecognizer.get(i).process(record[i]);
			}
			currentHandleCursor++;
		}
		return isHanded;
	}

	private void resizeCategoryRecognizer(String[] record) {
		if (columnIdxToCategoryRecognizer.size() > 0) {
			// already resized
			return;
		}
		for (int idx = 0; idx < record.length; idx++) {
			CategoryRecognizer recognizer = builder.build();
			columnIdxToCategoryRecognizer.put(idx, recognizer);
		}
	}

	public void end() {
		for (CategoryRecognizer rn : columnIdxToCategoryRecognizer.values()) {
			rn.reset();
		}
	}

	public List<SemanticType> getResult() {
		Iterator<Integer> colIdxIterator = columnIdxToCategoryRecognizer
				.keySet().iterator();
		while (colIdxIterator.hasNext()) {
			Integer colIdx = colIdxIterator.next();
			Collection<Category> result = columnIdxToCategoryRecognizer.get(
					colIdx).getResult();
			for (Category semCategory : result) {
				results.get(colIdx).putSemanticNameToCount(
						semCategory.getCategoryName(), semCategory.getCount());
			}
		}
		return results;
	}

	public void setSemanticProperties(String ddPath, String kwPath,
			Mode searchMode) {
		this.ddPath = ddPath;
		this.kwPath = kwPath;
		this.semanticRecognizerMode = searchMode;
	}

}
