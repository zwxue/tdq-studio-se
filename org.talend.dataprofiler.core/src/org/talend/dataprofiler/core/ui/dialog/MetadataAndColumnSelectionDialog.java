// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.provider.DBTablesViewLabelProvider;
import org.talend.dataprofiler.core.ui.editor.analysis.TablesSelectionDialog;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider;
import org.talend.dataprofiler.core.ui.wizard.analysis.provider.ColumnContentProvider;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DFColumnRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class MetadataAndColumnSelectionDialog extends SelectionStatusDialog {

    private TreeViewer fViewer;

    protected ILabelProvider fLabelProvider;

    protected ITreeContentProvider fContentProvider;

    private static Logger log = Logger.getLogger(TablesSelectionDialog.class);

    private IStatus fCurrStatus = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "", null); //$NON-NLS-1$

    private ISelectionStatusValidator fValidator = null;
    /**
     * DOC yyin MetadataAndColumnSelectionDialog constructor comment.
     * @param parent
     */
    public MetadataAndColumnSelectionDialog(Shell parent, String title, List<IRepositoryNode> reposViewObjList) {
        super(parent);
        fLabelProvider = new DBTablesViewLabelProvider();
        fContentProvider = new ColumnContentProvider();
        
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        // LayoutUtil.doDefaultLayout(composite, new DialogField[] { fCategoryList }, true, 5, 5);
        // LayoutUtil.setHorizontalGrabbing(fCategoryList.getListControl(null));
        Dialog.applyDialogFont(container);

        setHelpAvailable(false);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        Label nameLabel = new Label(container, SWT.NONE);
        nameLabel.setText(""); //$NON-NLS-1$

        createMetaDataTree(container);

        createSelectionValidator();
        // TODO
        // super.setInitialSelections(reposViewObjList.toArray());

        addListeners();

        updateStatus(this.fCurrStatus);
        return container;
    }

    /**
     * add selection validator: when selected columns are not in the same table, return error
     * 
     */
    private void createSelectionValidator() {
        this.fValidator = new ISelectionStatusValidator() {

            public IStatus validate(Object[] selection) {

                IStatus status = Status.OK_STATUS;
                try {
                    List<ModelElement> modelElements = translateNodeIntoModelElement(selection);
                    if (allCheckedElements.size() > 0
                            && ModelElementHelper.isFromSameTable(modelElements)) {
                    // no code needed
                    }else {
                        status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
                            DefaultMessagesImpl.getString("MetadataAndColumnSelectionDialog.canNotFinish")); //$NON-NLS-1$
                    }
                } catch (Exception e) {
                    log.error(e, e);
                    status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
                            DefaultMessagesImpl.getString("MetadataAndColumnSelectionDialog.canNotFinish")); //$NON-NLS-1$

                }
                return status;
            }

        };
    }

    private List<ModelElement> translateNodeIntoModelElement(Object[] objs) {

        boolean isMdm = false;
        boolean isDelimitedFile = false;
        if (objs != null && objs.length != 0) {
            isMdm = RepNodeUtils.isMDM(objs[0]);
            isDelimitedFile = RepNodeUtils.isDelimitedFile(objs[0]);
            // TODO if (objs[0] instanceof DBTableRepNode || objs[0] instanceof DBViewRepNode) {
            // // when only one node selected and it is a table/view level node, should select all children of it
            //
            // }

            if (!(objs[0] instanceof DBColumnRepNode || isMdm || isDelimitedFile)) {
                return null;
            }
        }
        List<ModelElement> modelElementList = new ArrayList<ModelElement>();
        for (Object object : objs) {
            IRepositoryNode repObj = (IRepositoryNode) object;
            if (isMdm) {
                modelElementList.add(((MetadataXmlElementTypeRepositoryObject) repObj.getObject()).getModelElement());
            } else {// delimited file or database
                modelElementList.add(((MetadataColumnRepositoryObject) repObj.getObject()).getTdColumn());
            }
        }
        return modelElementList;
    }
    List<Object> allCheckedElements = new ArrayList<Object>();

    protected void addListener(ISelectionChangedListener selectionChangedListener) {
        fViewer.addSelectionChangedListener(selectionChangedListener);
    }

    protected void addListeners() {

        addListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                try {
                    Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                    allCheckedElements.clear();
                    if (object instanceof DBColumnRepNode || object instanceof DFColumnRepNode
                            || object instanceof MDMXmlElementRepNode) {
                        @SuppressWarnings("unchecked")
                        List<IRepositoryNode> list = ((IStructuredSelection) event.getSelection()).toList();
                        allCheckedElements.addAll(list);

                        updateOKStatus();
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        });
    }

    @Override
    public Object[] getResult() {
        return allCheckedElements.toArray();
    }

    private void createMetaDataTree(Composite parent) {
        Composite treeContainer = new Composite(parent, SWT.NONE);
        int style =  SWT.BORDER | SWT.MULTI ;

        fViewer = new TreeViewer(treeContainer, style);
        fViewer.setContentProvider(fContentProvider);
        fViewer.setLabelProvider(fLabelProvider);
        fViewer.setInput(ResourceManager.getMetadataFolder());
        ((ResourceViewContentProvider) fContentProvider).setTreeViewer(fViewer);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = 600;
        data.heightHint = 500;
        treeContainer.setLayoutData(data);
        treeContainer.setLayout(new FillLayout());
    }

    protected void updateOKStatus() {
        if (fValidator != null) {
            fCurrStatus = fValidator.validate(this.allCheckedElements.toArray());
            updateStatus(fCurrStatus);
        } else if (!fCurrStatus.isOK()) {
            fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, "", //$NON-NLS-1$
                        null);
        } else {
            fCurrStatus = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, "", null);
        }
        updateStatus(fCurrStatus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
     */
    @Override
    protected void computeResult() {

    }

}
