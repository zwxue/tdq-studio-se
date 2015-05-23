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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;

/**
 * Semantic type infer executor.
 */
public class SemanticAnalyzer implements Analyzer<SemanticType> {

    private final ResizableList<SemanticType> results = new ResizableList<SemanticType>(SemanticType.class);

    private final Map<Integer, CategoryRecognizer> columnIdxToCategoryRecognizer = new HashMap<Integer, CategoryRecognizer>();

    private Mode semanticRecognizerMode = Mode.LUCENE;

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

    public void init() {
        currentHandleCursor = 1;
        columnIdxToCategoryRecognizer.clear();
    }

    public boolean analyze(String... record) {
        results.resize(record.length);
        return true;
    }

    public void end() {
        for (CategoryRecognizer rn : columnIdxToCategoryRecognizer.values()) {
            rn.reset();
        }
    }

    public List<SemanticType> getResult() {
        return results;
    }

    public void setSemanticProperties(String ddPath, String kwPath, Mode searchMode) {

    }
}
