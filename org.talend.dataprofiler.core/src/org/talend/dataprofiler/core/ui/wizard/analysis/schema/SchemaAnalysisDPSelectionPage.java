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
package org.talend.dataprofiler.core.ui.wizard.analysis.schema;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisDPSelectionPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.provider.SchemaContentProvider;
import org.talend.dq.analysis.parameters.PackagesAnalyisParameter;
import org.talend.dq.nodes.DBSchemaRepNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.resource.relational.Schema;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class SchemaAnalysisDPSelectionPage extends AnalysisDPSelectionPage {

    private static String newAnaStr = DefaultMessagesImpl.getString("SchemaAnalysisPageStep0.newAnalysis"); //$NON-NLS-1$

    private static String chooseConnStr = DefaultMessagesImpl.getString("SchemaAnalysisPageStep0.chooseSchema"); //$NON-NLS-1$

    private static String connsStr = DefaultMessagesImpl.getString("SchemaAnalysisPageStep0.schemas"); //$NON-NLS-1$

    public SchemaAnalysisDPSelectionPage() {
        super(newAnaStr, chooseConnStr, connsStr, new SchemaContentProvider());
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void addListeners() {
        addListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                // TODO Auto-generated method stub
                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (object instanceof Schema) {
                    advanceToNextPageOrFinish();
                }
            }

        });
        addListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                PackagesAnalyisParameter schemaPanameter = (PackagesAnalyisParameter) getConnectionParams();
                List<RepositoryNode> nodes = new ArrayList<RepositoryNode>();
                if (object instanceof RepositoryNode) {
                    DBSchemaRepNode catalogNode = (DBSchemaRepNode) object;
                    Schema schema = ((MetadataSchemaRepositoryObject) catalogNode.getObject()).getSchema();
                    Connection tdProvider = ConnectionHelper.getTdDataProvider(SwitchHelpers.PACKAGE_SWITCH
                            .doSwitch(schema));
                    nodes.add(catalogNode);
                    if (tdProvider != null && schemaPanameter != null) {
                        schemaPanameter.setTdDataProvider(tdProvider);
                        schemaPanameter.setPackages(nodes);
                    }
                    setPageComplete(true);
                } else {
                    setPageComplete(false);
                }
            }

        });

    }
}
