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

import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class CustomerBoxDataset extends DefaultBoxAndWhiskerCategoryDataset implements IDataEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<ChartDataEntity> dataEnities;

    public CustomerBoxDataset() {
        dataEnities = new ArrayList<ChartDataEntity>();
    }

    public void addDataEntity(ChartDataEntity dataEntity) {
        dataEnities.add(dataEntity);
    }

    public ChartDataEntity[] getDataEntities() {
        return dataEnities.toArray(new ChartDataEntity[dataEnities.size()]);
    }

}
