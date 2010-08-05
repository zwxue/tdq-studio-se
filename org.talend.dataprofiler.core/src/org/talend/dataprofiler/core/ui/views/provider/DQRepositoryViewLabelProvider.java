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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.DQRecycleBinNode;
import org.talend.dataprofiler.core.recycle.IRecycleBin;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dataprofiler.ecos.model.IEcosComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.nodes.foldernode.AbstractFolderNode;
import org.talend.dq.nodes.foldernode.IFolderNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class DQRepositoryViewLabelProvider extends AdapterFactoryLabelProvider {

    public DQRepositoryViewLabelProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    public Image getImage(Object element) {

        if (element instanceof IFolderNode) {
            return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        } else if (element instanceof IFile) { // MOD xqliu 2010-08-05 bug 14203
            IFile file = (IFile) element;
            String fileExtension = file.getFileExtension();
            if (FactoriesUtil.isProvFile(fileExtension)) {
                if (ConnectionUtils.isMdmConnection(file)) {
                    return ImageLib.getImage(ImageLib.MDM_CONNECTION);
                }
                return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
            }
        } else if (element instanceof TdColumn) {
            if (ColumnHelper.isPrimaryKey((TdColumn) element)) {
                // get the icon for primary key
                return ImageLib.getImage(ImageLib.PK_COLUMN);
            }
        } else if (element instanceof IEcosComponent) {
            return ImageLib.getImage(ImageLib.EXCHANGE);
        } else if (element instanceof IEcosCategory) {
            return ImageLib.getImage(ImageLib.EXCHANGE);
        } else if (element instanceof IndicatorCategory) {
            return ImageLib.getImage(ImageLib.IND_CATEGORY);
        } else if (element instanceof IndicatorDefinition) {
            return ImageLib.getImage(ImageLib.IND_DEFINITION);
        } else if (element instanceof TdView) {
            return ImageLib.getImage(ImageLib.VIEW);
        } else if (element instanceof TdXMLDocument) {
            return ImageLib.getImage(ImageLib.XML_DOC);
        } else if (element instanceof TdXMLElement) {
            return ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
        } else if (element instanceof IRecycleBin) {
            return ImageLib.getImage(ImageLib.RECYCLEBIN_EMPTY);
        }
        // MOD qiongli
        if (element instanceof DQRecycleBinNode) {
            DQRecycleBinNode rbn = (DQRecycleBinNode) element;
            Object obj = rbn.getObject();
            if (obj instanceof IFile) {
                IFile file = (IFile) obj;
                String type = file.getFileExtension();
                if (type.equals(FactoriesUtil.ANA)) {
                    return ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
                } else if (type.equals(FactoriesUtil.REP)) {
                    return ImageLib.getImage(ImageLib.REPORT_OBJECT);
                } else if (type.equals(FactoriesUtil.PATTERN)) {
                    return ImageLib.getImage(ImageLib.PATTERN_REG);
                } else if (type.equals(FactoriesUtil.DQRULE)) {
                    return ImageLib.getImage(ImageLib.DQ_RULE);
                } else if (type.equals(FactoriesUtil.PROV)) {
                    return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
                } else if (type.equals(FactoriesUtil.DEFINITION)) {
                    return ImageLib.getImage(ImageLib.IND_DEFINITION);
                }
            } else if (obj instanceof IFolder) {
                return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
            }
        }
        // ~

        return super.getImage(element);
    }

    public String getText(Object element) {
        String tableOwner = null;
        if (element instanceof ModelElement) {
            tableOwner = ColumnSetHelper.getTableOwner((ModelElement) element);
        }
        if (element instanceof AbstractFolderNode) {
            if (((IFolderNode) element).getChildren() != null) {
                return ((IFolderNode) element).getName() + "(" + ((IFolderNode) element).getChildren().length + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }

            return ((IFolderNode) element).getName();
        } else if (element instanceof IEcosComponent) {
            return ((IEcosComponent) element).getName();
        } else if (element instanceof IEcosCategory) {
            return ((IEcosCategory) element).getName();
        } else if (element instanceof IndicatorDefinition) {
            return ((IndicatorDefinition) element).getName();
        } else if (element instanceof IndicatorCategory) {
            return ((IndicatorCategory) element).getName();
        } else if (element instanceof IRecycleBin) {
            return ((IRecycleBin) element).getName();
        }

        // PTODO qzhang fixed bug 4176: Display expressions as children of the
        // patterns
        if (element instanceof RegularExpression) {
            RegularExpression regExp = (RegularExpression) element;
            return regExp.getExpression().getLanguage();
        } else if (element instanceof Connection) {
            return ((Connection) element).getName();
        }

        // MOD mzhao feature 10238
        if (element instanceof TdXMLDocument) {
            return ((TdXMLDocument) element).getName();
        } else if (element instanceof TdXMLElement) {
            String elemLabe = ((TdXMLElement) element).getName();
            String elementType = ((TdXMLElement) element).getJavaType();
            if (elementType != null && !StringUtils.isEmpty(elementType)) {
                elemLabe += " (" + elementType + ")";
            }
            return elemLabe;
        } else if ((element instanceof TdTable || element instanceof TdView) && tableOwner != null && !"".equals(tableOwner)) {
            return super.getText(element) + "(" + tableOwner + ")";
        }
        // MOD qiongli
        if (element instanceof DQRecycleBinNode) {
            DQRecycleBinNode rbn = (DQRecycleBinNode) element;
            Object obj = rbn.getObject();
            if (obj instanceof IFile) {
                IFile file = (IFile) obj;
                ModelElement mElement = ModelElementFileFactory.getModelElement(file);

                if (mElement != null) {
                    return DqRepositoryViewService.buildElementName(mElement);
                }
            } else if (obj instanceof IFolder) {
                return ((IFolder) obj).getName();
            }

        }
        String text = super.getText(element);
        return "".equals(text) ? DefaultMessagesImpl.getString("DQRepositoryViewLabelProvider.noName") : text;
    }
}
