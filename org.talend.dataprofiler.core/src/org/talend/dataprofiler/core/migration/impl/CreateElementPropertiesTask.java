// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.reports.TdReport;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.dq.writer.impl.DQRuleWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class CreateElementPropertiesTask extends AbstractMigrationTask {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#execute()
     */
    public boolean execute() {
        Collection<Analysis> allAnalysis = AnaResourceFileHelper.getInstance().getAllAnalysis();

        Collection<WhereRule> allDQRules = DQRuleResourceFileHelper.getInstance().getAllDQRules();

        Collection<Pattern> allPatternes = PatternResourceFileHelper.getInstance().getAllPatternes();

        List<TdDataProvider> allDataProviders = PrvResourceFileHelper.getInstance().getAllDataProviders();

        Collection<TdReport> allReports = RepResourceFileHelper.getInstance().getAllReports();

        Collection<IndicatorDefinition> allUDIs = UDIResourceFileHelper.getInstance().getAllUDIs();

        List<ModelElement> allElement = new ArrayList<ModelElement>();
        allElement.addAll(allAnalysis);
        allElement.addAll(allDQRules);
        allElement.addAll(allPatternes);
        allElement.addAll(allDataProviders);
        allElement.addAll(allReports);
        allElement.addAll(allUDIs);

        saveProperties(allElement);

        return true;
    }

    /**
     * DOC bZhou Comment method "saveProperties".
     * 
     * @param elementList
     */
    private void saveProperties(Collection<ModelElement> elementList) {
        for (ModelElement element : elementList) {
            String propertyPath = MetadataHelper.getPropertyPath(element);

            if (propertyPath == null) {
                DQRuleWriter writer = ElementWriterFactory.getInstance().createdRuleWriter();
                writer.savePerperties(element);
            } else {
                IFile propertyFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(propertyPath));
                if (!propertyFile.exists()) {
                    DQRuleWriter writer = ElementWriterFactory.getInstance().createdRuleWriter();
                    writer.savePerperties(element);
                }
            }
        }
    }

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
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getOrder()
     */
    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2009, 10, 13);
        return calender.getTime();
    }

}
