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
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.repository.model.repositoryObject.MetadataColumnRepositoryObject;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dq.nodes.ColumnRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sql.TalendTypeConvert;

/**
 * DOC msjian class global comment. Detailled comment
 * 
 */
public class SummaryStatisAnaDPSelectionPage extends ColumnAnalysisDOSelectionPage {

    protected static Logger log = Logger.getLogger(SummaryStatisAnaDPSelectionPage.class);

    /**
     */
    public SummaryStatisAnaDPSelectionPage() {
        super();
        setPageComplete(false);
    }

    @Override
    protected void addListeners() {

        addListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {

                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (object instanceof ColumnRepNode) {
                    // only support interger type
                    RepositoryNode repositoryNode = (ColumnRepNode) object;
                    MetadataColumn column = ((MetadataColumnRepositoryObject) repositoryNode.getObject()).getTdColumn();
                    int javaSQLType = TalendTypeConvert.convertToJDBCType(column.getTalendType());
                    if (Java2SqlType.isNumbericInSQL(javaSQLType)) {
                        advanceToNextPageOrFinish();
                    }
                }
            }

        });
        addListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                try {

                    Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                    nodes = new ArrayList<IRepositoryNode>();
                    if (object instanceof ColumnRepNode) {
                        List<IRepositoryNode> list = ((IStructuredSelection) event.getSelection()).toList();
                        nodes.addAll(list);
                        if (nodes.size() == 0 || RepNodeUtils.isValidSelectionFromSameTable(nodes)) {
                            if (RepNodeUtils.isAllNumberalColumns(nodes)) {
                                setPageComplete(true);
                                setMessage(chooseConnStr);
                            } else {
                                setPageComplete(false);
                                setMessage(
                                        "Only numerical type columns can be selected for the summary statistics Analysis.", ERROR); //$NON-NLS-1$
                            }
                        } else {
                            setPageComplete(false);
                            setMessage("Columns can not be selected from different tables/views", ERROR); //$NON-NLS-1$
                        }
                    } else {
                        setPageComplete(false);
                        setMessage(chooseConnStr);
                    }

                } catch (Exception e) {
                    log.error(e, e);
                }
            }

        });
    }

}
