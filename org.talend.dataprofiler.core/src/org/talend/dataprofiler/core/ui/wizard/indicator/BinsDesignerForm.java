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
package org.talend.dataprofiler.core.ui.wizard.indicator;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.model.ViewerDataFactory;
import org.talend.dataprofiler.core.model.nodes.indicator.option.SliceEntity;
import org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.utils.CheckValueUtils;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.BinsDesignerParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class BinsDesignerForm extends AbstractIndicatorForm {

    private Text minValue, maxValue, numbOfBins;

    private Button addSlice, delSlice;

    private Button isSetRange;

    private TableViewer tableViewer;

    protected BinsDesignerParameter parameter;

    public BinsDesignerForm(Composite parent, int style, AbstractIndicatorParameter parameter) {
        super(parent, style, parameter);

        this.parameter = (BinsDesignerParameter) parameter;
        this.setupForm();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#adaptFormToReadOnly()
     */
    @Override
    protected void adaptFormToReadOnly() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#addFields()
     */
    @Override
    protected void addFields() {
        this.setLayout(new GridLayout(2, false));

        GridData gdText = new GridData(GridData.FILL_HORIZONTAL);

        Label minValueText = new Label(this, SWT.NONE);
        minValueText.setText("minimal value");
        minValue = new Text(this, SWT.BORDER);
        minValue.setLayoutData(gdText);

        Label maxValueText = new Label(this, SWT.NONE);
        maxValueText.setText("maximal value");
        maxValue = new Text(this, SWT.BORDER);
        maxValue.setLayoutData(gdText);

        Label numOfBinsText = new Label(this, SWT.NONE);
        numOfBinsText.setText("number of bins");

        GridData gdTxt = new GridData();
        gdTxt.widthHint = 50;
        numbOfBins = new Text(this, SWT.BORDER);
        numbOfBins.setLayoutData(gdTxt);

        Composite rangeComp = new Composite(this, SWT.NONE);
        rangeComp.setLayout(new GridLayout());
        GridData gdComp = new GridData(GridData.FILL_BOTH);
        gdComp.horizontalSpan = 2;
        rangeComp.setLayoutData(gdComp);

        isSetRange = new Button(rangeComp, SWT.CHECK);
        isSetRange.setText("Set ranges manually");

        tableViewer = new TableViewer(rangeComp, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        tableViewer.setLabelProvider(new BinsLableProvider());
        tableViewer.setContentProvider(new BinsContentProvider());

        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        GridData tableData = new GridData(GridData.FILL_HORIZONTAL);
        tableData.heightHint = 80;
        table.setLayoutData(tableData);

        TableColumn column1 = new TableColumn(table, SWT.NONE);
        column1.setText("Low");
        column1.setWidth(150);
        column1.setAlignment(SWT.CENTER);
        TableColumn column2 = new TableColumn(table, SWT.NONE);
        column2.setWidth(150);
        column2.setText("Data");
        column2.setAlignment(SWT.CENTER);
        TableColumn column3 = new TableColumn(table, SWT.NONE);
        column3.setWidth(150);
        column3.setText("High");
        column3.setAlignment(SWT.CENTER);

        tableViewer.setColumnProperties(new String[] { "low", "data", "high" });

        CellEditor[] cellEditor = new CellEditor[3];
        cellEditor[0] = new TextCellEditor(table);
        cellEditor[1] = null;
        cellEditor[2] = new TextCellEditor(table);

        tableViewer.setCellEditors(cellEditor);
        tableViewer.setCellModifier(new SliceCellModifier());

        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        Composite operationBTNComp = new Composite(rangeComp, SWT.NONE);
        operationBTNComp.setLayout(new FillLayout());
        operationBTNComp.setLayoutData(gd);

        addSlice = new Button(operationBTNComp, SWT.NONE);
        addSlice.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addSlice.setEnabled(false);

        delSlice = new Button(operationBTNComp, SWT.NONE);
        delSlice.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delSlice.setEnabled(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#addFieldsListeners()
     */
    @Override
    protected void addFieldsListeners() {

        minValue.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String min = minValue.getText();

                if (min == "") {
                    updateStatus(IStatus.ERROR, MSG_EMPTY);
                } else if (!CheckValueUtils.isNumberValue(min)) {
                    updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                } else {
                    updateStatus(IStatus.OK, MSG_OK);
                    parameter.setMinValue(Double.valueOf(min));
                }
            }

        });

        maxValue.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String max = maxValue.getText();

                if (max == "") {
                    updateStatus(IStatus.ERROR, MSG_EMPTY);
                } else if (!CheckValueUtils.isNumberValue(max)) {
                    updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                } else {
                    updateStatus(IStatus.OK, MSG_OK);
                    parameter.setMaxValue(Double.valueOf(max));
                }
            }

        });

        numbOfBins.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                String numb = numbOfBins.getText();

                if (numb == "") {
                    updateStatus(IStatus.ERROR, MSG_EMPTY);
                } else if (!CheckValueUtils.isNumberValue(numb)) {
                    updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                } else {
                    updateStatus(IStatus.OK, MSG_OK);
                    parameter.setNumOfBins(Integer.parseInt(numb));
                }
            }

        });

        isSetRange.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                boolean flag = ((Button) e.getSource()).getSelection();
                double numb = Double.parseDouble(numbOfBins.getText());
                double min = Double.parseDouble(minValue.getText());
                double max = Double.parseDouble(maxValue.getText());

                if (flag && numb > 0) {
                    addSlice.setEnabled(true);
                    delSlice.setEnabled(true);

                    minValue.setEnabled(false);
                    maxValue.setEnabled(false);
                    numbOfBins.setEnabled(false);

                    tableViewer.setInput(ViewerDataFactory.createSliceFormData(min, max, numb));

                    parameter.setBinsData(tableViewer.getInput());
                } else {
                    addSlice.setEnabled(false);
                    delSlice.setEnabled(false);

                    minValue.setEnabled(true);
                    maxValue.setEnabled(true);
                    numbOfBins.setEnabled(true);

                    tableViewer.setInput("");
                }
            }

        });

        addSlice.addSelectionListener(new SelectionAdapter() {

            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (tableViewer.getInput() instanceof List) {
                    List<SliceEntity> inputList = (List<SliceEntity>) tableViewer.getInput();

                    SliceEntity newEntity = new SliceEntity();
                    inputList.add(newEntity);

                    tableViewer.setInput(inputList);
                }
            }

        });

        delSlice.addSelectionListener(new SelectionAdapter() {

            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean flag = !tableViewer.getSelection().isEmpty();

                if (flag) {
                    IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
                    SliceEntity entity = (SliceEntity) selection.getFirstElement();
                    if (tableViewer.getInput() instanceof List) {
                        List<SliceEntity> inputList = (List<SliceEntity>) tableViewer.getInput();
                        inputList.remove(entity);

                        tableViewer.setInput(inputList);
                    }
                }
            }

        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#addUtilsButtonListeners()
     */
    @Override
    protected void addUtilsButtonListeners() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#initialize()
     */
    @Override
    protected void initialize() {

        minValue.setText(String.valueOf(parameter.getMinValue()));
        maxValue.setText(String.valueOf(parameter.getMaxValue()));
        numbOfBins.setText(String.valueOf(parameter.getNumOfBins()));

        if (parameter.getBinsDataFromExsitingDomain().size() > 0) {
            addSlice.setEnabled(true);
            delSlice.setEnabled(true);

            minValue.setEnabled(false);
            maxValue.setEnabled(false);
            numbOfBins.setEnabled(false);
            isSetRange.setSelection(true);

            tableViewer.setInput(parameter.getBinsDataFromExsitingDomain());
        }
    }

    /**
     * DOC zqin BinsDesignerForm class global comment. Detailled comment
     */
    class BinsLableProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {

            SliceEntity entity = (SliceEntity) element;
            switch (columnIndex) {
            case 0:
                return entity.getLowValue();
            case 1:
                return PluginConstant.LESS_OR_EQUAL + " value <";
            case 2:
                return entity.getHighValue();
            default:
                return "";
            }
        }

    }

    /**
     * DOC zqin BinsDesignerForm class global comment. Detailled comment
     */
    class BinsContentProvider implements IStructuredContentProvider {

        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                return ((List<SliceEntity>) inputElement).toArray();
            }

            return new Object[0];
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * DOC zqin BinsDesignerForm class global comment. Detailled comment
     */
    class SliceCellModifier implements ICellModifier {

        public boolean canModify(Object element, String property) {
            if (property.equals("data")) {
                return false;
            }

            return true;
        }

        public Object getValue(Object element, String property) {
            SliceEntity entity = (SliceEntity) element;

            if (property.equals("low")) {
                return entity.getLowValue();
            } else if (property.equals("high")) {
                return entity.getHighValue();
            }

            return "";
        }

        public void modify(Object element, String property, Object value) {
            TableItem item = (TableItem) element;
            SliceEntity entity = (SliceEntity) item.getData();

            if (property.equals("low")) {
                entity.setLowValue(value.toString());

                parameter.setBinsData(tableViewer.getInput());
            } else if (property.equals("high")) {
                entity.setHighValue(value.toString());

                parameter.setBinsData(tableViewer.getInput());
            }

            tableViewer.refresh();
        }
    }

    @Override
    public FormEnum getFormEnum() {
        return FormEnum.BinsDesignerForm;
    }

    @Override
    protected boolean checkFieldsValue() {
        // TODO Auto-generated method stub
        return false;
    }

}
