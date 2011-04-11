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
package org.talend.dataprofiler.core.ui.views.provider;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.exchange.ExchangeCategoryRepNode;
import org.talend.dataprofiler.core.ui.exchange.ExchangeComponentRepNode;
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
import org.talend.dq.nodes.ReportAnalysisRepNode;
import org.talend.dq.nodes.ReportFileRepNode;
import org.talend.dq.nodes.ReportFolderRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.ReportSubFolderRepNode;
import org.talend.dq.nodes.ReportSubFolderRepNode.ReportSubFolderType;
import org.talend.dq.nodes.RuleRepNode;
import org.talend.dq.nodes.SourceFileRepNode;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;

/**
 * @author rli
 */
public class DQRepositoryViewLabelProvider extends AdapterFactoryLabelProvider {

    private static final String LEFT = "(";//$NON-NLS-1$

    private static final String RIGHT = ")";//$NON-NLS-1$

    public DQRepositoryViewLabelProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    public Image getImage(Object element) {
        Image image = super.getImage(element);

        if (element instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) element;
            IRepositoryViewObject viewObject = node.getObject();
            ENodeType type = node.getType();
            if (node instanceof ReportAnalysisRepNode) {
                image = ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
            }
            if (element instanceof RecycleBinRepNode) {
                image = ImageLib.getImage(ImageLib.RECYCLEBIN_EMPTY);
            } else if (type.equals(ENodeType.SYSTEM_FOLDER)) {
                if (viewObject.getLabel().equals(EResourceConstant.DATA_PROFILING.getName())) {
                    image = ImageLib.getImage(ImageLib.DATA_PROFILING);
                } else if (viewObject.getLabel().equals(EResourceConstant.METADATA.getName())) {
                    image = ImageLib.getImage(ImageLib.METADATA);
                } else if (node instanceof DBConnectionFolderRepNode) {
                    image = ImageLib.getImage(ImageLib.CONNECTION);
                } else if (node instanceof MDMConnectionFolderRepNode) {
                    image = ImageLib.getImage(ImageLib.MDM_CONNECTION);
                } else if (viewObject.getLabel().equals(EResourceConstant.FILEDELIMITED.getName())) {
                    image = ImageLib.getImage(ImageLib.FILE_DELIMITED);
                } else if (viewObject.getLabel().equals(EResourceConstant.LIBRARIES.getName())) {
                    image = ImageLib.getImage(ImageLib.LIBRARIES);
                } else if (viewObject.getLabel().equals(EResourceConstant.EXCHANGE.getName())) {
                    image = ImageLib.getImage(ImageLib.EXCHANGE);
                } else {
                    image = ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
                }
            } else if (type.equals(ENodeType.SIMPLE_FOLDER)) {
                image = ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
            } else if (type.equals(ENodeType.REPOSITORY_ELEMENT)) {
                if (node instanceof DBConnectionRepNode) {
                    if (!isSupportedConnection(node)) {
                        image = ImageLib.createErrorIcon(ImageLib.TD_DATAPROVIDER).createImage();
                    }
                    image = ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
                } else if (node instanceof MDMConnectionRepNode) {
                    image = ImageLib.getImage(ImageLib.MDM_CONNECTION);
                } else if (node instanceof DFConnectionRepNode) {
                    image = ImageLib.getImage(ImageLib.FILE_DELIMITED);
                } else if (node instanceof AnalysisRepNode) {
                    image = ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
                } else if (node instanceof ReportRepNode) {
                    image = ImageLib.getImage(ImageLib.REPORT_OBJECT);
                } else if (node instanceof SysIndicatorDefinitionRepNode) {
                    image = ImageLib.getImage(ImageLib.IND_DEFINITION);
                } else if (node instanceof PatternRepNode) {
                    image = ImageLib.getImage(ImageLib.PATTERN_REG);
                } else if (node instanceof RuleRepNode) {
                    image = ImageLib.getImage(ImageLib.DQ_RULE);
                } else if (node instanceof SourceFileRepNode) {
                    image = ImageLib.getImage(ImageLib.SOURCE_FILE);
                } else if (node instanceof ExchangeCategoryRepNode || node instanceof ExchangeComponentRepNode) {
                    image = ImageLib.getImage(ImageLib.EXCHANGE);
                } else if (node instanceof RepositoryNode) {
                    // MOD qiongli 2011-1-18 get image for nodes in recycle bin
                    Image imageNode = getImageByContentType((RepositoryNode) node);
                    if (image != null) {
                        image = imageNode;
                    }
                }
                // MOD klliu 2010-04-11 20468: Unfolder "exchange",get many NPE
                // exchange folder did not contain viewObject.
                if (viewObject != null) {
                    // MOD yyi 2011-04-07 19696: "Lock element"
                    if (ERepositoryStatus.LOCK_BY_USER == ProxyRepositoryFactory.getInstance().getStatus(viewObject)) {
                        image = ImageLib.createLockedIcon(ImageDescriptor.createFromImage(image)).createImage();
                    }
                }
            } else if (type.equals(ENodeType.TDQ_REPOSITORY_ELEMENT)) {
                if (node instanceof DBCatalogRepNode) {
                    image = ImageLib.getImage(ImageLib.CATALOG);
                } else if (node instanceof DBSchemaRepNode) {
                    image = ImageLib.getImage(ImageLib.SCHEMA);
                } else if (node instanceof DBTableFolderRepNode) {
                    image = ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
                } else if (node instanceof DBViewFolderRepNode) {
                    image = ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
                } else if (node instanceof DBTableRepNode || node instanceof DFTableRepNode) {
                    image = ImageLib.getImage(ImageLib.TABLE);
                } else if (node instanceof DBViewRepNode) {
                    image = ImageLib.getImage(ImageLib.VIEW);
                } else if (node instanceof DBColumnRepNode) {
                    if (((DBColumnRepNode) node).isKey()) {
                        image = ImageLib.getImage(ImageLib.PK_COLUMN);
                    } else {
                        image = ImageLib.getImage(ImageLib.TD_COLUMN);
                    }
                } else if (node instanceof DFColumnRepNode) {

                    image = ImageLib.getImage(ImageLib.TD_COLUMN);
                } else if (node instanceof MDMSchemaRepNode) {
                    image = ImageLib.getImage(ImageLib.XML_DOC);
                } else if (node instanceof MDMXmlElementRepNode) {
                    image = ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
                } else if (node instanceof DBColumnFolderRepNode || node instanceof DFColumnFolderRepNode) {
                    image = ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
                }
                // else if (node instanceof PatternLanguageRepNode) {
                // return ImageLib.getImage(ImageLib.);
                // }
            }

        }
        // ~

