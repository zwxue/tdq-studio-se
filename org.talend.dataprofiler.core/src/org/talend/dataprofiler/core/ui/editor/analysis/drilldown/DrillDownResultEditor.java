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
package org.talend.dataprofiler.core.ui.editor.analysis.drilldown;

import java.util.Hashtable;
import java.util.List;

import net.sourceforge.sqlexplorer.dataset.actions.ExportCSVAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.CorePlugin;

/**
 * 
 * DOC zshen class global comment. Detailled comment
 * 
 * Display result for drill down operation
 */
public class DrillDownResultEditor extends EditorPart {

    private TableViewer tableView;

    private ExportCSVAction exportAction;

    private Hashtable<String, Action> actionList;

    // public static final String[] ACTION_NAME = { "save as" , "export" };
    public static final String[] ACTION_NAME = { "export" };

    public DrillDownResultEditor() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        this.setSite(site);
        this.setInput(input);
        DrillDownEditorInput ddEditorInput = (DrillDownEditorInput) input;
        this.setPartName(ddEditorInput.getMenuType());
        this.setTitleToolTip(ddEditorInput.getToolTipText());

    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout();
        // layout.marginTop = 10;
        parent.setLayout(layout);
        makeAction();
        createCoolBar(parent);
        // createPopupMenu();
        tableView = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
        Table table = tableView.getTable();
        MenuManager popupMenu = new MenuManager();
        // IAction newRowAction = new NewRowAction();
        popupMenu.add(exportAction);
        Menu menu = popupMenu.createContextMenu(table);
        table.setMenu(menu);

