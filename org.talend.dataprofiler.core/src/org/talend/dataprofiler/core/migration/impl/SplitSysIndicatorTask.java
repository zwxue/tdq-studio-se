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



import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.talend.commons.emf.CwmResource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
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
public class SplitSysIndicatorTask extends AWorkspaceTask {

    private static Logger log = Logger.getLogger(SplitSysIndicatorTask.class);
    public SplitSysIndicatorTask() {
    }

    @Override
    protected boolean doExecute() throws Exception {

        // Copy system indicators.
        DQStructureManager manager = DQStructureManager.getInstance();
        IFolder systemIndicatorFoler = manager.createNewFolder(ResourceManager.getIndicatorFolder(),
                EResourceConstant.SYSTEM_INDICATORS);
        manager.copyFilesToFolder(CorePlugin.getDefault(), DQStructureManager.SYSTEM_INDICATOR_PATH, true, systemIndicatorFoler,
                null);

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
        if (ind == null || ind.getIndicatorDefinition() == null) {
            return;
        }
        CwmResource indDefResource = (CwmResource) ind.getIndicatorDefinition().eResource();
        // Find updated indicator definition
        IndicatorDefinition updatedDefinition = DefinitionHandler.getInstance().getDefinitionById(
                indDefResource.getID(ind.getIndicatorDefinition()));
        ind.setIndicatorDefinition(updatedDefinition);
    }

    private Collection<Analysis> searchAllAnalysis(IFolder folder) {
        Collection<Analysis> analyses = new ArrayList<Analysis>();
        try {
            for (IResource resource : folder.members()) {
                if (resource.getType() == IResource.FOLDER) {
                    analyses.addAll(searchAllAnalysis(folder.getFolder(resource.getName())));
                    continue;
                }
                IFile file = (IFile) resource;
                if (file.getFileExtension().equals(FactoriesUtil.ANA)) {
                    analyses.add(AnaResourceFileHelper.getInstance().findAnalysis(file));
                }
            }
        } catch (CoreException e) {
            log.error(e);
        }
        return analyses;
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    public Date getOrder() {
        return createDate(2010, 07, 07);
    }

}
