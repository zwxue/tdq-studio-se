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
package org.talend.dataprofiler.core.ui.dialog.provider;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.views.provider.MNComposedAdapterFactory;
import org.talend.dq.nodes.foldernode.IFolderNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class DBTablesViewLabelProvider extends AdapterFactoryLabelProvider {

    private static Logger log = Logger.getLogger(DBTablesViewLabelProvider.class);

    public DBTablesViewLabelProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider#getColumnImage(java.lang.Object, int)
     */
    @Override
    public Image getColumnImage(Object object, int columnIndex) {
        return getImage(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider#getColumnText(java.lang.Object, int)
     */
    @Override
    public String getColumnText(Object object, int columnIndex) {
        return getText(object);
    }

    public Image getImage(Object element) {
        if (element instanceof IProject) {
            return ImageLib.getImage(ImageLib.PROJECT_ACTIVE);
        } else if (element instanceof IFolder) {
            return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        } else if (element instanceof IFolderNode) {
            return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        } else if (element instanceof IFile) {
            return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
        } else if (element instanceof TdXmlSchema) {
            return ImageLib.getImage(ImageLib.XML_DOC);
        } else if (element instanceof TdXmlElementType) {
            return ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
        }
        return super.getImage(element);
    }

    public String getText(Object element) {
        String tableOwner = null;
        if (element instanceof ModelElement) {
            tableOwner = ColumnSetHelper.getTableOwner((ModelElement) element);
        }
        if (element instanceof IContainer) {
            return ((IContainer) element).getName();
        } else if (element instanceof IFolderNode) {
            return ((IFolderNode) element).getName();
        } else if (element instanceof IRepositoryViewObject) {
            IRepositoryViewObject reposViewObj = (IRepositoryViewObject) element;
            ConnectionItem item = (ConnectionItem) reposViewObj.getProperty().getItem();
            String decorateText = PluginConstant.EMPTY_STRING;
            if (item.getConnection() != null) {
                decorateText = item.getConnection().getName();
            }
            return decorateText;
        }

        // else if (element instanceof IFile) {
        // if (FactoriesUtil.isProvFile(((IFile) element).getFileExtension())) {
        // IFile file = (IFile) element;
        // TypedReturnCode<Connection> rc = PrvResourceFileHelper.getInstance().findProvider(file);
        // String decorateText = PluginConstant.EMPTY_STRING;
        // if (rc.isOk()) {
        // decorateText = rc.getObject().getName();
        // } else {
        // log.warn(rc.getMessage());
        // }
        // return decorateText;
        // }
        // }

        else if (element instanceof TdXmlSchema) {
            return ((TdXmlSchema) element).getName();
        } else if (element instanceof TdXmlElementType) {
            String elemLabe = ((TdXmlElementType) element).getName();
            String elementType = ((TdXmlElementType) element).getJavaType();
            if (elementType != null && !StringUtils.isEmpty(elementType)) {
                elemLabe += " (" + elementType + ")";
            }
            return elemLabe;
        } else if ((element instanceof TdTable || element instanceof TdView) && tableOwner != null && !"".equals(tableOwner)) {
            return super.getText(element) + "(" + tableOwner + ")";
        }

        return super.getText(element);
    }
}
