// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.views.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.ui.part.ResourceTransfer;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.helper.RepResourceFileHelper;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ResourceDropAdapterAssistant extends CommonDropAdapterAssistant {

    private static final IResource[] NO_RESOURCES = new IResource[0];

    private boolean dropRep = false;

    private boolean dropSql = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.CommonDropAdapterAssistant#handleDrop (org.eclipse.ui.navigator.CommonDropAdapter,
     * org.eclipse.swt.dnd.DropTargetEvent, java.lang.Object)
     */
    @Override
    public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object target) {
        // alwaysOverwrite = false;
        if (aDropAdapter.getCurrentTarget() == null || aDropTargetEvent.data == null) {
            return Status.CANCEL_STATUS;
        }
        // IStatus status = null;
        IResource[] resources = null;
        TransferData currentTransfer = aDropAdapter.getCurrentTransfer();
        if (LocalSelectionTransfer.getTransfer().isSupportedType(currentTransfer)) {
            resources = getSelectedResources();
        } else if (ResourceTransfer.getInstance().isSupportedType(currentTransfer)) {
            resources = (IResource[]) aDropTargetEvent.data;
        }

        if ((target instanceof IFile) && dropRep) {
            TdReport findReport = RepResourceFileHelper.getInstance().findReport(((IFile) target));
            if (resources != null && resources.length > 0) {
                List<Analysis> anaList = new ArrayList<Analysis>();
                for (IResource res : resources) {
                    Analysis findAnalysis = AnaResourceFileHelper.getInstance().findAnalysis((IFile) res);
                    if (findAnalysis != null) {
                        anaList.add(findAnalysis);
                    }
                }
                ReportHelper.addAnalyses(anaList, findReport);
                RepResourceFileHelper.getInstance().save(findReport);
            }
        } else if ((target instanceof IFolder) && dropSql) {
            IFolder folder = (IFolder) target;
            IPath location = folder.getLocation();
            for (IResource res : resources) {
                String name = res.getName();
                new File(res.getLocation().toPortableString()).renameTo(new File(location.append(name).toPortableString()));
            }
        }
        CorePlugin.getDefault().refreshWorkSpace();
        ((DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID)).getCommonViewer().refresh();
        return null;
    }

    /**
     * Returns the resource selection from the LocalSelectionTransfer.
     * 
     * @return the resource selection from the LocalSelectionTransfer
     */
    private IResource[] getSelectedResources() {

        ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
        if (selection instanceof IStructuredSelection) {
            return getSelectedResources((IStructuredSelection) selection);
        }
        return NO_RESOURCES;
    }

    /**
     * Returns the resource selection from the LocalSelectionTransfer.
     * 
     * @return the resource selection from the LocalSelectionTransfer
     */
    @SuppressWarnings("unchecked")
    private IResource[] getSelectedResources(IStructuredSelection selection) {
        ArrayList selectedResources = new ArrayList();

        for (Iterator i = selection.iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof IResource) {
                selectedResources.add(o);
            } else if (o instanceof IAdaptable) {
                IAdaptable a = (IAdaptable) o;
                IResource r = (IResource) a.getAdapter(IResource.class);
                if (r != null) {
                    selectedResources.add(r);
                }
            }
        }
        return (IResource[]) selectedResources.toArray(new IResource[selectedResources.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.CommonDropAdapterAssistant#validateDrop(java.lang.Object, int,
     * org.eclipse.swt.dnd.TransferData)
     */
    @Override
    public IStatus validateDrop(Object target, int operation, TransferData transferType) {
        for (IResource res : getSelectedResources()) {
            if (res instanceof IFile) {
                IFile file = (IFile) res;
                if (file.getFileExtension().equals(FactoriesUtil.SQL)) {
                    if (target instanceof IFolder) {
                        IFolder folder = (IFolder) target;
                        IPath path = new Path(DQStructureManager.LIBRARIES);
                        path = path.append(DQStructureManager.SOURCE_FILES);
                        IPath fullPath = folder.getFullPath();
                        if (path.isPrefixOf(fullPath)) {
                            dropSql = true;
                            return Status.OK_STATUS;
                        }
                    }
                }
                if (res.getName().endsWith(PluginConstant.ANA_SUFFIX)) {
                    if (target instanceof IFile) {
                        IFile tfile = (IFile) target;
                        if (tfile.getFileExtension().equals(FactoriesUtil.REP)) {
                            dropRep = true;
                            return Status.OK_STATUS;
                        }
                    }
                }
            }
        }
        return Status.CANCEL_STATUS;
    }
}
