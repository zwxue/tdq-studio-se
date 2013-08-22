// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.section.definition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataquality.record.linkage.ui.section.MatchingKeySection;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.MatchRule;


/**
 * created by zshen on Aug 21, 2013
 * Detailled comment
 *
 */
public class MatchKeyDefinitionSection extends MatchingKeySection {

    private List<MatchRule> matchRules = new ArrayList<MatchRule>();
    /**
     * DOC zshen MatchKeyDefinitionSection constructor comment.
     *
     * @param form
     * @param parent
     * @param style
     * @param toolkit
     * @param analysis
     */
    public MatchKeyDefinitionSection(ScrolledForm form, Composite parent, FormToolkit toolkit) {
        super(form, parent, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED, toolkit, null);
        super.setIsNeedSubChart(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataquality.record.linkage.ui.section.MatchingKeySection#getSectionName()
     */
    @Override
    protected String getSectionName() {
        return MatchAnalysisConstant.MATCHING_KEY_DEFINITION_SECTION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.MatchingKeySection#getMatchRuleList()
     */
    @Override
    protected List<MatchRule> getMatchRuleList() {
        return matchRules;
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.section.MatchingKeySection#createSubChart(org.eclipse.swt.widgets.Composite
     * )
     */
    @Override
    protected void createSubChart(Composite sectionClient) {
        // don't need the chart so do nothing at here
    }

    /**
     * Sets the matchRules.
     *
     * @param matchRules the matchRules to set
     */
    public void setMatchRules(List<MatchRule> matchRules) {
        this.matchRules = matchRules;
    }


}