        exportAction.setTable(table);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(table);
        if (this.getEditorInput() instanceof DrillDownEditorInput) {
            DrillDownEditorInput ddEditorInput = (DrillDownEditorInput) this.getEditorInput();
            // Analysis analysis = ddEditorInput.getAnalysis();
            // Indicator indicator = ddEditorInput.getCurrIndicator();
            addTableColumn(ddEditorInput, table);
            table.setLinesVisible(true);
            table.setHeaderVisible(true);
            table.setData(ddEditorInput.getDataSet());
            tableView.setLabelProvider(new DrillDownResultLabelProvider());
            tableView.setContentProvider(new DrillDownResultContentProvider());
            tableView.setInput(this.getEditorInput());

            for (TableColumn packColumn : table.getColumns()) {
                packColumn.pack();
            }
            // AnalyzedDataSet anaDataSet = analysis.getResults().getIndicToRowMap().get(indicator);
        }

    }

    // public void createPopuMenu() {
    // MenuManager manager = new MenuManager("mymenu", "mymenu");
    // manager.setRemoveAllWhenShown(true);
    // manager.addMenuListener(new IMenuListener() {
    //
    // public void menuAboutToShow(IMenuManager manager) {
    // manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    //
    // }
    //
    // });
    // Table table = tableView.getTable();
    // manager.createContextMenu(table);
    //
    // }

    private void createCoolBar(Composite parent) {
        CoolBar coolBar = new CoolBar(parent, SWT.HORIZONTAL | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(coolBar);
        actionList = new Hashtable<String, Action>();
        // actionList.put(ACTION_NAME[0], saveAction);
        // actionList.put(ACTION_NAME[1], exportAction);
        actionList.put(ACTION_NAME[0], exportAction);
        ToolBar toolBar = new ToolBar(coolBar, SWT.RIGHT | SWT.FLAT);
        toolBar.pack();

        for (int i = 0; i < ACTION_NAME.length; i++) {
            ToolItem item = new ToolItem(toolBar, SWT.PUSH);
            item.setImage(actionList.get(ACTION_NAME[i]).getImageDescriptor().createImage());
            // item.setText(actionList.get(ACTION_NAME[i]).getText());
            item.setToolTipText(actionList.get(ACTION_NAME[i]).getText());
            item.addSelectionListener(new ListenToTheAction(actionList.get(ACTION_NAME[i])));
            // item.setEnabled(false);

        }

        CoolItem coolEdit = new CoolItem(coolBar, SWT.HORIZONTAL | SWT.DROP_DOWN);
        coolEdit.setControl(toolBar);
        // coolEdit.addSelectionListener(new CoolItemSelectionListener());

        // ToolBar toolBar2 = new ToolBar(coolBar, SWT.RIGHT | SWT.FLAT);
        // ToolItem item = null;
        //
        // item = new ToolItem(toolBar2, SWT.PUSH);
        // item.setImage(guideAction.getImageDescriptor().createImage());
        // item.setText(guideAction.getText());
        // item.setToolTipText(guideAction.getText());
        // item.addSelectionListener(new ListenToTheAction(guideAction));
        // actionList.put(actionName[5], guideAction);
        //
        // CoolItem coolfun = new CoolItem(coolBar, SWT.HORIZONTAL | SWT.DROP_DOWN);
        // coolfun.setControl(toolBar2);
        // coolfun.addSelectionListener(new CoolItemSelectionListener());
        //
        // ToolBar toolBar3 = new ToolBar(coolBar, SWT.RIGHT | SWT.FLAT);
        //
        // item = new ToolItem(toolBar3, SWT.PUSH);
        // item.setImage(displayPackageTreeAction.getImageDescriptor().createImage());
        // item.setText(displayPackageTreeAction.getText());
        // item.setToolTipText(displayPackageTreeAction.getText());
        // item.addSelectionListener(new ListenToTheAction(displayPackageTreeAction));
        // actionList.put(actionName[6], displayPackageTreeAction);
        //
        // item = new ToolItem(toolBar3, SWT.PUSH);
        // item.setImage(displayDBViewerAction.getImageDescriptor().createImage());
        // item.setText(displayDBViewerAction.getText());
        // item.setToolTipText(displayDBViewerAction.getText());
        // item.addSelectionListener(new ListenToTheAction(displayDBViewerAction));
        // actionList.put(actionName[7], displayDBViewerAction);
        //
        // CoolItem cooldis = new CoolItem(coolBar, SWT.HORIZONTAL | SWT.DROP_DOWN);
        // cooldis.setControl(toolBar3);
        // cooldis.addSelectionListener(new CoolItemSelectionListener());

        CoolItem[] coolItems = coolBar.getItems();
        for (int i = 0; i < coolItems.length; i++) {
            CoolItem coolItem = coolItems[i];
            Control control = coolItem.getControl();
            Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            Point coolSize = coolItem.computeSize(size.x, size.y);
            if (control instanceof ToolBar) {
                ToolBar bar = (ToolBar) control;
                if (bar.getItemCount() > 0) {

                    size.x = bar.getItem(0).getWidth();

                }
            }
            coolItem.setMinimumSize(size);
            coolItem.setPreferredSize(coolSize);
            coolItem.setSize(coolSize);
        }
        Point p = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        toolBar.setSize(p);
        Point p2 = coolEdit.computeSize(p.x, p.y);
        coolEdit.setControl(toolBar);
        coolEdit.setSize(p2);
        // coolBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        coolBar.setLocked(true);
    }

    /*
     * init Action save as,export
     */
    private void makeAction() {

        // // save as
        // saveAction = new Action(ACTION_NAME[0]) {
        //
        // @Override
        // public void run() {
        //
        // MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        // SWT.YES
        // | SWT.NO);
        // messageBox.setText("Confirmation");
        // messageBox.setMessage("action test");
        // // int value =
        // messageBox.open();
        // // if (value == SWT.YES) {
        // //
        // // String sqls = generateAlterSQL();
        // // Class cls = input.getClass();
        // // Method med;
        // // try {
        // // med = cls.getMethod("openSQLEditorBySQL", String.class);
        // // med.invoke(input, sqls);
        // // } catch (Exception e) {
        // // System.out.println(e);
        // // }
        // // this.setEnabled(true);
        // // }
        // // fileDialog();
        // }
        //
        // };
        // saveAction.setImageDescriptor(Activator.getImageDescriptor("icons/save.gif"));
        // saveAction.setText("save as");
        // saveAction.setEnabled(true);
        // export
        exportAction = new ExportCSVAction();
        exportAction.setImageDescriptor(CorePlugin.getImageDescriptor("icons/export_wiz.gif"));
        exportAction.setEnabled(true);
    }

    private void addTableColumn(DrillDownEditorInput ddEditorInput, Table table) {
        // Indicator indicator = ddEditorInput.getCurrIndicator();
        // String menuType = ddEditorInput.getMenuType();
        // List<TdColumn> columnElementList = null;
        // if (DrillDownEditorInput.judgeMenuType(menuType, DrillDownEditorInput.MENU_VALUE_TYPE)) {
        // columnElementList = new ArrayList<TdColumn>();
        // columnElementList.add((TdColumn) indicator.getAnalyzedElement());
        // } else {
        // columnElementList = TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(indicator.getAnalyzedElement()
        // .eContainer()));
        // }

        List<TdColumn> columnElementList = ddEditorInput.filterAdaptColumnHeader();
        for (TdColumn columnElement : columnElementList) {
            TableColumn column = new TableColumn(table, SWT.CENTER);
            column.setText(columnElement.getName());
        }
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    /**
     * 
     * DOC zshen DrillDownResultEditor class global comment. Detailled comment
     * 
     */
    private class DrillDownResultLabelProvider implements ITableLabelProvider {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage(Object element, int columnIndex) {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object element, int columnIndex) {
            // TODO Auto-generated method stub

            Object object = ((Object[]) element)[columnIndex];
            if (object != null) {
                return object.toString();
            }
            return "(null)";
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        public void addListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
         */
        public void dispose() {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
         */
        public boolean isLabelProperty(Object element, String property) {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        public void removeListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }

    }

    /**
     * 
     * DOC zshen DrillDownResultEditor class global comment. Detailled comment
     */
    private class DrillDownResultContentProvider implements IStructuredContentProvider {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof DrillDownEditorInput) {
                DrillDownEditorInput ddEditorInput = ((DrillDownEditorInput) inputElement);
                List<Object[]> newColumnElementList = ddEditorInput.filterAdaptDataList();
                // currIndicator = ddEditorInput.getCurrIndicator();
                // String menuType = ddEditorInput.getMenuType();
                // List<Object[]> newColumnElementList = new ArrayList<Object[]>();
                // AnalyzedDataSet analysisDataSet = ((DrillDownEditorInput) inputElement).getAnalysis().getResults()
                // .getIndicToRowMap().get(currIndicator);
                // if (analysisDataSet.getData() != null && analysisDataSet.getData().size() > 0) {
                // newColumnElementList.addAll(analysisDataSet.getData());
                // } else if (analysisDataSet.getFrequencyData() != null && analysisDataSet.getFrequencyData().size() >
                // 0) {
                // String selectValue = ddEditorInput.getSelectValue();
                // newColumnElementList.addAll(analysisDataSet.getFrequencyData().get(selectValue));
                // } else if (analysisDataSet.getPatternData() != null && analysisDataSet.getPatternData().size() > 0) {
                // if (DrillDownEditorInput.judgeMenuType(menuType, DrillDownEditorInput.MENU_INVALID_TYPE)) {
                // newColumnElementList.addAll((List<Object[]>) analysisDataSet.getPatternData().get(
                // AnalyzedDataSetImpl.INVALID_VALUE));
                // } else if (DrillDownEditorInput.judgeMenuType(menuType, DrillDownEditorInput.MENU_VALID_TYPE)) {
                // newColumnElementList.addAll((List<Object[]>) analysisDataSet.getPatternData().get(
                // AnalyzedDataSetImpl.VALID_VALUE));
                // }
                // }
                if (ddEditorInput.isDataSpill()) {
                    Object[] leaveOutData = new Object[newColumnElementList.get(0).length];
                    for (int i = 0; i < newColumnElementList.get(0).length; i++) {
                        leaveOutData[i] = "...";
                    }
                    newColumnElementList.add(leaveOutData);
                }
                return newColumnElementList.toArray();
            }
            return new Object[0];
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
         * java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // TODO Auto-generated method stub
            viewer.getControl().pack();
        }

    }

    /**
     * 
     * DOC zshen DrillDownResultEditor class global comment. Detailled comment
     */
    private class ListenToTheAction extends SelectionAdapter {

        Action theAction;

        public ListenToTheAction(Action action) {
            super();
            this.theAction = action;
        }

        public void widgetSelected(SelectionEvent e) {
            this.theAction.run();
            // changeCoolBarState();

        }
    }

}
