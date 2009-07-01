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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;
import org.talend.resource.ResourceManager;

/**
 * 
 * DOC mzhao 2009-07-01 feature 7482.
 */
public class ReorderingLibraryFoldersTask extends AbstractMigrationTask {

    private static Logger log = Logger.getLogger(ReorderingLibraryFoldersTask.class);

    public ReorderingLibraryFoldersTask() {
    }

    public ReorderingLibraryFoldersTask(String id, String name, String version) {
        super(id, name, version);
    }

    public boolean execute() {
        IProject rootProject = ResourceManager.getRootProject();
        IFolder libraryFolder = rootProject.getFolder(ResourceManager.LIBRARIES_FOLDER_NAME);
        try {

            IFolder regexFolder = DQStructureManager.getInstance().createNewFoler(
                    libraryFolder.getFolder(DQStructureManager.PATTERNS), DQStructureManager.REGEX);
            libraryFolder.getFolder(DQStructureManager.PATTERNS).setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY,
                    null);
            regexFolder
                    .setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY, DQStructureManager.PATTERNS_FOLDER_PROPERTY);
            IFolder patternFolder = libraryFolder.getFolder(DQStructureManager.PATTERNS);
            for (IResource resource : patternFolder.members()) {
                if (regexFolder.getName().equals(resource.getName())) {
                    continue;
                }
                resource.copy(regexFolder.getFolder(resource.getName()).getFullPath(), true, null);
                resource.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY,
                        DQStructureManager.PATTERNS_FOLDER_PROPERTY);
                resource.delete(true, null);
            }

            IFolder sqlFolder = DQStructureManager.getInstance().createNewFoler(
                    libraryFolder.getFolder(DQStructureManager.PATTERNS), DQStructureManager.SQL);

            sqlFolder.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY,
                    DQStructureManager.SQLPATTERNS_FOLDER_PROPERTY);

            IFolder sqlPatternsFolder = libraryFolder.getFolder(DQStructureManager.SQL_PATTERNS);
            for (IResource resource : sqlPatternsFolder.members()) {
                resource.copy(sqlFolder.getFolder(resource.getName()).getFullPath(), true, null);
                resource.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY,
                        DQStructureManager.SQLPATTERNS_FOLDER_PROPERTY);
            }
            sqlPatternsFolder.delete(true, null);

            IFolder rulesFolder = DQStructureManager.getInstance().createNewFoler(libraryFolder, DQStructureManager.RULES);
            IFolder rulesSQLFolder = DQStructureManager.getInstance().createNewFoler(rulesFolder, DQStructureManager.SQL);
            rulesSQLFolder.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY,
                    DQStructureManager.DQRULES_FOLDER_PROPERTY);

            IFolder dqRulesFolder = libraryFolder.getFolder(DQStructureManager.DQ_RULES);
            for (IResource resource : dqRulesFolder.members()) {
                resource.copy(rulesSQLFolder.getFolder(resource.getName()).getFullPath(), true, null);
                resource
                        .setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY, DQStructureManager.DQRULES_FOLDER_PROPERTY);
            }
            dqRulesFolder.delete(true, null);

            // Refresh project
            rootProject.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e) {
            log.error(e, e);
        }

        return true;
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.STUCTRUE;
    }

    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2009, 07, 01);
        return calender.getTime();
    }

}
