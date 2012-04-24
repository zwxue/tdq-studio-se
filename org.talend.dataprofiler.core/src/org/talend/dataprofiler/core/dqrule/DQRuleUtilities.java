// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.dqrule;

import org.eclipse.core.resources.IFile;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.model.TableIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.TableIndicatorUnit;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.sql.IndicatorSqlFactory;
import org.talend.dataquality.indicators.sql.WhereRuleAideIndicator;
import org.talend.dataquality.indicators.sql.WhereRuleIndicator;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class DQRuleUtilities {

    private DQRuleUtilities() {
    }

    /**
     * DOC xqliu Comment method "createIndicatorUnit".
     * 
     * @param fe
     * @param tableIndicator
     * @param analysis
     * @return
     */
    public static TableIndicatorUnit createIndicatorUnit(IFile fe, TableIndicator tableIndicator, Analysis analysis) {
        WhereRule whereRule = DQRuleResourceFileHelper.getInstance().findWhereRule(fe);

        for (Indicator indicator : tableIndicator.getIndicators()) {
            if (whereRule.getName().equals(indicator.getName())) {
                return null;
            }
        }

        WhereRuleIndicator wrIndicator = IndicatorSqlFactory.eINSTANCE.createWhereRuleIndicator();
        wrIndicator.setAnalyzedElement(tableIndicator.getColumnSet());
        wrIndicator.setIndicatorDefinition(whereRule);

        IndicatorEnum type = IndicatorEnum.findIndicatorEnum(wrIndicator.eClass());
        TableIndicatorUnit addIndicatorUnit = tableIndicator.addSpecialIndicator(fe, type, wrIndicator);
        DependenciesHandler.getInstance().setUsageDependencyOn(analysis, whereRule);
        return addIndicatorUnit;
    }

    /**
     * DOC xqliu Comment method "createIndicatorAideUnit".
     * 
     * @param fe
     * @param tableIndicator
     * @param analysis
     * @return
     */
    public static TableIndicatorUnit createIndicatorAideUnit(IFile fe, TableIndicator tableIndicator, Analysis analysis) {
        WhereRule whereRule = DQRuleResourceFileHelper.getInstance().findWhereRule(fe);

        for (Indicator indicator : tableIndicator.getIndicators()) {
            if (indicator instanceof WhereRuleAideIndicator && whereRule.getName().equals(indicator.getName())) {
                return null;
            }
        }

        WhereRuleAideIndicator wraIndicator = IndicatorSqlFactory.eINSTANCE.createWhereRuleAideIndicator();
        wraIndicator.setAnalyzedElement(tableIndicator.getColumnSet());
        wraIndicator.setIndicatorDefinition(whereRule);

        IndicatorEnum type = IndicatorEnum.findIndicatorEnum(wraIndicator.eClass());
        TableIndicatorUnit addIndicatorUnit = tableIndicator.addSpecialIndicator(fe, type, wraIndicator);
        DependenciesHandler.getInstance().setUsageDependencyOn(analysis, whereRule);
        return addIndicatorUnit;
    }
}
