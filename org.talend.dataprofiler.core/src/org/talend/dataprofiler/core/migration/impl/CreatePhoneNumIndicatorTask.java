// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataprofiler.core.migration.helper.TalendDefinitionFileUpdate;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class CreatePhoneNumIndicatorTask extends AbstractWorksapceUpdateTask {

    String separator = System.getProperty("line.separator"); //$NON-NLS-1$

    String phoneNumStr = " <categories xmi:id=\"_Ohyz4bEEEeCVE-ofo1XCug\" name=\"Phone Number Statistics\" label=\"Phone Number Statistics\">\r\n"
            + "    <taggedValue xmi:id=\"_ZSFGoLEEEeCVE-ofo1XCug\" tag=\"Description\" value=\"do some phone number statistics by several counting methods\"/>\r\n"
            + "    <taggedValue xmi:id=\"_rEFIoLEEEeCVE-ofo1XCug\" tag=\"Purpose\" value=\"evaluate different phone number of records\"/>\r\n"
            + "  </categories>";

    String endLine = "</dataquality.indicators.definition:IndicatorsDefinitions>"; //$NON-NLS-1$

    private static Logger log = Logger.getLogger(CreatePhoneNumIndicatorTask.class);

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2011, 10, 25);
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        TalendDefinitionFileUpdate talendDefinitionFileUpdate = new TalendDefinitionFileUpdate();
        if (-1 == talendDefinitionFileUpdate.indexOf("name=\"Phone Number Statistics\"")) {
            talendDefinitionFileUpdate.add(endLine, phoneNumStr + separator + endLine);
            talendDefinitionFileUpdate.replace(this.getClass().getName());
            IPath definitionPath = ResourceManager.getLibrariesFolder().getFullPath().append(".Talend.definition");
            URI uri = URI.createPlatformResourceURI(definitionPath.toString(), false);
            EMFSharedResources.getInstance().reloadResource(uri);
            String name = "Phone Number Statistics";
            IFolder folder = ResourceManager.getSystemIndicatorFolder().getFolder(name);
            if (!folder.exists()) {
                copyToPhoneNumFolder(folder);
            }
            DefinitionHandler.getInstance().reloadIndicatorsDefinitions();
        }

        return true;
    }

    /**
     * 
     * copy the definition files and create the realted property file into workspace.
     * 
     * @param folder
     */
    private void copyToPhoneNumFolder(IFolder folder) {

        try {
            DQStructureManager manager = DQStructureManager.getInstance();
                folder.create(false, true, null);
            manager.copyFilesToFolder(CorePlugin.getDefault(), new Path("/indicators/Phone Number Statistics").toString(), false,
                    folder, null);

        } catch (Exception e1) {
            log.error("do migration for Phone Number Indicator failed:", e1);
        }
    }

}
