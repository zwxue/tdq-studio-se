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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dq.helper.EObjectHelper;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class XMLDataProviderHandle extends RepositoryViewObjectHandle {

    /**
     * DOC bZhou XMLDataProviderHandle constructor comment.
     * 
     * @param file
     */
    XMLDataProviderHandle(Property property) {
        super(property);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicate()
     */
    public IFile duplicate() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#delete()
     */
    public boolean delete() throws Exception {
        if (isPhysicalDelete()) {
            Connection connection = ((ConnectionItem) getProperty().getItem()).getConnection();
            // MOD qiongli 2010-9-29 bug 14469
            if (connection != null && connection.eIsProxy()) {
                connection = (Connection) EObjectHelper.resolveObject(connection);
            }
            MDMConnection mdmConnection = SwitchHelpers.MDMCONNECTION_SWITCH.doSwitch(connection);
            if (mdmConnection != null) {
                EList<Package> packages = mdmConnection.getDataPackage();
                if (packages != null && packages.size() > 0) {
                    TdXmlSchema tdXmlDocument = SwitchHelpers.XMLSCHEMA_SWITCH.doSwitch(packages.get(0));
                    if (tdXmlDocument != null) {
                        // MOD xqliu 2010-08-09 bug 14469
                        IFolder mdmFolder = ResourceManager.getMDMConnectionFolder();
                        IFolder folder=(IFolder)mdmFolder.getFile(tdXmlDocument.getXsdFilePath()).getParent();
                        folder.delete(true, null);
                        IFolder xsdFolder = mdmFolder.getFolder(".xsd");
                        if (xsdFolder.members().length == 0) {
                            xsdFolder.delete(true, null);
                        }
                        // ~ 14469
                    }
                }
            }
        }

        return super.delete();
    }

}
