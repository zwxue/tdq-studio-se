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
package org.talend.dataprofiler.core.ui.editor.preview.model.states.freq;

import java.util.List;

import org.talend.commons.utils.SpecialValueDisplay;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.model.ICustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerDefaultCategoryDataset;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dq.indicators.ext.FrequencyExt;
import org.talend.dq.indicators.preview.EIndicatorChartType;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC yyi class global comment. Detailled comment
 */
public class DateFrequencyStatisticsState extends FrequencyStatisticsState {

    private EIndicatorChartType type;

    /**
     * DOC yyi DateFrequencyStatisticsState constructor comment.
     * 
     * @param units
     */
    public DateFrequencyStatisticsState(List<IndicatorUnit> units) {
        super(units);
        // TODO Auto-generated constructor stub
    }

    public DateFrequencyStatisticsState(List<IndicatorUnit> units, EIndicatorChartType type) {
        this(units);
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.freq.FrequencyTypeStates#getCustomerDataset()
     */
    @Override
    public ICustomerDataset getCustomerDataset() {
        CustomerDefaultCategoryDataset customerdataset = new CustomerDefaultCategoryDataset();

        for (IndicatorUnit unit : units) {
            if (unit.isExcuted()) {
                FrequencyExt[] frequencyExt = (FrequencyExt[]) unit.getValue();

                sortIndicator(frequencyExt);

                int numOfShown = frequencyExt.length;
                IndicatorParameters parameters = unit.getIndicator().getParameters();
                if (parameters != null) {
                    if (parameters.getTopN() < numOfShown) {
                        numOfShown = parameters.getTopN();
                    }
                }

                for (int i = 0; i < numOfShown; i++) {
                    FrequencyExt freqExt = frequencyExt[i];
                    String keyLabel = String.valueOf(freqExt.getKey());
                    if ("null".equals(keyLabel)) { //$NON-NLS-1$
                        keyLabel = SpecialValueDisplay.NULL_FIELD;
                    }
                    if ("".equals(keyLabel)) { //$NON-NLS-1$
                        keyLabel = SpecialValueDisplay.EMPTY_FIELD;
                    }

                    customerdataset.addValue(freqExt.getValue(), unit.getIndicatorName(), keyLabel);

                    ChartDataEntity entity = new ChartDataEntity();
                    entity.setIndicator(unit.getIndicator());
                    // MOD mzhao feature:6307 display soundex distinct count and real count.
                    entity.setKey(freqExt.getKey());
                    entity.setLabelNull(freqExt.getKey() == null);
                    entity.setLabel(keyLabel);
                    entity.setValue(String.valueOf(freqExt.getValue()));
                    entity.setPercent(freqExt.getFrequency());

                    customerdataset.addDataEntity(entity);
                }
            }
        }
        return customerdataset;
    }

    @Override
    protected String getTitle() {
        return null == type ? DefaultMessagesImpl.getString("DateFrequencyStatisticsState.DateFrequencyStatistics") : type//$NON-NLS-1$
                .getLiteral();
    }
}
