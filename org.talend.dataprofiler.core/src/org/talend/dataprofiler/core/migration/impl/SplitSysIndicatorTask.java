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

import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.CwmResource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.IndicatorsDefinitions;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.IndicatorDefinitionWriter;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import orgomg.cwm.objectmodel.core.ModelElement;

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
        IndicatorsDefinitions indicatorDefinitions = DefinitionHandler.getInstance().getIndicatorsDefinitions();
        IndicatorDefinitionWriter indDefWriter = ElementWriterFactory.getInstance().createIndicatorDefinitionWriter();

        DQStructureManager manager = DQStructureManager.getInstance();
        IFolder indFolder = ResourceManager.getIndicatorFolder();
        ResourceService.setNoSubFolderProperty(indFolder);
        try {
            // create System indicators folder
            indFolder = manager.createNewFolder(indFolder, EResourceConstant.SYSTEM_INDICATORS.getName());
            // Loop system indicators and save separately.
            Iterator<IndicatorDefinition> indIt = indicatorDefinitions.getIndicatorDefinitions().iterator();
            while (indIt.hasNext()) {
                IndicatorDefinition indDef = indIt.next();
                String categoryName = indDef.getCategories() == null || indDef.getCategories().size() == 0 ? PluginConstant.UNCATEGORIES
                        : indDef.getCategories().get(0).getName();
                IFolder categoryFolder = manager.createNewFolder(indFolder, categoryName);
                String filename = indDef.getName() + org.talend.dataquality.PluginConstant.DOT_STRING + FactoriesUtil.DEFINITION;
                IFile file = categoryFolder.getFile(filename);
                EObject copiedObj = EcoreUtil.copy(indDef);
                indDefWriter.save((ModelElement) copiedObj, file);
                // Copy xml id (for indicator definition only)
                ((CwmResource) copiedObj.eResource()).setID(copiedObj, ((CwmResource) indDef.eResource()).getID(indDef));
                indDefWriter.save((ModelElement) copiedObj);
            }
            // Remove all indicator definitions from .talend.definition file, it should be only contain categories.
            indicatorDefinitions.getIndicatorDefinitions().clear();
            indDefWriter.save(indicatorDefinitions);

        } catch (Throwable exc) {
            log.error(exc);
            return false;
        }
        return true;
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    public Date getOrder() {
        return createDate(2010, 07, 07);
    }

}
