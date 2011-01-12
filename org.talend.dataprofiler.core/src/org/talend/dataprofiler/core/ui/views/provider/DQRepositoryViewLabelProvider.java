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

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.core.model.properties.DelimitedFileConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DBCatalogRepNode;
import org.talend.dq.nodes.DBColumnFolderRepNode;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DBConnectionFolderRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DBSchemaRepNode;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewFolderRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFColumnFolderRepNode;
import org.talend.dq.nodes.DFColumnRepNode;
import org.talend.dq.nodes.DFTableRepNode;
import org.talend.dq.nodes.MDMConnectionFolderRepNode;
import org.talend.dq.nodes.MDMConnectionRepNode;
import org.talend.dq.nodes.MDMSchemaRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.dq.nodes.PatternRepNode;
import org.talend.dq.nodes.RecycleBinRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.RuleRepNode;
import org.talend.dq.nodes.SourceFileRepNode;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.resource.EResourceConstant;

/**
 * @author rli
 * 
 */
public class DQRepositoryViewLabelProvider extends AdapterFactoryLabelProvider {

    public DQRepositoryViewLabelProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    public Image getImage(Object element) {

        // if (element instanceof IFolderNode) {
        // return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        // } else if (element instanceof TdColumn) {
        // if (ColumnHelper.isPrimaryKey((TdColumn) element)) {
        // // get the icon for primary key
        // return ImageLib.getImage(ImageLib.PK_COLUMN);
        // }
        // } else if (element instanceof IEcosComponent) {
        // return ImageLib.getImage(ImageLib.EXCHANGE);
        // } else if (element instanceof IEcosCategory) {
        // return ImageLib.getImage(ImageLib.EXCHANGE);
        // } else if (element instanceof IndicatorCategory) {
        // return ImageLib.getImage(ImageLib.IND_CATEGORY);
        // } else if (element instanceof IndicatorDefinition) {
        // return ImageLib.getImage(ImageLib.IND_DEFINITION);
        // } else if (element instanceof TdView) {
        // return ImageLib.getImage(ImageLib.VIEW);
        // } else if (element instanceof TdXmlSchema) {
        // return ImageLib.getImage(ImageLib.XML_DOC);
        // } else if (element instanceof TdXmlElementType) {
        // return ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
        // } else if (element instanceof IRecycleBin) {
        // return ImageLib.getImage(ImageLib.RECYCLEBIN_EMPTY);
        // } else
        // // MOD qiongli
        // if (element instanceof DQRecycleBinNode) {
        // DQRecycleBinNode rbn = (DQRecycleBinNode) element;
        // Object obj = rbn.getObject();
        // // MOD qiongli 2010-10-8,bug 15674
        // if (obj instanceof Property) {
        // Property property = (Property) obj;
        // Item item = property.getItem();
        // if (item instanceof TDQAnalysisItem) {
        // IFile file = PropertyHelper.getItemFile(property);
        // Analysis analysis = AnaResourceFileHelper.getInstance().findAnalysis(file);
        // ColumnDependencyAnalysisHandler analysisHandler = new ColumnDependencyAnalysisHandler();
        // analysisHandler.setAnalysis(analysis);
        // if (analysisHandler.getResultMetadata().getExecutionNumber() != 0) {
        // if (!analysisHandler.getResultMetadata().isLastRunOk()) {
        // return ImageLib.createErrorIcon(ImageLib.ANALYSIS_OBJECT).createImage();
        // } else if (analysisHandler.getResultMetadata().isOutThreshold()) {
        // return ImageLib.createInvalidIcon(ImageLib.ANALYSIS_OBJECT).createImage();
        // }
        // }
        // return ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
        // } else if (item instanceof TDQReportItem) {
        // return ImageLib.getImage(ImageLib.REPORT_OBJECT);
        // } else if (item instanceof TDQPatternItem) {
        // return ImageLib.getImage(ImageLib.PATTERN_REG);
        // } else if (item instanceof TDQBusinessRuleItem) {
        // return ImageLib.getImage(ImageLib.DQ_RULE);
        // } else if (item instanceof TDQIndicatorDefinitionItem) {
        // return ImageLib.getImage(ImageLib.IND_DEFINITION);
        // } else if (item instanceof MDMConnectionItem) {
        // return ImageLib.getImage(ImageLib.MDM_CONNECTION);
        // } else if (item instanceof ConnectionItem) {
        // return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
        // }
        // } else if (obj instanceof IFolder) {
        // return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        // }
        // } else if (element instanceof MDMConnection) {
        // return ImageLib.getImage(ImageLib.MDM_CONNECTION);
        // } else
        if (element instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) element;
            IRepositoryViewObject viewObject = node.getObject();
            ENodeType type = node.getType();
            if (element instanceof RecycleBinRepNode) {
                return ImageLib.getImage(ImageLib.RECYCLEBIN_EMPTY);
            } else if (type.equals(ENodeType.SYSTEM_FOLDER)) {
                if (viewObject.getLabel().equals(EResourceConstant.DATA_PROFILING.getName())) {
                    return ImageLib.getImage(ImageLib.DATA_PROFILING);
                } else if (viewObject.getLabel().equals(EResourceConstant.METADATA.getName())) {
                    return ImageLib.getImage(ImageLib.METADATA);
                } else if (node instanceof DBConnectionFolderRepNode) {
                    return ImageLib.getImage(ImageLib.CONNECTION);
                } else if (node instanceof MDMConnectionFolderRepNode) {
                    return ImageLib.getImage(ImageLib.MDM_CONNECTION);
                } else if (viewObject.getLabel().equals(EResourceConstant.FILEDELIMITED.getName())) {
                    return ImageLib.getImage(ImageLib.FILE_DELIMITED);
                } else if (viewObject.getLabel().equals(EResourceConstant.LIBRARIES.getName())) {
                    return ImageLib.getImage(ImageLib.LIBRARIES);
                } else if (viewObject.getLabel().equals(EResourceConstant.EXCHANGE.getName())) {
                    return ImageLib.getImage(ImageLib.EXCHANGE);
                }
                return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
            } else if (type.equals(ENodeType.SIMPLE_FOLDER)) {
                return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
            } else if (type.equals(ENodeType.REPOSITORY_ELEMENT)) {
                Item item = viewObject.getProperty().getItem();
                if (node instanceof DBConnectionRepNode) {
                    return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
                } else if (node instanceof MDMConnectionRepNode) {
                    return ImageLib.getImage(ImageLib.MDM_CONNECTION);
                } else if (item instanceof DelimitedFileConnectionItem) {
                    return ImageLib.getImage(ImageLib.FILE_DELIMITED);
                } else if (node instanceof AnalysisRepNode) {
                    return ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
                } else if (node instanceof ReportRepNode) {
                    return ImageLib.getImage(ImageLib.REPORT_OBJECT);
                } else if (node instanceof SysIndicatorDefinitionRepNode) {
                    return ImageLib.getImage(ImageLib.IND_DEFINITION);
                } else if (node instanceof PatternRepNode) {
                    return ImageLib.getImage(ImageLib.PATTERN_REG);
                } else if (node instanceof RuleRepNode) {
                    return ImageLib.getImage(ImageLib.DQ_RULE);
                }
            } else if (type.equals(ENodeType.TDQ_REPOSITORY_ELEMENT)) {
                if (node instanceof DBCatalogRepNode) {
                    return ImageLib.getImage(ImageLib.CATALOG);
                } else if (node instanceof DBSchemaRepNode) {
                    return ImageLib.getImage(ImageLib.SCHEMA);
                } else if (node instanceof DBTableFolderRepNode) {
                    return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
                } else if (node instanceof DBViewFolderRepNode) {
                    return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
                } else if (node instanceof DBTableRepNode || node instanceof DFTableRepNode) {
                    return ImageLib.getImage(ImageLib.TABLE);
                } else if (node instanceof DBViewRepNode) {
                    return ImageLib.getImage(ImageLib.VIEW);
                } else if (node instanceof DBColumnRepNode || node instanceof DFColumnRepNode) {
                    return ImageLib.getImage(ImageLib.TD_COLUMN);
                } else if (node instanceof MDMSchemaRepNode) {
                    return ImageLib.getImage(ImageLib.XML_DOC);
                } else if (node instanceof MDMXmlElementRepNode) {
                    return ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
                } else if (node instanceof DBColumnFolderRepNode || node instanceof DFColumnFolderRepNode) {
                    return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
                }
            }
        }
        // ~

