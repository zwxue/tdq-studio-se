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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dataprofiler.core.migration.helper.VersionCompareHelper;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.ResourceManager;
import org.talend.utils.ProductVersion;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ThresholdsValuesTask extends AWorkspaceTask {

    private static Logger log = Logger.getLogger(ThresholdsValuesTask.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#execute()
     */
    public boolean execute() {
        try {
            // There handle a special case, the Thresholds is different in TOP and TDQ in 3.2.2(r33000)
            ProductVersion vesion = WorkspaceVersionHelper.getVesion();
            ProductVersion version322 = new ProductVersion(3, 2, 2);
            if (VersionCompareHelper.isEqual(vesion, version322) && ResourceManager.getReportsFolder().exists()) {
                return true;
            }

            Collection<Analysis> allAnalysis = AnaResourceFileHelper.getInstance().getAllAnalysis();
            for (Analysis analysis : allAnalysis) {
                EList<Indicator> indicators = analysis.getResults().getIndicators();
                for (Indicator indicator : indicators) {
                    String[] pThresholds = IndicatorHelper.getIndicatorThresholdInPercent(indicator);
                    if (pThresholds != null) {
                        String sOldMin = pThresholds[0];
                        String sOldMax = pThresholds[1];

                        String sNewMin = null, sNewMax = null;
                        if (StringUtils.isNotBlank(sOldMin)) {
                            sNewMin = String.valueOf(Double.valueOf(sOldMin) / 100);
                        }

                        if (StringUtils.isNotBlank(sOldMax)) {
                            sNewMax = String.valueOf(Double.valueOf(sOldMax) / 100);
                        }

                        IndicatorHelper.setIndicatorThresholdInPercent(indicator.getParameters(), sNewMin, sNewMax);
                    }
                }

                ElementWriterFactory.getInstance().createAnalysisWrite().save(analysis);
            }

            return true;
        } catch (Exception e) {
            log.error(e);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2010, 4, 6);
        return calender.getTime();
    }

}
