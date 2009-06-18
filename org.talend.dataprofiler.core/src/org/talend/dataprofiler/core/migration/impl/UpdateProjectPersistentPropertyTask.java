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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;
import org.talend.dataquality.ResourceManager;
import org.talend.dataquality.exception.ExceptionHandler;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UpdateProjectPersistentPropertyTask extends AbstractMigrationTask {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#execute()
	 */
	public boolean execute() {
		String[] folderNames = { ResourceManager.DATA_PROFILING_FOLDER_NAME,
				ResourceManager.LIBRARIES_FOLDER_NAME,
				ResourceManager.METADATA_FOLDER_NAME };
		try {
			checkProjectPersistentProperty(folderNames);
		} catch (CoreException e) {
			ExceptionHandler.process(e);
		}
		return false;
	}

	/**
	 * 
	 * DOC xqliu Comment method "checkProjectPersistentProperty".
	 * 
	 * @param projects
	 * @throws CoreException
	 */
	private void checkProjectPersistentProperty(String[] folderNames)
			throws CoreException {
		IFolder folder;
		for (String folderName : folderNames) {
			folder = ResourceManager.getRootProject().getFolder(folderName);
			if (folder != null
					&& folder
							.getPersistentProperty(DQStructureManager.PROJECT_TDQ_KEY) == null) {
				folder.setPersistentProperty(
						DQStructureManager.PROJECT_TDQ_KEY,
						DQStructureManager.PROJECT_TDQ_PROPERTY);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getOrder()
	 */
	public Date getOrder() {
		Calendar calender = Calendar.getInstance();
		calender.set(2009, 2, 13);
		return calender.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#
	 * getMigrationTaskType()
	 */
	public MigrationTaskType getMigrationTaskType() {
		return MigrationTaskType.FILE;
	}
}