        return image;
    }

    public String getText(Object element) {
        if (element instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) element;
            if (node instanceof RecycleBinRepNode || node instanceof ExchangeCategoryRepNode
                    || node instanceof ExchangeComponentRepNode) {
                // virtual node, get the lable of node directly
                return node.getLabel();
            } else if (node instanceof DBConnectionSubFolderRepNode) {
                return ((DBConnectionSubFolderRepNode) node).getObject().getLabel();
            } else if (node instanceof DBConnectionFolderRepNode) {
                return "DB " + ((DBConnectionFolderRepNode) node).getObject().getLabel();
            } else if (node instanceof DBTableFolderRepNode) {
                return ((DBTableFolderRepNode) node).getNodeName();
            } else if (node instanceof DBViewFolderRepNode) {
                return ((DBViewFolderRepNode) node).getNodeName();
            } else if (node instanceof DBColumnFolderRepNode || node instanceof DFColumnFolderRepNode) {
                return DefaultMessagesImpl.getString("ColumnFolderNode.columns");
            } // ~MOD klliu 2011-03-29 bug 19936
            else if (node instanceof DBColumnRepNode) {
                DBColumnRepNode columnNode = (DBColumnRepNode) node;
                return columnNode.getLabel() + LEFT + columnNode.getNodeDataType() + RIGHT;
            } else if (node instanceof DFColumnRepNode) {
                DFColumnRepNode dfColumnRepNode = (DFColumnRepNode) node;
                String nodeDataType = dfColumnRepNode.getNodeDataType();
                return dfColumnRepNode.getId() + LEFT + nodeDataType + RIGHT;
            } else if (node instanceof MDMXmlElementRepNode) {
                MDMXmlElementRepNode mdmColumnRepNode = (MDMXmlElementRepNode) node;
                String nodeDataType = mdmColumnRepNode.getNodeDataType();
                if (!"".equals(nodeDataType)) {//$NON-NLS-1$
                    return mdmColumnRepNode.getTdXmlElementType().getName() + LEFT + nodeDataType + RIGHT;
                }
                return mdmColumnRepNode.getTdXmlElementType().getName();
            }
            // ~
            else if (node instanceof SourceFileRepNode) {
                return ((SourceFileRepNode) node).getLabel();
            } else if (node instanceof AnalysisRepNode || node instanceof ReportRepNode
                    || node instanceof SysIndicatorDefinitionRepNode || node instanceof PatternRepNode
                    || node instanceof RuleRepNode) {
                // return node.getObject().getLabel() + " " + node.getObject().getVersion();
                return node.getLabel() + " " + node.getObject().getVersion();
            } else if (node instanceof DBConnectionRepNode && !isSupportedConnection(node)) {
                return node.getObject().getLabel() + "(Unsupported)";
            } else if (node instanceof DBCatalogRepNode) {
                // MOD zshen to modify catalog name when connection is ODBC
                String catalogName = node.getObject().getLabel();
                IPath catalogPath = new Path(catalogName);
                catalogName = catalogPath.removeFileExtension().lastSegment();
                return catalogName;
            } else if (node instanceof AnalysisFolderRepNode) {
                if (node instanceof AnalysisSubFolderRepNode) {
                    AnalysisSubFolderRepNode anaSubNode = (AnalysisSubFolderRepNode) node;
                    if (node.getObject() == null) {
                        return DefaultMessagesImpl.getString("AnalysisSubFolderRepNode.analyzedElement") + anaSubNode.getCount();
                    }
                    return anaSubNode.getLabelWithCount();
                }
                AnalysisFolderRepNode anaNode = (AnalysisFolderRepNode) node;
                return anaNode.getLabelWithCount();
            } else if (node instanceof ReportFolderRepNode) {
                if (node instanceof ReportSubFolderRepNode) {
                    ReportSubFolderRepNode repSubNode = (ReportSubFolderRepNode) node;
                    if (!ReportSubFolderType.SUB_FOLDER.equals(repSubNode.getReportSubFolderType())) {
                        return (String) node.getProperties(EProperties.LABEL) + repSubNode.getCount();
                    }
                    return repSubNode.getLabelWithCount();
                }
                ReportFolderRepNode repNode = (ReportFolderRepNode) node;
                return repNode.getLabelWithCount();
            } else if (node instanceof PatternLanguageRepNode) {
                return node.getLabel();
            } else if (node instanceof ReportFileRepNode || node instanceof ReportAnalysisRepNode) {
                return node.getLabel();
            }
            String label = node.getObject().getLabel();
            boolean startsWith = label.startsWith(DQStructureManager.PREFIX_TDQ);
            if (startsWith) {
                label = label.substring(4, label.length());
                return label;
            } else if (label.equals(EResourceConstant.METADATA.getName())) {
                label = label.substring(0, 1).toUpperCase() + label.substring(1);
                return label;

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
        if (type == ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT) {
            return ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
        } else if (type == ERepositoryObjectType.METADATA_MDMCONNECTION) {
            return ImageLib.getImage(ImageLib.MDM_CONNECTION);
        } else if (type == ERepositoryObjectType.METADATA_CONNECTIONS) {
            return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
        } else if (type == ERepositoryObjectType.METADATA_FILE_DELIMITED) {
            return ImageLib.getImage(ImageLib.FILE_DELIMITED);
        } else if (type == ERepositoryObjectType.TDQ_REPORT_ELEMENT) {
            return ImageLib.getImage(ImageLib.REPORT_OBJECT);
        } else if (type == ERepositoryObjectType.TDQ_INDICATOR_ELEMENT) {
            return ImageLib.getImage(ImageLib.IND_DEFINITION);
        } else if (type == ERepositoryObjectType.TDQ_PATTERN_ELEMENT) {
            return ImageLib.getImage(ImageLib.PATTERN_REG);
        } else if (type == ERepositoryObjectType.TDQ_RULES) {
            return ImageLib.getImage(ImageLib.DQ_RULE);
        } else if (type == ERepositoryObjectType.TDQ_RULES_SQL) {
            return ImageLib.getImage(ImageLib.DQ_RULE);
        } else if (type == ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT) {
            return ImageLib.getImage(ImageLib.SOURCE_FILE);
        } else if (type == ERepositoryObjectType.TDQ_EXCHANGE) {
            return ImageLib.getImage(ImageLib.EXCHANGE);
        } else if (type == ERepositoryObjectType.FOLDER) {
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
