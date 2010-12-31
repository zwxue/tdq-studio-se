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
package org.talend.dataprofiler.core.ui.views.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.FolderHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataprofiler.core.ui.views.nodes.AnalysisFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.AnalysisRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.DBConnectionFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.DBConnectionRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.ExchangeFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.IndicatorFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.JrxmlTempFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.MDMConnectionFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.PatternRegexFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.PatternSqlFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.ReportFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.RulesFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.SourceFileFolderRepNode;
import org.talend.dataprofiler.core.ui.views.nodes.UserDefIndicatorFolderRepNode;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.analysis.category.CategoryHandler;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.repository.localprovider.model.LocalFolderHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;

/**
 * DOC klliu class global comment. Detailled comment
 */
public final class RepositoryNodeBuilder {

    private static RepositoryNodeBuilder instance;

    /**
     * DOC klliu RepositoryNodeBuilder constructor comment.
     */
    public RepositoryNodeBuilder() {

    }

    // /**
    // * Create TdColumnRepositoryNode under ColumnFolderNodeRepositoryNode.
    // *
    // * @param node parent ColumnFolderNodeRepositoryNode
    // * @param tdqFolder parent ColumnFolderNodeViewObject
    // */
    // public void createRepositoryNodeTdColumn(RepositoryNode node, TDQFolderObject tdqFolder) {
    // // ((TDQFolderObject) tdqFolder).getFolderNode().loadChildren();
    // // Object[] children = tdqFolder.getFolderNode().getChildren();
    // // for (Object obj : children) {
    // // if (obj instanceof TdColumn) {
    // // TdColumn tdColumn = (TdColumn) obj;
    // // MetadataColumnRepositoryObject metadataColumn = new MetadataColumnRepositoryObject(tdqFolder.getViewObject(),
    // // tdColumn);
    // // tdqFolder.getChildren().add(metadataColumn);
    // // metadataColumn.setId(tdColumn.getName());
    // // metadataColumn.setLabel(tdColumn.getName());
    // // RepositoryNode columnNode = new RepositoryNode(metadataColumn, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // // columnNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_COLUMN);
    // // columnNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_COLUMN);
    // // metadataColumn.setRepositoryNode(columnNode);
    // // node.getChildren().add(columnNode);
    // // }
    // // }
    // }
    //
    // /**
    // * Create TdViewRepositoryNode under ViewFolderNodeRepositoryNode.
    // *
    // * @param node parent ViewFolderNodeRepositoryNode
    // * @param tdqFolder parent ViewFolderNodeViewObject
    // */
    // public void createRepositoryNodeTdView(RepositoryNode node, TDQFolderObject tdqFolder) {
    // // tdqFolder.getFolderNode().loadChildren();
    // // Object[] children = tdqFolder.getFolderNode().getChildren();
    // // if (children != null) {
    // // for (Object obj : children) {
    // // if (obj instanceof TdView) {
    // // TdView tdView = (TdView) obj;
    // // tdView.setTableType("VIEW");
    // // TdViewRepositoryObject metadataView = new TdViewRepositoryObject(tdqFolder.getViewObject(), tdView);
    // // tdqFolder.getChildren().add(metadataView);
    // // metadataView.setTableName(tdView.getName());
    // // metadataView.setLabel(tdView.getName());
    // // metadataView.setId(tdView.getName());
    // // RepositoryNode viewNode = new RepositoryNode(metadataView, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // // viewNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_TABLE);
    // // viewNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_TABLE);
    // // metadataView.setRepositoryNode(viewNode);
    // // node.getChildren().add(viewNode);
    // // }
    // // }
    // // }
    // }
    //
    // /**
    // * Create TdTableRepositoryNode under TableFolderNodeRepositoryNode.
    // *
    // * @param node parent TableFolderNodeRepositoryNode
    // * @param tdqFolder parent TableFolderNodeViewObject
    // */
    // public void createRepositoryNodeTdTable(RepositoryNode node, TDQFolderObject tdqFolder) {
    // // tdqFolder.getFolderNode().loadChildren();
    // // Object[] children = tdqFolder.getFolderNode().getChildren();
    // // if (children != null) {
    // // for (Object obj : children) {
    // // if (obj instanceof TdTable) {
    // // TdTable tdTable = (TdTable) obj;
    // // tdTable.setTableType("TABLE");
    // // TdTableRepositoryObject metadataTable = new TdTableRepositoryObject(tdqFolder.getViewObject(), tdTable);
    // // tdqFolder.getChildren().add(metadataTable);
    // // metadataTable.setTableName(tdTable.getName());
    // // metadataTable.setLabel(tdTable.getName());
    // // metadataTable.setId(tdTable.getName());
    // // RepositoryNode tableNode = new RepositoryNode(metadataTable, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // // tableNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_TABLE);
    // // tableNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_TABLE);
    // // metadataTable.setRepositoryNode(tableNode);
    // // node.getChildren().add(tableNode);
    // // }
    // // }
    // // }
    // }
    //
    // /**
    // * Create ColumnFolderNodeRepositoryNode under TdViewRepositoryNode.
    // *
    // * @param node parent TdViewRepositoryNode
    // * @param metadataView parent TdViewViewObject
    // */
    // public void createRepositoryNodeColumnFolderNode(RepositoryNode node, TdViewRepositoryObject metadataView) {
    // // TDQFolderObject tdqColumnFolder = new TDQFolderObject(metadataView.getViewObject(),
    // // ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // metadataView.getChildren().add(tdqColumnFolder);
    // // ColumnFolderNode columnFolderNode = new ColumnFolderNode();
    // // columnFolderNode.setParent(metadataView.getTdView());
    // // tdqColumnFolder.setFolderNode(columnFolderNode);
    // // tdqColumnFolder.setFolderType(ETDQFolderType.COLUMN_FOLDER);
    // // RepositoryNode columnNode = new RepositoryNode(tdqColumnFolder, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // // columnNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // columnNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // tdqColumnFolder.setRepositoryNode(columnNode);
    // // node.getChildren().add(columnNode);
    // }
    //
    // /**
    // * Create ColumnFolderNodeRepositoryNode under TdTableRepositoryNode.
    // *
    // * @param node parent TdTableRepositoryNode
    // * @param metadataTable parent TdTableViewObject
    // */
    // public void createRepositoryNodeColumnFolderNode(RepositoryNode node, TdTableRepositoryObject metadataTable) {
    // // TDQFolderObject tdqColumnFolder = new TDQFolderObject(metadataTable.getViewObject(),
    // // ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // metadataTable.getChildren().add(tdqColumnFolder);
    // // ColumnFolderNode columnFolderNode = new ColumnFolderNode();
    // // columnFolderNode.setParent(metadataTable.getTdTable());
    // // tdqColumnFolder.setFolderNode(columnFolderNode);
    // // tdqColumnFolder.setFolderType(ETDQFolderType.COLUMN_FOLDER);
    // // RepositoryNode columnNode = new RepositoryNode(tdqColumnFolder, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // // columnNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // columnNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // tdqColumnFolder.setRepositoryNode(columnNode);
    // // node.getChildren().add(columnNode);
    // }
    //
    // /**
    // * Create ViewFolderNodeRepositoryNode under CatalogRepositoryNode.
    // *
    // * @param node parent RepositoryNode
    // * @param metadataCatalog parent CatalogViewObject
    // */
    // public void createRepositoryNodeViewFolderNode(RepositoryNode node, MetadataCatalogRepositoryObject
    // metadataCatalog) {
    // // TDQFolderObject tdqViewFolder = new TDQFolderObject(metadataCatalog.getViewObject(),
    // // ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // metadataCatalog.getChildren().add(tdqViewFolder);
    // // ViewFolderNode viewFolderNode = new ViewFolderNode();
    // // viewFolderNode.setParent(metadataCatalog.getCatalog());
    // // tdqViewFolder.setFolderNode(viewFolderNode);
    // // tdqViewFolder.setFolderType(ETDQFolderType.VIEW_FOLDER);
    // // RepositoryNode viewNode = new RepositoryNode(tdqViewFolder, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // // viewNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // viewNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // tdqViewFolder.setRepositoryNode(viewNode);
    // // node.getChildren().add(viewNode);
    // }
    //
    // /**
    // * Create TableFolderNodeRepositoryNode under CatalogRepositoryNode.
    // *
    // * @param node parent RepositoryNode
    // * @param metadataCatalog parent CatalogViewObject
    // */
    // public void createRepositoryNodeTableFolderNode(RepositoryNode node, MetadataCatalogRepositoryObject
    // metadataCatalog) {
    // // TDQFolderObject tdqTableFolder = new TDQFolderObject(metadataCatalog.getViewObject(),
    // // ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // metadataCatalog.getChildren().add(tdqTableFolder);
    // // TableFolderNode tableFolderNode = new TableFolderNode();
    // // tableFolderNode.setParent(metadataCatalog.getCatalog());
    // // tdqTableFolder.setFolderNode(tableFolderNode);
    // // tdqTableFolder.setFolderType(ETDQFolderType.TABLE_FOLDER);
    // // RepositoryNode tableNode = new RepositoryNode(tdqTableFolder, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // // tableNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // tableNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // tdqTableFolder.setRepositoryNode(tableNode);
    // // node.getChildren().add(tableNode);
    // }

