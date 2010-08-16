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
package org.talend.dataprofiler.core.migration.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * 
 * DOC zshen class global comment. Detailled comment change the properties "FileName" to a Reference of Connection
 */
public class ExchangeFileNameToReferenceTask extends AWorkspaceTask {

    protected static Logger log = Logger.getLogger(ExchangeFileNameToReferenceTask.class);

    private static final String DB_CONNECTION = "TDQ_Metadata/DB Connections";

    private static final String MDM_CONNECTION = "TDQ_Metadata/MDM Connections";

    public ExchangeFileNameToReferenceTask() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean doExecute() throws Exception {
        boolean returnFlag = true;
        IFolder tDQDbFolder = ResourceManager.getRootProject().getFolder(new Path(DB_CONNECTION));
        IFolder tDQMdmFolder = ResourceManager.getRootProject().getFolder(new Path(MDM_CONNECTION));
        IFolder dbFolder = ResourceManager.getConnectionFolder();
        IFolder mdmFolder = ResourceManager.getMDMConnectionFolder();
        List<IResource> resources = new ArrayList<IResource>();
        try {
            if (tDQDbFolder != null && tDQDbFolder.exists()) {
                resources.addAll(Arrays.asList(tDQDbFolder.members()));
            }
            if (tDQMdmFolder != null && tDQMdmFolder.exists()) {
                resources.addAll(Arrays.asList(tDQMdmFolder.members()));
            }
        } catch (CoreException e1) {
            log.error(e1, e1);
            returnFlag = false;
        }
        if (resources.size() > 0) {
            for (IResource resource : resources) {
                if (resource instanceof IFile && FactoriesUtil.PROPERTIES_EXTENSION.equals(resource.getFileExtension())) {
                    IFile file = (IFile) resource;
                    Property property = PropertyHelper.getProperty(file);

                    Item item = property.getItem();
                    if (item instanceof TDQItem) {

                        IFile theFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
                                file.getFullPath().removeFileExtension().addFileExtension(FactoriesUtil.PROV));
                        TypedReturnCode<Connection> returnCode = PrvResourceFileHelper.getInstance().findProvider(theFile);
                        if (returnCode.isOk()) {
                            PrvResourceFileHelper.getInstance().save(returnCode.getObject());
                        }
                    } else {
                        continue;
                    }

                }

                // tDQDbFolder.move(dbFolder.getFullPath(), true, null);
                // tDQMdmFolder.move(mdmFolder.getFullPath(), true, null);

            }
            for (IResource theResource : tDQDbFolder.members()) {
                theResource.move(dbFolder.getFullPath().append(theResource.getName()), true, null);
            }
            for (IResource theResource : tDQMdmFolder.members()) {
                theResource.move(mdmFolder.getFullPath().append(theResource.getName()), true, null);
            }

        }
        return returnFlag;
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    public Date getOrder() {
        return createDate(2010, 8, 13);
    }

}
