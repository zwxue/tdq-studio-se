// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.preview.model.states;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.model.ICustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.entity.TableStructureEntity;
import org.talend.dq.indicators.IndicatorCommonUtil;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public abstract class AbstractChartTypeStates implements IChartTypeStates {

    protected List<IndicatorUnit> units = new ArrayList<IndicatorUnit>();

    public AbstractChartTypeStates(List<IndicatorUnit> units) {

        if (units != null) {
            this.units.addAll(check(units));
        }
    }

    /**
     * DOC bZhou Comment method "check".
     * 
     * @param units2
     * @return
     */
    private Collection<? extends IndicatorUnit> check(List<IndicatorUnit> parameterUnits) {
        List<IndicatorUnit> validUnitList = new ArrayList<IndicatorUnit>();

        for (IndicatorUnit unit : parameterUnits) {

            IndicatorEnum type = unit.getType();
            if (type != null && !unit.isExcuted()
                    && (type == IndicatorEnum.IQRIndicatorEnum || type == IndicatorEnum.RangeIndicatorEnum)) {
                IndicatorCommonUtil.getIndicatorValue(unit.getIndicator());
            }
            if (unit.isExcuted()) {
                validUnitList.add(unit);
            }
        }

        return validUnitList;
    }

    public List<JFreeChart> getChartList() {
        return null;
    }

    public ChartDataEntity[] getDataEntity() {
        ICustomerDataset customerDataset = getCustomerDataset();
        if (customerDataset != null) {
            return customerDataset.getDataEntities();
        }

        return null;
    }

    public CategoryDataset getDataset() {
        ICustomerDataset customerDataset = getCustomerDataset();
        if (customerDataset != null) {
            return (CategoryDataset) customerDataset;
        }

        return null;
    }

    public TableViewer getTableForm(Composite parent) {
        TableViewer tbViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);

        Table table = tbViewer.getTable();

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gd = new GridData();
        gd.heightHint = 253;
        gd.widthHint = 500;
        gd.verticalAlignment = SWT.BEGINNING;
        table.setLayoutData(gd);

        createTableColumnStructure(getTableStructure(), table);

        tbViewer.setLabelProvider(getLabelProvider());

        tbViewer.setContentProvider(getContentProvider());

        return tbViewer;
    }

    protected abstract TableStructureEntity getTableStructure();

    protected abstract ITableLabelProvider getLabelProvider();

    protected abstract IStructuredContentProvider getContentProvider();

    private void createTableColumnStructure(TableStructureEntity entity, Table table) {
        if (entity.isValid()) {
            for (int i = 0; i < entity.getColumnCount(); i++) {
                TableColumn column = new TableColumn(table, SWT.NONE);
                column.setText(entity.getFieldNames()[i]);
                column.setWidth(entity.getFieldWidths()[i]);
            }
        }
    }

    public XYDataset getXYDataset() {
        if (getCustomerXYDataset() != null) {
            return (XYDataset) getCustomerXYDataset();
        }

        return null;
    }

    public ICustomerDataset getCustomerXYDataset() {
        return null;
    }

    public PieDataset getPieDataset() {
        return null;
    }
}
