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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.DQRuleWriter;
import org.talend.dq.writer.impl.DataProviderWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.IndicatorDefinitionWriter;
import org.talend.dq.writer.impl.PatternWriter;
import org.talend.dq.writer.impl.ReportWriter;
import org.talend.resource.ResourceManager;


/**
 * DOC xqliu  class global comment. Detailled comment
 */
public class UpdatePropertiesFileTask extends AWorkspaceTask {

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        List<File> fileList = new ArrayList<File>();
        FilesUtils.getAllFilesFromFolder(getRootProjectFile(), fileList, null);
        for (File file : fileList) {
            if (file.isFile()) {
                IFile ifile = file2IFile(file);
                if (file.getName().endsWith(FactoriesUtil.ANA)) {
                    AnalysisWriter analysisWriter = ElementWriterFactory.getInstance().createAnalysisWrite();
                    analysisWriter.savePerperties(AnaResourceFileHelper.getInstance().findAnalysis(ifile));
                } else if (file.getName().endsWith(FactoriesUtil.REP)) {
                    ReportWriter reportWriter = ElementWriterFactory.getInstance().createReportWriter();
                    reportWriter.savePerperties(RepResourceFileHelper.getInstance().findReport(ifile));
                } else if (file.getName().endsWith(FactoriesUtil.DEFINITION)) {
                    IndicatorDefinitionWriter indicatorDefinitionWriter = ElementWriterFactory.getInstance()
                            .createIndicatorDefinitionWriter();
                    IndicatorDefinition indicatorDefinition = IndicatorResourceFileHelper.getInstance().findIndDefinition(ifile);
                    if (indicatorDefinition != null) { // .talend.definition don't include any IndicatorDefinition now
                        indicatorDefinitionWriter.savePerperties(indicatorDefinition);
                    }
                } else if (file.getName().endsWith(FactoriesUtil.DQRULE)) {
                    DQRuleWriter dqRuleWriter = ElementWriterFactory.getInstance().createdRuleWriter();
                    dqRuleWriter.savePerperties(DQRuleResourceFileHelper.getInstance().findWhereRule(ifile));
                } else if (file.getName().endsWith(FactoriesUtil.PATTERN)) {
                    PatternWriter patternWriter = ElementWriterFactory.getInstance().createPatternWriter();
                    patternWriter.savePerperties(PatternResourceFileHelper.getInstance().findPattern(ifile));
                } else if (file.getName().endsWith(FactoriesUtil.PROV)) {
                    DataProviderWriter dataProviderWriter = ElementWriterFactory.getInstance().createDataProviderWriter();
                    dataProviderWriter.savePerperties(PrvResourceFileHelper.getInstance().findProvider(ifile).getObject());
                } else if (file.getName().endsWith(FactoriesUtil.SOFTWARE_SYSTEM)) {
                    // TODO how to handle this type file???
                } else {
                    // unknown file type, so do nothing
                }
            } else {
                // only update files, don't need to update folders
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        // MOD xqliu 2010-09-15 bug 13941 this task should be called before ExchangeFileNameToReferenceTask
        // return createDate(2010, 8, 17);
        return createDate(2010, 8, 12);
    }

    /**
     * DOC xqliu Comment method "file2IFile".
     * 
     * @param file
     * @return
     */
    private IFile file2IFile(File file) {
        String filePath = file.getAbsolutePath();
        String projectPath = getRootProjectFile().getAbsolutePath();
        filePath = filePath.substring(projectPath.length());
        return ReponsitoryContextBridge.getRootProject().getFile(filePath);
    }

    /**
     * DOC xqliu Comment method "getRootProjectFile".
     * 
     * @return
     */
    private File getRootProjectFile() {
        return ResourceManager.getRootProject().getLocation().toFile();
    }
}
