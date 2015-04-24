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
package org.talend.dq.datascience;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.dataquality.semantic.recognizer.Category;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;
import org.talend.utils.exceptions.TalendException;

/**
 * created by zhao <br>
 * Bridge for data sampling and semantic category API.
 *
 */
public class SemanticCategoryRecognizerBridge {

    // One recognizer per column, <Column index, Category recognizer>
    private Map<Integer, CategoryRecognizer> categoryRecognizers = new HashMap<Integer, CategoryRecognizer>();

    private DataSamplingBridge sampler;

    private List<String[]> semanticCategories = new ArrayList<String[]>();

    /**
     * 
     * DOC zhao Inferring semantic catagory given sampling data and category recognizer.
     * 
     * @param semanticSearchMode semantic search model.
     * @param indexPathDD the data dictionary index path.
     * @param indexPathKW the key word index path.
     */
    public void inferSemanticCategory(Mode semanticSearchMode, String indexPathDD, String indexPathKW) throws TalendException {
        while (sampler.hasNext()) {
            Object[] record;
            try {
                record = sampler.getRecord();
                // Init the recognizers
            } catch (TalendException e) {
                // log.error(e.getMessage(), e);
                continue;
            }
            int colIdx = 0;
            for (Object fieldData : record) {
                CategoryRecognizer categoryRecognizer = categoryRecognizers.get(colIdx);
                if (categoryRecognizer == null) {
                    // If categoryRecognizer is initialized before, initialize it firstly.
                    CategoryRecognizerBuilder b = CategoryRecognizerBuilder.newBuilder();
                    b.setMode(semanticSearchMode);
                    categoryRecognizer = b.ddPath(indexPathDD).kwPath(indexPathKW).build();
                    categoryRecognizer.prepare();
                    categoryRecognizers.put(colIdx, categoryRecognizer);
                }
                categoryRecognizer.process(fieldData.toString());
                colIdx++;
            }
        }

        for (CategoryRecognizer categoryRecognizer : categoryRecognizers.values()) {
            Collection<Category> result = categoryRecognizer.getResult();
            for (Category frequencyTableItem : result) {
                // logger.debug("frequencyTableItem = " + frequencyTableItem.getCategoryName() + " / " +
                // frequencyTableItem.getCount()
                // + " / " + frequencyTableItem.getFrequency());
            }
        }

    }

}
