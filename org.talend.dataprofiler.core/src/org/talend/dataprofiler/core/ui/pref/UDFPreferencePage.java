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
package org.talend.dataprofiler.core.ui.pref;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.pref.ui.FunctionEntity;
import org.talend.dataprofiler.core.ui.pref.ui.NewUDFFunctionDialog;
import org.talend.dq.indicators.definitions.DefinitionHandler;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class UDFPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Button addBTN;

    private Button editBTN;

    private Button removeBTN;

    private TableViewer tableViewer;

    private SelectionListener selectionListener;

    private List<FunctionEntity> entityList;

    /**
     * DOC Zqin UDFPreferencePage constructor comment.
     */
    public UDFPreferencePage() {
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {

        entityList = new ArrayList<FunctionEntity>();

        for (String language : SupportDBUrlStore.getInstance().getDBLanguages()) {
            String function = ResourcesPlugin.getPlugin().getPluginPreferences().getString(language);
            if (function != null && !"".equals(function)) { //$NON-NLS-1$
                FunctionEntity entity = new FunctionEntity();
                entity.setFunction(function);
                entity.setLanguage(language);
                entityList.add(entity);
            }
        }
    }

    @Override
    public boolean performOk() {
        super.performOk();

        for (FunctionEntity entity : entityList) {
            ResourcesPlugin.getPlugin().getPluginPreferences().setValue(entity.getLanguage(), entity.getFunction());
            DefinitionHandler.getInstance().updateRegex(entity.getLanguage(), entity.getFunction());
        }

        DefinitionHandler.getInstance().saveResource();

        return true;
    }

    @Override
    protected void performDefaults() {

        for (FunctionEntity entity : entityList) {
            ResourcesPlugin.getPlugin().getPluginPreferences().setValue(entity.getLanguage(), ""); //$NON-NLS-1$
        }

        entityList.clear();
        tableViewer.refresh();

        DefinitionHandler.getInstance().restaureDefaultRegexDefinitions();
        DefinitionHandler.getInstance().saveResource();

        super.performDefaults();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label header = new Label(mainComposite, SWT.WRAP);
        header.setText(DefaultMessagesImpl.getString("UDFPreferencePage.headerText")); //$NON-NLS-1$
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.widthHint = 500;
        header.setLayoutData(gd);

        Composite composite = new Composite(mainComposite, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(table);

        TableColumn column1 = new TableColumn(table, SWT.NONE);
        column1.setText(DefaultMessagesImpl.getString("UDFPreferencePage.Function")); //$NON-NLS-1$
        column1.setWidth(450);
        TableColumn column2 = new TableColumn(table, SWT.NONE);
        column2.setText(DefaultMessagesImpl.getString("UDFPreferencePage.Language")); //$NON-NLS-1$
        column2.setWidth(150);

        tableViewer.setLabelProvider(new MyLabelProvider());
        tableViewer.setContentProvider(new MyContentProvider());
        tableViewer.setInput(entityList);

        Composite btnBox = new Composite(composite, SWT.NONE);
        btnBox.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(btnBox);

        addBTN = createPushButton(btnBox, DefaultMessagesImpl.getString("UDFPreferencePage.Add")); //$NON-NLS-1$
        editBTN = createPushButton(btnBox, DefaultMessagesImpl.getString("UDFPreferencePage.Edit")); //$NON-NLS-1$
        removeBTN = createPushButton(btnBox, DefaultMessagesImpl.getString("UDFPreferencePage.Remove")); //$NON-NLS-1$

        return mainComposite;
    }

    private Button createPushButton(Composite parent, String key) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(JFaceResources.getString(key));
        button.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = Math.max(IDialogConstants.BUTTON_WIDTH, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        button.addSelectionListener(getSelectionListener());
        return button;
    }

    private SelectionListener getSelectionListener() {
        if (selectionListener == null) {
            createSelectionListener();
        }
        return selectionListener;
    }

    /**
     * Creates a selection listener.
     */
    public void createSelectionListener() {
        selectionListener = new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                Widget widget = event.widget;

                if (widget == addBTN) {
                    openAddDialog();
                } else if (widget == editBTN) {
                    openEditDialog();
                } else if (widget == removeBTN) {
                    doRemove();
                }
            }
        };
    }

    private void openAddDialog() {
        NewUDFFunctionDialog dialog = new NewUDFFunctionDialog(getShell(), entityList);
        if (Window.OK == dialog.open()) {
            entityList.add(dialog.getFunctionEntity());
            tableViewer.setInput(entityList);
        }
    }

    private void openEditDialog() {

        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        if (selection.isEmpty()) {
            return;
        }

        FunctionEntity entity = (FunctionEntity) selection.getFirstElement();

        NewUDFFunctionDialog dialog = new NewUDFFunctionDialog(getShell(), entity, entityList);
        dialog.open();
        tableViewer.refresh();
    }

    private void doRemove() {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        if (selection.isEmpty()) {
            return;
        }

        FunctionEntity entity = (FunctionEntity) selection.getFirstElement();

        entityList.remove(entity);
        tableViewer.refresh();

        ResourcesPlugin.getPlugin().getPluginPreferences().setValue(entity.getLanguage(), ""); //$NON-NLS-1$
        
        //also need remove this item from .Talend.definite file.
        DefinitionHandler.getInstance().removeRegexFunction(entity.getLanguage());
        
    }

    /**
     * DOC Zqin UDFPreferencePage class global comment. Detailled comment
     */
    class MyLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {

            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            FunctionEntity entity = (FunctionEntity) element;
            if (columnIndex == 0) {
                return entity.getFunction();
            } else if (columnIndex == 1) {
                return entity.getLanguage();
            } else {
                return null;
            }
        }

    }

    /**
     * DOC Zqin UDFPreferencePage class global comment. Detailled comment
     */
    class MyContentProvider implements IStructuredContentProvider {

        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            List<FunctionEntity> list = (List) inputElement;
            return list.toArray();
        }

        public void dispose() {
            // TODO Auto-generated method stub

        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // TODO Auto-generated method stub

        }

    }

}
