// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.util.AnalysisSwitch;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.util.PatternSwitch;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.util.DefinitionSwitch;
import org.talend.dataquality.rules.DQRule;
import org.talend.dataquality.rules.util.RulesSwitch;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwmx.analysis.informationreporting.Report;
import orgomg.cwmx.analysis.informationreporting.util.InformationreportingSwitch;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class ModelElementIdentifier {

    private ModelElementIdentifier() {
        //
    }

    public static final AnalysisSwitch<Analysis> ANALYSIS = new AnalysisSwitch<Analysis>() {

        public Analysis caseAnalysis(Analysis object) {
            return object;
        };
    };

    public static final InformationreportingSwitch<Report> REPORT = new InformationreportingSwitch<Report>() {

        public Report caseReport(Report object) {
            return object;
        };
    };

    public static final PatternSwitch<Pattern> PATTERN = new PatternSwitch<Pattern>() {

        public Pattern casePattern(Pattern object) {
            return object;
        };
    };

    public static final DefinitionSwitch<IndicatorDefinition> INDICATOR_DEFINITION = new DefinitionSwitch<IndicatorDefinition>() {

        public IndicatorDefinition caseIndicatorDefinition(IndicatorDefinition object) {
            return object;
        };
    };

    public static final RulesSwitch<DQRule> DQ_RULE = new RulesSwitch<DQRule>() {

        public DQRule caseDQRule(DQRule object) {
            return object;
        };
    };

    /**
     * DOC bZhou Comment method "isAnalysis".
     * 
     * @param element
     * @return
     */
    public static boolean isAnalysis(ModelElement element) {
        return ANALYSIS.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isReport".
     * 
     * @param element
     * @return
     */
    public static boolean isReport(ModelElement element) {
        return REPORT.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isPattern".
     * 
     * @param element
     * @return
     */
    public static boolean isPattern(ModelElement element) {
        return PATTERN.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isID".
     * 
     * @param element
     * @return
     */
    public static boolean isID(ModelElement element) {
        return INDICATOR_DEFINITION.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isDQRule".
     * 
     * @param element
     * @return
     */
    public static boolean isDQRule(ModelElement element) {
        return DQ_RULE.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isDataProvider".
     * 
     * @param element
     * @return
     */
    public static boolean isDataProvider(ModelElement element) {
        return SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(element) != null;
    }
}
