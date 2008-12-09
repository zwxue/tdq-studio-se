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
package org.talend.dataprofiler.core.ui.editor.preview.model.states;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jfree.data.category.CategoryDataset;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorCommonUtil;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.model.entity.TableStructureEntity;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public abstract class AbstractChartTypeStates implements IChartTypeStates {

    protected List<IndicatorUnit> units = null;

    public AbstractChartTypeStates(List<IndicatorUnit> units) {

        Iterator<IndicatorUnit> it = units.iterator();
        while (it.hasNext()) {
            IndicatorUnit unit = it.next();
            if (!unit.isExcuted()) {
                it.remove();
            } else {
                IndicatorCommonUtil.getIndicatorValue(unit);
            }
        }

        this.units = units;
    }

    public ChartDataEntity[] getDataEntity() {
        // TODO Auto-generated method stub
        if (getCustomerDataset() != null) {
            return getCustomerDataset().getDataEntities();
        }

        return null;
    }

    public CategoryDataset getDataset() {
        // TODO Auto-generated method stub
        if (getCustomerDataset() != null) {
            return (CategoryDataset) getCustomerDataset();
        }

        return null;
    }

    public TableViewer getTableForm(Composite parent) {
        TableViewer tbViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);

        Table table = tbViewer.getTable();

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gd = new GridData();
        gd.heightHint = 220;
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

}
