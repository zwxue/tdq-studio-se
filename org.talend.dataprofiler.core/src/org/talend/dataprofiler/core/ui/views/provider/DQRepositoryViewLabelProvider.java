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

import java.util.List;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.exchange.ExchangeCategoryRepNode;
import org.talend.dataprofiler.core.ui.exchange.ExchangeComponentRepNode;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.AnalysisSubFolderRepNode;
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
import org.talend.dq.nodes.DFConnectionRepNode;
import org.talend.dq.nodes.DFTableRepNode;
import org.talend.dq.nodes.MDMConnectionFolderRepNode;
import org.talend.dq.nodes.MDMConnectionRepNode;
import org.talend.dq.nodes.MDMSchemaRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.dq.nodes.PatternLanguageRepNode;
import org.talend.dq.nodes.PatternRepNode;
import org.talend.dq.nodes.RecycleBinRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.ReportSubFolderRepNode;
import org.talend.dq.nodes.RuleRepNode;
import org.talend.dq.nodes.SourceFileRepNode;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;

/**
 * @author rli
 */
public class DQRepositoryViewLabelProvider extends AdapterFactoryLabelProvider {

    public DQRepositoryViewLabelProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    public Image getImage(Object element) {
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
                if (node instanceof DBConnectionRepNode) {
                    if (!isSupportedConnection(node)) {
                        return ImageLib.createErrorIcon(ImageLib.TD_DATAPROVIDER).createImage();
                    }
                    return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
                } else if (node instanceof MDMConnectionRepNode) {
                    return ImageLib.getImage(ImageLib.MDM_CONNECTION);
                } else if (node instanceof DFConnectionRepNode) {
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
                } else if (node instanceof SourceFileRepNode) {
                    return ImageLib.getImage(ImageLib.SOURCE_FILE);
                } else if (node instanceof ExchangeCategoryRepNode || node instanceof ExchangeComponentRepNode) {
                    return ImageLib.getImage(ImageLib.EXCHANGE);
                } else if (node instanceof RepositoryNode) {
                    // MOD qiongli 2011-1-18 get image for nodes in recycle bin
                    Image image = getImageByContentType((RepositoryNode) node);
                    if (image != null) {
                        return image;
                    }
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
                // else if (node instanceof PatternLanguageRepNode) {
                // return ImageLib.getImage(ImageLib.);
                // }
            }
        }
        // ~

        return super.getImage(element);
    }

    public String getText(Object element) {
        if (element instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) element;
            if (node instanceof RecycleBinRepNode || node instanceof ExchangeCategoryRepNode
                    || node instanceof ExchangeComponentRepNode) {
                // virtual node, get the lable of node directly
                return node.getLabel();
            } else if (node instanceof DBTableFolderRepNode) {
                return ((DBTableFolderRepNode) node).getNodeName();
            } else if (node instanceof DBViewFolderRepNode) {
                return ((DBViewFolderRepNode) node).getNodeName();
            } else if (node instanceof DBColumnFolderRepNode || node instanceof DFColumnFolderRepNode) {
                return DefaultMessagesImpl.getString("ColumnFolderNode.columns");
            } else if (node instanceof SourceFileRepNode) {
                return ((SourceFileRepNode) node).getLabel();
            } else if (node instanceof AnalysisRepNode || node instanceof ReportRepNode
                    || node instanceof SysIndicatorDefinitionRepNode || node instanceof PatternRepNode
                    || node instanceof RuleRepNode) {
                return node.getObject().getLabel() + " " + node.getObject().getVersion();
            } else if (node instanceof DBConnectionRepNode && !isSupportedConnection(node)) {
                return node.getObject().getLabel() + "(Unsupported)";
            } else if (node instanceof AnalysisSubFolderRepNode) {
                IRepositoryViewObject object = node.getObject();
                if (object == null) {
                    return DefaultMessagesImpl.getString("AnalysisSubFolderRepNode.analyzedElement");
                }
            } else if (node instanceof ReportSubFolderRepNode) {
                IRepositoryViewObject object = node.getObject();
                if (object == null) {
                    return (String) node.getProperties(EProperties.LABEL);
                }
            } else if (node instanceof PatternLanguageRepNode) {
                return node.getLabel();
            }
            return node.getObject().getLabel();
        }
        String text = super.getText(element);
        return "".equals(text) ? DefaultMessagesImpl.getString("DQRepositoryViewLabelProvider.noName") : text;
    }

    /**
     * 
     * DOC qiongli Comment method "getImageByContentType".
     * 
     * @param repositoryNode
     * @return
     */
    private Image getImageByContentType(RepositoryNode repositoryNode) {
        ERepositoryObjectType type = (ERepositoryObjectType) repositoryNode.getProperties(EProperties.CONTENT_TYPE);
        if (type == null) {
            return null;
        }

        switch (type) {
        case TDQ_ANALYSIS_ELEMENT:
            return ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
        case METADATA_MDMCONNECTION:
            return ImageLib.getImage(ImageLib.MDM_CONNECTION);
        case METADATA_CONNECTIONS:
            return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
        case METADATA_FILE_DELIMITED:
            return ImageLib.getImage(ImageLib.FILE_DELIMITED);
        case TDQ_REPORT_ELEMENT:
            return ImageLib.getImage(ImageLib.REPORT_OBJECT);
        case TDQ_INDICATOR_ELEMENT:
            return ImageLib.getImage(ImageLib.IND_DEFINITION);
        case TDQ_PATTERN_ELEMENT:
            return ImageLib.getImage(ImageLib.PATTERN_REG);
        case TDQ_RULES:
            return ImageLib.getImage(ImageLib.DQ_RULE);
        case TDQ_RULES_SQL:
            return ImageLib.getImage(ImageLib.DQ_RULE);
        case TDQ_SOURCE_FILE_ELEMENT:
            return ImageLib.getImage(ImageLib.SOURCE_FILE);
        case TDQ_EXCHANGE:
            return ImageLib.getImage(ImageLib.EXCHANGE);
        case FOLDER:
            return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        }
        return null;

    }

    private boolean isSupportedConnection(IRepositoryNode repNode) {
        ERepositoryObjectType objectType = repNode.getObjectType();

        if (objectType == ERepositoryObjectType.METADATA_CONNECTIONS) {
            ConnectionItem connectionItem = (ConnectionItem) repNode.getObject().getProperty().getItem();
            if (connectionItem.getConnection() instanceof DatabaseConnection) {
                String databaseType = ((DatabaseConnection) connectionItem.getConnection()).getDatabaseType();
                List<String> tdqSupportDBType = MetadataConnectionUtils.getTDQSupportDBTemplate();
                return tdqSupportDBType.contains(databaseType);
            }
        }

        return false;
    }
}
