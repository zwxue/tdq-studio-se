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
package org.talend.dataprofiler.core.ui.editor.preview.model.states.utils;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.utils.format.StringFormatUtil;

/**
 * created by yyin on 2014-12-2 Detailled comment
 * 
 */
public class CommonStateUtil {

    public static String getUnitValue(Object unitValue, int style) {
        String defaultValue = String.valueOf(Double.NaN);
        if (style == StringFormatUtil.INT_NUMBER) {
            defaultValue = "0"; //$NON-NLS-1$
        }
        return unitValue != null ? StringFormatUtil.format(unitValue, style).toString() : defaultValue;
    }

    public static String getUnitValue(Object unitValue) {
        // TDQ-11643: msjian make the chart value format like "X.XX" the same to table values.
        return getUnitValue(unitValue, StringFormatUtil.DOUBLE_NUMBER);
    }

    /**
     * create the data entity
     * 
     * @param unit
     * @param value: String type: maybe Double or integer
     * @param label
     * @return
     */
    public static ChartDataEntity createDataEntity(IndicatorUnit unit, String value, String label) {
        ChartDataEntity entity = new ChartDataEntity();
        entity.setIndicator(unit.getIndicator());
        entity.setLabel(label);
        entity.setValue(value);
        entity.setPercent(Double.valueOf(value) / unit.getIndicator().getCount());
        return entity;
    }

    public static ChartDataEntity[] getDataEntity(List<IndicatorUnit> units, int style) {
        List<ChartDataEntity> dataEnities = new ArrayList<ChartDataEntity>();

        for (IndicatorUnit unit : units) {
            String value = getUnitValue(unit.getValue(), style);
            ChartDataEntity entity = createDataEntity(unit, value, unit.getIndicatorName());
            dataEnities.add(entity);
        }

        return dataEnities.toArray(new ChartDataEntity[dataEnities.size()]);
    }
}