    // /**
    // * Create SchemaRepositoryNode under CatalogRepositoryNode.
    // *
    // * @param node parent CatalogRepositoryNode
    // * @param metadataCatalog parent CatalogViewObject
    // * @param schema the schema should to be added under the catalog
    // */
    // public void createRepositoryNodeSchema(RepositoryNode node, MetadataCatalogRepositoryObject metadataCatalog,
    // Schema schema) {
    // MetadataSchemaRepositoryObject metadataSchema = new
    // MetadataSchemaRepositoryObject(metadataCatalog.getViewObject(),
    // schema);
    // metadataCatalog.getChildren().add(metadataSchema);
    // RepositoryNode schemaNode = new RepositoryNode(metadataSchema, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // schemaNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_SCHEMA);
    // schemaNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_SCHEMA);
    // metadataSchema.setRepositoryNode(schemaNode);
    // node.getChildren().add(schemaNode);
    // }
    //
    // /**
    // * Create ViewFolderNodeRepositoryNode under SchemaRepositoryNode.
    // *
    // * @param node parent SchemaRepositoryNode
    // * @param metadataSchema parent SchemaViewObject
    // */
    // public void createRepositoryNodeViewFolderNode(RepositoryNode node, MetadataSchemaRepositoryObject
    // metadataSchema) {
    // // TDQFolderObject tdqViewFolder = new TDQFolderObject(metadataSchema.getViewObject(),
    // // ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // metadataSchema.getChildren().add(tdqViewFolder);
    // // ViewFolderNode viewFolderNode = new ViewFolderNode();
    // // viewFolderNode.setParent(metadataSchema.getSchema());
    // // tdqViewFolder.setFolderNode(viewFolderNode);
    // // tdqViewFolder.setFolderType(ETDQFolderType.VIEW_FOLDER);
    // // RepositoryNode viewNode = new RepositoryNode(tdqViewFolder, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // // viewNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // viewNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // tdqViewFolder.setRepositoryNode(viewNode);
    // // node.getChildren().add(viewNode);
    // }
    //
    // /**
    // * Create TableFolderNodeRepositoryNode under SchemaRepositoryNode.
    // *
    // * @param node parent SchemaRepositoryNode
    // * @param metadataSchema parent SchemaViewObject
    // */
    // public void createRepositoryNodeTableFolderNode(RepositoryNode node, MetadataSchemaRepositoryObject
    // metadataSchema) {
    // // TDQFolderObject tdqTableFolder = new TDQFolderObject(metadataSchema.getViewObject(),
    // // ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // metadataSchema.getChildren().add(tdqTableFolder);
    // // TableFolderNode tableFolderNode = new TableFolderNode();
    // // tableFolderNode.setParent(metadataSchema.getSchema());
    // // tdqTableFolder.setFolderNode(tableFolderNode);
    // // tdqTableFolder.setFolderType(ETDQFolderType.TABLE_FOLDER);
    // // RepositoryNode tableNode = new RepositoryNode(tdqTableFolder, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // // tableNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // tableNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_FOLDER_NODE);
    // // tdqTableFolder.setRepositoryNode(tableNode);
    // // node.getChildren().add(tableNode);
    // }

