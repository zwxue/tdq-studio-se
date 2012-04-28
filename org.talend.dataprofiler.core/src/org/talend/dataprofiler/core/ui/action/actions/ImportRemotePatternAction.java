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
package org.talend.dataprofiler.core.ui.action.actions;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.EEcosCategory;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.ImportFactory;
import org.talend.dataprofiler.core.ui.dialog.message.ImportInfoDialog;
import org.talend.dataprofiler.ecos.EcosConstants;
import org.talend.dataprofiler.ecos.jobs.ComponentDownloader;
import org.talend.dataprofiler.ecos.jobs.ComponentInstaller;
import org.talend.dataprofiler.ecos.jobs.DownloadListener;
import org.talend.dataprofiler.ecos.model.IEcosComponent;
import org.talend.dataprofiler.ecos.service.JobService;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ImportRemotePatternAction extends Action {

    private IEcosComponent[] components;

    private int fExtensionDownloaded;

    private List<IEcosComponent> fInstalledComponents;

    public ImportRemotePatternAction(IEcosComponent[] components) {
        super(DefaultMessagesImpl.getString("ImportRemotePatternAction.ImportDqRepository")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.IMPORT));

        this.components = components;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        setEnabled(false);
        try {
            Job job = new DownloadJob(components);
            fExtensionDownloaded = 0;
            fInstalledComponents = new ArrayList<IEcosComponent>();
            job.addJobChangeListener(new JobChangeAdapter() {

                @Override
                public void done(final IJobChangeEvent event) {

                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            updateUI(event);
                        }
                    });
                }
            });
            JobService.scheduleUserJob(job);

        } catch (Throwable e) {
            ExceptionHandler.process(e);
        }
    }

    private void updateUI(IJobChangeEvent event) {

        final List<ReturnCode> information = new ArrayList<ReturnCode>();

        setEnabled(true);
        if (fExtensionDownloaded > 0) {
            for (IEcosComponent componet : fInstalledComponents) {
                List<File> validFiles = extractFiles(componet, information);

                if (!validFiles.isEmpty()) {
                    String categoryName = componet.getCategry().getName();

                    EEcosCategory ecosCategory = EEcosCategory.getEcosCategory(categoryName);

                    if (ecosCategory != null) {
                        EResourceConstant resourceType = ecosCategory.getResource();
                        for (File oneFile : validFiles) {
                            information.addAll(ImportFactory.doInport(resourceType, oneFile, componet.getName()));
                        }
                    }
                }
            }

            // MOD qiongli 2011-11-28 TDQ-4038,give the message when there is nothing to import.
            if (information.isEmpty()) {
                information.add(new ReturnCode(DefaultMessagesImpl.getString("ImportRemotePatternAction.NothingImport"), false)); //$NON-NLS-1$
            }
            ImportInfoDialog
                    .openImportInformation(
                            null,
                            DefaultMessagesImpl.getString("ImportRemotePatternAction.ImportFinish"), (ReturnCode[]) information.toArray(new ReturnCode[0])); //$NON-NLS-1$

            CorePlugin.getDefault().refreshDQView();
        }
    }

    /**
     * DOC bZhou Comment method "extractFiles".
     * 
     * @param componet
     * @param information
     * @return
     */
    private List<File> extractFiles(IEcosComponent componet, List<ReturnCode> information) {
        List<File> files = new ArrayList<File>();

        try {
            String categoryName = componet.getCategry().getName();
            // MOD msjian 2012-2-22 TDQ-4603: change the unzip folder to system temp folder, else there is no svn info files for the old targetFolder
            // String targetFolder = ResourceManager.getExchangeFolder().getLocation().append(categoryName).toString();
            String targetFolder = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
            // TDQ-4603 ~
            File componentFileFolder = ComponentInstaller.unzip(componet.getInstalledLocation(), targetFolder);

            FilesUtils.getAllFilesFromFolder(componentFileFolder, files, new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return !FilesUtils.isSVNFolder(dir) && name.endsWith("csv"); //$NON-NLS-1$
                }
            });

            if (files.isEmpty()) {
                information.add(new ReturnCode("No valid exchange extension file(CSV) found in " + componet.getName(), false)); //$NON-NLS-1$
            }

        } catch (Exception e) {
            ExceptionHandler.process(e);
        }

        return files;
    }

    /**
     * Notify after download complete.
     * 
     * @param extension
     */
    void extensionDownloadCompleted(IEcosComponent extension) {
        fExtensionDownloaded++;
        fInstalledComponents.add(extension);
    }

    /**
     * DOC bZhou ImportRemotePatternAction class global comment. Detailled comment
     */
    class DownloadJob extends Job implements DownloadListener {

        private IProgressMonitor fMonitor = null;

        private String fProgressLabel;

        private int fBytesDownloaded;

        private IEcosComponent[] fExtensions;

        public DownloadJob(IEcosComponent[] extensions) {
            super(EcosConstants.DOWNLOAD_TASK_TITLE);
            fExtensions = extensions;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            SubMonitor progress = SubMonitor.convert(monitor, fExtensions.length * 10 + 5);
            progress.setTaskName(this.getName());
            for (IEcosComponent extension : fExtensions) {
                if (progress.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }
                fMonitor = progress.newChild(10);
                downloadExtension(extension, fMonitor);
            }
            progress.setTaskName(EcosConstants.RELOAD_PALETTE);
            // progress.done();
            return Status.OK_STATUS;
        }

        private void downloadExtension(IEcosComponent extension, IProgressMonitor monitor) {

            // get the latest revision url
            String componentUrl = extension.getLatestRevision().getUrl();
            monitor.setTaskName(EcosConstants.DOWNLOAD_TASK_NAME + componentUrl);
            String targetFolder = ResourceManager.getExchangeFolder().getLocation().toOSString();
            try {
                String fileName = extension.getLatestRevision().getFileName();
                // fileName = extension.getLatestRevision().getFileName();
                File localZipFile = new File(targetFolder, fileName);

                if (extension.getInstalledLocation() != null && extension.getInstalledRevision() != null) {
                    // if already install the latest revision, ignore
                    if (extension.getInstalledRevision().getName().equals(extension.getLatestRevision().getName())) {
                        if (localZipFile.exists() && checkIfExisted(extension.getInstalledLocation())) {
                            monitor.done();

                            extensionDownloadCompleted(extension);

                            return;
                        }
                    } else {
                        // before installing the new revision, delete the older
                        // revision that has been installed
                        FileUtils.deleteDirectory(new File(extension.getInstalledLocation()));
                        extension.setInstalledLocation(null);
                        extension.setInstalledRevision(null);
                    }
                }

                URL url = new URL(componentUrl);

                monitor.setTaskName(EcosConstants.DOWNLOAD_TASK_NAME + url.toString());
                ComponentDownloader downloader = new ComponentDownloader();
                downloader.addDownloadListener(this);
                // block until download complete
                downloader.download(url, localZipFile);

                // check if the job is cancelled
                if (!monitor.isCanceled()) {
                    // update extesion status
                    extension.setInstalledRevision(extension.getLatestRevision());
                    extension.setInstalledLocation(localZipFile.getAbsolutePath());
                    monitor.done();
                    extensionDownloadCompleted(extension);
                }

            } catch (Exception e) {
                ExceptionHandler.process(e);
            }
        }

        /**
         * Check if the component folder really exist, as the user may delete the folder from filesystem.
         * 
         * @param installedLocation
         * @return
         */
        private boolean checkIfExisted(String installedLocation) {
            try {
                File dir = new File(installedLocation);
                if (dir.exists()) {
                    return true;
                }
            } catch (Throwable e) {
                // do nothing;
            }
            return false;
        }

        public void downloadComplete() {
        }

        public void downloadProgress(ComponentDownloader downloader, int bytesRead) {
            if (fMonitor.isCanceled()) {
                // cancel download
                downloader.setCancel(true);
                return;
            }
            fBytesDownloaded += bytesRead;
            fMonitor.setTaskName(toKbFormat(fBytesDownloaded) + fProgressLabel);
            fMonitor.worked(bytesRead);
        }

        public void downloadStart(int totalSize) {
            fProgressLabel = "/" + toKbFormat(totalSize); //$NON-NLS-1$
            fBytesDownloaded = 0;
            fMonitor.beginTask("0 KB" + fProgressLabel, totalSize); //$NON-NLS-1$
        }

        private String toKbFormat(int size) {
            return String.format("%1$s KB", size >> 10); //$NON-NLS-1$
        }
    }
}
