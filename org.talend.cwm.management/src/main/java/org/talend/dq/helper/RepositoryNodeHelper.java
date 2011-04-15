// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.model.ISubRepositoryObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.reports.TdReport;
import org.talend.dataquality.rules.DQRule;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.nodes.AnalysisFolderRepNode;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.AnalysisSubFolderRepNode;
import org.talend.dq.nodes.DBCatalogRepNode;
import org.talend.dq.nodes.DBColumnFolderRepNode;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DBConnectionFolderRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DBConnectionSubFolderRepNode;
import org.talend.dq.nodes.DBSchemaRepNode;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewFolderRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFColumnRepNode;
import org.talend.dq.nodes.DFConnectionFolderRepNode;
import org.talend.dq.nodes.DFConnectionRepNode;
import org.talend.dq.nodes.DFConnectionSubFolderRepNode;
import org.talend.dq.nodes.DFTableRepNode;
import org.talend.dq.nodes.JrxmlTempFolderRepNode;
import org.talend.dq.nodes.JrxmlTempleteRepNode;
import org.talend.dq.nodes.MDMConnectionFolderRepNode;
import org.talend.dq.nodes.MDMConnectionRepNode;
import org.talend.dq.nodes.MDMConnectionSubFolderRepNode;
import org.talend.dq.nodes.MDMSchemaRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.dq.nodes.PatternRegexFolderRepNode;
import org.talend.dq.nodes.PatternRegexSubFolderRepNode;
import org.talend.dq.nodes.PatternRepNode;
import org.talend.dq.nodes.PatternSqlFolderRepNode;
import org.talend.dq.nodes.PatternSqlSubFolderRepNode;
import org.talend.dq.nodes.RecycleBinRepNode;
import org.talend.dq.nodes.ReportAnalysisRepNode;
import org.talend.dq.nodes.ReportFileRepNode;
import org.talend.dq.nodes.ReportFolderRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.ReportSubFolderRepNode;
import org.talend.dq.nodes.RuleRepNode;
import org.talend.dq.nodes.RulesFolderRepNode;
import org.talend.dq.nodes.RulesSubFolderRepNode;
import org.talend.dq.nodes.SourceFileFolderRepNode;
import org.talend.dq.nodes.SourceFileRepNode;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.dq.nodes.SysIndicatorFolderRepNode;
import org.talend.dq.nodes.UserDefIndicatorFolderRepNode;
import org.talend.dq.nodes.UserDefIndicatorSubFolderRepNode;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.record.RecordFile;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * Helper class for RepositoryNode.
 */
public final class RepositoryNodeHelper {

    public static final String DQRESPOSITORYVIEW = "org.talend.dataprofiler.core.ui.views.DQRespositoryView"; //$NON-NLS-1$

    public static final String DI_REPOSITORY_NAME = "Repository"; //$NON-NLS-1$

    private RepositoryNodeHelper() {
    }

    public static IPath getPath(IRepositoryNode node) {
        if (node == null) {
            return null;
        }
        if (node.isBin()) {
            return new Path(""); //$NON-NLS-1$
        }
        if (node.getType() == null) {
            return null;
        }
        switch (node.getType()) {
        case SYSTEM_FOLDER:
            ERepositoryObjectType contentType = node.getContentType();
            if (contentType == null) {
                Item item = node.getObject().getProperty().getItem();
                contentType = ERepositoryObjectType.getItemType(item);
            }
            return new Path(ERepositoryObjectType.getFolderName(contentType)); //$NON-NLS-1$
        case SIMPLE_FOLDER:
            String label = "";//$NON-NLS-1$
            if (node.getObject() != null) {
                label = node.getObject().getProperty().getLabel();
            }
            if (node instanceof ReportSubFolderRepNode) {
                ReportSubFolderRepNode reportSubFolderRepNode = (ReportSubFolderRepNode) node;
                label = reportSubFolderRepNode.getLabel();
            }
            return getPath(node.getParent()).append(label);
        default:
        }
        return getPath(node.getParent());
    }

    public static ERepositoryObjectType retrieveRepObjectTypeByPath(String path) {
        if (EResourceConstant.DATA_PROFILING.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_DATA_PROFILING;
        } else if (EResourceConstant.ANALYSIS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT;
        } else if (EResourceConstant.REPORTS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_REPORT_ELEMENT;
        } else if (EResourceConstant.LIBRARIES.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_LIBRARIES;
        } else if (EResourceConstant.EXCHANGE.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_EXCHANGE;
        } else if (EResourceConstant.INDICATORS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_INDICATOR_ELEMENT;
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
            return ERepositoryObjectType.TDQ_JRAXML_ELEMENT;
        } else if (EResourceConstant.PATTERNS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_PATTERN_ELEMENT;
        } else if (EResourceConstant.PATTERN_REGEX.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_PATTERN_REGEX;
        } else if (EResourceConstant.PATTERN_SQL.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_PATTERN_SQL;
        } else if (EResourceConstant.RULES.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_RULES;
        } else if (EResourceConstant.RULES_SQL.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_RULES_SQL;
        } else if (EResourceConstant.SOURCE_FILES.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT;
        } else if (EResourceConstant.METADATA.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA;
        } else if (EResourceConstant.DB_CONNECTIONS.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA_CONNECTIONS;
        } else if (EResourceConstant.MDM_CONNECTIONS.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA_MDMCONNECTION;
        } else if (EResourceConstant.FILEDELIMITED.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA_FILE_DELIMITED;
        }
        return null;
    }

