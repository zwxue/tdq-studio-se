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
package org.talend.dataprofiler.core.migration;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.migration.IMigrationTask.MigrationTaskCategory;
import org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask.MigrationTaskType;
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

    static List<IMigrationTask> allTasks = null;

    static {

        allTasks = new ArrayList<IMigrationTask>();

        IConfigurationElement[] elems = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
        for (IConfigurationElement elem : elems) {
            try {
                String taskName = elem.getAttribute(ATTR_NAME);
                String taskID = elem.getAttribute(ATTR_PID);

                IMigrationTask task = (IMigrationTask) elem.createExecutableExtension(ATTR_CLASS);
                task.setName(taskName);
                task.setId(taskID);

                if (task.getTaskCategory() == MigrationTaskCategory.WORKSPACE) {
                    String version = elem.getAttribute(ATTR_VERSION);
                    ((IWorkspaceMigrationTask) task).setVersion(version);
                }

                allTasks.add(task);
            } catch (CoreException e) {
                log.error(e, e);
            }
        }

        sortTasks(allTasks);
    }

    private MigrationTaskManager() {
    }

    /**
     * DOC bZhou Comment method "getValidTasks".
     * 
     * @param workspaceVersion
     * @param currentVersion
     * @param tasks
     * @return
     */
    public static List<IMigrationTask> getValidTasks(ProductVersion workspaceVersion, ProductVersion currentVersion,
            List<IMigrationTask> tasks) {

        List<IMigrationTask> validTasks = new ArrayList<IMigrationTask>();

        for (IMigrationTask task : tasks) {

            if (task.getTaskCategory() == MigrationTaskCategory.WORKSPACE) {
                IWorkspaceMigrationTask wTask = (IWorkspaceMigrationTask) task;
                ProductVersion taskVersion = ProductVersion.fromString(wTask.getVersion());
                if (taskVersion.compareTo(workspaceVersion) > 0 && taskVersion.compareTo(currentVersion) <= 0) {
                    validTasks.add(task);
                }
            }

            if (task.getTaskCategory() == MigrationTaskCategory.PROJECT) {
                validTasks.add(task);
            }
        }

        return validTasks;
    }

    /**
     * DOC bZhou Comment method "getValidTasks".
     * 
     * @return
     */
    public static List<IMigrationTask> getValidTasks() {
        ProductVersion wVersion = WorkspaceVersionHelper.getVesion();
        ProductVersion cVersion = CorePlugin.getDefault().getProductVersion();
        return getValidTasks(wVersion, cVersion, allTasks);
    }

    /**
     * DOC bZhou Comment method "findValidMigrationTasks".
     * 
     * @return
     */
    public static List<IMigrationTask> findWorksapceTasks() {
        List<IMigrationTask> validTasks = getValidTasks();

        Iterator<IMigrationTask> it = validTasks.iterator();

        while (it.hasNext()) {
            IMigrationTask task = it.next();
            if (task.getMigrationTaskType() == MigrationTaskType.DATABASE) {
                it.remove();
            }
        }

        return validTasks;
    }

    /**
     * DOC bZhou Comment method "findTasksByCategory".
     * 
     * @param category
     * @return
     */
    public static List<IMigrationTask> findTasksByCategory(MigrationTaskCategory category) {
        List<IMigrationTask> validTasks = new ArrayList<IMigrationTask>();

        for (IMigrationTask task : allTasks) {
            if (task.getTaskCategory() == category) {
                validTasks.add(task);
            }
        }

        return validTasks;
    }

    /**
     * DOC bZhou Comment method "findMigrationTaskByPID".
     * 
     * @param pid
     * @return
     */
    public static IMigrationTask findMigrationTaskByPID(String pid) {
        for (IMigrationTask task : allTasks) {
            if (pid != null && pid.equals(task.getId())) {
                return task;
            }
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "findWorkspaceTaskByType".
     * 
     * @param type
     * @param specifiedVersion
     * @return
     */
    public static List<IMigrationTask> findWorkspaceTaskByType(MigrationTaskType type, ProductVersion specifiedVersion) {
        List<IMigrationTask> validTasks = new ArrayList<IMigrationTask>();

        for (IMigrationTask task : allTasks) {
            if (task.getMigrationTaskType() == type) {
                validTasks.add(task);
            }
        }

        ProductVersion currentVersion = CorePlugin.getDefault().getProductVersion();
        return getValidTasks(specifiedVersion, currentVersion, validTasks);
    }

    /**
     * DOC bZhou Comment method "sortTasks".
     * 
     * @param tasks
     */
    private static void sortTasks(List<IMigrationTask> tasks) {
        Collections.sort(tasks, new Comparator<IMigrationTask>() {

            public int compare(IMigrationTask o1, IMigrationTask o2) {
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
    public static void doMigrationTask(final List<IMigrationTask> tasks) {

        if (!tasks.isEmpty()) {
            IRunnableWithProgress op = new IRunnableWithProgress() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
                 */
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(DefaultMessagesImpl.getString("MigrationTaskManager.MigrationTaskJob"), tasks.size()); //$NON-NLS-1$

                    doMigrationTask(tasks, monitor);
                }

            };

            try {
                ProgressUI.popProgressDialog(op);
            } catch (InvocationTargetException e) {
                log.error(e, e);
            } catch (InterruptedException e) {
                log.error(e, e);
            }
        }
    }

    /**
     * DOC bZhou Comment method "doMigrationTask".
     * 
     * @param tasks
     * @param monitor
     */
    public static void doMigrationTask(List<IMigrationTask> tasks, IProgressMonitor monitor) {
        monitor.beginTask("Migration...", tasks.size());

        for (IMigrationTask task : tasks) {

            if (monitor.isCanceled()) {
                break;
            }

            monitor.subTask(task.getName());

            if (task.valid()) {
                if (!task.execute()) {
                    log.warn("Migration Task failed: " + task.getName());
                } else {
                    log.info("Migration Task success: " + task.getName());
                    System.out.println("Migration Task success: " + task.getName());
                }
            }

            monitor.worked(1);
        }

        monitor.done();
    }
}
