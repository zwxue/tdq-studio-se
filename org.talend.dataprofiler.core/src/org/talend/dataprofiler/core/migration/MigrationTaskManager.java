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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask.MigrationTaskType;
import org.talend.dataprofiler.core.migration.helper.DataBaseVersionHelper;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.utils.ProductVersion;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public final class MigrationTaskManager {

    protected static Logger log = Logger.getLogger(MigrationTaskManager.class);

    public static final String EXTENSION_ID = "org.talend.dataprofiler.core.migrationtask"; //$NON-NLS-1$

    public static final String ATTR_CLASS = "class"; //$NON-NLS-1$

    public static final String ATTR_VERSION = "version"; //$NON-NLS-1$

    public static final String ATTR_NAME = "name"; //$NON-NLS-1$

    public static final String ATTR_PID = "id"; //$NON-NLS-1$

    private MigrationTaskManager() {
    }

    /**
     * DOC bZhou Comment method "findValidMigrationTasks".
     * 
     * @return
     */
    public static List<IWorkspaceMigrationTask> findValidMigrationTasks() {
        List<IWorkspaceMigrationTask> validTasks = new ArrayList<IWorkspaceMigrationTask>();

        ProductVersion workspaceVersion = WorkspaceVersionHelper.getVesion();
        ProductVersion currentVersion = CorePlugin.getDefault().getProductVersion();

        List<IWorkspaceMigrationTask> allTasks = findAllMigrationTasks();
        for (IWorkspaceMigrationTask task : allTasks) {
            ProductVersion taskVersion = ProductVersion.fromString(task.getVersion());
            if (taskVersion.compareTo(workspaceVersion) > 0 && taskVersion.compareTo(currentVersion) <= 0) {
                validTasks.add(task);
            }
        }

        return validTasks;
    }

    /**
     * DOC bZhou Comment method "findAllMigrationTasks".
     * 
     * @return
     */
    public static List<IWorkspaceMigrationTask> findAllMigrationTasks() {
        List<IWorkspaceMigrationTask> allTasks = new ArrayList<IWorkspaceMigrationTask>();

        IConfigurationElement[] elems = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
        for (IConfigurationElement elem : elems) {
            try {
                String taskName = elem.getAttribute(ATTR_NAME);
                String taskID = elem.getAttribute(ATTR_PID);
                String version = elem.getAttribute(ATTR_VERSION);

                IWorkspaceMigrationTask migrationTask = (IWorkspaceMigrationTask) elem.createExecutableExtension(ATTR_CLASS);
                migrationTask.setName(taskName);
                migrationTask.setId(taskID);
                migrationTask.setVersion(version);

                allTasks.add(migrationTask);
            } catch (CoreException e) {
                e.printStackTrace();
                log.error(e, e);
            }
        }

        sortTasks(allTasks);

        return allTasks;
    }

    /**
     * DOC bZhou Comment method "findMigrationTaskByPID".
     * 
     * @param pid
     * @return
     */
    public static IWorkspaceMigrationTask findMigrationTaskByPID(String pid) {
        List<IWorkspaceMigrationTask> allTasks = findAllMigrationTasks();
        for (IWorkspaceMigrationTask task : allTasks) {
            if (pid != null && pid.equals(task.getId())) {
                return task;
            }
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "findMigrationTaskByType".
     * 
     * @param type
     * @return
     */
    public static List<IWorkspaceMigrationTask> findMigrationTaskByType(MigrationTaskType... types) {
        List<IWorkspaceMigrationTask> allTasks = findAllMigrationTasks();
        List<IWorkspaceMigrationTask> validTasks = new ArrayList<IWorkspaceMigrationTask>();

        for (IWorkspaceMigrationTask task : allTasks) {
            for (MigrationTaskType type : types) {
                if (task.getMigrationTaskType() == type) {
                    validTasks.add(task);
                }
            }
        }

        return validTasks;
    }

    /**
     * DOC bZhou Comment method "sortTasks".
     * 
     * @param tasks
     */
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

    /**
     * DOC bZhou Comment method "doMigrationTask".
     * 
     * @param tasks
     */
    public static void doMigrationTask(final List<IWorkspaceMigrationTask> tasks) {

        if (!tasks.isEmpty()) {
            IRunnableWithProgress op = new IRunnableWithProgress() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
                 */
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Migration Task Job", tasks.size());

                    for (IWorkspaceMigrationTask task : tasks) {

                        if (monitor.isCanceled()) {
                            break;
                        }

                        monitor.subTask(task.getName());

                        log.warn("now executing " + task.getName());

                        task.execute();

                        monitor.worked(1);
                    }

                    monitor.done();

                    WorkspaceVersionHelper.storeVersion();

                    DataBaseVersionHelper.storeVersion();
                }
            };

            try {
                ProgressUI.popProgressDialog(op);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
