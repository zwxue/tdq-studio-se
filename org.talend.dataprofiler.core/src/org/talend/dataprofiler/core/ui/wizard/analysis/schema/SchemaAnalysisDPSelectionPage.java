// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisDPSelectionPage;
import org.talend.dq.analysis.parameters.PackagesAnalyisParameter;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class SchemaAnalysisDPSelectionPage extends AnalysisDPSelectionPage {

    private static String newAnaStr = DefaultMessagesImpl.getString("SchemaAnalysisPageStep0.newAnalysis");

    private static String chooseConnStr = DefaultMessagesImpl.getString("SchemaAnalysisPageStep0.chooseSchema");

    private static String connsStr = DefaultMessagesImpl.getString("SchemaAnalysisPageStep0.schemas");

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
                if (object instanceof TdTable) {
                    advanceToNextPageOrFinish();
                }
            }

        });
        addListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                PackagesAnalyisParameter tablePanameter = (PackagesAnalyisParameter) getConnectionParams();
                if (object instanceof TdTable || object instanceof TdView) {
                    // Package schema = ColumnSetHelper.getParentCatalogOrSchema((ColumnSet) object);
                    // if (schema != null && RelationalPackage.eINSTANCE.getTdSchema().equals(schema.eClass())) {
                    // TdDataProvider tdProvider = DataProviderHelper.getTdDataProvider(SwitchHelpers.PACKAGE_SWITCH
                    // .doSwitch(schema));
                    // if (tdProvider != null && tablePanameter != null) {
                    // tablePanameter.setTdDataProvider(tdProvider);
                    // tablePanameter.setPackages(new Package[] { SwitchHelpers.PACKAGE_SWITCH.doSwitch(schema) });
                    // }
                    // setPageComplete(true);
                    // }

                } else {
                    setPageComplete(false);
                }
            }

        });

    }
}
