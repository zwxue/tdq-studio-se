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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.analysis.impl.AnalyzedDataSetImpl;
import org.talend.dataquality.indicators.Indicator;

/**
 * 
 * DOC zshen class global comment. Detailled comment
 * 
 * Display result for drill down operation
 */
public class DrillDownResultEditor extends EditorPart {

    private TableViewer tableView;

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
        tableView = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
        Table table = tableView.getTable();

        if (this.getEditorInput() instanceof DrillDownEditorInput) {
            DrillDownEditorInput ddEditorInput = (DrillDownEditorInput) this.getEditorInput();
            Analysis analysis = ddEditorInput.getAnalysis();
            Indicator indicator = ddEditorInput.getCurrIndicator();
            addTableColumn(ddEditorInput, table);
            table.setLinesVisible(true);
            table.setHeaderVisible(true);
            tableView.setLabelProvider(new DrillDownResultLabelProvider());
            tableView.setContentProvider(new DrillDownResultContentProvider());
            tableView.setInput(this.getEditorInput());
            for (TableColumn packColumn : table.getColumns()) {
                packColumn.pack();
            }
            AnalyzedDataSet anaDataSet = analysis.getResults().getIndicToRowMap().get(indicator);
        }
    }

    private void addTableColumn(DrillDownEditorInput ddEditorInput, Table table) {
        Indicator indicator = ddEditorInput.getCurrIndicator();
        String menuType = ddEditorInput.getMenuType();
        List<TdColumn> columnElementList = null;
        if (DrillDownEditorInput.judgeMenuType(menuType, DrillDownEditorInput.MENU_VALUE_TYPE)) {
            columnElementList = new ArrayList<TdColumn>();
            columnElementList.add((TdColumn) indicator.getAnalyzedElement());
        } else {
            columnElementList = TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(indicator.getAnalyzedElement()
                    .eContainer()));
        }

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
        private Indicator currIndicator;

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof DrillDownEditorInput) {
                DrillDownEditorInput ddEditorInput = ((DrillDownEditorInput) inputElement);
                currIndicator = ddEditorInput.getCurrIndicator();
                String menuType = ddEditorInput.getMenuType();
                List<Object[]> newColumnElementList = new ArrayList<Object[]>();
                AnalyzedDataSet analysisDataSet = ((DrillDownEditorInput) inputElement).getAnalysis().getResults()
                        .getIndicToRowMap().get(currIndicator);
                if (analysisDataSet.getData() != null && analysisDataSet.getData().size() > 0) {
                    newColumnElementList.addAll(analysisDataSet.getData());
                } else if (analysisDataSet.getFrequencyData() != null && analysisDataSet.getFrequencyData().size() > 0) {
                    String selectValue = ddEditorInput.getSelectValue();
                    newColumnElementList.addAll(analysisDataSet.getFrequencyData().get(selectValue));
                } else if (analysisDataSet.getPatternData() != null && analysisDataSet.getPatternData().size() > 0) {
                    if (DrillDownEditorInput.judgeMenuType(menuType, DrillDownEditorInput.MENU_INVALID_TYPE)) {
                        newColumnElementList.addAll((List<Object[]>) analysisDataSet.getPatternData().get(
                                AnalyzedDataSetImpl.INVALID_VALUE));
                    } else if (DrillDownEditorInput.judgeMenuType(menuType, DrillDownEditorInput.MENU_VALID_TYPE)) {
                        newColumnElementList.addAll((List<Object[]>) analysisDataSet.getPatternData().get(
                                AnalyzedDataSetImpl.VALID_VALUE));
                    }
                }
                if (analysisDataSet.getDataCount() < Double.parseDouble(ddEditorInput.getComputeValue())) {
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

}
