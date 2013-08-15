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
package org.talend.dataquality.record.linkage.ui.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping;


/**
 * created by zshen on Aug 7, 2013
 * Detailled comment
 *
 */
public class ExecuteMatchRuleDoGroupAction extends Action {

    private List<String[]> resultStrList = new ArrayList<String[]>();

    List<String[]> inputList = new ArrayList<String[]>();

    private static Logger log = Logger.getLogger(ExecuteMatchRuleDoGroupAction.class);

    private static final String columnDelimiter = "|"; //$NON-NLS-1$

    AbstractRecordGrouping recordGrouping = new AbstractRecordGrouping() {

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(java.lang.String)
         */
        @Override
        protected void outputRow(String row) {
            resultStrList.add(StringUtils.splitByWholeSeparatorPreserveAllTokens(row, columnDelimiter));
        }

    };

    public ExecuteMatchRuleDoGroupAction() {
        recordGrouping.setColumnDelimiter(columnDelimiter);
        recordGrouping.setIsOutputDistDetails(true);
        recordGrouping.setAcceptableThreshold(0.95f);
    }

    public void addRuleMatcher(List<Map<String, String>> ruleMatcherConvertResult) {
        recordGrouping.addMatchRule(ruleMatcherConvertResult);

    }

    /**
     * Sets the inputList.
     *
     * @param inputList the inputList to set
     */
    public void setInputList(List<String[]> inputList) {
        this.inputList = inputList;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        recordGrouping.initialize();
        try {
            for (String[] inputRow : inputList) {
                recordGrouping.doGroup(inputRow);
            }
            recordGrouping.end();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * Getter for resultStrList.
     * 
     * @return the resultStrList
     */
    public List<String[]> getResultStrList() {
        return this.resultStrList;
    }

}
