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
package org.talend.dataquality.record.linkage.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.MatchRuleTableViewer;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.MatchRule;

/**
 * created by zshen on Jul 31, 2013 Detailled comment
 * 
 */
public class MatchRuleTableComposite extends AbsMatchAnalysisTableComposite {

    /**
     * DOC zshen MatchRuleComposite constructor comment.
     * 
     * @param parent
     * @param style
     */
    public MatchRuleTableComposite(Composite parent, int style) {
        super(parent, style);
    }

    /**
     * DOC zshen Comment method "createContent".
     */
    private void createContent() {
        initHeaders();
        createTable();

    }

    /**
     * DOC zshen Comment method "initHeaders".
     */
    @Override
    protected void initHeaders() {
        headers.add(MatchAnalysisConstant.MATCH_KEY_NAME); // 14
        headers.add(MatchAnalysisConstant.COLUMN); // 14
        headers.add(MatchAnalysisConstant.MATCHING_TYPE); // 12
        headers.add(MatchAnalysisConstant.CUSTOM_MATCHER_CLASS); // 20
        headers.add(MatchAnalysisConstant.CONFIDENCE_WEIGHT); // 17
        headers.add(MatchAnalysisConstant.HANDLE_NULL); // 11
    }

    /**
     * DOC zshen Comment method "createTable".
     */
    @Override
    protected void createTable() {
        tableViewer = new MatchRuleTableViewer(this, getTableStyle());
        tableViewer.initTable(headers);
    }

    @Override
    protected int getTableStyle() {
        int style = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
        return style;
    }

    public void setInputData(MatchRule inputMatcher) {
        ((MatchRuleTableViewer) tableViewer).setInputData(inputMatcher);
    }

    public void getInputData() {
        tableViewer.getInput();
    }

}
