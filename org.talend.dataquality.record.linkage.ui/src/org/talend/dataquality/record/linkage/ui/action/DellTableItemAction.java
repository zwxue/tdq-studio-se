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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.talend.dataquality.record.linkage.ui.composite.MatchRuleTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer;
import org.talend.dataquality.record.linkage.ui.section.MatchingKeySection;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.MatchKeyDefinition;

/**
 * created by zshen on Aug 2, 2013 Detailled comment
 * 
 */
public class DellTableItemAction extends Action {

    private AbstractMatchAnalysisTableViewer tv = null;

    private MatchingKeySection matchingKeySection = null;

    private String columnName = null;

    private List<AbstractMatchAnalysisTableViewer> tvs = new ArrayList<AbstractMatchAnalysisTableViewer>();

    public DellTableItemAction(AbstractMatchAnalysisTableViewer tv) {
        this.tv = tv;
        setText("Dell"); //$NON-NLS-1$
    }

    public DellTableItemAction(MatchingKeySection matchingKeySection, String columnName) {
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
    private Collection<? extends AbstractMatchAnalysisTableViewer> getTableViewerList() {
        List<AbstractMatchAnalysisTableViewer> returnList = new ArrayList<AbstractMatchAnalysisTableViewer>();
        if (matchingKeySection == null) {
            return returnList;
        }
        CTabFolder ctabFolder = matchingKeySection.getRuleFolder();
        if (ctabFolder == null || ctabFolder.getItemCount() == 0) {
            return returnList;
        }

        for (CTabItem item : ctabFolder.getItems()) {
            MatchRuleTableComposite data = (MatchRuleTableComposite) item
                    .getData(MatchAnalysisConstant.MATCH_RULE_TABLE_COMPOSITE);
            returnList.add(data.getTableViewer());
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
        for (AbstractMatchAnalysisTableViewer currentTV : tvs) {
            currentTV.removeElement(columnName);
        }

        if (tv != null) {
            StructuredSelection selection = (StructuredSelection) tv.getSelection();
            Iterator<Object> selectioniterator = selection.iterator();
            while (selectioniterator.hasNext()) {
                Object next = selectioniterator.next();
                if (next instanceof MatchKeyDefinition) {
                    tv.removeElement(((MatchKeyDefinition) next).getColumn());
                }
            }
            tv.refresh();
        }
    }

}
