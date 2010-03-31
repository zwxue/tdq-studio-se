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
package org.talend.dataprofiler.core.ui.wizard.analysis.connection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisDPSelectionPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.provider.ConnectionsContentProvider;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * 
 * @author zqin
 * 
 * 
 */
public class ConnAnalysisDPSelectionPage extends AnalysisDPSelectionPage {

    private static String newAnaStr = DefaultMessagesImpl.getString("ConnAnalysisPageStep0.newAnalysis"); //$NON-NLS-1$

    private static String chooseConnStr = DefaultMessagesImpl.getString("ConnAnalysisPageStep0.chooseConnection"); //$NON-NLS-1$

    private static String connsStr = DefaultMessagesImpl.getString("ConnAnalysisPageStep0.connections"); //$NON-NLS-1$

    /**
     * 
     * @param pageName
     */
    public ConnAnalysisDPSelectionPage() {
        super(newAnaStr, chooseConnStr, connsStr, new ConnectionsContentProvider());
        setPageComplete(false);
    }

    @Override
    protected void addListeners() {

        addListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                // TODO Auto-generated method stub
                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (object instanceof IFile) {
                    IFile file = (IFile) object;
                    if (file.getParent() != null) {
                        advanceToNextPageOrFinish();
                    }
                }
            }

        });
        addListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {

                // get the dataprovider from the seleted connection
                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                AnalysisFilterParameter connPanameter = (AnalysisFilterParameter) getConnectionParams();
                if (object instanceof IFile) {
                    IFile file = (IFile) object;
                    // MOD mzhao 2010-3-30, bug 12037, Currently make it unable to use for MDM Connection overview
                    // analysis.
                    if (ConnectionUtils.isMdmConnection(file)) {
                        setPageComplete(false);
                        return;
                    }

                    TypedReturnCode<TdDataProvider> tdProvider = PrvResourceFileHelper.getInstance().findProvider(file);

                    if (tdProvider != null && connPanameter != null) {

                        connPanameter.setTdDataProvider(tdProvider.getObject());
                    }

                    setPageComplete(true);
                } else {
                    setPageComplete(false);
                }
            }

        });

    }

}
