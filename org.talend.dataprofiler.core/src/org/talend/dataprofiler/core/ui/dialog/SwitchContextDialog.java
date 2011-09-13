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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.core.model.context.ContextUtils;
import org.talend.core.model.properties.ContextItem;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.designer.core.model.utils.emf.talendfile.ContextParameterType;
import org.talend.dq.nodes.DBConnectionRepNode;

/**
 * DOC msjian  class global comment. Detailled comment
 */
public class SwitchContextDialog extends Dialog {

    private Object obj;

    private CCombo combo = null;

    private Tree tree = null;

    private String contextName = null;

    /**
     * DOC msjian SwitchContextDialog constructor comment.
     * 
     * @param parentShell
     */
    public SwitchContextDialog(Shell parentShell, Object obj) {
        super(parentShell);
        this.obj = obj;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        GridLayout layout = new GridLayout(1, false);

        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        createSelectComposite(container);

        createCompositeToDisplayContext(container);

        return container;
    }

    /**
     * DOC msjian Comment method "createSelectComposite".
     * 
     * @param container
     */
    private void createSelectComposite(Composite container) {
        Composite bgComposite = new Composite(container, SWT.NONE);
        bgComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        bgComposite.setLayout(new GridLayout(2, false));
        Label label = new Label(bgComposite, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("SwitchContextDialog.selectAndContext")); //$NON-NLS-1$

        combo = new CCombo(bgComposite, SWT.BORDER);
        combo.setEditable(false);
        combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        initContexts();

        combo.addSelectionListener(new SelectionAdapter() {
            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                setContextName(combo.getText().trim());
                updateContextDetail();
            }

        });
    }

    /**
     * DOC msjian Comment method "createCompositeToDisplayContext".
     */
    private void createCompositeToDisplayContext(Composite container) {
        Composite bgComposite = new Composite(container, SWT.NONE);
        bgComposite.setLayout(new GridLayout());
        bgComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        Label label = new Label(bgComposite, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("SwitchContextDialog.contextDetail")); //$NON-NLS-1$

        tree = new Tree(bgComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        TreeColumn column1 = new TreeColumn(tree, SWT.NONE);
        column1.setText(DefaultMessagesImpl.getString("SwitchContextDialog.name")); //$NON-NLS-1$
        column1.setWidth(120);

        TreeColumn column2 = new TreeColumn(tree, SWT.NONE);
        column2.setText(DefaultMessagesImpl.getString("SwitchContextDialog.value")); //$NON-NLS-1$
        column2.setWidth(120);

        updateContextDetail();
    }

    /**
     * DOC msjian Comment method "updateContextDetail".
     */
    private void updateContextDetail() {
        tree.removeAll();

        ContextItem objContextItem = ContextUtils.getContextItemByName(getContextName());

        @SuppressWarnings("unchecked")
        EList<ContextParameterType> params = ((org.talend.designer.core.model.utils.emf.talendfile.impl.ContextTypeImpl) (objContextItem
                .getContext().get(0))).getContextParameter();
        if (params != null) {
            for (ContextParameterType param : params) {
                TreeItem item = new TreeItem(tree, SWT.NONE);
                item.setText(new String[] { param.getName(), param.getValue() });
            }
        }
    }

    /**
     * DOC msjian Comment method "initContexts".
     */
    private void initContexts() {
        if (obj instanceof DBConnectionRepNode) {
            String objContextId = ((DBConnectionRepNode) obj).getDatabaseConnection().getContextId();
            ContextItem objContextItem = ContextUtils.getContextItemById2(objContextId);
            if (objContextItem != null) {
                combo.add(objContextItem.getProperty().getLabel());
                setContextName(objContextItem.getProperty().getLabel());
            }
        }

        // add all
        List<ContextItem> contextItems = ContextUtils.getAllContextItem();
        if (contextItems != null) {
            for (ContextItem context : contextItems) {
                if (!context.getProperty().getLabel().equals(this.contextName)) {
                    combo.add(context.getProperty().getLabel());
                }
            }
        }

        combo.select(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(DefaultMessagesImpl.getString("SwitchContextDialog.selectContext")); //$NON-NLS-1$
        newShell.setSize(new Point(500, 450));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "OK", true); //$NON-NLS-1$
        createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false); //$NON-NLS-1$
    }

    /**
     * Getter for contextName.
     * 
     * @return the contextName
     */
    public String getContextName() {
        return this.contextName;
    }

    /**
     * Sets the contextName.
     * 
     * @param contextName the contextName to set
     */
    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

}
