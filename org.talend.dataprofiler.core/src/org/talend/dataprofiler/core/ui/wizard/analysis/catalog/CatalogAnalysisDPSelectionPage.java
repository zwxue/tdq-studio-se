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
package org.talend.dataprofiler.core.ui.wizard.analysis.catalog;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisDPSelectionPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.provider.CatalogContentProvider;
import org.talend.dq.analysis.parameters.PackagesAnalyisParameter;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;

/**
 * DOC mzhao class global comment. Detailled comment
 * 
 * @author mzhao
 * 
 */
public class CatalogAnalysisDPSelectionPage extends AnalysisDPSelectionPage {

    protected static Logger log = Logger.getLogger(CatalogAnalysisDPSelectionPage.class);

    private static String newAnaStr = DefaultMessagesImpl.getString("CatologAnalysisPageStep0.newAnalysis"); //$NON-NLS-1$

    private static String chooseConnStr = DefaultMessagesImpl.getString("CatologAnalysisPageStep0.chooseCatalog"); //$NON-NLS-1$

    private static String connsStr = DefaultMessagesImpl.getString("CatologAnalysisPageStep0.catalogs"); //$NON-NLS-1$

    /**
     * @param pageName
     */
    public CatalogAnalysisDPSelectionPage() {
        super(newAnaStr, chooseConnStr, connsStr, new CatalogContentProvider()); //$NON-NLS-1$

        setPageComplete(false);
    }

    @Override
    protected void addListeners() {

        addListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                // TODO Auto-generated method stub
                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (object instanceof Catalog) {
                    advanceToNextPageOrFinish();
                }
            }

        });
        addListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                try {
                    Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                    PackagesAnalyisParameter catalogPanameter = (PackagesAnalyisParameter) getConnectionParams();
                    if (object instanceof Catalog) {
                        Catalog catalog = (Catalog) object;
                        Connection tdProvider = ConnectionHelper
                                .getTdDataProvider(SwitchHelpers.PACKAGE_SWITCH
                                .doSwitch(catalog));
                        if (tdProvider != null && catalogPanameter != null) {
                            catalogPanameter.setTdDataProvider(tdProvider);
                            catalogPanameter.setPackages(new Package[] { SwitchHelpers.PACKAGE_SWITCH.doSwitch(catalog) });
                        }
                        setPageComplete(true);
                    } else {
                        setPageComplete(false);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    log.error(e, e);
                }
            }

        });

    }

}