    // /**
    // * Create TdXmlElementTypeRepositoryNode under TdXmlElementTypeRepositoryNode.
    // *
    // * @param node parent TdXmlElementTypeRepositoryNode
    // * @param metadataXmlElementTypeParent parent TdXmlElementTypeViewObject
    // */
    // public void createRepositoryNodeTdXmlElementTypeSub(RepositoryNode node,
    // MetadataXmlElementTypeRepositoryObject metadataXmlElementTypeParent) {
    // TdXmlElementType tdXmlElementTypeParent = metadataXmlElementTypeParent.getTdXmlElementType();
    // List<TdXmlElementType> xmlElements = DqRepositoryViewService.getXMLElements(tdXmlElementTypeParent);
    // for (TdXmlElementType tdXmlElementType : xmlElements) {
    // MetadataXmlElementTypeRepositoryObject metadataXmlElementType = new MetadataXmlElementTypeRepositoryObject(
    // metadataXmlElementTypeParent.getViewObject(), tdXmlElementType);
    // metadataXmlElementTypeParent.getChildren().add(metadataXmlElementType);
    // RepositoryNode xmlElementTypeNode = new RepositoryNode(metadataXmlElementType, node,
    // ENodeType.TDQ_REPOSITORY_ELEMENT);
    // xmlElementTypeNode.setProperties(EProperties.LABEL, ERepositoryObjectType.MDM_CONCEPT);
    // xmlElementTypeNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_CATALOG);
    // metadataXmlElementType.setRepositoryNode(xmlElementTypeNode);
    // node.getChildren().add(xmlElementTypeNode);
    // }
    // }
    //
    // /**
    // * Create TdXmlElementTypeRepositoryNode under TdXmlSchemaRepositoryNode.
    // *
    // * @param node parent TdXmlSchemaRepositoryNode
    // * @param metadataXmlSchema parent TdXmlSchemaViewObject
    // */
    // public void createRepositoryNodeTdXmlElementType(RepositoryNode node, MetadataXmlSchemaRepositoryObject
    // metadataXmlSchema) {
    // TdXmlSchema tdXmlSchema = metadataXmlSchema.getTdXmlSchema();
    // List<ModelElement> xmlElements = DqRepositoryViewService.getXMLElements(tdXmlSchema);
    // for (ModelElement mElement : xmlElements) {
    // if (mElement instanceof TdXmlElementType) {
    // MetadataXmlElementTypeRepositoryObject metadataXmlElementType = new MetadataXmlElementTypeRepositoryObject(
    // metadataXmlSchema.getViewObject(), (TdXmlElementType) mElement);
    // metadataXmlSchema.getChildren().add(metadataXmlElementType);
    // RepositoryNode xmlElementTypeNode = new RepositoryNode(metadataXmlElementType, node,
    // ENodeType.TDQ_REPOSITORY_ELEMENT);
    // xmlElementTypeNode.setProperties(EProperties.LABEL, ERepositoryObjectType.MDM_ELEMENT_TYPE);
    // xmlElementTypeNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_CATALOG);
    // metadataXmlElementType.setRepositoryNode(xmlElementTypeNode);
    // node.getChildren().add(xmlElementTypeNode);
    // }
    // }
    // }