    public static List<DBColumnRepNode> getColumnNodeList(Object[] objs) {
        List<DBColumnRepNode> nodeList = new ArrayList<DBColumnRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBColumnRepNode) {
                nodeList.add((DBColumnRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasColumnNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBColumnRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<DBTableRepNode> getTableNodeList(Object[] objs) {
        List<DBTableRepNode> nodeList = new ArrayList<DBTableRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBTableRepNode) {
                nodeList.add((DBTableRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasTableNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBTableRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<DBViewRepNode> getViewNodeList(Object[] objs) {
        List<DBViewRepNode> nodeList = new ArrayList<DBViewRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBViewRepNode) {
                nodeList.add((DBViewRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasViewNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBViewRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<DBSchemaRepNode> getSchemaNodeList(Object[] objs) {
        List<DBSchemaRepNode> nodeList = new ArrayList<DBSchemaRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBSchemaRepNode) {
                nodeList.add((DBSchemaRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasSchemaNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBSchemaRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<DBCatalogRepNode> getCatalogNodeList(Object[] objs) {
        List<DBCatalogRepNode> nodeList = new ArrayList<DBCatalogRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBCatalogRepNode) {
                nodeList.add((DBCatalogRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasCatalogNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBCatalogRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<DBConnectionRepNode> getDbConnectionNodeList(Object[] objs) {
        List<DBConnectionRepNode> nodeList = new ArrayList<DBConnectionRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBConnectionRepNode) {
                nodeList.add((DBConnectionRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasDbConnectionNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBConnectionRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<MDMConnectionRepNode> getMdmConnectionNodeList(Object[] objs) {
        List<MDMConnectionRepNode> nodeList = new ArrayList<MDMConnectionRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof MDMConnectionRepNode) {
                nodeList.add((MDMConnectionRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasMdmConnectionNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof MDMConnectionRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<TdColumn> getTdColumnList(Object[] objs) {
        List<TdColumn> list = new ArrayList<TdColumn>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof TdColumn) {
                list.add((TdColumn) obj);
            }
        }
        return list;
    }

    public static boolean hasTdColumn(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof TdColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * find RepositoryNode by property, if no RepositoryNode, return null.
     * 
     * @param property
     * @return
     */
    public static RepositoryNode recursiveFind(Property property) {
        ModelElement resourceModelElement = getResourceModelElement(property.getItem());
        if (resourceModelElement != null) {
            return recursiveFind(resourceModelElement);
        }

        return null;
    }

    /**
     * find RepositoryNode by ModelElement, if no RepositoryNode, return null.
     * 
     * @param modelElement
     * @return
     */
    public static RepositoryNode recursiveFind(ModelElement modelElement) {
        RepositoryNode node = null;
        if (modelElement instanceof Analysis) {
            node = recursiveFindAnalysis((Analysis) modelElement);
        } else if (modelElement instanceof TdReport) {
            node = recursiveFindReport((Report) modelElement);
        } else if (modelElement instanceof DatabaseConnection) {
            node = recursiveFindDatabaseConnection((DatabaseConnection) modelElement);
        } else if (modelElement instanceof Catalog) {
            node = recursiveFindCatalog((Catalog) modelElement);
        } else if (modelElement instanceof Schema) {
            node = recursiveFindSchema((Schema) modelElement);
        } else if (modelElement instanceof TdTable) {
            node = recursiveFindTdTable((TdTable) modelElement);
        } else if (modelElement instanceof TdView) {
            node = recursiveFindTdView((TdView) modelElement);
        } else if (modelElement instanceof TdColumn) {
            node = recursiveFindTdColumn((TdColumn) modelElement);
        } else if (modelElement instanceof MDMConnection) {
            node = recursiveFindMDMConnection((MDMConnection) modelElement);
        } else if (modelElement instanceof TdXmlSchema) {
            node = recursiveFindTdXmlSchema((TdXmlSchema) modelElement);
        } else if (modelElement instanceof TdXmlElementType) {
            node = recursiveFindTdXmlElementType((TdXmlElementType) modelElement);
        } else if (modelElement instanceof DelimitedFileConnection) {
            node = recursiveFindDFConnection((DelimitedFileConnection) modelElement);
        } else if (modelElement instanceof MetadataTable) {
            node = recursiveFindMetadataTable((MetadataTable) modelElement);
        } else if (modelElement instanceof MetadataColumn) {
            node = recursiveFindMetadataColumn((MetadataColumn) modelElement);
        } else if (modelElement instanceof Pattern) {
            node = recursiveFindPattern((Pattern) modelElement);
        } else if (modelElement instanceof IndicatorDefinition) {
            if (modelElement instanceof WhereRule) {
                node = recursiveFindRuleSql((WhereRule) modelElement);
            } else {
                node = recursiveFindIndicatorDefinition((IndicatorDefinition) modelElement);
            }
        }
        return node;
        // !!!following codes are testing codes, please don't delete them, thanks!!!
        // RepositoryNode repNode = recursiveFindByUuid(uuid, nodes);
        // if (repNode != null) {
        // System.out.println("[class name: " + repNode.getClass().getName() + "][uuid: " + uuid + "][id: " +
        // repNode.getId()
        // + "][label: " + repNode.getLabel() + "]");
        // } else {
        // System.out.println("[class name: " + modelElement.getClass().getName() + "][uuid: " + uuid + "][name: "
        // + modelElement.getName() + "] NOT FOUND!!!");
        // }
        // return repNode;
    }

    public static List<DBConnectionRepNode> getDBConnectionRepNodes(IRepositoryNode parrentNode, boolean recursiveFind,
            boolean withDeleted) {
        List<DBConnectionRepNode> result = new ArrayList<DBConnectionRepNode>();
        if (parrentNode != null
                && (parrentNode instanceof DBConnectionFolderRepNode || parrentNode instanceof DBConnectionSubFolderRepNode)) {
            List<IRepositoryNode> children = parrentNode.getChildren(withDeleted);
            if (children.size() > 0) {
                for (IRepositoryNode inode : children) {
                    if (inode instanceof DBConnectionRepNode) {
                        result.add((DBConnectionRepNode) inode);
                    } else if (inode instanceof DBConnectionFolderRepNode || inode instanceof DBConnectionSubFolderRepNode) {
                        if (recursiveFind) {
                            result.addAll(getDBConnectionRepNodes(inode, recursiveFind, withDeleted));
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<MDMConnectionRepNode> getMDMConnectionRepNodes(IRepositoryNode parrentNode, boolean recursiveFind,
            boolean withDeleted) {
        List<MDMConnectionRepNode> result = new ArrayList<MDMConnectionRepNode>();
        if (parrentNode != null
                && (parrentNode instanceof MDMConnectionFolderRepNode || parrentNode instanceof MDMConnectionSubFolderRepNode)) {
            List<IRepositoryNode> children = parrentNode.getChildren(withDeleted);
            if (children.size() > 0) {
                for (IRepositoryNode inode : children) {
                    if (inode instanceof MDMConnectionRepNode) {
                        result.add((MDMConnectionRepNode) inode);
                    } else if (inode instanceof MDMConnectionFolderRepNode || inode instanceof MDMConnectionSubFolderRepNode) {
                        if (recursiveFind) {
                            result.addAll(getMDMConnectionRepNodes(inode, recursiveFind, withDeleted));
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<DFConnectionRepNode> getDFConnectionRepNodes(IRepositoryNode parrentNode, boolean recursiveFind,
            boolean withDeleted) {
        List<DFConnectionRepNode> result = new ArrayList<DFConnectionRepNode>();
        if (parrentNode != null
                && (parrentNode instanceof DFConnectionFolderRepNode || parrentNode instanceof DFConnectionSubFolderRepNode)) {
            List<IRepositoryNode> children = parrentNode.getChildren(withDeleted);
            if (children.size() > 0) {
                for (IRepositoryNode inode : children) {
                    if (inode instanceof DFConnectionRepNode) {
                        result.add((DFConnectionRepNode) inode);
                    } else if (inode instanceof DFConnectionFolderRepNode || inode instanceof DFConnectionSubFolderRepNode) {
                        if (recursiveFind) {
                            result.addAll(getDFConnectionRepNodes(inode, recursiveFind, withDeleted));
                        }
                    }
                }
            }
        }
        return result;
    }

    // MOD qiongli 2011-4-6,bug 20218,Add parameter 'withDeleted'.
    public static List<AnalysisRepNode> getAnalysisRepNodes(IRepositoryNode parrentNode, boolean recursiveFind,
            boolean withDeleted) {
        List<AnalysisRepNode> result = new ArrayList<AnalysisRepNode>();
        if (parrentNode != null
                && (parrentNode instanceof AnalysisFolderRepNode || parrentNode instanceof AnalysisSubFolderRepNode)) {
            List<IRepositoryNode> children = parrentNode.getChildren(withDeleted);
            if (children.size() > 0) {
                for (IRepositoryNode inode : children) {
                    if (inode instanceof AnalysisRepNode) {
                        result.add((AnalysisRepNode) inode);
                    } else if (inode instanceof AnalysisFolderRepNode || inode instanceof AnalysisSubFolderRepNode) {
                        if (recursiveFind) {
                            result.addAll(getAnalysisRepNodes(inode, recursiveFind, withDeleted));
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<ReportRepNode> getReportRepNodes(IRepositoryNode parrentNode, boolean recursiveFind, boolean withDeleted) {
        List<ReportRepNode> result = new ArrayList<ReportRepNode>();
        if (parrentNode != null && (parrentNode instanceof ReportFolderRepNode || parrentNode instanceof ReportSubFolderRepNode)) {
            List<IRepositoryNode> children = parrentNode.getChildren(withDeleted);
            if (children.size() > 0) {
                for (IRepositoryNode inode : children) {
                    if (inode instanceof ReportRepNode) {
                        result.add((ReportRepNode) inode);
                    } else if (inode instanceof ReportFolderRepNode || inode instanceof ReportSubFolderRepNode) {
                        if (recursiveFind) {
                            result.addAll(getReportRepNodes(inode, recursiveFind, withDeleted));
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<SysIndicatorDefinitionRepNode> getIndicatorDefinitionRepNodes(IRepositoryNode parrentNode,
            boolean recursiveFind, boolean withDeleted) {
        List<SysIndicatorDefinitionRepNode> result = new ArrayList<SysIndicatorDefinitionRepNode>();
        if (parrentNode != null
                && (parrentNode instanceof SysIndicatorFolderRepNode || parrentNode instanceof UserDefIndicatorFolderRepNode || parrentNode instanceof UserDefIndicatorSubFolderRepNode)) {
            List<IRepositoryNode> children = parrentNode.getChildren(withDeleted);
            if (children.size() > 0) {
                for (IRepositoryNode inode : children) {
                    if (inode instanceof SysIndicatorDefinitionRepNode) {
                        result.add((SysIndicatorDefinitionRepNode) inode);
                    } else if (inode instanceof SysIndicatorFolderRepNode || inode instanceof UserDefIndicatorFolderRepNode
                            || inode instanceof UserDefIndicatorSubFolderRepNode) {
                        if (recursiveFind) {
                            result.addAll(getIndicatorDefinitionRepNodes(inode, recursiveFind, withDeleted));
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<PatternRepNode> getPatternRepNodes(IRepositoryNode parrentNode, boolean recursiveFind, boolean withDeleted) {
        List<PatternRepNode> result = new ArrayList<PatternRepNode>();
        if (parrentNode != null
                && (parrentNode instanceof PatternRegexFolderRepNode || parrentNode instanceof PatternRegexSubFolderRepNode
                        || parrentNode instanceof PatternSqlFolderRepNode || parrentNode instanceof PatternSqlSubFolderRepNode)) {
            List<IRepositoryNode> children = parrentNode.getChildren(withDeleted);
            if (children.size() > 0) {
                for (IRepositoryNode inode : children) {
                    if (inode instanceof PatternRepNode) {
                        result.add((PatternRepNode) inode);
                    } else if (inode instanceof PatternRegexFolderRepNode || inode instanceof PatternRegexSubFolderRepNode
                            || inode instanceof PatternSqlFolderRepNode || inode instanceof PatternSqlSubFolderRepNode) {
                        if (recursiveFind) {
                            result.addAll(getPatternRepNodes(inode, recursiveFind, withDeleted));
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<RuleRepNode> getRuleRepNodes(IRepositoryNode parrentNode, boolean recursiveFind, boolean withDeleted) {
        List<RuleRepNode> result = new ArrayList<RuleRepNode>();
        if (parrentNode != null && (parrentNode instanceof RulesFolderRepNode || parrentNode instanceof RulesSubFolderRepNode)) {
            List<IRepositoryNode> children = parrentNode.getChildren(withDeleted);
            if (children.size() > 0) {
                for (IRepositoryNode inode : children) {
                    if (inode instanceof RuleRepNode) {
                        result.add((RuleRepNode) inode);
                    } else if (inode instanceof RulesFolderRepNode || inode instanceof RulesSubFolderRepNode) {
                        if (recursiveFind) {
                            result.addAll(getRuleRepNodes(inode, recursiveFind, withDeleted));
                        }
                    }
                }
            }
        }
        return result;
    }

    public static DBConnectionRepNode recursiveFindDatabaseConnection(DatabaseConnection dbConn) {
        if (dbConn == null) {
            return null;
        }
        String uuid = ResourceHelper.getUUID(dbConn);
        if (uuid == null) {
            return null;
        }
        List<DBConnectionRepNode> dbConnectionRepNodes = getDBConnectionRepNodes(
                getMetadataFolderNode(EResourceConstant.DB_CONNECTIONS), true, true);
        if (dbConnectionRepNodes.size() > 0) {
            for (DBConnectionRepNode childNode : dbConnectionRepNodes) {
                if (uuid.equals(ResourceHelper.getUUID(childNode.getDatabaseConnection()))) {
                    return childNode;
                }
            }
        }
        return null;
    }

    public static MDMConnectionRepNode recursiveFindMDMConnection(MDMConnection mdmConn) {
        if (mdmConn == null) {
            return null;
        }
        String uuid = ResourceHelper.getUUID(mdmConn);
        if (uuid == null) {
            return null;
        }
        List<MDMConnectionRepNode> mdmConnectionRepNodes = getMDMConnectionRepNodes(
                getMetadataFolderNode(EResourceConstant.MDM_CONNECTIONS), true, true);
        if (mdmConnectionRepNodes.size() > 0) {
            for (MDMConnectionRepNode childNode : mdmConnectionRepNodes) {
                if (uuid.equals(ResourceHelper.getUUID(childNode.getMdmConnection()))) {
                    return childNode;
                }
            }
        }
        return null;
    }

    public static DFConnectionRepNode recursiveFindDFConnection(DelimitedFileConnection dfConn) {
        if (dfConn == null) {
            return null;
        }
        String uuid = ResourceHelper.getUUID(dfConn);
        if (uuid == null) {
            return null;
        }
        List<DFConnectionRepNode> dfConnectionRepNodes = getDFConnectionRepNodes(
                getMetadataFolderNode(EResourceConstant.FILEDELIMITED), true, true);
        if (dfConnectionRepNodes.size() > 0) {
            for (DFConnectionRepNode childNode : dfConnectionRepNodes) {
                if (uuid.equals(ResourceHelper.getUUID(childNode.getDfConnection()))) {
                    return childNode;
                }
            }
        }
        return null;
    }

    public static AnalysisRepNode recursiveFindAnalysis(Analysis analysis) {
        if (analysis == null) {
            return null;
        }
        String uuid = ResourceHelper.getUUID(analysis);
        if (uuid == null) {
            return null;
        }
        // MOD qiongli 2011-4-6,bug 20218,add parameter withDeleted(true), contain child is in recycle bin.
        List<AnalysisRepNode> analysisRepNodes = getAnalysisRepNodes(getDataProfilingFolderNode(EResourceConstant.ANALYSIS),
                true, true);
        if (analysisRepNodes.size() > 0) {
            for (AnalysisRepNode childNode : analysisRepNodes) {
                if (uuid.equals(ResourceHelper.getUUID(childNode.getAnalysis()))) {
                    return childNode;
                }
            }
        }
        return null;
    }

    public static ReportRepNode recursiveFindReport(Report report) {
        if (report == null) {
            return null;
        }
        String uuid = ResourceHelper.getUUID(report);
        if (uuid == null) {
            return null;
        }
        List<ReportRepNode> reportRepNodes = getReportRepNodes(getDataProfilingFolderNode(EResourceConstant.REPORTS), true, true);
        if (reportRepNodes.size() > 0) {
            for (ReportRepNode childNode : reportRepNodes) {
                if (uuid.equals(ResourceHelper.getUUID(childNode.getReport()))) {
                    return childNode;
                }
            }
        }
        return null;
    }

    public static SysIndicatorDefinitionRepNode recursiveFindIndicatorDefinition(IndicatorDefinition indDef) {
        if (indDef == null) {
            return null;
        }
        String uuid = ResourceHelper.getUUID(indDef);
        if (uuid == null) {
            return null;
        }
        List<SysIndicatorDefinitionRepNode> indicatorDefinitionRepNodes = getIndicatorDefinitionRepNodes(
                getLibrariesFolderNode(EResourceConstant.SYSTEM_INDICATORS), true, true);
        indicatorDefinitionRepNodes.addAll(getIndicatorDefinitionRepNodes(
                getLibrariesFolderNode(EResourceConstant.USER_DEFINED_INDICATORS), true, true));
        if (indicatorDefinitionRepNodes.size() > 0) {
            for (SysIndicatorDefinitionRepNode childNode : indicatorDefinitionRepNodes) {
                if (uuid.equals(ResourceHelper.getUUID(childNode.getIndicatorDefinition()))) {
                    return childNode;
                }
            }
        }
        return null;
    }

    public static PatternRepNode recursiveFindPattern(Pattern pattern) {
        if (pattern == null) {
            return null;
        }
        String uuid = ResourceHelper.getUUID(pattern);
        if (uuid == null) {
            return null;
        }
        List<PatternRepNode> patternRepNodes = getPatternRepNodes(getLibrariesFolderNode(EResourceConstant.PATTERN_REGEX), true,
                true);
        patternRepNodes.addAll(getPatternRepNodes(getLibrariesFolderNode(EResourceConstant.PATTERN_SQL), true, true));
        if (patternRepNodes.size() > 0) {
            for (PatternRepNode childNode : patternRepNodes) {
                if (uuid.equals(ResourceHelper.getUUID(childNode.getPattern()))) {
                    return childNode;
                }
            }
        }
        return null;
    }

    public static RuleRepNode recursiveFindRuleSql(DQRule rule) {
        if (rule == null) {
            return null;
        }
        String uuid = ResourceHelper.getUUID(rule);
        if (uuid == null) {
            return null;
        }
        List<RuleRepNode> ruleRepNodes = getRuleRepNodes(getLibrariesFolderNode(EResourceConstant.RULES_SQL), true, true);
        if (ruleRepNodes.size() > 0) {
            for (RuleRepNode childNode : ruleRepNodes) {
                if (uuid.equals(ResourceHelper.getUUID(childNode.getRule()))) {
                    return childNode;
                }
            }
        }
        return null;
    }

    public static DBCatalogRepNode recursiveFindCatalog(Catalog catalog) {
        if (catalog == null) {
            return null;
        }
        String uuidCatalog = ResourceHelper.getUUID(catalog);
        if (uuidCatalog == null) {
            return null;
        }
        IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(catalog));
        if (connNode == null) {
            return null;
        }
        List<IRepositoryNode> children = connNode.getChildren();
        if (children.size() > 0) {
            IRepositoryNode iRepositoryNode = children.get(0);
            if (iRepositoryNode != null && iRepositoryNode instanceof DBCatalogRepNode) {
                for (IRepositoryNode childNode : children) {
                    if (childNode != null && childNode instanceof DBCatalogRepNode) {
                        DBCatalogRepNode dbCatalogRepNode = (DBCatalogRepNode) childNode;
                        if (uuidCatalog.equals(ResourceHelper.getUUID(dbCatalogRepNode.getCatalog()))) {
                            return dbCatalogRepNode;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static DBSchemaRepNode recursiveFindSchema(Schema schema) {
        if (schema == null) {
            return null;
        }
        String uuidSchema = ResourceHelper.getUUID(schema);
        if (uuidSchema == null) {
            return null;
        }
        Catalog catalog = CatalogHelper.getParentCatalog(schema);
        // Schema's parent is catalog (MS SQL Server)
        if (catalog != null) {
            DBCatalogRepNode catalogNode = recursiveFindCatalog(catalog);
            List<IRepositoryNode> children = catalogNode.getChildren();
            if (children.size() > 0) {
                IRepositoryNode iRepositoryNode = children.get(0);
                if (iRepositoryNode != null && iRepositoryNode instanceof DBSchemaRepNode) {
                    for (IRepositoryNode childNode : children) {
                        if (childNode != null && childNode instanceof DBSchemaRepNode) {
                            DBSchemaRepNode dbSchemaRepNode = (DBSchemaRepNode) childNode;
                            if (uuidSchema.equals(ResourceHelper.getUUID(dbSchemaRepNode.getSchema()))) {
                                return dbSchemaRepNode;
                            }
                        }
                    }
                }
            }
        }
        // schema's parent is connection (e.g Oracle)
        IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(schema));
        if (connNode != null) {
            List<IRepositoryNode> children = connNode.getChildren();
            if (children.size() > 0) {
                IRepositoryNode iRepositoryNode = children.get(0);
                if (iRepositoryNode != null && iRepositoryNode instanceof DBSchemaRepNode) {
                    for (IRepositoryNode childNode : children) {
                        if (childNode != null && childNode instanceof DBSchemaRepNode) {
                            DBSchemaRepNode dbSchemaRepNode = (DBSchemaRepNode) childNode;
                            if (uuidSchema.equals(ResourceHelper.getUUID(dbSchemaRepNode.getSchema()))) {
                                return dbSchemaRepNode;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static DBTableRepNode recursiveFindTdTable(TdTable tdTable) {
        if (tdTable == null) {
            return null;
        }
        String uuidTdTable = ResourceHelper.getUUID(tdTable);
        if (uuidTdTable == null) {
            return null;
        }
        IRepositoryNode schemaOrCatalogNode = recursiveFind(ColumnSetHelper.getParentCatalogOrSchema(tdTable));
        if (schemaOrCatalogNode == null) {
            return null;
        }
        List<IRepositoryNode> children = schemaOrCatalogNode.getChildren().get(0).getChildren();
        if (children.size() > 0) {
            IRepositoryNode iRepositoryNode = children.get(0);
            if (iRepositoryNode != null && iRepositoryNode instanceof DBTableRepNode) {
                for (IRepositoryNode childNode : children) {
                    if (childNode != null && childNode instanceof DBTableRepNode) {
                        DBTableRepNode dbTableRepNode = (DBTableRepNode) childNode;
                        if (uuidTdTable.equals(ResourceHelper.getUUID(dbTableRepNode.getTdTable()))) {
                            return dbTableRepNode;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static DBViewRepNode recursiveFindTdView(TdView tdView) {
        if (tdView == null) {
            return null;
        }
        String uuidTdView = ResourceHelper.getUUID(tdView);
        if (uuidTdView == null) {
            return null;
        }
        IRepositoryNode schemaOrCatalogNode = recursiveFind(ColumnSetHelper.getParentCatalogOrSchema(tdView));
        if (schemaOrCatalogNode == null) {
            return null;
        }
        List<IRepositoryNode> children = schemaOrCatalogNode.getChildren().get(1).getChildren();
        if (children.size() > 0) {
            IRepositoryNode iRepositoryNode = children.get(0);
            if (iRepositoryNode != null && iRepositoryNode instanceof DBViewRepNode) {
                for (IRepositoryNode childNode : children) {
                    if (childNode != null && childNode instanceof DBViewRepNode) {
                        DBViewRepNode dbViewRepNode = (DBViewRepNode) childNode;
                        if (uuidTdView.equals(ResourceHelper.getUUID(dbViewRepNode.getTdView()))) {
                            return dbViewRepNode;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static DBColumnRepNode recursiveFindTdColumn(TdColumn tdColumn) {
        if (tdColumn == null) {
            return null;
        }
        String uuidTdColumn = ResourceHelper.getUUID(tdColumn);
        if (uuidTdColumn == null) {
            return null;
        }
        IRepositoryNode columnSetNode = recursiveFind(ColumnHelper.getColumnOwnerAsColumnSet(tdColumn));
        if (columnSetNode == null) {
            return null;
        }
        List<IRepositoryNode> children = columnSetNode.getChildren().get(0).getChildren();
        IRepositoryNode iRepositoryNode = children.get(0);
        if (iRepositoryNode != null && iRepositoryNode instanceof DBColumnRepNode) {
            for (IRepositoryNode childNode : children) {
                if (childNode != null && childNode instanceof DBColumnRepNode) {
                    DBColumnRepNode dbColumnRepNode = (DBColumnRepNode) childNode;
                    if (uuidTdColumn.equals(ResourceHelper.getUUID(dbColumnRepNode.getTdColumn()))) {
                        return dbColumnRepNode;
                    }
                }
            }
        }
        return null;
    }

    public static MDMSchemaRepNode recursiveFindTdXmlSchema(TdXmlSchema tdXmlSchema) {
        if (tdXmlSchema == null) {
            return null;
        }
        String uuidTdXmlSchema = ResourceHelper.getUUID(tdXmlSchema);
        if (uuidTdXmlSchema == null) {
            return null;
        }
        EList<DataManager> dataManager = tdXmlSchema.getDataManager();
        if (dataManager.size() > 0) {
            RepositoryNode connNode = recursiveFind(dataManager.get(0));
            if (connNode != null) {
                List<IRepositoryNode> children = connNode.getChildren();
                if (children.size() > 0) {
                    for (IRepositoryNode childNode : children) {
                        if (childNode != null && childNode instanceof MDMSchemaRepNode) {
                            MDMSchemaRepNode mdmSchemaRepNode = (MDMSchemaRepNode) childNode;
                            if (uuidTdXmlSchema.equals(ResourceHelper.getUUID(mdmSchemaRepNode.getTdXmlSchema()))) {
                                return mdmSchemaRepNode;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static MDMXmlElementRepNode recursiveFindTdXmlElementType(TdXmlElementType tdXmlElementType) {
        if (tdXmlElementType == null) {
            return null;
        }
        TdXmlSchema ownedDocument = tdXmlElementType.getOwnedDocument();
        if (ownedDocument != null) {
            RepositoryNode xmlSchemaNode = recursiveFind(ownedDocument);
            if (xmlSchemaNode != null) {
                return recursiveFindXmlElementType(xmlSchemaNode.getChildren(), tdXmlElementType);
            }
        }
        return null;
    }

    public static DFTableRepNode recursiveFindMetadataTable(MetadataTable metadataTable) {
        if (metadataTable == null) {
            return null;
        }
        String uuidMetadataTable = ResourceHelper.getUUID(metadataTable);
        if (uuidMetadataTable == null) {
            return null;
        }
        if (metadataTable.getNamespace() instanceof RecordFile) {
            IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(metadataTable));
            List<IRepositoryNode> children = connNode.getChildren();
            if (children.size() > 0) {
                for (IRepositoryNode childNode : children) {
                    if (childNode != null && childNode instanceof DFTableRepNode) {
                        DFTableRepNode dfTableRepNode = (DFTableRepNode) childNode;
                        if (uuidMetadataTable.equals(ResourceHelper.getUUID(dfTableRepNode.getMetadataTable()))) {
                            return dfTableRepNode;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static DFColumnRepNode recursiveFindMetadataColumn(MetadataColumn metadataColumn) {
        if (metadataColumn == null) {
            return null;
        }
        String uuidMetadataColumn = ResourceHelper.getUUID(metadataColumn);
        if (uuidMetadataColumn == null) {
            return null;
        }
        IRepositoryNode columnSetNode = recursiveFind(ColumnHelper.getColumnOwnerAsMetadataTable(metadataColumn));
        if (columnSetNode == null) {
            return null;
        }
        List<IRepositoryNode> children = columnSetNode.getChildren().get(0).getChildren();
        if (children.size() > 0) {
            for (IRepositoryNode childNode : children) {
                if (childNode != null && childNode instanceof DFColumnRepNode) {
                    DFColumnRepNode dfColumnRepNode = (DFColumnRepNode) childNode;
                    if (uuidMetadataColumn.equals(ResourceHelper.getUUID(dfColumnRepNode.getMetadataColumn()))) {
                        return dfColumnRepNode;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 
     * find a node from recycle bin.
     * 
     * @param modelElement
     * @return
     */
    public static RepositoryNode recursiveFindRecycleBin(ModelElement modelElement) {
        if (modelElement == null) {
            return null;
        }
        String uuid = ResourceHelper.getUUID(modelElement);
        RepositoryNode recyBinNode = getRecycleBinRootNode();
        if (uuid == null || recyBinNode == null) {
            return null;
        }
        List<IRepositoryNode> children = recyBinNode.getChildren();
        return recursiveFindByUuid(uuid, children);
    }

    // !!!following codes are useful codes, please don't delete them, thanks!!!
    // public static RepositoryNode recursiveFind(ModelElement modelElement) {
    // if (modelElement instanceof Analysis) {
    // Analysis analysis = (Analysis) modelElement;
    // List<IRepositoryNode> dataprofilingNode = getDataProfilingRepositoryNodes(true);
    // for (IRepositoryNode anaNode : dataprofilingNode) {
    // Item itemTemp = ((IRepositoryViewObject) anaNode.getObject()).getProperty().getItem();
    // if (itemTemp instanceof TDQAnalysisItem) {
    // TDQAnalysisItem item = (TDQAnalysisItem) itemTemp;
    // if (ResourceHelper.getUUID(analysis).equals(ResourceHelper.getUUID(item.getAnalysis()))) {
    // return (RepositoryNode) anaNode;
    // }
    // } else if (itemTemp instanceof FolderItem) {
    // List<TDQAnalysisItem> anaItems = getAnalysisItemsFromFolderItem((FolderItem) itemTemp);
    // for (TDQAnalysisItem anaItem : anaItems) {
    // if (ResourceHelper.getUUID(analysis).equals(ResourceHelper.getUUID(anaItem.getAnalysis()))) {
    // return (RepositoryNode) anaNode;
    // }
    // }
    // }
    // }
    // } else if (modelElement instanceof TdReport) {
    // TdReport report = (TdReport) modelElement;
    // List<IRepositoryNode> dataprofilingNode = getDataProfilingRepositoryNodes(true);
    // for (IRepositoryNode patternNode : dataprofilingNode) {
    // Item itemTemp = ((IRepositoryViewObject) patternNode.getObject()).getProperty().getItem();
    // if (itemTemp instanceof TDQReportItem) {
    // TDQReportItem item = (TDQReportItem) itemTemp;
    // if (ResourceHelper.getUUID(report).equals(ResourceHelper.getUUID(item.getReport()))) {
    // return (RepositoryNode) patternNode;
    // }
    // } else if (itemTemp instanceof FolderItem) {
    // List<TDQReportItem> reportItems = getReportItemsFromFolderItem((FolderItem) itemTemp);
    // for (TDQReportItem patternItem : reportItems) {
    // if (ResourceHelper.getUUID(report).equals(ResourceHelper.getUUID(patternItem.getReport()))) {
    // return (RepositoryNode) patternNode;
    // }
    // }
    // }
    // }
    // } else if (modelElement instanceof TdColumn) {
    // TdColumn column = (TdColumn) modelElement;
    // IRepositoryNode columnSetNode = recursiveFind(ColumnHelper.getColumnOwnerAsColumnSet(column));
    // for (IRepositoryNode columnNode : columnSetNode.getChildren().get(0).getChildren()) {
    // TdColumn columnOnUI = (TdColumn) ((MetadataColumnRepositoryObject) columnNode.getObject()).getTdColumn();
    // if (ResourceHelper.getUUID(column).equals(ResourceHelper.getUUID(columnOnUI))) {
    // return (RepositoryNode) columnNode;
    // }
    // }
    // } else if (modelElement instanceof TdTable) {
    // TdTable table = (TdTable) modelElement;
    // IRepositoryNode schemaOrCatalogNode = recursiveFind(ColumnSetHelper.getParentCatalogOrSchema(modelElement));
    // for (IRepositoryNode tableNode : schemaOrCatalogNode.getChildren().get(0).getChildren()) {
    // TdTable tableOnUI = (TdTable) ((TdTableRepositoryObject) tableNode.getObject()).getTdTable();
    // if (ResourceHelper.getUUID(table).equals(ResourceHelper.getUUID(tableOnUI))) {
    // return (RepositoryNode) tableNode;
    // }
    // }
    // } else if (modelElement instanceof TdView) {
    // TdView view = (TdView) modelElement;
    // IRepositoryNode schemaOrCatalogNode = recursiveFind(ColumnSetHelper.getParentCatalogOrSchema(modelElement));
    // for (IRepositoryNode viewNode : schemaOrCatalogNode.getChildren().get(1).getChildren()) {
    // TdView viewOnUI = (TdView) ((TdViewRepositoryObject) viewNode.getObject()).getTdView();
    // if (ResourceHelper.getUUID(view).equals(ResourceHelper.getUUID(viewOnUI))) {
    // return (RepositoryNode) viewNode;
    // }
    // }
    // } else if (modelElement instanceof MetadataColumn) {
    // // MOD qiongli 2011-1-12 for delimted file
    // MetadataColumn column = (MetadataColumn) modelElement;
    // IRepositoryNode columnSetNode = recursiveFind(ColumnHelper.getColumnOwnerAsMetadataTable(column));
    // for (IRepositoryNode columnNode : columnSetNode.getChildren().get(0).getChildren()) {
    // MetadataColumn columnOnUI = ((MetadataColumnRepositoryObject) columnNode.getObject()).getTdColumn();
    // if (ResourceHelper.getUUID(column).equals(ResourceHelper.getUUID(columnOnUI))) {
    // return (RepositoryNode) columnNode;
    // }
    // }
    // } else if (modelElement instanceof MetadataTable) {
    // // MOD qiongli 2011-1-12 for delimted file
    // MetadataTable table = (MetadataTable) modelElement;
    // if (table.getNamespace() instanceof RecordFile) {
    // IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(table));
    // for (IRepositoryNode tableNode : connNode.getChildren()) {
    // MetadataTable tableOnUI = (MetadataTable) ((MetadataTableRepositoryObject) tableNode.getObject()).getTable();
    // if (ResourceHelper.getUUID(table).equals(ResourceHelper.getUUID(tableOnUI))) {
    // return (RepositoryNode) tableNode;
    // }
    // }
    // }
    // } else if (modelElement instanceof Catalog) {
    // Catalog catalog = (Catalog) modelElement;
    // IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(catalog));
    // for (IRepositoryNode catalogNode : connNode.getChildren()) {
    // Catalog catalogOnUI = ((MetadataCatalogRepositoryObject) catalogNode.getObject()).getCatalog();
    // if (ResourceHelper.getUUID(catalog).equals(ResourceHelper.getUUID(catalogOnUI))) {
    // return (RepositoryNode) catalogNode;
    // }
    // }
    // } else if (modelElement instanceof Schema) {
    // Schema schema = (Schema) modelElement;
    // Catalog catalog = CatalogHelper.getParentCatalog(schema);
    // // Schema's parent is catalog (MS SQL Server)
    // if (catalog != null) {
    // IRepositoryNode catalogNode = recursiveFind(catalog);
    // for (IRepositoryNode schemaNode : catalogNode.getChildren()) {
    // Schema schemaOnUI = ((MetadataSchemaRepositoryObject) schemaNode.getObject()).getSchema();
    // if (ResourceHelper.getUUID(schema).equals(ResourceHelper.getUUID(schemaOnUI))) {
    // return (RepositoryNode) schemaNode;
    // }
    // }
    // }
    // // schema's parent is connection (e.g Oracle)
    // IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(schema));
    // for (IRepositoryNode schemaNode : connNode.getChildren()) {
    // Schema schemaOnUI = ((MetadataSchemaRepositoryObject) schemaNode.getObject()).getSchema();
    // if (ResourceHelper.getUUID(schema).equals(ResourceHelper.getUUID(schemaOnUI))) {
    // return (RepositoryNode) schemaNode;
    // }
    // }
    // } else if (modelElement instanceof Connection) {
    //
    // Connection connection = (Connection) modelElement;
    // List<IRepositoryNode> connsNode = getConnectionRepositoryNodes(true);
    // for (IRepositoryNode connNode : connsNode) {
    // Item itemTemp = ((IRepositoryViewObject) connNode.getObject()).getProperty().getItem();
    // if (itemTemp instanceof ConnectionItem) {
    // ConnectionItem item = (ConnectionItem) itemTemp;
    // if (connection.eIsProxy()) {
    // ResourceSet resourceSet = ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider()
    // .getResourceManager().resourceSet;
    // connection = (Connection) EcoreUtil.resolve(connection, resourceSet);
    // }
    // if (ResourceHelper.getUUID(connection).equals(ResourceHelper.getUUID(item.getConnection()))) {
    // return (RepositoryNode) connNode;
    // }
    // } else if (itemTemp instanceof FolderItem) {
    // List<ConnectionItem> connItems = getConnectionItemsFromFolderItem((FolderItem) itemTemp);
    // for (ConnectionItem connItem : connItems) {
    // if (ResourceHelper.getUUID(connection).equals(ResourceHelper.getUUID(connItem.getConnection()))) {
    // return (RepositoryNode) connNode;
    // }
    // }
    // }
    // }
    // } else if (modelElement instanceof Pattern) {
    // Pattern pattern = (Pattern) modelElement;
    // List<IRepositoryNode> patternsNode = getPatternsRepositoryNodes(true);
    // for (IRepositoryNode patternNode : patternsNode) {
    // Item itemTemp = ((IRepositoryViewObject) patternNode.getObject()).getProperty().getItem();
    // if (itemTemp instanceof TDQPatternItem) {
    // TDQPatternItem item = (TDQPatternItem) itemTemp;
    // if (ResourceHelper.getUUID(pattern).equals(ResourceHelper.getUUID(item.getPattern()))) {
    // return (RepositoryNode) patternNode;
    // }
    // } else if (itemTemp instanceof FolderItem) {
    // List<TDQPatternItem> patternItems = getPatternsItemsFromFolderItem((FolderItem) itemTemp);
    // for (TDQPatternItem patternItem : patternItems) {
    // if (ResourceHelper.getUUID(pattern).equals(ResourceHelper.getUUID(patternItem.getPattern()))) {
    // return (RepositoryNode) patternNode;
    // }
    // }
    // }
    // }
    // } else if (modelElement instanceof IndicatorDefinition) {
    // IndicatorDefinition udi = (IndicatorDefinition) modelElement;
    // List<IRepositoryNode> udisNode = getUdisRepositoryNodes(true);
    // for (IRepositoryNode udiNode : udisNode) {
    // Item itemTemp = ((IRepositoryViewObject) udiNode.getObject()).getProperty().getItem();
    // if (itemTemp instanceof TDQIndicatorDefinitionItem) {
    // TDQIndicatorDefinitionItem item = (TDQIndicatorDefinitionItem) itemTemp;
    // if (ResourceHelper.getUUID(udi).equals(ResourceHelper.getUUID(item.getIndicatorDefinition()))) {
    // return (RepositoryNode) udiNode;
    // }
    // } else if (itemTemp instanceof TDQBusinessRuleItem) {
    // TDQBusinessRuleItem item = (TDQBusinessRuleItem) itemTemp;
    // if (ResourceHelper.getUUID(udi).equals(ResourceHelper.getUUID(item.getDqrule()))) {
    // return (RepositoryNode) udiNode;
    // }
    // } else if (itemTemp instanceof FolderItem) {
    // List<TDQIndicatorDefinitionItem> udiItems = getIndicatorItemsFromFolderItem((FolderItem) itemTemp);
    // for (TDQIndicatorDefinitionItem udiItem : udiItems) {
    // if (ResourceHelper.getUUID(udi).equals(ResourceHelper.getUUID(udiItem.getIndicatorDefinition()))) {
    // return (RepositoryNode) udiNode;
    // }
    // }
    // }
    // }
    // } else if (modelElement instanceof TdXmlElementType) {
    // TdXmlElementType xmlElementType = (TdXmlElementType) modelElement;
    // TdXmlSchema ownedDocument = xmlElementType.getOwnedDocument();
    // if (ownedDocument != null) {
    // RepositoryNode xmlSchemaNode = recursiveFind(ownedDocument);
    // if (xmlSchemaNode != null) {
    // return recursiveFindXmlElementType(xmlSchemaNode.getChildren(), xmlElementType);
    // }
    // }
    // } else if (modelElement instanceof TdXmlSchema) {
    // TdXmlSchema xmlSchema = (TdXmlSchema) modelElement;
    // EList<DataManager> dataManager = xmlSchema.getDataManager();
    // if (dataManager.size() > 0) {
    // RepositoryNode connNode = recursiveFind(dataManager.get(0));
    // if (connNode != null) {
    // for (IRepositoryNode xmlSchemaNode : connNode.getChildren()) {
    // if (xmlSchemaNode instanceof MDMSchemaRepNode) {
    // TdXmlSchema tdXmlSchemaOnUi = ((MDMSchemaRepNode) xmlSchemaNode).getTdXmlSchema();
    // if (ResourceHelper.getUUID(xmlSchema).equals(ResourceHelper.getUUID(tdXmlSchemaOnUi))) {
    // return (RepositoryNode) xmlSchemaNode;
    // }
    // }
    // }
    // }
    // }
    // }
    // return null;
    // }

    /**
     * recursive find the ReopsitoryNode accroding to xmlElementType under nodes.
     * 
     * @param nodes
     * @param xmlElementType
     * @return
     */
    private static MDMXmlElementRepNode recursiveFindXmlElementType(List<IRepositoryNode> nodes, TdXmlElementType xmlElementType) {
        if (nodes != null && nodes.size() > 0) {
            for (IRepositoryNode node : nodes) {
                if (node instanceof MDMXmlElementRepNode) {
                    TdXmlElementType tdXmlElementTypeOnUi = ((MDMXmlElementRepNode) node).getTdXmlElementType();
                    if (ResourceHelper.getUUID(xmlElementType).equals(ResourceHelper.getUUID(tdXmlElementTypeOnUi))) {
                        return (MDMXmlElementRepNode) node;
                    } else {
                        MDMXmlElementRepNode recursiveFindXmlElementType = recursiveFindXmlElementType(node.getChildren(),
                                xmlElementType);
                        if (recursiveFindXmlElementType != null) {
                            return recursiveFindXmlElementType;
                        }
                    }
                }
            }
        }
        return null;
    }

    // /**
    // * get all the ConnectionItems from FolderItem (recursive).
    // *
    // * @param folderItem
    // * @return
    // */
    // private static List<ConnectionItem> getConnectionItemsFromFolderItem(FolderItem folderItem) {
    // List<ConnectionItem> list = new ArrayList<ConnectionItem>();
    // EList objs = folderItem.getChildren();
    // for (Object obj : objs) {
    // if (obj instanceof FolderItem) {
    // list.addAll(getConnectionItemsFromFolderItem((FolderItem) obj));
    // } else if (obj instanceof ConnectionItem) {
    // list.add((ConnectionItem) obj);
    // }
    // }
    // return list;
    // }
    //
    // private static List<TDQAnalysisItem> getAnalysisItemsFromFolderItem(FolderItem folderItem) {
    // List<TDQAnalysisItem> list = new ArrayList<TDQAnalysisItem>();
    // EList objs = folderItem.getChildren();
    // for (Object obj : objs) {
    // if (obj instanceof FolderItem) {
    // list.addAll(getAnalysisItemsFromFolderItem((FolderItem) obj));
    // } else if (obj instanceof TDQAnalysisItem) {
    // list.add((TDQAnalysisItem) obj);
    // }
    // }
    // return list;
    // }
    //
    // private static List<TDQReportItem> getReportItemsFromFolderItem(FolderItem folderItem) {
    // List<TDQReportItem> list = new ArrayList<TDQReportItem>();
    // EList objs = folderItem.getChildren();
    // for (Object obj : objs) {
    // if (obj instanceof FolderItem) {
    // list.addAll(getReportItemsFromFolderItem((FolderItem) obj));
    // } else if (obj instanceof TDQAnalysisItem) {
    // list.add((TDQReportItem) obj);
    // }
    // }
    // return list;
    // }
    //
    // private static List<TDQPatternItem> getPatternsItemsFromFolderItem(FolderItem folderItem) {
    // List<TDQPatternItem> list = new ArrayList<TDQPatternItem>();
    // EList objs = folderItem.getChildren();
    // for (Object obj : objs) {
    // if (obj instanceof FolderItem) {
    // list.addAll(getPatternsItemsFromFolderItem((FolderItem) obj));
    // } else if (obj instanceof TDQAnalysisItem) {
    // list.add((TDQPatternItem) obj);
    // }
    // }
    // return list;
    // }
    //
    // private static List<TDQIndicatorDefinitionItem> getIndicatorItemsFromFolderItem(FolderItem folderItem) {
    // List<TDQIndicatorDefinitionItem> list = new ArrayList<TDQIndicatorDefinitionItem>();
    // EList objs = folderItem.getChildren();
    // for (Object obj : objs) {
    // if (obj instanceof FolderItem) {
    // list.addAll(getIndicatorItemsFromFolderItem((FolderItem) obj));
    // } else if (obj instanceof TDQIndicatorDefinitionItem) {
    // list.add((TDQIndicatorDefinitionItem) obj);
    // }
    // }
    // return list;
    // }

    /**
     * ADD mzhao 15750 , build dq metadata tree, get connection root node.
     */

    public static List<IRepositoryNode> getConnectionRepositoryNodes(boolean withDeleted) {
        RepositoryNode node = getRootNode(ERepositoryObjectType.METADATA);
        List<IRepositoryNode> connNodes = new ArrayList<IRepositoryNode>();
        if (node != null) {
            List<IRepositoryNode> childrens = node.getChildren();
            for (IRepositoryNode subNode : childrens) {
                if (subNode instanceof DBConnectionFolderRepNode || subNode instanceof DFConnectionFolderRepNode
                        || subNode instanceof MDMConnectionFolderRepNode) {
                    connNodes.addAll(getModelElementFromFolder(subNode, withDeleted));
                }
            }
        }
        return connNodes;
    }

    public static List<IRepositoryNode> getDBConnectionRepositoryNodes(boolean withDeleted) {
        RepositoryNode node = getRootNode(ERepositoryObjectType.METADATA);
        List<IRepositoryNode> connNodes = new ArrayList<IRepositoryNode>();
        if (node != null) {
            List<IRepositoryNode> childrens = node.getChildren();
            for (IRepositoryNode subNode : childrens) {
                if (subNode instanceof DBConnectionFolderRepNode) {
                    connNodes.addAll(getModelElementFromFolder(subNode, withDeleted));
                }
            }
        }
        return connNodes;
    }

    /**
     * 
     * zshen Comment method "getRepositoryFolderNode".
     * 
     * @param folderConstant
     * @return one RepositoryFolderNode which corresponding to the value of folderConstant
     */
    public static IRepositoryNode getMetadataFolderNode(EResourceConstant folderConstant) {
        String[] folderPathArray = folderConstant.getPath().split("/");
        if (folderPathArray.length <= 0) {
            return null;
        }
        IRepositoryNode node = getRootNode(ERepositoryObjectType.METADATA);
        if (node != null) {
            for (int i = 1; folderPathArray.length > i; i++) {
                for (IRepositoryNode childNode : node.getChildren()) {
                    if (childNode.getObject().getLabel().equalsIgnoreCase(folderPathArray[i])) {
                        node = childNode;
                        break;
                    }
                }
            }
        }
        return node;
    }

    public static IRepositoryNode getDataProfilingFolderNode(EResourceConstant folderConstant) {
        String[] folderPathArray = folderConstant.getPath().split("/");
        if (folderPathArray.length <= 0) {
            return null;
        }

        IRepositoryNode node = getRootNode(ERepositoryObjectType.TDQ_DATA_PROFILING);
        if (node != null) {
            for (int i = 1; folderPathArray.length > i; i++) {
                for (IRepositoryNode childNode : node.getChildren()) {
                    if (childNode.getObject().getLabel().equalsIgnoreCase(folderPathArray[i])) {
                        node = childNode;
                        break;
                    }
                }
            }
        }
        return node;
    }

    public static IRepositoryNode getLibrariesFolderNode(EResourceConstant folderConstant) {
        String[] folderPathArray = folderConstant.getPath().split("/");
        if (folderPathArray.length <= 0) {
            return null;
        }

        IRepositoryNode node = getRootNode(ERepositoryObjectType.TDQ_LIBRARIES);
        if (node != null) {
            for (int i = 1; folderPathArray.length > i; i++) {
                for (IRepositoryNode childNode : node.getChildren()) {
                    if (childNode.getObject().getLabel().equalsIgnoreCase(folderPathArray[i])) {
                        node = childNode;
                        break;
                    }
                }
            }
        }
        return node;
    }

    public static List<IRepositoryNode> getDataProfilingRepositoryNodes(boolean withDeleted) {
        RepositoryNode node = getRootNode(ERepositoryObjectType.TDQ_DATA_PROFILING);// t.DATA_PROFILING.getName());
        List<IRepositoryNode> dataProfilingNodes = new ArrayList<IRepositoryNode>();
        if (node != null) {
            List<IRepositoryNode> childrens = node.getChildren();
            for (IRepositoryNode subNode : childrens) {
                if (subNode instanceof AnalysisFolderRepNode || subNode instanceof ReportFolderRepNode) {
                    dataProfilingNodes.addAll(getModelElementFromFolder(subNode, withDeleted));
                }
            }
        }
        return dataProfilingNodes;
    }

    // private static List<IRepositoryNode> getAnalysisFromFolder(IRepositoryNode folderNode) {
    // List<IRepositoryNode> repositoryNodeList = new ArrayList<IRepositoryNode>();
    // if (isFolderNode(folderNode.getType())) {
    // for (IRepositoryNode thefolderNode : folderNode.getChildren()) {
    // repositoryNodeList.addAll(getAnalysisFromFolder(thefolderNode));
    // }
    // } else {
    // repositoryNodeList.add(folderNode);
    // }
    // return repositoryNodeList;
    // }

    public static List<IRepositoryNode> getPatternsRepositoryNodes(boolean withDeleted) {
        RepositoryNode node = getRootNode(ERepositoryObjectType.TDQ_LIBRARIES);// .LIBRARIES.getName());
        List<IRepositoryNode> patternsNodes = new ArrayList<IRepositoryNode>();
        if (node != null) {
            List<IRepositoryNode> childrens = node.getChildren();
            for (IRepositoryNode subNode : childrens) {
                if (EResourceConstant.PATTERNS.getName().equals((subNode.getObject().getLabel()))) {
                    List<IRepositoryNode> subChildren = subNode.getChildren();
                    for (IRepositoryNode patternsNode : subChildren) {
                        if (patternsNode instanceof PatternRegexFolderRepNode || patternsNode instanceof PatternSqlFolderRepNode) {
                            patternsNodes.addAll(getModelElementFromFolder(patternsNode, withDeleted));
                        }
                    }
                    return patternsNodes;

                }

            }
        }
        return patternsNodes;
    }

    public static List<IRepositoryNode> getUdisRepositoryNodes(boolean withDeleted) {
        RepositoryNode node = getRootNode(ERepositoryObjectType.TDQ_LIBRARIES);// EResourceConstant.LIBRARIES.getName());
        List<IRepositoryNode> udisNodes = new ArrayList<IRepositoryNode>();
        if (node != null) {
            List<IRepositoryNode> childrens = node.getChildren();
            for (IRepositoryNode subNode : childrens) {
                if (EResourceConstant.INDICATORS.getName().equals((subNode.getObject().getLabel()))
                        || EResourceConstant.RULES.getName().equals((subNode.getObject().getLabel()))) {
                    List<IRepositoryNode> subChildren = subNode.getChildren();
                    for (IRepositoryNode udisNode : subChildren) {
                        if (udisNode instanceof UserDefIndicatorFolderRepNode || udisNode instanceof RulesFolderRepNode) {
                            udisNodes.addAll(getModelElementFromFolder(udisNode, withDeleted));
                        }
                    }

                }

            }
        }
        return udisNodes;
    }

    /**
     * 
     * Add zshen 15750 get all the Connection Node from one folder node.
     * 
     * @param folderNode any node
     * @return
     */
    private static List<IRepositoryNode> getModelElementFromFolder(IRepositoryNode folderNode, boolean withDelted) {
        // MOD qiongli 2011-2-23,bug 17588 ,add param withDeleted.
        List<IRepositoryNode> repositoryNodeList = new ArrayList<IRepositoryNode>();
        if (isFolderNode(folderNode.getType())) {
            for (IRepositoryNode thefolderNode : folderNode.getChildren(withDelted)) {
                repositoryNodeList.addAll(getModelElementFromFolder(thefolderNode, withDelted));
            }
        } else {
            repositoryNodeList.add(folderNode);
        }
        return repositoryNodeList;
    }

    /**
     * 
     * Add zshen 15750 Decided whether one node is a Folder Node.
     * 
     * @param nodeType the Type of nodes
     * @return
     */
    private static boolean isFolderNode(ENodeType nodeType) {

        switch (nodeType) {
        case SYSTEM_FOLDER:
        case SIMPLE_FOLDER:
            return true;
        default:
            return false;
        }
    }

    /**
     * get the metadata element from a node, if there have not metadata element, return null.
     * 
     * @param repositoryNode
     * @return
     */
    public static ModelElement getMetadataElement(IRepositoryNode repositoryNode) {
        ISubRepositoryObject metadataObject = null;
        if (repositoryNode instanceof DBTableFolderRepNode || repositoryNode instanceof DBViewFolderRepNode
                || repositoryNode instanceof DBColumnFolderRepNode) {
            metadataObject = (ISubRepositoryObject) repositoryNode.getParent().getObject();
        } else if (repositoryNode.getObject() instanceof ISubRepositoryObject) {
            metadataObject = (ISubRepositoryObject) repositoryNode.getObject();
        }
        if (repositoryNode instanceof DBConnectionRepNode || repositoryNode instanceof DFConnectionRepNode
                || repositoryNode instanceof MDMConnectionRepNode) {
            return ((ConnectionItem) repositoryNode.getObject().getProperty().getItem()).getConnection();
        }
        if (metadataObject != null) {
            return metadataObject.getModelElement();
        }
        return null;
    }

    /**
     * DOC klliu Comment method "getRootNode".
     * 
     * @return
     */
    public static RepositoryNode getRootNode(ERepositoryObjectType nodeName) {
        // MOD klliu bug 19138 In DI that can't find MDMConnectionFolderRepNode when create MDM connection
        // ~2011-03-22
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
            IWorkbenchPart activePart = activePage == null ? null : activePage.getActivePart();
            if (activePart != null && activePart.getTitle().equals(DI_REPOSITORY_NAME)) {
                return getRootNode(nodeName, true);
            }
        }
        // ~
        return getRootNode(nodeName, false);
    }

    public static RepositoryNode getRootNode(ERepositoryObjectType nodeName, boolean open) {
        FolderItem folderItem = ProxyRepositoryFactory.getInstance().getFolderItem(
                ProjectManager.getInstance().getCurrentProject(), nodeName, Path.EMPTY);
        RepositoryNode node = null;
        CommonViewer commonViewer = getDQCommonViewer(open);
        if (commonViewer != null) {
            TreeItem[] items = commonViewer.getTree().getItems();
            for (TreeItem item : items) {
                node = (RepositoryNode) item.getData();
                // MOD qiongli 2011-2-16,bug 18642.filter recycle bin node.
                if (node.isBin()) {
                    continue;
                }
                String viewFolderID = node.getObject().getProperty().getId();
                if (folderItem != null) {
                    String folderID = folderItem.getProperty().getId();
                    if (viewFolderID.equals(folderID)) {
                        return node;
                    }
                }
            }
        }
        return node;
    }

    /**
     * 
     * get recycle bin node.
     * 
     * @return
     */
    public static RepositoryNode getRecycleBinRootNode() {
        RepositoryNode node = null;
        CommonViewer commonViewer = getDQCommonViewer(false);
        if (commonViewer != null) {
            TreeItem[] items = commonViewer.getTree().getItems();
            for (TreeItem item : items) {
                node = (RepositoryNode) item.getData();
                if (node.isBin()) {
                    break;
                }
            }
        }
        return node;
    }

    /**
     * DOC klliu 15750 Comment method "getDQRespositoryView".
     * 
     * @return
     */
    public static CommonViewer getDQCommonViewer() {
        IViewPart part = null;
        CommonViewer commonViewer = null;
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
            if (activePage != null) {
                part = activePage.findView(DQRESPOSITORYVIEW);
                if (part == null) {
                    return null;
                }
                CommonNavigator dqView = (CommonNavigator) part;
                commonViewer = dqView.getCommonViewer();
            }
        }
        return commonViewer;
    }

    public static CommonViewer getDQCommonViewer(boolean open) {
        IViewPart part = null;
        CommonViewer commonViewer = null;
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
            if (activePage != null) {
                part = activePage.findView(DQRESPOSITORYVIEW);
                if (part == null) {
                    if (open) {
                        try {
                            part = activePage.showView(DQRESPOSITORYVIEW);
                        } catch (PartInitException e) {
                            e.printStackTrace();
                        }
                        if (part == null) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
                CommonNavigator dqView = (CommonNavigator) part;
                commonViewer = dqView.getCommonViewer();
            }
        }
        return commonViewer;
    }

    public static boolean canOpenEditor(RepositoryNode node) {
        return node instanceof AnalysisRepNode || node instanceof SysIndicatorDefinitionRepNode || node instanceof PatternRepNode
                || node instanceof JrxmlTempleteRepNode || node instanceof SourceFileRepNode || node instanceof RuleRepNode
                || node instanceof DBConnectionRepNode || node instanceof MDMConnectionRepNode || node instanceof ReportRepNode
                || node instanceof ReportFileRepNode;
    }

    public static List<IRepositoryNode> getNmaedColumnSetNodes(IRepositoryNode node) {
        ArrayList<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        if (node instanceof DBCatalogRepNode || node instanceof DBSchemaRepNode || node instanceof DBTableFolderRepNode
                || node instanceof DBViewFolderRepNode) {
            List<IRepositoryNode> childrens = node.getChildren();
            for (IRepositoryNode children : childrens) {
                list.addAll(getNmaedColumnSetNodes(children));
            }
        } else if (node instanceof DBTableRepNode || node instanceof DBViewRepNode) {
            list.add(node);
        }
        return list;
    }

    public static List<IRepositoryNode> getRepositoryNodeList(Object[] objs) {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        for (Object obj : objs) {
            if (obj instanceof IRepositoryNode) {
                list.add((IRepositoryNode) obj);
            }
        }
        return list;
    }

    public static List<IRepositoryNode> getRepositoryNodeList(Object[] objs, List<ENodeType> nodeTypes) {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        for (Object obj : objs) {
            if (obj instanceof IRepositoryNode) {
                IRepositoryNode node = (IRepositoryNode) obj;
                for (ENodeType nodeType : nodeTypes) {
                    if (nodeType.equals(node.getType())) {
                        list.add(node);
                        break;
                    }
                }
            }
        }
        return list;
    }

    /**
     * get the (Resource) ModelElement from a node(include: connection, analysis, business rule, indicator definition,
     * pattern, report), if there have not ModelElement return null.
     * 
     * @param node
     * @return
     */
    public static ModelElement getResourceModelElement(IRepositoryNode node) {
        if (node != null) {
            ENodeType nodeType = node.getType();
            if (ENodeType.REPOSITORY_ELEMENT.equals(nodeType) || ENodeType.TDQ_REPOSITORY_ELEMENT.equals(nodeType)) {
                IRepositoryViewObject object = node.getObject();
                if (object != null) {
                    Property property = object.getProperty();
                    if (property != null) {
                        return getResourceModelElement(property.getItem());
                    }
                }
            }
        }
        return null;
    }

    /**
     * get the (Resource) ModelElement from a item(include: connection, analysis, business rule, indicator definition,
     * pattern, report), if there have not ModelElement return null.
     * 
     * @param item
     * @return
     */
    public static ModelElement getResourceModelElement(Item item) {
        if (item != null && item instanceof TDQItem) {
            if (item instanceof TDQAnalysisItem) {
                return ((TDQAnalysisItem) item).getAnalysis();
            } else if (item instanceof TDQBusinessRuleItem) {
                return ((TDQBusinessRuleItem) item).getDqrule();
            } else if (item instanceof TDQIndicatorDefinitionItem) {
                return ((TDQIndicatorDefinitionItem) item).getIndicatorDefinition();
            } else if (item instanceof TDQPatternItem) {
                return ((TDQPatternItem) item).getPattern();
            } else if (item instanceof TDQReportItem) {
                return ((TDQReportItem) item).getReport();
            }
        } else if (item != null && item instanceof ConnectionItem) {
            return ((ConnectionItem) item).getConnection();
        }
        return null;
    }

    /**
     * get the (Sub) ModelElement from a node(include: catalog, schema, table, view, column, xmlSchema, xmlElement), if
     * there have not ModelElement return null.
     * 
     * @param node
     * @return
     */
    public static ModelElement getSubModelElement(IRepositoryNode node) {
        if (node != null) {
            if (node instanceof DBCatalogRepNode) {
                return ((DBCatalogRepNode) node).getCatalog();
            } else if (node instanceof DBSchemaRepNode) {
                return ((DBSchemaRepNode) node).getSchema();
            } else if (node instanceof DBTableRepNode) {
                return ((DBTableRepNode) node).getTdTable();
            } else if (node instanceof DBViewRepNode) {
                return ((DBViewRepNode) node).getTdView();
            } else if (node instanceof DBColumnRepNode) {
                return ((DBColumnRepNode) node).getTdColumn();
            } else if (node instanceof MDMSchemaRepNode) {
                return ((MDMSchemaRepNode) node).getTdXmlSchema();
            } else if (node instanceof MDMXmlElementRepNode) {
                return ((MDMXmlElementRepNode) node).getTdXmlElementType();
            } else if (node instanceof DFColumnRepNode) {
                return ((DFColumnRepNode) node).getMetadataColumn();
            } else if (node instanceof DFTableRepNode) {
                return ((DFTableRepNode) node).getMetadataTable();
            }
        }
        return null;
    }

    /**
     * get ModelElement from IRepositoryNode, if there no ModelElement, return null.
     * 
     * @param node
     * @return
     */
    public static ModelElement getModelElementFromRepositoryNode(IRepositoryNode node) {
        ModelElement metadataElement = getMetadataElement(node);
        if (metadataElement != null) {
            return metadataElement;
        } else {
            metadataElement = getResourceModelElement(node);
            if (metadataElement != null) {
                return metadataElement;
            } else {
                metadataElement = getSubModelElement(node);
                if (metadataElement != null) {
                    return metadataElement;
                }
            }
        }
        return null;
    }

    /**
     * 
     * if logical delete state is true .
     * 
     * @param node
     * @return
     */
    public static boolean isStateDeleted(IRepositoryNode node) {
        final IRepositoryViewObject viewObject = node.getObject();
        final IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

        // TDQ's ISubRepositoryObject will return a null when call getAbstractMetadataObject()
        if (viewObject instanceof ISubRepositoryObject) {
            ISubRepositoryObject subRepositoryObject = (ISubRepositoryObject) viewObject;
            if (subRepositoryObject.getAbstractMetadataObject() == null) {
                return false;
            }
        }

        if (node instanceof ReportAnalysisRepNode || node instanceof ReportFileRepNode) {
            return false;
        } else {
            if (viewObject != null && factory.getStatus(viewObject) == ERepositoryStatus.DELETED) {
                return true;
            }
        }

        return false;
    }

    /**
     * find the RepositoryNode according to the ModelElement.
     * 
     * @param modelElement
     * @return a RepositoryNode or null
     * @deprecated use recursiveFind(ModelElement) instead
     */
    public static RepositoryNode recursiveFind2(ModelElement modelElement) {
        return recursiveFind(modelElement);
        // String uuid = ResourceHelper.getUUID(modelElement);
        // List<IRepositoryNode> nodes = new ArrayList<IRepositoryNode>();
        //
        // if (modelElement instanceof Analysis) {
        // nodes.add(getDataProfilingFolderNode(EResourceConstant.ANALYSIS));
        // } else if (modelElement instanceof TdReport) {
        // nodes.add(getDataProfilingFolderNode(EResourceConstant.REPORTS));
        // } else if (modelElement instanceof Connection || modelElement instanceof Catalog || modelElement instanceof
        // Schema
        // || modelElement instanceof MetadataColumn || modelElement instanceof MetadataTable
        // || modelElement instanceof TdTable || modelElement instanceof TdView || modelElement instanceof TdColumn
        // || modelElement instanceof TdXmlElementType || modelElement instanceof TdXmlSchema) {
        // nodes.add(getMetadataFolderNode(EResourceConstant.METADATA));
        // } else if (modelElement instanceof Pattern) {
        // nodes.add(getLibrariesFolderNode(EResourceConstant.PATTERNS));
        // } else if (modelElement instanceof IndicatorDefinition) {
        // if (modelElement instanceof WhereRule) {
        // nodes.add(getLibrariesFolderNode(EResourceConstant.RULES));
        // } else {
        // nodes.add(getLibrariesFolderNode(EResourceConstant.INDICATORS));
        // }
        // }
        // return recursiveFindByUuid(uuid, nodes);
    }

    /**
     * find the RepositoryNode according to the ModelElement's uuid.
     * 
     * @param uuid
     * @return a RepositoryNode or null
     * @deprecated this method is too slow, please use recursiveFindByUuid(String uuid, List<IRepositoryNode> nodes)
     * instead
     */
    public static RepositoryNode recursiveFindByUuid(String uuid) {
        return recursiveFind(uuid, getTdqRootNodes());
    }

    /**
     * find the RepositoryNode according to the ModelElement's uuid which be included in the nodes and those children.
     * 
     * @param uuid
     * @param nodes
     * @return a RepositoryNode or null
     */
    public static RepositoryNode recursiveFindByUuid(String uuid, List<IRepositoryNode> nodes) {
        if (uuid != null && nodes != null) {
            for (IRepositoryNode node : nodes) {
                ModelElement modelElement = getModelElementFromRepositoryNode(node);
                if (modelElement != null && uuid.equals(ResourceHelper.getUUID(modelElement))) {
                    return (RepositoryNode) node;
                } else {
                    if (needFindInChildren(node)) {
                        RepositoryNode recursiveFind = recursiveFindByUuid(uuid, node.getChildren());
                        if (recursiveFind != null) {
                            return recursiveFind;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * should to find ModelElement in the node's children or not.
     * 
     * @param node
     * @return
     */
    private static boolean needFindInChildren(IRepositoryNode node) {
        boolean result = true;
        String exchangeFolderClassName = "org.talend.dataprofiler.core.ui.exchange.ExchangeFolderRepNode";
        if (node instanceof AnalysisRepNode || node instanceof ReportRepNode
                || exchangeFolderClassName.equals(node.getClass().getName()) || node instanceof SysIndicatorDefinitionRepNode
                || node instanceof PatternRepNode || node instanceof RuleRepNode || node instanceof SourceFileFolderRepNode
                || node instanceof RecycleBinRepNode || node instanceof DBColumnRepNode || node instanceof DFColumnRepNode
                || node instanceof JrxmlTempFolderRepNode) {
            result = false;
        }
        return result;
    }

    /**
     * find the RepositoryNode according to the nodeId.
     * 
     * @param nodeId
     * @return a RepositoryNode or null
     */
    public static RepositoryNode recursiveFind(String nodeId) {
        return recursiveFind(nodeId, getTdqRootNodes());
    }

    /**
     * find the RepositoryNode according to the nodeId which be included in the nodes and those children..
     * 
     * @param nodeId
     * @return a RepositoryNode or null
     */
    public static RepositoryNode recursiveFind(String nodeId, List<IRepositoryNode> nodes) {
        assert nodeId != null;
        assert nodes != null;
        for (IRepositoryNode node : nodes) {
            if (nodeId.equals(node.getId())) {
                return (RepositoryNode) node;
            } else {
                if (needFindInChildren(node)) {
                    RepositoryNode recursiveFind = recursiveFind(nodeId, node.getChildren());
                    if (recursiveFind != null) {
                        return recursiveFind;
                    }
                }
            }
        }
        return null;
    }

    public static List<IRepositoryNode> getTdqRootNodes() {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        list.add(getRootNode(ERepositoryObjectType.TDQ_DATA_PROFILING, true));
        list.add(getRootNode(ERepositoryObjectType.TDQ_LIBRARIES, true));
        list.add(getRootNode(ERepositoryObjectType.METADATA, true));
        return list;
    }

    /**
     * DOC xqliu Comment method "getMdmChildren".
     * 
     * @param parentElement
     * @param hasChildren
     * @return
     */
    public static Object[] getMdmChildren(Object parentElement, boolean hasChildren) {
        List<IRepositoryNode> children = new ArrayList<IRepositoryNode>();
        List<IRepositoryNode> result = new ArrayList<IRepositoryNode>();
        if (parentElement instanceof MDMSchemaRepNode) {
            children = ((MDMSchemaRepNode) parentElement).getChildren();
        } else if (parentElement instanceof MDMXmlElementRepNode) {
            children = ((MDMXmlElementRepNode) parentElement).getChildren();
        }
        for (IRepositoryNode node : children) {
            boolean hasChildren2 = node.hasChildren();
            if ((hasChildren && hasChildren2) || (!hasChildren && !hasChildren2)) {
                result.add(node);
            }
        }
        return result.toArray();
    }

    /**
     * 
     * DOC qiongli Comment method "hasDependences".
     * 
     * @param node
     * @return
     */
    public static boolean hasDependencyClients(IRepositoryNode node) {
        List<ModelElement> dependencies = EObjectHelper.getDependencyClients(node);
        if (dependencies == null || dependencies.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 
     * Add qiongli:find all REPOSITORY_ELEMENT by folderNode.
     * 
     * @param folderNode
     * @param withDelted
     * @return
     */
    public static List<IRepositoryNode> getRepositoryElementFromFolder(IRepositoryNode folderNode, boolean withDelted) {
        List<IRepositoryNode> elementNodes = new ArrayList<IRepositoryNode>();
        if (isFolderNode(folderNode.getType())) {
            for (IRepositoryNode subNode : folderNode.getChildren(withDelted)) {
                elementNodes.addAll(getRepositoryElementFromFolder(subNode, withDelted));
            }
        } else {
            elementNodes.add(folderNode);
        }
        return elementNodes;

    }

    public static String getLocker(RepositoryNode node) {
        if (node != null) {
            return getLocker((RepositoryViewObject) node.getObject());
        }
        return ""; //$NON-NLS-1$
    }

    public static String getLocker(RepositoryViewObject viewObject) {
        if (viewObject != null) {
            Property property = viewObject.getProperty();
            if (property != null) {
                Item item = property.getItem();
                if (item != null) {
                    return ProxyRepositoryFactory.getInstance().getLockInfo(item).getUser();
                }
            }
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * get SourceFileRepNodes which under the parentNode.
     * 
     * @param parentNode
     * @param recursive
     * @return
     */
    public static List<SourceFileRepNode> getSourceFileRepNodes(RepositoryNode parentNode, boolean recursive) {
        List<SourceFileRepNode> result = new ArrayList<SourceFileRepNode>();
        List<IRepositoryNode> children = parentNode.getChildren();
        for (IRepositoryNode node : children) {
            if (node instanceof SourceFileRepNode) {
                result.add((SourceFileRepNode) node);
            } else if (node instanceof SourceFileFolderRepNode) {
                if (recursive) {
                    result.addAll(getSourceFileRepNodes((SourceFileFolderRepNode) node, recursive));
                }
            }
        }
        return result;
    }

    /**
     * get RepositoryNode which contains a ModelElment(include: Analysis, Report, IndicatorDefinition, Pattern, DqRule)
     * under the parentNode.
     * 
     * @param parentNode
     * @param recursive
     * @return
     */
    public static List<RepositoryNode> getModelElementRepNodes(RepositoryNode parentNode, boolean recursive) {
        List<RepositoryNode> result = new ArrayList<RepositoryNode>();
        List<IRepositoryNode> children = parentNode.getChildren();
        for (IRepositoryNode node : children) {
            ModelElement modelElementFromRepositoryNode = RepositoryNodeHelper.getModelElementFromRepositoryNode(node);
            if (modelElementFromRepositoryNode != null) {
                result.add((RepositoryNode) node);
            } else {
                boolean isFolder = false;
                if (node instanceof AnalysisFolderRepNode) {
                    AnalysisFolderRepNode anaFolderRepNode = (AnalysisFolderRepNode) node;
                    isFolder = !anaFolderRepNode.isVirtualFolder();
                } else if (node instanceof ReportFolderRepNode) {
                    ReportFolderRepNode repFolderRepNode = (ReportFolderRepNode) node;
                    isFolder = !repFolderRepNode.isVirtualFolder();
                } else if (node instanceof UserDefIndicatorFolderRepNode || node instanceof PatternRegexFolderRepNode
                        || node instanceof PatternRegexSubFolderRepNode || node instanceof PatternSqlFolderRepNode
                        || node instanceof PatternSqlSubFolderRepNode || node instanceof RulesFolderRepNode) {
                    isFolder = true;
                }
                if (isFolder && recursive) {
                    result.addAll(getModelElementRepNodes((RepositoryNode) node, recursive));
                }
            }
        }
        return result;
    }

    /**
     * get the ModelElement uuid list from RepositoryNode list.
     * 
     * @param nodes
     * @return
     */
    public static List<String> getUuids(List<? extends IRepositoryNode> nodes) {
        List<String> result = new ArrayList<String>();
        for (IRepositoryNode node : nodes) {
            ModelElement metadataElement = RepositoryNodeHelper.getModelElementFromRepositoryNode(node);
            if (metadataElement != null) {
                String uuid = ResourceHelper.getUUID(metadataElement);
                if (uuid != null) {
                    result.add(uuid);
                }
            }
        }
        return result;
    }

    /**
     * get the ERepositoryObjectType of the RepositoryNode.
     * 
     * @param node
     * @return
     */
    public static ERepositoryObjectType getERepositoryObjectType(RepositoryNode node) {

        return null;
    }

    /**
     * get the relative path of the RepositoryNode.
     * 
     * @param node
     * @return
     */
    public static IPath getRelativePath(RepositoryNode node) {
        return null;
    }

    /**
     * judge the nodeList contains the node, the node must own a ModelElement.
     * 
     * @param nodeList
     * @param node
     * @return
     */
    public static boolean containsModelElementNode(List<IRepositoryNode> nodeList, IRepositoryNode node) {
        if (nodeList == null || nodeList.size() == 0 || node == null) {
            return false;
        } else {
            ModelElement meNode = RepositoryNodeHelper.getModelElementFromRepositoryNode(node);
            if (meNode != null) {
                String uuid = ResourceHelper.getUUID(meNode);
                if (uuid != null) {
                    for (IRepositoryNode repNode : nodeList) {
                        ModelElement meRepNode = RepositoryNodeHelper.getModelElementFromRepositoryNode(repNode);
                        if (meRepNode != null) {
                            if (uuid.equals(ResourceHelper.getUUID(meRepNode))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
