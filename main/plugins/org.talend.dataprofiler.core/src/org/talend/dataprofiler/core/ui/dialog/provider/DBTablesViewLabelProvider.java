// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import org.eclipse.swt.graphics.Image;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.MDMConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewLabelProvider;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.resource.ResourceManager;

/**
 * @author rli
 * 
 */
public class DBTablesViewLabelProvider extends DQRepositoryViewLabelProvider {

    private static Logger log = Logger.getLogger(DBTablesViewLabelProvider.class);

    public DBTablesViewLabelProvider() {
        super();
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

    @Override
    public Image getImage(Object element) {
        if (element instanceof IProject) {
            return ImageLib.getImage(ImageLib.PROJECT_ACTIVE);
        } else if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            if (ResourceManager.isMdmConnectionFolder(folder)) {
                return ImageLib.getImage(ImageLib.MDM_CONNECTION);
            } else if (ResourceManager.isTDQConnectionFolder(folder)) {
                return ImageLib.getImage(ImageLib.CONNECTION);
            }
            return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        } else if (element instanceof IFolderNode) {
            return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        } else if (element instanceof IFile) {
            return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
        } else if (element instanceof TdXmlSchema) {
            return ImageLib.getImage(ImageLib.XML_DOC);
        } else if (element instanceof TdXmlElementType) {
            return ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
        } else if (element instanceof IRepositoryViewObject) {
            IRepositoryViewObject conn = (IRepositoryViewObject) element;
            Item connItem = conn.getProperty().getItem();
            if (connItem instanceof MDMConnectionItem) {
                return ImageLib.getImage(ImageLib.MDM_CONNECTION);
            }
            return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
        }
        return super.getImage(element);
    }

    @Override
    public String getText(Object element) {
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

        else if (element instanceof TdXmlSchema) {
            return ((TdXmlSchema) element).getName();
        } else if (element instanceof TdXmlElementType) {
            String elemLabe = ((TdXmlElementType) element).getName();
            String elementType = ((TdXmlElementType) element).getJavaType();
            if (elementType != null && !StringUtils.isEmpty(elementType)) {
                elemLabe += " (" + elementType + ")";//$NON-NLS-1$ //$NON-NLS-2$
            }
            return elemLabe;
        }

        return super.getText(element);
    }
}
