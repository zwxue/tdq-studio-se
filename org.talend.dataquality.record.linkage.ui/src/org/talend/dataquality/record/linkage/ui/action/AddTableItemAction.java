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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.jfree.util.Log;
import org.talend.commons.exception.BusinessException;
import org.talend.dataquality.record.linkage.ui.composite.MatchRuleTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.MatchRuleTableViewer;
import org.talend.dataquality.record.linkage.ui.section.MatchingKeySection;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;


/**
 * created by zshen on Aug 2, 2013
 * Detailled comment
 *
 */
public class AddTableItemAction extends Action {

    private List<MatchRuleTableViewer> tvs = new ArrayList<MatchRuleTableViewer>();

    private MatchingKeySection matchingKeySection = null;

    private String columnName = null;

    boolean isAlreadyAdded = Boolean.FALSE;

    public AddTableItemAction(MatchRuleTableViewer tv, String columnName) {
        this.tvs.add(tv);
        this.columnName = columnName;
    }

    public AddTableItemAction(MatchingKeySection matchingKeySection, String columnName) {
        this.matchingKeySection = matchingKeySection;
        this.tvs.addAll(getTableViewerList());
        this.columnName = columnName;
    }

    /**
     * DOC zshen Comment method "getTableViewerList".
     *
     * @param ctabFolder
     * @return
     */
    private Collection<? extends MatchRuleTableViewer> getTableViewerList() {
        List<MatchRuleTableViewer> returnList = new ArrayList<MatchRuleTableViewer>();
        if (matchingKeySection == null) {
            return returnList;
        }
        CTabFolder ctabFolder = matchingKeySection.getRuleFolder();
        if (ctabFolder == null || ctabFolder.getItemCount() == 0) {
            return returnList;
        }

        for (CTabItem item : ctabFolder.getItems()) {
            MatchRuleTableComposite data = (MatchRuleTableComposite) item.getData(MatchAnalysisConstant.MATCH_RULE_TABLE_COMPOSITE);
            returnList.add(data.getMatchRuleTableViewer());
        }

        return returnList;
    }
    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        int successNum = 0;
        isAlreadyAdded = Boolean.FALSE;

        try {
            if (tvs.size() == 0) {
                CTabItem newTabItem = createNewRuleTabItem();
                MatchRuleTableComposite data = (MatchRuleTableComposite) newTabItem
                        .getData(MatchAnalysisConstant.MATCH_RULE_TABLE_COMPOSITE);
                this.tvs.add(data.getMatchRuleTableViewer());
            }

            for (MatchRuleTableViewer currentTV : tvs) {
                boolean addSuccessed = currentTV.addElement(columnName);
                if (addSuccessed) {
                    successNum++;
                }

            }
            if (successNum == 0) {
                isAlreadyAdded = Boolean.TRUE;
                // MessageDialog.openError(null, "element duplicate warring", "the column " + columnName
                // + "is already exist, adding operation is fail");
            }
        } catch (BusinessException e) {
            Log.error("matchingKeySection is can not be init");
        }
    }

    /**
     * DOC zshen Comment method "createNewRuleTabItem".
     *
     * @return
     * @throws BusinessException
     */
    private CTabItem createNewRuleTabItem() throws BusinessException {
        if (matchingKeySection == null) {
            throw new BusinessException();
        }
        return matchingKeySection.addRuleTab(false);
    }

    public boolean isAlreadyAdded() {
        return this.isAlreadyAdded;
    }
}
