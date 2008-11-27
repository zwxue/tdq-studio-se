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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.forms.editor.FormEditor;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class ConnectionMasterDetailsPage extends AbstractFilterMetadataPage {

    public ConnectionMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    protected void fillDataProvider() {
        EList<ModelElement> analysedElements = this.analysis.getContext().getAnalysedElements();
        tdDataProvider = null;
        if (analysedElements.size() > 0) {
            tdDataProvider = (TdDataProvider) analysedElements.get(0);
        }
    }

    protected List<TdCatalog> getCatalogs() {
        List<TdCatalog> catalogs = DataProviderHelper.getTdCatalogs(tdDataProvider);
        return catalogs;
    }

    public List<CatalogIndicator> getCatalogIndicators() {
        ConnectionIndicator conIndicator = (ConnectionIndicator) analysis.getResults().getIndicators().get(0);
        return conIndicator.getCatalogIndicators();
    }

    public List<SchemaIndicator> getSchemaIndicators() {
        ConnectionIndicator conIndicator = (ConnectionIndicator) analysis.getResults().getIndicators().get(0);
        return conIndicator.getSchemaIndicators();
    }

}
