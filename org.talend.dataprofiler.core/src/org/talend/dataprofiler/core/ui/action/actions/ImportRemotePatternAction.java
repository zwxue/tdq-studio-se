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
package org.talend.dataprofiler.core.ui.action.actions;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.ImportFactory;
import org.talend.dataprofiler.core.ui.dialog.message.ImportInfoDialog;
import org.talend.dataprofiler.ecos.EcosConstants;
import org.talend.dataprofiler.ecos.jobs.ComponentDownloader;
import org.talend.dataprofiler.ecos.jobs.ComponentInstaller;
import org.talend.dataprofiler.ecos.jobs.DownloadListener;
import org.talend.dataprofiler.ecos.model.IEcosComponent;
import org.talend.dataprofiler.ecos.service.JobService;
import org.talend.dataquality.domain.pattern.ExpressionType;
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
            // File componentFolder = EcosystemService.getComponentFolder();
            for (IEcosComponent componet : fInstalledComponents) {
                File file = new File(componet.getInstalledLocation());

                List<File> files = new ArrayList<File>();
                extractFiles(file, files);

                // MOD yyi 8746: strange behaviour for imported patterns!
                ExpressionType type = ExpressionType.REGEXP;
                IFolder folder = ResourceManager.getPatternFolder();

                if ("regex".equalsIgnoreCase(componet.getCategry().getName())) { //$NON-NLS-1$
                    type = ExpressionType.REGEXP;
                    folder = ResourceManager.getPatternRegexFolder();
                } else if ("pattern".equalsIgnoreCase(componet.getCategry().getName())) { //$NON-NLS-1$
                    type = ExpressionType.REGEXP;
                    folder = ResourceManager.getPatternRegexFolder();
                } else if ("SQL".equalsIgnoreCase(componet.getCategry().getName())) { //$NON-NLS-1$
                    // MOD yyi 8960: Patterns imported from Exchange/SQL can not be put in "Patterns/SQL" folder
                    type = ExpressionType.SQL_LIKE;
                    folder = ResourceManager.getPatternSQLFolder();
                } else if ("indicator".equalsIgnoreCase(componet.getCategry().getName())) { //$NON-NLS-1$
                    type = null;
                }

                for (File oneFile : files) {
                    if (type == null) {
                        IFolder udiFolder = ResourceManager.getUDIFolder();
                        information.addAll(ImportFactory.importIndicatorToStucture(oneFile, udiFolder, true, true));
                    } else {
                        information.addAll(ImportFactory.importToStucture(oneFile, folder, type, true, true));
                    }

                }
            }

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {

                    ImportInfoDialog
                            .openImportInformation(
                                    null,
                                    DefaultMessagesImpl.getString("ImportRemotePatternAction.ImportFinish"), (ReturnCode[]) information.toArray(new ReturnCode[0])); //$NON-NLS-1$
                }
            });

            CorePlugin.getDefault().refreshDQView();
        }
    }

    private void extractFiles(File parentFile, List<File> existedFiles) {

        if (parentFile.isDirectory()) {
            for (File file : parentFile.listFiles()) {
                if (file.isDirectory()) {
                    extractFiles(file, existedFiles);
                }

                existedFiles.add(file);
            }
        } else {
            existedFiles.add(parentFile);
        }
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
                    File componentFile = ComponentInstaller.unzip(localZipFile.getAbsolutePath(), targetFolder);
                    // update extesion status
                    extension.setInstalledRevision(extension.getLatestRevision());
                    extension.setInstalledLocation(componentFile.getAbsolutePath());
                    monitor.done();
                    extensionDownloadCompleted(extension);
                }
                // the component zip file
                // localZipFile.delete();
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
