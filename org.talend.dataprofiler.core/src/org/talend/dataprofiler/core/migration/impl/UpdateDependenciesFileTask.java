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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.resource.ResourceManager;

/**
 * @author scorreia
 * 
 * This migration task updates the dependencies between analyses and patterns. See bug 9497 (and a consequence 9461).
 * This task must be executed before moving all patterns in the "Rules/SQL" folder.
 */
public class UpdateDependenciesFileTask extends AbstractMigrationTask {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#execute()
     */
    public boolean execute() {
        try {
            // MOD scorreia 2009-10-07 load all analyses and find those which use patterns.
            IProject rootProject = ResourceManager.getRootProject();
            IFolder dataprofilingFolder = rootProject.getFolder(ResourceManager.DATA_PROFILING_FOLDER_NAME);
            IFolder analysesFolder = dataprofilingFolder.getFolder(DQStructureManager.ANALYSIS);
            updateDependencies(analysesFolder);
            return true;
        } catch (CoreException e) {
            ExceptionHandler.process(e);
            return false;
        }

    }

    /**
     * Method "updateDependencies" updates dependencies between elements in TOP.
     * 
     * @param analysesSubFolder
     * @throws CoreException
     */
    private void updateDependencies(IFolder analysesSubFolder) throws CoreException {
        for (IResource resource : analysesSubFolder.members()) {
            if (resource instanceof IFolder) {
                IFolder folder = (IFolder) resource;
                updateDependencies(folder);
            }

            if (resource instanceof IFile) {
                IFile file = (IFile) resource;
                final Analysis analysis = AnaResourceFileHelper.getInstance().findAnalysis(file);
                // update dependency between analyses and patterns
                final List<Pattern> patterns = AnalysisHelper.getPatterns(analysis);
                for (Pattern pattern : patterns) {
                    DependenciesHandler.getInstance().setDependencyOn(analysis, pattern);
                    AnaResourceFileHelper.getInstance().save(analysis);
                }
                // update dependency between analyses and dq rules
                final List<IndicatorDefinition> userDefinedIndicators = AnalysisHelper.getUserDefinedIndicators(analysis);
                for (IndicatorDefinition indicatorDefinition : userDefinedIndicators) {
                    DependenciesHandler.getInstance().setDependencyOn(analysis, indicatorDefinition);
                    AnaResourceFileHelper.getInstance().save(analysis);
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
        calender.set(2009, 6, 1);
        return calender.getTime();
    }

}
