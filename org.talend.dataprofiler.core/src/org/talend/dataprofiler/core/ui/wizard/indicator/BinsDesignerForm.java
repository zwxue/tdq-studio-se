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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.model.ViewerDataFactory;
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

        Button isSetRange = new Button(rangeComp, SWT.CHECK);
        isSetRange.setText("Set ranges manually");

        TableViewer tableViewer = new TableViewer(rangeComp, SWT.BORDER);
        tableViewer.setLabelProvider(new BinsLableProvider());
        tableViewer.setContentProvider(new BinsContentProvider());

        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

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

        tableViewer.setInput(ViewerDataFactory.createBinsFormData());
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

                checkFieldsValue();
                if (isStatusOnValid()) {
                    parameter.setMinValue(Double.valueOf(minValue.getText()));
                }
            }

        });

        maxValue.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                checkFieldsValue();
                if (isStatusOnValid()) {
                    parameter.setMaxValue(Double.valueOf(maxValue.getText()));
                }
            }

        });

        numbOfBins.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                checkFieldsValue();
                if (isStatusOnValid()) {
                    parameter.setNumOfBins(Integer.parseInt(numbOfBins.getText()));
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
     * @see org.talend.dataprofiler.core.ui.utils.AbstractForm#checkFieldsValue()
     */
    @Override
    protected boolean checkFieldsValue() {
        if (minValue.getText() == "" || maxValue.getText() == "" || numbOfBins.getText() == "") {
            updateStatus(IStatus.ERROR, MSG_EMPTY);
            return false;
        }

        if (!CheckValueUtils.isNumberValue(minValue.getText()) || !CheckValueUtils.isNumberValue(maxValue.getText())
                || !CheckValueUtils.isNumberValue(numbOfBins.getText())) {
            updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
            return false;
        }

        updateStatus(IStatus.OK, MSG_OK);
        return true;
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
    }

    /**
     * DOC zqin BinsDesignerForm class global comment. Detailled comment
     */
    class BinsLableProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {

            return null;
        }

    }

    /**
     * DOC zqin BinsDesignerForm class global comment. Detailled comment
     */
    class BinsContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            return null;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    @Override
    public FormEnum getFormEnum() {
        return FormEnum.BinsDesignerForm;
    }

}
