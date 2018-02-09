// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisDPSelectionPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.provider.ColumnContentProvider;
import org.talend.dq.nodes.DBCatalogRepNode;
import org.talend.repository.model.IRepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment,2011-02-16 feature 15387
 */
public class ColumnAnalysisDOSelectionPage extends AnalysisDPSelectionPage {

    protected static Logger log = Logger.getLogger(ColumnAnalysisDOSelectionPage.class);

    private static String newAnaStr = DefaultMessagesImpl.getString("ColumnAnalysisPageStep0.newAnalysis"); //$NON-NLS-1$

    private static String chooseConnStr = DefaultMessagesImpl.getString("ColumnAnalysisPageStep0.chooseColumn"); //$NON-NLS-1$

    private static String connsStr = DefaultMessagesImpl.getString("ColumnAnalysisPageStep0.Columns"); //$NON-NLS-1$

    public List<IRepositoryNode> nodes;

    /**
     * @param pageName
     */
    public ColumnAnalysisDOSelectionPage() {
        super(newAnaStr, chooseConnStr, connsStr, new ColumnContentProvider(), true);

        setPageComplete(true);
    }

    /**
     * @param contentProvider, use diffirent ContentProvider for Match analysis and column analysis.
     */
    public ColumnAnalysisDOSelectionPage(ResourceViewContentProvider contentProvider) {
        super(newAnaStr, chooseConnStr, connsStr, contentProvider, true);

        setPageComplete(true);
    }

    @Override
    protected void addListeners() {

        addListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (object instanceof DBCatalogRepNode) {
                    advanceToNextPageOrFinish();
                }
            }

        });
        addListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                try {
                    Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                    nodes = new ArrayList<IRepositoryNode>();
                    if (object instanceof IRepositoryNode) {
                        List<IRepositoryNode> list = ((IStructuredSelection) event.getSelection()).toList();
                        nodes.addAll(list);
                        updateCompleteState();
                    } else {
                        setPageComplete(false);
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            }

        });
    }

    /**
     * 
     * DOC talend Comment method "updateCompleteState".
     */
    private void updateCompleteState() {
        // Nodes come from same table or empty both are valid
        if (nodes.size() == 0 || RepNodeUtils.isValidSelectionFromSameTable(nodes)) {
            setPageComplete(true);
            this.setMessage(chooseConnStr);
        } else {
            setPageComplete(false);
            this.setMessage("Columns can not be selected from different tables/views", ERROR);
        }

    }
}
