// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.migration.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.talend.dataprofiler.migration.IMigrationTask;
import org.talend.dataprofiler.migration.IMigrationTask.MigrationTaskCategory;
import org.talend.dataprofiler.migration.IWorkspaceMigrationTask;
import org.talend.dataprofiler.migration.IWorkspaceMigrationTask.MigrationTaskType;
import org.talend.dataprofiler.migration.MigrationPlugin;
import org.talend.utils.ProductVersion;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class MigrationTaskManager {

    private static Logger log = Logger.getLogger(MigrationTaskManager.class);

    private List<IMigrationTask> allMigrationTask;

    private ProductVersion workspaceVersion;

    private ProductVersion currentVersion;

    private MigrationTaskType taskType;

    public MigrationTaskManager(ProductVersion workspaceVersion) {
        this(null, workspaceVersion, null, null);
    }

    public MigrationTaskManager(ProductVersion workspaceVersion, MigrationTaskType taskType) {
        this(null, workspaceVersion, null, taskType);
    }

    public MigrationTaskManager(ProductVersion workspaceVersion, ProductVersion currentVersion, MigrationTaskType taskType) {
        this(null, workspaceVersion, currentVersion, taskType);
    }

    public MigrationTaskManager(IMigrationTaskProvider taskProvider, ProductVersion workspaceVersion,
            ProductVersion currentVersion, MigrationTaskType taskType) {
        if (taskProvider == null) {
            taskProvider = new DefaultMigrationTaskProvider();
        }

        if (currentVersion == null) {
            this.currentVersion = MigrationPlugin.getDefault().getProductVersion();
        } else {
            this.currentVersion = currentVersion;
        }

        this.workspaceVersion = workspaceVersion;
        this.taskType = taskType;

        this.allMigrationTask = new ArrayList<IMigrationTask>();
        if (taskProvider != null) {
            allMigrationTask.addAll(Arrays.asList(taskProvider.getMigrationTasks()));
            sortTasks(allMigrationTask);
        }
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
    public List<IMigrationTask> getValidTasks() {

        if (taskType != null) {
            return getTaskByType(taskType);
        } else {
            return getWorksapceTasks();
        }
    }

    /**
     * DOC bZhou Comment method "getWorksapceTasks".
     * 
     * @param wVersion
     * 
     * @return
     */
    public List<IMigrationTask> getWorksapceTasks() {
        List<IMigrationTask> validTasks = getValidTasks(workspaceVersion, currentVersion, allMigrationTask);

        Iterator<IMigrationTask> it = validTasks.iterator();

        while (it.hasNext()) {
            IMigrationTask task = it.next();
            if (task.getMigrationTaskType() == MigrationTaskType.DATABASE) {
                it.remove();
            }
        }

        List<IMigrationTask> resortList = new ArrayList<IMigrationTask>();
        for (IMigrationTask task : validTasks) {
            if (task.isModelTask()) {
                resortList.add(task);
            }
        }

        for (IMigrationTask task : validTasks) {
            if (!task.isModelTask()) {
                resortList.add(task);
            }
        }

        return resortList;
    }

    /**
     * DOC bZhou Comment method "getTaskByType".
     * 
     * @param type
     * @param specifiedVersion
     * @return
     */
    public List<IMigrationTask> getTaskByType(MigrationTaskType type) {
        List<IMigrationTask> validTasks = new ArrayList<IMigrationTask>();

        for (IMigrationTask task : allMigrationTask) {
            if (task.getMigrationTaskType() == type) {
                validTasks.add(task);
            }
        }

        return getValidTasks(workspaceVersion, currentVersion, validTasks);
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
     * @param monitor
     */
    public void doMigrationTask(IProgressMonitor monitor) {
        doMigrationTask(getValidTasks(), monitor);
    }

    /**
     * DOC bZhou Comment method "doMigrationTask".
     * 
     * @param tasks
     * @param monitor
     */
    public static void doMigrationTask(List<IMigrationTask> tasks, IProgressMonitor monitor) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        monitor.beginTask("Migration...", tasks.size());

        for (IMigrationTask task : tasks) {

            if (monitor.isCanceled()) {
                break;
            }

            monitor.subTask(task.getName());

            if (task.valid()) {
                if (!task.execute()) {
                    log.error("Migration Task failed: " + task.getName());
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("Migration Task success: " + task.getId());
                    }
                }
            }

            monitor.worked(1);
        }

        monitor.done();
    }
}
