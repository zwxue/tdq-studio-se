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

import org.eclipse.jface.action.Action;
import org.talend.dataquality.record.linkage.ui.section.MatchingKeySection;

/**
 * created by zshen on Aug 2, 2013 Detailled comment
 * 
 */
public class AddMatchingKeyAction extends Action {

    private MatchingKeySection matchingKeySection = null;

    private String columnName = null;

    boolean isAlreadyAdded = Boolean.FALSE;

    public AddMatchingKeyAction(MatchingKeySection matchingKeySection, String columnName) {
        this.matchingKeySection = matchingKeySection;
        this.columnName = columnName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if (columnName == null) {
            // TODO yyin popup to notify user that no column name specified
            return;
        }
        matchingKeySection.createMatchKeyFromCurrentMatchRule(columnName);
    }

    public boolean isAlreadyAdded() {
        return this.isAlreadyAdded;
    }
}