    /**
     * DOC klliu Comment method "retrieveRepObjectType".
     * 
     * @param path
     * @return
     */
    public ERepositoryObjectType retrieveRepObjectTypeByPath(String path) {
        if (EResourceConstant.DATA_PROFILING.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_DATA_PROFILING;
        } else if (EResourceConstant.ANALYSIS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_ANALYSIS;
        } else if (EResourceConstant.REPORTS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_REPORTS;
        } else if (EResourceConstant.LIBRARIES.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_LIBRARIES;
        } else if (EResourceConstant.EXCHANGE.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_EXCHANGE;
        } else if (EResourceConstant.INDICATORS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_INDICATORS;
        } else if (EResourceConstant.SYSTEM_INDICATORS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_SYSTEM_INDICATORS;
        } else if (EResourceConstant.SYSTEM_INDICATORS_ADVANCED_STATISTICS.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_ADVANCED_STATISTICS;
        } else if (EResourceConstant.SYSTEM_INDICATORS_BUSINESS_RULES.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_BUSINESS_RULES;
        } else if (EResourceConstant.SYSTEM_INDICATORS_CORRELATION.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_CORRELATION;
        } else if (EResourceConstant.SYSTEM_INDICATORS_FUNCTIONAL_DEPENDENCY.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_FUNCTIONAL_DEPENDENCY;
        } else if (EResourceConstant.SYSTEM_INDICATORS_OVERVIEW.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_OVERVIEW;
        } else if (EResourceConstant.SYSTEM_INDICATORS_PATTERN_FINDER.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_FINDER;
        } else if (EResourceConstant.SYSTEM_INDICATORS_PATTERN_MATCHING.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_MATCHING;
        } else if (EResourceConstant.SYSTEM_INDICATORS_ROW_COMPARISON.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_ROW_COMPARISON;
        } else if (EResourceConstant.SYSTEM_INDICATORS_SIMPLE_STATISTICS.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_SIMPLE_STATISTICS;
        } else if (EResourceConstant.SYSTEM_INDICATORS_SOUNDEX.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_SOUNDEX;
        } else if (EResourceConstant.SYSTEM_INDICATORS_SUMMARY_STATISTICS.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_SUMMARY_STATISTICS;
        } else if (EResourceConstant.SYSTEM_INDICATORS_TEXT_STATISTICS.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_TEXT_STATISTICS;
        } else if (EResourceConstant.USER_DEFINED_INDICATORS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS;
        } else if (EResourceConstant.JRXML_TEMPLATE.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_JRXMLTEMPLATE;
        } else if (EResourceConstant.PATTERNS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_PATTERNS;
        } else if (EResourceConstant.PATTERN_REGEX.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_PATTERN_REGEX;
        } else if (EResourceConstant.PATTERN_SQL.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_PATTERN_SQL;
        } else if (EResourceConstant.RULES.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_RULES;
        } else if (EResourceConstant.RULES_SQL.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_RULES_SQL;
        } else if (EResourceConstant.SOURCE_FILES.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_SOURCE_FILES;
        } else if (EResourceConstant.METADATA.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA;
        } else if (EResourceConstant.DB_CONNECTIONS.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA_CONNECTIONS;
        } else if (EResourceConstant.MDM_CONNECTIONS.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA_MDMCONNECTION;
        }
        return null;
    }

