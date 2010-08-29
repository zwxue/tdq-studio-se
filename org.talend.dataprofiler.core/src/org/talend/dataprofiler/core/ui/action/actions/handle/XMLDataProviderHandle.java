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

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class XMLDataProviderHandle {

    IRepositoryViewObject repositoryViewObj = null;
    /**
     * DOC bZhou XMLDataProviderHandle constructor comment.
     * 
     * @param file
     */
    XMLDataProviderHandle(IRepositoryViewObject reposObj) {
        repositoryViewObj = reposObj;
    }

    public boolean delete(boolean isPhysical) throws Exception {

        if (isPhysical) {
            Connection connection = ((ConnectionItem) repositoryViewObj.getProperty().getItem()).getConnection();
            MDMConnection mdmConnection = SwitchHelpers.MDMCONNECTION_SWITCH.doSwitch(connection);
            if (mdmConnection != null) {
                EList<Package> packages = mdmConnection.getDataPackage();
                if (packages != null && packages.size() > 0) {
                    TdXmlSchema tdXmlDocument = SwitchHelpers.XMLSCHEMA_SWITCH.doSwitch(packages.get(0));
                    if (tdXmlDocument != null) {
                        // MOD xqliu 2010-08-09 bug 14469
                        ResourceManager.getMDMConnectionFolder().getFile(tdXmlDocument.getXsdFilePath()).getParent().delete(true,
                                null);
                        // ~ 14469
                    }
                }
            }
        }
        return true;
    }

}
