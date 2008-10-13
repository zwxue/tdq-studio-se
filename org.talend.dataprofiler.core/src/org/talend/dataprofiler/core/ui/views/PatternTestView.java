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
package org.talend.dataprofiler.core.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.part.ViewPart;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.views.layout.BorderLayout;

/**
 * DOC qwei class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */

public class PatternTestView extends ViewPart {

    private Combo dbCombo;

    private Text textArea, regularText;

    Button sqlButton, patternButton, saveButton, testButton;

    BorderLayout blayout;

    Composite butPane;

    List<TdDataProvider> listTdDataProviders = new ArrayList<TdDataProvider>();

    private static Logger log = Logger.getLogger(PatternTestView.class);

    @Override
    public void createPartControl(final Composite parent) {
        // TODO Auto-generated method stub

        BorderLayout blay = new BorderLayout();
        parent.setLayout(blay);
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(BorderLayout.NORTH);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.numColumns = 1;
        composite.setLayout(layout);

        Composite coboCom = new Composite(composite, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        coboCom.setLayout(layout);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.CENTER;
        coboCom.setLayoutData(data);
        Label dbLabel = new Label(coboCom, SWT.NONE);
        dbLabel.setText("DB Connections");
        dbCombo = new Combo(coboCom, SWT.DROP_DOWN);
        data = new GridData();
        data.widthHint = 80;
        data.heightHint = 100;
        dbCombo.setLayoutData(data);
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.METADATA).getFolder(
                DQStructureManager.DB_CONNECTIONS);
        listTdDataProviders = DqRepositoryViewService.listTdDataProviders(folder, true);
        List<String> items = new ArrayList<String>();

        for (TdDataProvider tdDataProvider : listTdDataProviders) {
            List<TdCatalog> tdCatalogs = CatalogHelper.getTdCatalogs(tdDataProvider.getDataPackage());
            items.add(tdDataProvider.getName());
        }
        if (!items.isEmpty()) {
            dbCombo.setItems(items.toArray(new String[0]));
            if (dbCombo.getText().equals("")) {
                dbCombo.setText(items.get(0));
            }
        }
        Composite imgCom = new Composite(composite, SWT.NONE);
        imgCom.setLayout(layout);
        data = new GridData();
        data.horizontalAlignment = GridData.END;
        imgCom.setLayoutData(data);
        Label img = new Label(imgCom, SWT.NONE);
        Image image = ImageLib.getImage(ImageLib.EXCLAMATION);
        img.setImage(image);

        Label textAreaLabel = new Label(composite, SWT.NONE);
        textAreaLabel.setText("Text Area");
        textArea = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = 40;
        textArea.setLayoutData(data);

        Label regularLabel = new Label(composite, SWT.NONE);
        regularLabel.setText("Regular Expression");
        regularText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        regularText.setLayoutData(data);

        BorderLayout blayout = new BorderLayout();

        Composite bottom = new Composite(parent, SWT.NONE);
        bottom.setLayout(blayout);
        bottom.setLayoutData(BorderLayout.CENTER);

        Composite centerPane = new Composite(bottom, SWT.NONE);
        centerPane.setLayoutData(BorderLayout.CENTER);
        final Composite rightPane = new Composite(bottom, SWT.NONE);
        rightPane.setLayoutData(BorderLayout.EAST);

        GridLayout llayout = new GridLayout();
        llayout.numColumns = 3;
        centerPane.setLayout(llayout);

        GridLayout rlayout = new GridLayout();
        rlayout.numColumns = 1;
        rightPane.setLayout(rlayout);

        data = new GridData();
        data.heightHint = 25;
        data.widthHint = 80;

        Button sqlButton = new Button(centerPane, SWT.PUSH);
        sqlButton.setText("SQL");
        sqlButton.setLayoutData(data);
        Button patternButton = new Button(centerPane, SWT.PUSH);
        patternButton.setText("Create Pattern");
        patternButton.setLayoutData(data);
        final Button saveButton = new Button(centerPane, SWT.PUSH);
        saveButton.setText("Save");
        saveButton.setLayoutData(data);

        testButton = new Button(rightPane, SWT.PUSH);
        testButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                for (TdDataProvider tddataprovider : listTdDataProviders) {
                    if (tddataprovider.getName().equals(dbCombo.getText())) {
                        TreeSelectDialog dialog = new TreeSelectDialog(tddataprovider, testButton.getShell());
                        if (dialog.open() == Window.OK) {
                            Object[] objs = dialog.getResult();
                        }

                    }
                    MessageDialog.openWarning(new Shell(), "", "no database is selected!");
                }

            }

        });
        testButton.setText("Test");
        testButton.setLayoutData(data);
        initDBComboListener();
    }

    /**
     * 
     * DOC autumn PatternTestView class global comment. Detailled comment
     */
    class TreeSelectDialog extends SelectionDialog {

        TreeViewer treeViewer;

        List<Object> tdCatalogItemList = new ArrayList();

        String title;

        protected TreeSelectDialog(Shell parentShell) {

            super(parentShell);
        }

        public TreeSelectDialog(TdDataProvider tdDataProvider, Shell parentShell) {
            super(parentShell);
            title = tdDataProvider.getName();
            this.setTitle(title);
            tdCatalogItemList.addAll(CatalogHelper.getTdCatalogs(tdDataProvider.getDataPackage()));
        }

        private void createTreeViewer(Composite parent) {
            // treeViewer = new CheckboxTreeViewer(parent);
            treeViewer = new TreeViewer(parent, SWT.BORDER);
            treeViewer.setContentProvider(new TableViewContentProvider());
            treeViewer.setLabelProvider(new TableTreeLabelProvider());
            treeViewer.setInput(tdCatalogItemList);
            treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = (Composite) super.createDialogArea(parent);
            GridLayout layout = new GridLayout();
            layout.numColumns = 1;
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            createTreeViewer(composite);
            return composite;
        }

        /**
          * 
        */
        protected void initializeBounds() {
            this.getShell().setSize(400, 300);
        }

        /**
         * @since 3.4
         */
        protected boolean isResizable() {
            return false;
        }
    }

    /**
     * 
     * qwei SelectRepositoryContextDialog class global comment. Detailled comment
     */
    class TableViewContentProvider implements ITreeContentProvider {

        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof TdCatalog) {
                TdCatalog item = (TdCatalog) parentElement;

            }
            return null;
        }

        public Object getParent(Object element) {

            return element;
        }

        public boolean hasChildren(Object element) {
            return false;
        }

        public Object[] getElements(Object inputElement) {
            return ((List) inputElement).toArray();
        }

        public void dispose() {

        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        }

    }

    /**
     * 
     * qwei TableSelectionDialog class global comment. Detailled comment
     */
    class TableTreeLabelProvider implements ILabelProvider {

        public final Image getImage(Object element) {
            // obtain the base image by querying the element

            if (element instanceof TdCatalog) {
                return ImageLib.getImage(ImageLib.CATALOG);
            }
            return null;
        }

        public final String getText(Object element) {
            // query the element for its label
            String label = "";
            if (element instanceof TdCatalog) {
                TdCatalog tdCatalog = (TdCatalog) element;
                label = tdCatalog.getName();
            }
            return label;
        }

        public void addListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }

        public void dispose() {
            // TODO Auto-generated method stub

        }

        public boolean isLabelProperty(Object element, String property) {
            // TODO Auto-generated method stub
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }
    }

    /**
     * add DBCombo selectionListener DOC autumn Comment method "initDBComboLister".
     */
    private void initDBComboListener() {
        dbCombo.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                String strDBName = dbCombo.getText();
            }

        });
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }
}
