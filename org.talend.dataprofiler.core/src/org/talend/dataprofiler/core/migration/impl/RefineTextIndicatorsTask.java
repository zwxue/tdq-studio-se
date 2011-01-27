// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.TextIndicator;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.ResourceManager;

/**
 * 
 * DOC mzhao Migration task for text indicators refinement. Text indicator category has children more than before (3.2).<br>
 * In 4.1, indicator leaves of text stats which created on 3.2 must not be treated as a whole category. Must store them
 * separetely.
 */
public class RefineTextIndicatorsTask extends AbstractWorksapceUpdateTask {

    private static Logger log = Logger.getLogger(RefineTextIndicatorsTask.class);
    public RefineTextIndicatorsTask() {
    }

    @Override
    protected boolean doExecute() throws Exception {
        // Migration for analyses (indicator definition)
        try {
            Collection<Analysis> analyses = searchAllAnalysis(ResourceManager.getAnalysisFolder());
            AnalysisWriter analysisWriter = ElementWriterFactory.getInstance().createAnalysisWrite();
            for (Analysis analysis : analyses) {
                EList<Indicator> allIndics = analysis.getResults().getIndicators();
                List<Indicator> textIndLeaves = null;
                Indicator textIndCategory = null;
                for (Indicator indicator : allIndics) {
                    if (indicator instanceof TextIndicator) {
                        textIndCategory = indicator;
                        textIndLeaves = IndicatorHelper.getIndicatorLeaves(indicator);
                        break;
                    }
                }
                if (textIndCategory != null && textIndLeaves != null) {
                    analysis.getResults().getIndicators().remove(textIndCategory);
                    analysis.getResults().getIndicators().addAll(textIndLeaves);
                }
                analysisWriter.save(analysis);
            }
        } catch (Exception e) {
            log.error(e, e);
            return false;
        }
        return true;
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    public Date getOrder() {
        return createDate(2010, 8, 15);
    }

}
