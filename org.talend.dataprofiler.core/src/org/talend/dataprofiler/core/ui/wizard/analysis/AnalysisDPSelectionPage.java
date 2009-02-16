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
package org.talend.dataprofiler.core.ui.wizard.analysis;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.dialog.filter.TypedViewerFilter;
import org.talend.dataprofiler.core.ui.dialog.provider.DBTablesViewLabelProvider;
import org.talend.dataprofiler.core.ui.views.filters.EMFObjFilter;
import org.talend.dq.nodes.foldernode.IFolderNode;

/**
 * DOC mzhao class global comment. This class provide abstract methods for client to add different filter and listener.
 */
public abstract class AnalysisDPSelectionPage extends AbstractAnalysisWizardPage {

    private TreeViewer fViewer;

    protected ILabelProvider fLabelProvider;

    protected ITreeContentProvider fContentProvider;

    private String nameLabTxt = null;

    public AnalysisDPSelectionPage(String labText, AdapterFactoryContentProvider contentProvider) {
        init("", "", contentProvider, labText); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public AnalysisDPSelectionPage(String title, String message, String labText, AdapterFactoryContentProvider contentProvider) {
        init(title, message, contentProvider, labText);
    }

    private void init(String title, String message, AdapterFactoryContentProvider contentProvider, String labText) {
        setTitle(title); //$NON-NLS-1$
        setMessage(message); //$NON-NLS-1$
        setPageComplete(false);
        nameLabTxt = labText;
        fLabelProvider = new DBTablesViewLabelProvider();
        fContentProvider = contentProvider;
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        Label nameLabel = new Label(container, SWT.NONE);
        nameLabel.setText(nameLabTxt); //$NON-NLS-1$

        createMetaDataTree(container);
        setControl(container);
        addFilters();
        addListeners();
    }

    private void createMetaDataTree(Composite parent) {

        Composite treeContainer = new Composite(parent, SWT.NONE);
        treeContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
        treeContainer.setLayout(new FillLayout());

        fViewer = new TreeViewer(treeContainer, SWT.BORDER);
        fViewer.setContentProvider(fContentProvider);
        fViewer.setLabelProvider(fLabelProvider);
        fViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
        // fViewer.expandAll();
    }

    protected abstract void addListeners();

    @SuppressWarnings("unchecked")
    protected void addFilters() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        final Class[] acceptedClasses = new Class[] { IResource.class, IFolderNode.class, EObject.class, IFile.class };
        IProject[] allProjects = root.getProjects();
        ArrayList rejectedElements = new ArrayList(allProjects.length);
        for (int i = 0; i < allProjects.length; i++) {
            if (!allProjects[i].equals(ResourcesPlugin.getWorkspace().getRoot().getProject(PluginConstant.METADATA_PROJECTNAME))) {
                rejectedElements.add(allProjects[i]);
            }
        }
        rejectedElements.add(ResourcesPlugin.getWorkspace().getRoot().getProject(PluginConstant.METADATA_PROJECTNAME).getFile(
                ".project")); //$NON-NLS-1$
        ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());
        addFilter(filter);
    }

    protected void addFilter(ViewerFilter filter) {
        fViewer.addFilter(filter);
        fViewer.addFilter(new EMFObjFilter());
    }

    protected void addListener(IDoubleClickListener doubleClickListener) {
        fViewer.addDoubleClickListener(doubleClickListener);
    }

    protected void addListener(ISelectionChangedListener selectionChangedListener) {
        fViewer.addSelectionChangedListener(selectionChangedListener);
    }

}
