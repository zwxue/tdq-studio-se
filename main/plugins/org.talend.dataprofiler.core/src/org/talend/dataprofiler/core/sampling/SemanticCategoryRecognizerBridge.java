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
package org.talend.dataprofiler.core.sampling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.service.ISemanticStudioService;
import org.talend.dataprofiler.service.semantic.SemanticCategory;
import org.talend.dataquality.sampling.DataSamplingBridge;

/**
 * created by zhao <br>
 * Bridge for data sampling and semantic category API.
 *
 */
public class SemanticCategoryRecognizerBridge {

    private static Logger log = Logger.getLogger(SemanticCategoryRecognizerBridge.class);

    private DataSamplingBridge sampler;

    public SemanticCategoryRecognizerBridge(DataSamplingBridge sampler) {
        this.sampler = sampler;
    }

    /**
     * 
     * DOC zhao Inferring semantic catagory given sampling data and category recognizer.
     * 
     * @return a map of colum index to list of categories <column idx, List<Semantic category>>
     */
    public Map<Integer, List<SemanticCategory>> inferSemanticCategory() throws Exception {
        ISemanticStudioService service = CorePlugin.getDefault().getSemanticStudioService();
        if (service == null) {
            return new HashMap<Integer, List<SemanticCategory>>();
        }
        sampler.prepareData();
        while (sampler.hasNext()) {
            Object[] record;
            try {
                record = sampler.getRecord();
                // Init the recognizers
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                continue;
            }
            service.inferCategory(record);
        }
        sampler.finalizeDataSampling();
        return service.getSemanticCategory();
    }
}
