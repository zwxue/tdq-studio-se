// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import orgomg.cwm.objectmodel.core.ModelElement;

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

        WhereRuleIndicator[] compositeWhereRuleIndicator = createCompositeWhereRuleIndicator(tableIndicator.getColumnSet(),
                whereRule);
        IndicatorEnum type = IndicatorEnum.findIndicatorEnum(compositeWhereRuleIndicator[0].eClass());
        TableIndicatorUnit addIndicatorUnit = tableIndicator.addSpecialIndicator(fe, type, compositeWhereRuleIndicator[0]);
        DependenciesHandler.getInstance().setUsageDependencyOn(analysis, whereRule);

        // The where rule aid indicator won't be shown on UI, so just create a indicator unit.
        IndicatorEnum whereRuleAidType = IndicatorEnum.findIndicatorEnum(compositeWhereRuleIndicator[1].eClass());
        tableIndicator.addSpecialIndicator(fe, whereRuleAidType, compositeWhereRuleIndicator[1]);

        return addIndicatorUnit;
    }



    /**
     * @return 0 based index is WhereRuleIndicator, 1 based index is WhereRuleAideIndicator
     */
    public static WhereRuleIndicator[] createCompositeWhereRuleIndicator(ModelElement anaElement, WhereRule whereRuleDef) {
        WhereRuleIndicator wrIndicator = IndicatorSqlFactory.eINSTANCE.createWhereRuleIndicator();
        wrIndicator.setAnalyzedElement(anaElement);
        wrIndicator.setIndicatorDefinition(whereRuleDef);

        WhereRuleAideIndicator wraIndicator = IndicatorSqlFactory.eINSTANCE.createWhereRuleAideIndicator();
        wraIndicator.setAnalyzedElement(anaElement);
        wraIndicator.setIndicatorDefinition(whereRuleDef);
        return new WhereRuleIndicator[] { wrIndicator, wraIndicator };

    }

}
