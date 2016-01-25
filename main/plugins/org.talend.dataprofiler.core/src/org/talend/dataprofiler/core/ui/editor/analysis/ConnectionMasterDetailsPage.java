// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.forms.editor.FormEditor;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataprofiler.core.model.OverviewIndUIElement;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.ConnectionRepNode;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IProxyRepositoryService;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;

/**
 * @author rli
 */
public class ConnectionMasterDetailsPage extends AbstractFilterMetadataPage {

    private ConnectionRepNode connectionNode;

    public ConnectionMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    protected void fillDataProvider() {
        connectionNode = (ConnectionRepNode) getCurrentRepNodeOnUI();
        if (connectionNode != null) {
            IProxyRepositoryFactory factory = ((IProxyRepositoryService) GlobalServiceRegister.getDefault().getService(
                    IProxyRepositoryService.class)).getProxyRepositoryFactory();
            // FIXME lastVersion is never used.
            IRepositoryViewObject lastVersion = null;
            try {
                lastVersion = factory.getLastVersion(connectionNode.getObject().getId());
            } catch (PersistenceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ConnectionItem item = (ConnectionItem) connectionNode.getObject().getProperty().getItem();
            tdDataProvider = item.getConnection();
        } else {
            EList<ModelElement> analysedElements = this.analysis.getContext().getAnalysedElements();
            tdDataProvider = null;
            if (analysedElements.size() > 0) {
                tdDataProvider = (Connection) analysedElements.get(0);
            }
        }

    }

    protected List<Catalog> getCatalogs() {
        List<Catalog> catalogs = ConnectionHelper.getCatalogs(tdDataProvider);
        return catalogs;
    }

    public List<OverviewIndUIElement> getSchemaIndicators() {
        ConnectionIndicator conIndicator = (ConnectionIndicator) analysis.getResults().getIndicators().get(0);
        Connection analyzedElement = (Connection) conIndicator.getAnalyzedElement();
        EList<SchemaIndicator> schemaIndicators = conIndicator.getSchemaIndicators();
        List<OverviewIndUIElement> cataUIEleList = new ArrayList<OverviewIndUIElement>();
        RepositoryNode connNode = RepositoryNodeHelper.recursiveFind(analyzedElement);
        for (Indicator indicator : schemaIndicators) {
            for (IRepositoryNode schemaNode : connNode.getChildren()) {
                String nodeUuid = ResourceHelper.getUUID(((MetadataSchemaRepositoryObject) schemaNode.getObject()).getSchema());
                String anaUuid = ResourceHelper.getUUID(indicator.getAnalyzedElement());
                if (nodeUuid.equals(anaUuid)) {
                    OverviewIndUIElement cataUIEle = new OverviewIndUIElement();
                    cataUIEle.setNode(schemaNode);
                    cataUIEle.setOverviewIndicator(indicator);
                    cataUIEleList.add(cataUIEle);
                    break;
                }
            }
        }
        return cataUIEleList;
    }

    public List<OverviewIndUIElement> getCatalogIndicators() {
        ConnectionIndicator conIndicator = (ConnectionIndicator) analysis.getResults().getIndicators().get(0);
        Connection analyzedElement = (Connection) conIndicator.getAnalyzedElement();
        EList<CatalogIndicator> catalogIndicators = conIndicator.getCatalogIndicators();
        List<OverviewIndUIElement> cataUIEleList = new ArrayList<OverviewIndUIElement>();
        RepositoryNode connNode = RepositoryNodeHelper.recursiveFind(analyzedElement);
        for (Indicator indicator : catalogIndicators) {
            for (IRepositoryNode catalogNode : connNode.getChildren()) {
                Catalog catalog = ((MetadataCatalogRepositoryObject) catalogNode.getObject()).getCatalog();
                String connUuid = ResourceHelper.getUUID(catalog);
                String anaUuid = ResourceHelper.getUUID(indicator.getAnalyzedElement());

                boolean equals = connUuid.equals(anaUuid);
                if (equals) {
                    OverviewIndUIElement cataUIEle = new OverviewIndUIElement();
                    cataUIEle.setNode(catalogNode);
                    cataUIEle.setOverviewIndicator(indicator);
                    cataUIEleList.add(cataUIEle);
                    break;
                }
            }
        }

        return cataUIEleList;
    }
}