    // /**
    // * DOC klliu Comment method "createRepositoryNodeTdXmlSchema".
    // *
    // * @param node
    // * @param viewObject
    // * @param tdXmlSchema
    // */
    // public void createRepositoryNodeTdXmlSchema(RepositoryNode node, IRepositoryViewObject viewObject,
    // TdXmlSchemaImpl tdXmlSchema) {
    // MetadataXmlSchemaRepositoryObject metadataXmlSchema = new MetadataXmlSchemaRepositoryObject(viewObject,
    // tdXmlSchema);
    // RepositoryNode xmlSchemaNode = new RepositoryNode(metadataXmlSchema, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // xmlSchemaNode.setProperties(EProperties.LABEL, ERepositoryObjectType.MDM_SCHEMA);
    // xmlSchemaNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_CATALOG);
    // metadataXmlSchema.setRepositoryNode(xmlSchemaNode);
    // node.getChildren().add(xmlSchemaNode);
    // }

    // /**
    // * DOC klliu Comment method "createRepositoryNodeCatalog".
    // *
    // * @param node
    // * @param viewObject
    // * @param catalog
    // */
    // public void createRepositoryNodeCatalog(RepositoryNode node, IRepositoryViewObject viewObject,
    // orgomg.cwm.resource.relational.Catalog catalog) {
    // MetadataCatalogRepositoryObject metadataCatalog = new MetadataCatalogRepositoryObject(viewObject, catalog);
    // RepositoryNode catalogNode = new DBCatalogRepNode(metadataCatalog, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // catalogNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_CATALOG);
    // catalogNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_CATALOG);
    // metadataCatalog.setRepositoryNode(catalogNode);
    // node.getChildren().add(catalogNode);
    // }
    //
    // /**
    // * DOC klliu Comment method "createRepositoryNodeSchema".
    // *
    // * @param node
    // * @param viewObject
    // * @param schema
    // */
    // public void createRepositoryNodeSchema(RepositoryNode node, IRepositoryViewObject viewObject,
    // orgomg.cwm.resource.relational.Schema schema) {
    // MetadataSchemaRepositoryObject metadataSchema = new MetadataSchemaRepositoryObject(viewObject, schema);
    // RepositoryNode schemaNode = new DBSchemaRepNode(metadataSchema, node, ENodeType.TDQ_REPOSITORY_ELEMENT);
    // schemaNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_SCHEMA);
    // schemaNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_SCHEMA);
    // metadataSchema.setRepositoryNode(schemaNode);
    // node.getChildren().add(schemaNode);
    // }

