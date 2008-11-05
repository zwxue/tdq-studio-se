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
package org.talend.dataprofiler.core.ui.pref;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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

    private ListViewer listViewer;

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
            if (function != null && !"".equals(function)) {
                FunctionEntity entity = new FunctionEntity();
                entity.setFunction(function);
                entity.setLanguage(language);
                entityList.add(entity);
            }
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean performOk() {
        super.performOk();

        for (FunctionEntity entity : entityList) {
            ResourcesPlugin.getPlugin().getPluginPreferences().setValue(entity.getLanguage(), entity.getFunction());
        }
        DefinitionHandler.getInstance().updateRegex("MySQL", "'A','type'");
        // DefinitionHandler.getInstance().saveResource();

        return true;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label header = new Label(mainComposite, SWT.WRAP);
        header.setText(DefaultMessagesImpl.getString("UDFPreferencePage.headerText"));
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.widthHint = 500;
        header.setLayoutData(gd);

        Composite composite = new Composite(mainComposite, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        listViewer = new ListViewer(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(listViewer.getList());
        listViewer.setLabelProvider(new MyLabelProvider());
        listViewer.setContentProvider(new MyContentProvider());
        listViewer.setInput(entityList);

        Composite btnBox = new Composite(composite, SWT.NONE);
        btnBox.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(btnBox);

        addBTN = createPushButton(btnBox, "Add..");
        editBTN = createPushButton(btnBox, "Edit");
        removeBTN = createPushButton(btnBox, "Remove");

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
        NewUDFFunctionDialog dialog = new NewUDFFunctionDialog(getShell());
        if (Window.OK == dialog.open()) {
            entityList.add(dialog.getFunctionEntity());
            listViewer.setInput(entityList);
        }
    }

    private void openEditDialog() {

        StructuredSelection selection = (StructuredSelection) listViewer.getSelection();
        if (selection == null) {
            return;
        }

        FunctionEntity entity = (FunctionEntity) selection.getFirstElement();

        NewUDFFunctionDialog dialog = new NewUDFFunctionDialog(getShell(), entity);
        dialog.open();
        listViewer.refresh();
    }

    private void doRemove() {
        StructuredSelection selection = (StructuredSelection) listViewer.getSelection();
        if (selection.isEmpty()) {
            return;
        }

        FunctionEntity entity = (FunctionEntity) selection.getFirstElement();

        entityList.remove(entity);
        listViewer.setInput(entityList);

        ResourcesPlugin.getPlugin().getPluginPreferences().setValue(entity.getLanguage(), "");
    }

    /**
     * DOC Zqin UDFPreferencePage class global comment. Detailled comment
     */
    class MyLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {
            FunctionEntity entity = (FunctionEntity) element;
            return entity.getFunction();
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
