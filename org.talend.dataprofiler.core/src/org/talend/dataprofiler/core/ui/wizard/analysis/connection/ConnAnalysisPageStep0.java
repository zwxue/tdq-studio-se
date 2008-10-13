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
package org.talend.dataprofiler.core.ui.wizard.analysis.connection;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.dialog.filter.TypedViewerFilter;
import org.talend.dataprofiler.core.ui.dialog.provider.DBTablesViewLabelProvider;
import org.talend.dataprofiler.core.ui.views.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizardPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.provider.ConnectionsContentProvider;
import org.talend.dq.analysis.parameters.ConnectionAnalysisParameter;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * @author zqin
 * 
 */
public class ConnAnalysisPageStep0 extends AbstractAnalysisWizardPage {

    private TreeViewer fViewer;

    protected ILabelProvider fLabelProvider;

    protected ITreeContentProvider fContentProvider;

    /**
     * @param pageName
     */
    public ConnAnalysisPageStep0() {
        setTitle("New Analysis");
        setMessage("Choose a connection to analyze");
        setPageComplete(false);

        fLabelProvider = new DBTablesViewLabelProvider();
        fContentProvider = new ConnectionsContentProvider();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        Label nameLabel = new Label(container, SWT.NONE);
        nameLabel.setText("Connections:");

        createConnectionTree(container);
        addListeners();

        setControl(container);
    }

    protected void createConnectionTree(Composite parent) {

        Composite treeContainer = new Composite(parent, SWT.NONE);
        treeContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
        treeContainer.setLayout(new FillLayout());

        fViewer = new TreeViewer(treeContainer, SWT.BORDER);
        fViewer.setContentProvider(fContentProvider);
        fViewer.setLabelProvider(fLabelProvider);
        fViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
        fViewer.expandAll();

        addFilters();
    }

    @SuppressWarnings("unchecked")
    private void addFilters() {
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
                ".project"));
        ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());
        fViewer.addFilter(filter);
        fViewer.addFilter(new EMFObjFilter());
    }

    protected void addListeners() {
        fViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                // TODO Auto-generated method stub
                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (object instanceof IFile) {
                    IFile file = (IFile) object;
                    if (file.getParent() != null) {
                        advanceToNextPageOrFinish();
                    }
                }
            }

        });

        fViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {

                // get the dataprovider from the seleted connection
                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                ConnectionAnalysisParameter connPanameter = (ConnectionAnalysisParameter) getConnectionParams();
                if (object instanceof IFile) {
                    IFile file = (IFile) object;
                    TypedReturnCode<TdDataProvider> tdProvider = PrvResourceFileHelper.getInstance().findProvider(file);

                    if (tdProvider != null && connPanameter != null) {

                        connPanameter.setTdDataProvider(tdProvider.getObject());
                    }

                    setPageComplete(true);
                } else {
                    setPageComplete(false);
                }
            }

        });
    }

}