    /**
     * DOC klliu Comment method "createRepositoryNodeViewObjects".
     * 
     * @param node
     * @param path
     * @param repositoryObjectType
     * @param withDelete
     * @return
     */
    public List<IRepositoryNode> createRepositoryNodeViewObjects(RepositoryNode node, IPath path,
            ERepositoryObjectType repositoryObjectType, boolean withDelete) {
        List<IRepositoryViewObject> conList = ProxyRepositoryViewObject.fetchRepositoryViewObjectsByFolder(true,
                repositoryObjectType, null, withDelete, node);
        List<IRepositoryNode> repositoryNodes = getRepositoryViewObjectChildren(conList, node, withDelete);
        node.getChildren().addAll(repositoryNodes);
        return repositoryNodes;
    }

    /**
     * 
     * DOC klliu Comment method "createRepositoryNode".
     * 
     * @param node
     * @param path
     * @param repositoryObjectType
     * @param withDelete
     * @return
     */
    public IRepositoryNode createRepositoryNode(RepositoryNode node, IPath path, ERepositoryObjectType repositoryObjectType,
            boolean withDelete) {
        ProxyRepositoryViewObject.fetchRepositoryNodeByFolder(true, repositoryObjectType, null, withDelete, node);

        return node;
    }

    /**
     * DOC klliu Comment method "createSystemFolder".
     * 
     * @param node
     * @param resConstant
     * @return
     * @throws PersistenceException
     */
    public RepositoryNode createRepositoryNodeSystemFolder(FolderHelper folderHelper, RepositoryNode node,
            EResourceConstant resConstant) throws PersistenceException {
        IRepositoryViewObject folder = null;
        RepositoryNode subFolderNode = null;
        if (folderHelper != null) {
            FolderItem folder2 = folderHelper.getFolder(resConstant.getPath());
            folder = new Folder(folder2.getProperty(), retrieveRepObjectTypeByPath(resConstant.getPath()));
        } else {
            // folder = new Folder(resConstant.getName(), resConstant.getName());
            folder = ProxyRepositoryFactory.getInstance().createFolder(retrieveRepObjectTypeByPath(resConstant.getPath()),
                    Path.EMPTY, resConstant.getName());
        }
        switch (resConstant) {
        case ANALYSIS:
            AnalysisFolderRepNode anaFolderNode = new AnalysisFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(anaFolderNode);
            node.getChildren().add(anaFolderNode);
            return anaFolderNode;
        case REPORTS:
            ReportFolderRepNode repFolderNode = new ReportFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(repFolderNode);
            node.getChildren().add(repFolderNode);
            return repFolderNode;
        case SYSTEM_INDICATORS:
            IndicatorFolderRepNode systemIndicatorFolderNode = new IndicatorFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(systemIndicatorFolderNode);
            node.getChildren().add(systemIndicatorFolderNode);
            return systemIndicatorFolderNode;
        case USER_DEFINED_INDICATORS:
            UserDefIndicatorFolderRepNode userDefFolderNode = new UserDefIndicatorFolderRepNode(folder, node,
                    ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(userDefFolderNode);
            node.getChildren().add(userDefFolderNode);
            return userDefFolderNode;
        case JRXML_TEMPLATE:
            JrxmlTempFolderRepNode jrxmlFolderNode = new JrxmlTempFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(jrxmlFolderNode);
            node.getChildren().add(jrxmlFolderNode);
            return jrxmlFolderNode;
        case SOURCE_FILES:
            SourceFileFolderRepNode sourceFileFolder = new SourceFileFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(sourceFileFolder);
            node.getChildren().add(sourceFileFolder);
            return sourceFileFolder;
        case PATTERN_REGEX:
            PatternRegexFolderRepNode regexFolder = new PatternRegexFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(regexFolder);
            node.getChildren().add(regexFolder);
            return regexFolder;
        case PATTERN_SQL:
            PatternSqlFolderRepNode sqlFolder = new PatternSqlFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(sqlFolder);
            node.getChildren().add(sqlFolder);
            return sqlFolder;
        case RULES_SQL:
            RulesFolderRepNode ruleFolder = new RulesFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(ruleFolder);
            node.getChildren().add(ruleFolder);
            return ruleFolder;
        case DB_CONNECTIONS:
            DBConnectionFolderRepNode dbFolder = new DBConnectionFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(dbFolder);
            node.getChildren().add(dbFolder);
            return dbFolder;
        case MDM_CONNECTIONS:
            MDMConnectionFolderRepNode mdmFolder = new MDMConnectionFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(mdmFolder);
            node.getChildren().add(mdmFolder);
            return mdmFolder;
        case EXCHANGE:
            ExchangeFolderRepNode exchangeFolder = new ExchangeFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(exchangeFolder);
            node.getChildren().add(exchangeFolder);
            return exchangeFolder;
        default:
            subFolderNode = new RepositoryNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(subFolderNode);
            node.getChildren().add(subFolderNode);
            break;
        }
        return subFolderNode;
    }

    /**
     * DOC klliu Comment method "createRepositoryNodeSystemFolders".
     * 
     * @param node
     * @param resConstants
     * @return
     * @throws PersistenceException
     */
    public List<RepositoryNode> createRepositoryNodeSystemFolders(FolderHelper folderHelper, RepositoryNode node,
            List<EResourceConstant> resConstants) throws PersistenceException {
        List<RepositoryNode> repositoryNodes = new ArrayList<RepositoryNode>();
        for (EResourceConstant resConstant : resConstants) {
            repositoryNodes.add(createRepositoryNodeSystemFolder(folderHelper, node, resConstant));
        }
        return repositoryNodes;
    }

    /**
     * DOC klliu Comment method "getRepositoryViewObjectChildren".
     * 
     * @param repositoryViewObjects
     * @param node
     * @param withDelete
     * @return
     */
    public List<IRepositoryNode> getRepositoryViewObjectChildren(List<IRepositoryViewObject> repositoryViewObjects,
            RepositoryNode node, boolean withDelete) {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        Iterator<IRepositoryViewObject> iterator = repositoryViewObjects.iterator();
        while (iterator.hasNext()) {
            IRepositoryViewObject viewObject = iterator.next();
            if (viewObject.isDeleted() && !withDelete) {
                iterator.remove();
            } else {
                rectiveNodebyFolderNode(list, viewObject, node);
            }
        }
        return list;
    }

    /**
     * DOC klliu Comment method "rectiveNodebyFolderNode".
     * 
     * @param list
     * @param folderNode
     * @return
     */
    public RepositoryNode rectiveNodebyFolderNode(List<IRepositoryNode> list, IRepositoryViewObject viewObject,
            RepositoryNode folderNode) {
        if (folderNode instanceof AnalysisFolderRepNode) {
            AnalysisRepNode anaNode = new AnalysisRepNode(viewObject, folderNode, ENodeType.REPOSITORY_ELEMENT);
            viewObject.setRepositoryNode(anaNode);
            list.add(anaNode);
            return anaNode;
        } else if (folderNode instanceof DBConnectionFolderRepNode) {
            RepositoryNode connNode = new DBConnectionRepNode(viewObject, folderNode, ENodeType.REPOSITORY_ELEMENT);
            viewObject.setRepositoryNode(connNode);
            list.add(connNode);
            return connNode;
        }
        return null;
    }

    /**
     * DOC klliu Comment method "getIndicatorsChildren".
     * 
     * @param conList
     * @return
     */
    public Collection<? extends IRepositoryNode> getIndicatorsChildren(List<IRepositoryViewObject> conList) {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        Iterator<IRepositoryViewObject> iterator = conList.iterator();
        while (iterator.hasNext()) {
            IRepositoryViewObject indicatorFolder = iterator.next();
            list.add(new RepositoryNode(indicatorFolder, null, ENodeType.SYSTEM_FOLDER));
        }
        return list;
    }

    public FolderHelper getFolderHelper() {
        RepositoryContext repositoryContext = (RepositoryContext) CoreRuntimePlugin.getInstance().getContext()
                .getProperty(Context.REPOSITORY_CONTEXT_KEY);
        Project project = null;
        FolderHelper folderHelper = null;
        if (repositoryContext != null) {
            project = repositoryContext.getProject();
            if (project != null) {
                folderHelper = LocalFolderHelper.createInstance(project.getEmfProject(), repositoryContext.getUser());
            }
        }
        return folderHelper;
    }

    /**
     * DOC xqliu Comment method "getIndicatorsChildren".
     * 
     * @param category
     * @return
     */
    public Object[] getIndicatorsChildren(IndicatorCategory category) {
        List<IndicatorDefinition> indicatorDefinitionList = CategoryHandler.getIndicatorDefinitionList(category);
        if (indicatorDefinitionList == null) {
            indicatorDefinitionList = new ArrayList<IndicatorDefinition>();
        }
        return indicatorDefinitionList.toArray();
    }

    /**
     * DOC xqliu Comment method "getIndicatorChildren".
     * 
     * @return
     */
    public Object[] getIndicatorsChildren(IFolder folder) {
        List<Object> list = new ArrayList<Object>();
        // MOD mzhao feature 13676, split system indicators. 2010-07-08
        // list.add(new IndicatorFolderNode("System"));
        try {
            list.addAll(Arrays.asList(folder.members()));
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return list.toArray();
    }

    /**
     * DOC klliu Comment method "getInstance".
     * 
     * @return
     */
    public static RepositoryNodeBuilder getInstance() {
        if (instance == null) {
            instance = new RepositoryNodeBuilder();
        }
        return instance;
    }

}
