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
package org.talend.dq.nodes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class AnalysisSubFolderRepNode extends AnalysisFolderRepNode {

    private List<IRepositoryNode> anaElement;

    /**
     * DOC klliu AnalysisSubFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public AnalysisSubFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dq.nodes.AnalysisFolderRepNode#getChildren()
     */
    @Override
    public List<IRepositoryNode> getChildren() {
        IRepositoryViewObject anaViewObject = this.getObject();
        anaElement = new ArrayList<IRepositoryNode>();
        if (anaViewObject == null) {
            anaViewObject = this.getParent().getObject();
            TDQAnalysisItem item = (TDQAnalysisItem) anaViewObject.getProperty().getItem();
            Analysis analysis = item.getAnalysis();
            AnalysisContext context = analysis.getContext();
            EList<ModelElement> analysedElements = context.getAnalysedElements();
            // AnalysisResult results = analysis.getResults();
            // EList<Indicator> indicators = results.getIndicators();
            for (ModelElement analyzedElement : analysedElements) {
                // ModelElement analyzedElement = indicator.getAnalyzedElement();
                IRepositoryNode elementNode = doSwichNode(analyzedElement, this);
                if (elementNode instanceof DBConnectionRepNode) {
                    List<IRepositoryNode> children = elementNode.getChildren();
                    anaElement.addAll(children);
                } else {
                    anaElement.add(elementNode);
                }
            }
            return anaElement;
        }
        return super.getChildren();
    }

    /**
     * DOC klliu Comment method "doSwichNode".
     * 
     * @param analyzedElement
     * @return
     */
    private IRepositoryNode doSwichNode(ModelElement analyzedElement, AnalysisSubFolderRepNode childNodeFolder) {
        RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(analyzedElement);
        IRepositoryViewObject medataViewObject = recursiveFind.getObject();
        if (analyzedElement instanceof DatabaseConnection) {
            DBConnectionRepNode connNode = new DBConnectionRepNode(medataViewObject, this, ENodeType.REPOSITORY_ELEMENT);
            connNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CONNECTIONS);
            connNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CONNECTIONS);
            medataViewObject.setRepositoryNode(connNode);
            return connNode;
        } else if (analyzedElement instanceof Catalog) {
            RepositoryNode catalogNode = new DBCatalogRepNode(medataViewObject, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
            catalogNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_CATALOG);
            catalogNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_CATALOG);
            medataViewObject.setRepositoryNode(catalogNode);
            return catalogNode;
        } else if (analyzedElement instanceof Schema) {
            RepositoryNode schemaNode = new DBSchemaRepNode(medataViewObject, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
            schemaNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_SCHEMA);
            schemaNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_SCHEMA);
            medataViewObject.setRepositoryNode(schemaNode);
            return schemaNode;
        } else if (analyzedElement instanceof TdTable) {
            DBTableRepNode tableNode = new DBTableRepNode(medataViewObject, childNodeFolder, ENodeType.TDQ_REPOSITORY_ELEMENT);
            tableNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_TABLE);
            tableNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_TABLE);
            medataViewObject.setRepositoryNode(tableNode);
            return tableNode;
        } else if (analyzedElement instanceof MetadataColumn) {
            RepositoryNode columnNode = null;
            if (analyzedElement instanceof TdColumn) {
                columnNode = new DBColumnRepNode(medataViewObject, childNodeFolder, ENodeType.TDQ_REPOSITORY_ELEMENT);
            } else {
                columnNode = new DFColumnRepNode(medataViewObject, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
            }
            columnNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_COLUMN);
            columnNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_COLUMN);
            medataViewObject.setRepositoryNode(columnNode);
            return columnNode;
        } else if (analyzedElement instanceof TdXmlElementType) {
            MDMXmlElementRepNode mdmXmlElementNode = new MDMXmlElementRepNode(medataViewObject, this,
                    ENodeType.REPOSITORY_ELEMENT);
            mdmXmlElementNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.MDM_ELEMENT_TYPE);
            mdmXmlElementNode.setProperties(EProperties.LABEL, ERepositoryObjectType.MDM_ELEMENT_TYPE);
            medataViewObject.setRepositoryNode(mdmXmlElementNode);
            return mdmXmlElementNode;
        }
        return null;
    }

    public String getCount() {
        IRepositoryViewObject anaViewObject = this.getParent().getObject();
        TDQAnalysisItem item = (TDQAnalysisItem) anaViewObject.getProperty().getItem();
        Analysis analysis = item.getAnalysis();
        AnalysisContext context = analysis.getContext();
        EList<ModelElement> analysedElements = context.getAnalysedElements();
        if (analysedElements.size() == 1) {
            ModelElement modelElement = analysedElements.get(0);
            if (modelElement instanceof DatabaseConnection) {
                EList<Package> dataPackage = ((DatabaseConnection) modelElement).getDataPackage();
                return "(" + dataPackage.size() + ")";
            }
        }
        return "(" + analysedElements.size() + ")";
    }
}
