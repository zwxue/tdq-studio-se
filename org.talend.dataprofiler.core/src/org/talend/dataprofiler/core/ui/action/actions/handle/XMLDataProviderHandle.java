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
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class XMLDataProviderHandle extends EMFResourceHandle {

    /**
     * DOC bZhou XMLDataProviderHandle constructor comment.
     * 
     * @param file
     */
    public XMLDataProviderHandle(IFile file) {
        super(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.EMFResourceHandle#delete(boolean)
     */
    @Override
    public boolean delete(boolean isPhysical) throws Exception {

        if (isPhysical) {
            TypedReturnCode<Connection> trc = PrvResourceFileHelper.getInstance().findProvider(file);
            MDMConnection mdmConnection = SwitchHelpers.MDMCONNECTION_SWITCH.doSwitch(trc.getObject());
            if (mdmConnection != null) {
                EList<Package> packages = mdmConnection.getDataPackage();
                if (packages != null && packages.size() > 0) {
                    TdXMLDocument tdXmlDocument = SwitchHelpers.XMLDOCUMENT_SWITCH.doSwitch(packages.get(0));
                    if (tdXmlDocument != null) {
                        String xsdFilePath = tdXmlDocument.getXsdFilePath();
                        int indexOf = xsdFilePath.indexOf("/", xsdFilePath.indexOf("/") + 1);
                        IFolder folder = ResourceManager.getMDMConnectionFolder().getFolder(xsdFilePath.substring(0, indexOf));
                        folder.delete(true, null);
                    }
                }
            }
        }

        return super.delete(isPhysical);
    }

}
