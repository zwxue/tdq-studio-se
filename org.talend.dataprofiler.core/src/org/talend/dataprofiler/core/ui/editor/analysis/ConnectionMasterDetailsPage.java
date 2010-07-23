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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.forms.editor.FormEditor;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;

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
            tdDataProvider = (Connection) analysedElements.get(0);
        }
    }

    protected List<Catalog> getCatalogs() {
        List<Catalog> catalogs = ConnectionHelper.getCatalogs(tdDataProvider);
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
