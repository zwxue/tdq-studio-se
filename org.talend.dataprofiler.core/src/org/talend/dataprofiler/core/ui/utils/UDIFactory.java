// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import org.talend.dataprofiler.core.helper.UDIHelper;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.DomainFactory;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.sql.IndicatorSqlFactory;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dq.indicators.definitions.DefinitionHandler;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class UDIFactory {

    private UDIFactory() {

    }

    public static Indicator createUserDefIndicator(IndicatorDefinition indicatorDefinition) {
        UserDefIndicator userDefIndicator = IndicatorSqlFactory.eINSTANCE.createUserDefIndicator();
        IndicatorParameters indicParams = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        Domain validData = DomainFactory.eINSTANCE.createDomain();
        indicParams.setDataValidDomain(validData);
        userDefIndicator.setParameters(indicParams);
        userDefIndicator.setName(indicatorDefinition.getName());
        return userDefIndicator;
    }

    public static Indicator createIndicator(IndicatorDefinition indicatorDefinition) {
        IndicatorCategory category = UDIHelper.getUDICategory(indicatorDefinition);
        Indicator indicator = null;
        if (category != null) {
            if (category.getLabel().equals(DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory().getLabel())) {
                indicator = IndicatorsFactory.eINSTANCE.createRowCountIndicator();
            } else if (category.getLabel().equals(
                    DefinitionHandler.getInstance().getUserDefinedMatchIndicatorCategory().getLabel())) {
                indicator = IndicatorsFactory.eINSTANCE.createRegexpMatchingIndicator();
            } else if (category.getLabel().equals(
                    DefinitionHandler.getInstance().getUserDefinedFrequencyIndicatorCategory().getLabel())) {
                indicator = IndicatorsFactory.eINSTANCE.createFrequencyIndicator();
            }

            if (indicator != null) {
                IndicatorParameters indicParams = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
                indicator.setParameters(indicParams);
                indicator.setName(indicatorDefinition.getName());
            }
        }
        return indicator;
    }

}
