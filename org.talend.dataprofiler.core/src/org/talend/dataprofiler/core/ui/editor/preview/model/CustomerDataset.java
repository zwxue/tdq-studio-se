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
package org.talend.dataprofiler.core.ui.editor.preview.model;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class CustomerDataset implements IDataEntity {

    private List<ChartDataEntity> dataEnities;

    private CategoryDataset dataset;

    private Boolean valid;

    public CustomerDataset() {
        dataEnities = new ArrayList<ChartDataEntity>();
        dataset = new DefaultCategoryDataset();
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public void addDataEntity(ChartDataEntity dataEntity) {
        dataEnities.add(dataEntity);
    }

    public ChartDataEntity[] getDataEntities() {
        return dataEnities.toArray(new ChartDataEntity[dataEnities.size()]);
    }

    public CategoryDataset getDataset() {
        return dataset;
    }

    public void setDataset(CategoryDataset dataset) {
        this.dataset = dataset;
    }
}
