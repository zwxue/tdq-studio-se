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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.talend.commons.emf.CwmResource;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * 
 * DOC mzhao Migration task for splitting system indicators. The .talend.definition file would be replaced by several
 * separate files, each indicator definition is defined in its own file. feature: 13676, 2010-07-07
 */
public class SplitSysIndicatorTask extends AbstractWorksapceUpdateTask {

    private static Logger log = Logger.getLogger(SplitSysIndicatorTask.class);

    public SplitSysIndicatorTask() {
    }

    @Override
    protected boolean doExecute() throws Exception {

        // Copy system indicators.
        if (!ResourceManager.getSystemIndicatorFolder().exists()) {
            DQStructureManager manager = DQStructureManager.getInstance();
            IFolder systemIndicatorFoler = manager.createNewFolder(ResourceManager.getIndicatorFolder(),
                    EResourceConstant.SYSTEM_INDICATORS);
            manager.copyFilesToFolder(CorePlugin.getDefault(), DQStructureManager.SYSTEM_INDICATOR_PATH, true,
                    systemIndicatorFoler, null);
        }

        // Migration for analyses (indicator definition)
        Collection<Analysis> analyses = searchAllAnalysis(ResourceManager.getAnalysisFolder());
        AnalysisWriter analysisWriter = ElementWriterFactory.getInstance().createAnalysisWrite();
        for (Analysis ana : analyses) {
            for (Indicator ind : ana.getResults().getIndicators()) {
                updateIndDefinition(ind);
                if (ind instanceof CompositeIndicator) {
                    for (Indicator indLeave : IndicatorHelper.getIndicatorLeaves(ind)) {
                        updateIndDefinition(indLeave);
                    }
                }
            }
            analysisWriter.save(ana);
        }
        // Copy system indicator categories.
        ResourceManager.getLibrariesFolder().getFile(DefinitionHandler.FILENAME).delete(true, new NullProgressMonitor());
        DefinitionHandler.getInstance().copyDefinitionsIntoFolder(ResourceManager.getLibrariesFolder());

        return true;
    }

    private void updateIndDefinition(Indicator ind) {
        if (ind == null) {
            return;
        }
        IndicatorDefinition indicatorDefinition = ind.getIndicatorDefinition();
        if (indicatorDefinition == null) {
            return;
        }
        // ADD xqliu 2010-07-28 13676 don't update UserDefIndicator
        if (ind instanceof UserDefIndicator) {
            return;
        }
        // ~ 13676
        CwmResource indDefResource = (CwmResource) indicatorDefinition.eResource();
        if (indDefResource == null) {
            // MOD scorreia 2010-10-05 16030 set the link between indicators and their definition
            if (DefinitionHandler.getInstance().setDefaultIndicatorDefinition(ind)) {
                if (log.isDebugEnabled()) {
                    log.debug("MIGRATING: indicator definition " + indicatorDefinition.getName() + " in indicator "
                            + ind.getName() + " in analysis " + ind.eResource());
                }
            } else {
                // TODO externalize this string
                log.error("MIGRATION ERROR: Could not change the indicator definition " + indicatorDefinition.getName()
                        + " in indicator " + ind.getName() + " in analysis " + ind.eResource());
            }
            // ~ 16030
        } else {
            // Find updated indicator definition
            IndicatorDefinition updatedDefinition = DefinitionHandler.getInstance().getDefinitionById(
                    indDefResource.getID(ind.getIndicatorDefinition()));
            ind.setIndicatorDefinition(updatedDefinition);
        }

    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    public Date getOrder() {
        return createDate(2010, 07, 07);
    }

}