        return super.getImage(element);
    }

    public String getText(Object element) {
        // String tableOwner = null;
        // if (element instanceof ModelElement) {
        // tableOwner = ColumnSetHelper.getTableOwner((ModelElement) element);
        // }
        // if (element instanceof AbstractFolderNode) {
        // if (((IFolderNode) element).getChildren() != null) {
        //                return ((IFolderNode) element).getName() + "(" + ((IFolderNode) element).getChildren().length + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        // }
        //
        // return ((IFolderNode) element).getName();
        // } else if (element instanceof IEcosComponent) {
        // return ((IEcosComponent) element).getName();
        // } else if (element instanceof IEcosCategory) {
        // return ((IEcosCategory) element).getName();
        // } else if (element instanceof IndicatorDefinition) {
        // return ((IndicatorDefinition) element).getName();
        // } else if (element instanceof IndicatorCategory) {
        // return ((IndicatorCategory) element).getName();
        // } else if (element instanceof IRecycleBin) {
        // return ((IRecycleBin) element).getName();
        // }
        //
        // // PTODO qzhang fixed bug 4176: Display expressions as children of the
        // // patterns
        // if (element instanceof RegularExpression) {
        // RegularExpression regExp = (RegularExpression) element;
        // return regExp.getExpression().getLanguage();
        // } else if (element instanceof Connection) {
        // return ((Connection) element).getName();
        // }
        //
        // // MOD mzhao feature 10238
        // if (element instanceof TdXmlSchema) {
        // return ((TdXmlSchema) element).getName();
        // } else if (element instanceof TdXmlElementType) {
        // String elemLabe = ((TdXmlElementType) element).getName();
        // String elementType = ((TdXmlElementType) element).getJavaType();
        // if (elementType != null && !StringUtils.isEmpty(elementType)) {
        // elemLabe += " (" + elementType + ")";
        // }
        // return elemLabe;
        // } else if ((element instanceof TdTable || element instanceof TdView) && tableOwner != null &&
        // !"".equals(tableOwner)) {
        // return super.getText(element) + "(" + tableOwner + ")";
        // } else
        // // MOD qiongli :get the name of recycle bin's child
        // if (element instanceof DQRecycleBinNode) {
        // DQRecycleBinNode rbn = (DQRecycleBinNode) element;
        // Object obj = rbn.getObject();
        // // MOD qiongli 2010-8-10,bug 15674
        // if (obj instanceof Property) {
        // Property property = (Property) obj;
        // Item item = property.getItem();
        // if (item instanceof ConnectionItem) {
        // Connection connection = ((ConnectionItem) item).getConnection();
        // if (connection.eIsProxy()) {
        // connection = (Connection) EObjectHelper.resolveObject(connection);
        // }
        // return connection.getName();
        // }
        // return property.getLabel();
        // } else if (obj instanceof IFolder) {
        // return ((IFolder) obj).getName();
        // }
        //
        // } else
        if (element instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) element;
            if (node instanceof RecycleBinRepNode) {
                return node.getLabel();
            } else if (node instanceof DBTableFolderRepNode) {
                return ((DBTableFolderRepNode) node).getNodeName();
            } else if (node instanceof DBViewFolderRepNode) {
                return ((DBViewFolderRepNode) node).getNodeName();
            } else if (node instanceof DBColumnFolderRepNode || node instanceof DFColumnFolderRepNode) {
                return DefaultMessagesImpl.getString("ColumnFolderNode.columns");
            } else if (node instanceof SourceFileRepNode) {
                return ((SourceFileRepNode) node).getLabel();
            }
            if (node instanceof AnalysisRepNode || node instanceof ReportRepNode || node instanceof SysIndicatorDefinitionRepNode
                    || node instanceof PatternRepNode || node instanceof RuleRepNode) {
                return node.getObject().getLabel() + " " + node.getObject().getVersion();
            }
            return node.getObject().getLabel();
        }
        String text = super.getText(element);
        return "".equals(text) ? DefaultMessagesImpl.getString("DQRepositoryViewLabelProvider.noName") : text;
    }
}
