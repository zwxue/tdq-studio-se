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
package org.talend.dataprofiler.core.ui.editor.preview.model.dataset;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.ICustomerDataset;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class CustomerDefaultCategoryDataset extends DefaultCategoryDataset implements ICustomerDataset {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<ChartDataEntity> dataEnities;

    public CustomerDefaultCategoryDataset() {
        dataEnities = new ArrayList<ChartDataEntity>();
    }

    public void addDataEntity(ChartDataEntity dataEntity) {
        // TODO Auto-generated method stub
        dataEnities.add(dataEntity);
    }

    public void addDataEntity(ChartDataEntity[] dataEntity) {
        // TODO Auto-generated method stub
        for (ChartDataEntity data : dataEntity) {
            dataEnities.add(data);
        }
    }

    public ChartDataEntity[] getDataEntities() {
        // TODO Auto-generated method stub
        return dataEnities.toArray(new ChartDataEntity[dataEnities.size()]);
    }

}
