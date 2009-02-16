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
package org.talend.dataprofiler.core.migration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.utils.ProductVersion;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class MigrationTaskManager {

    public static final String EXTENSION_ID = "org.talend.dataprofiler.core.migrationtask"; //$NON-NLS-1$

    public static final String ATTR_CLASS = "class"; //$NON-NLS-1$

    public static final String ATTR_VERSION = "version"; //$NON-NLS-1$

    public static final String ATTR_PID = "pluginId"; //$NON-NLS-1$

    public static List<IWorkspaceMigrationTask> findValidMigrationTasks() {
        List<IWorkspaceMigrationTask> validTasks = new ArrayList<IWorkspaceMigrationTask>();

        IConfigurationElement[] elems = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
        ProductVersion workspaceVersion = WorkspaceVersionHelper.getVesion();
        ProductVersion currentVersion = CorePlugin.getDefault().getProductVersion();

        for (IConfigurationElement elem : elems) {
            String attribute = elem.getAttribute(ATTR_VERSION);
            ProductVersion taskVersion = ProductVersion.fromString(attribute);

            if (taskVersion.compareTo(workspaceVersion) > 0 && taskVersion.compareTo(currentVersion) <= 0) {
                try {
                    validTasks.add((IWorkspaceMigrationTask) elem.createExecutableExtension(ATTR_CLASS));
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }

        sortTasks(validTasks);

        return validTasks;
    }

    public static List<IWorkspaceMigrationTask> findAllMigrationTasks() {
        List<IWorkspaceMigrationTask> allTasks = new ArrayList<IWorkspaceMigrationTask>();

        IConfigurationElement[] elems = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
        for (IConfigurationElement elem : elems) {
            try {
                allTasks.add((IWorkspaceMigrationTask) elem.createExecutableExtension(ATTR_CLASS));
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }

        sortTasks(allTasks);

        return allTasks;
    }

    public static IWorkspaceMigrationTask findMigrationTaskFromPID(String pid) {
        IConfigurationElement[] elems = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
        for (IConfigurationElement elem : elems) {
            String attribute = elem.getAttribute(ATTR_PID);
            if (attribute.equals(pid)) {
                try {
                    return (IWorkspaceMigrationTask) elem.createExecutableExtension(ATTR_CLASS);
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private static void sortTasks(List<IWorkspaceMigrationTask> tasks) {
        Collections.sort(tasks, new Comparator<IWorkspaceMigrationTask>() {

            public int compare(IWorkspaceMigrationTask o1, IWorkspaceMigrationTask o2) {
                if (o1.getOrder() == null || o2.getOrder() == null) {
                    return 0;
                }

                return o1.getOrder().compareTo(o2.getOrder());
            }

        });
    }
}
