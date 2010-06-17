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

import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.writer.impl.DQRuleWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class CreateElementPropertiesTask extends AWorkspaceTask {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.AWorkspaceTask#valid()
     */
    @Override
    public boolean valid() {
        return !DQStructureManager.getInstance().isNeedCreateStructure() && super.valid();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        ModelElement[] allElements = ModelElementFileFactory.getALLElements(false);

        for (ModelElement element : allElements) {
            String propertyPath = MetadataHelper.getPropertyPath(element);

            if (propertyPath == null) {
                DQRuleWriter writer = ElementWriterFactory.getInstance().createdRuleWriter();
                writer.savePerperties(element);
            } else {
                IFile propertyFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(propertyPath));
                if (!propertyFile.exists()) {
                    DQRuleWriter writer = ElementWriterFactory.getInstance().createdRuleWriter();
                    writer.savePerperties(element);
                }
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2009, 10, 13);
    }

}
