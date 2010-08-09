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

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.resource.ResourceManager;


/**
 * DOC xqliu  class global comment. Detailled comment
 */
public class RefactMdmMetadataTask extends AWorkspaceTask {

    private static Logger log = Logger.getLogger(RefactMdmMetadataTask.class);

    private Map<String, String> replaceStringMapMdm;

    public Map<String, String> getReplaceStringMapMdm() {
        if (this.replaceStringMapMdm == null) {
            this.replaceStringMapMdm = initReplaceStringMapMdm();
        }
        return this.replaceStringMapMdm;
    }

    /**
     * DOC xqliu Comment method "initReplaceStringMapMdm".
     * 
     * @return
     */
    private Map<String, String> initReplaceStringMapMdm() {
        Map<String, String> result = new HashMap<String, String>();
        result.put("TdXMLDocument", "TdXmlSchema");
        result.put("TdXMLElement", "TdXmlElementType");
        return result;
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        boolean result = true;

        File fileMdmConnection = new File(ResourceManager.getMDMConnectionFolder().getRawLocationURI());
        File fileAnalysis = new File(ResourceManager.getAnalysisFolder().getRawLocationURI());

        try {
            String[] mdmFileExtentionNames = { ".prv" };
            String[] anaFileExtentionNames = { ".ana" };
            result = FilesUtils.migrateFolder(fileMdmConnection, mdmFileExtentionNames, this.getReplaceStringMapMdm(), log)
                    && FilesUtils.migrateFolder(fileAnalysis, anaFileExtentionNames, this.getReplaceStringMapMdm(), log);
        } catch (Exception e) {
            result = false;
            log.error(e, e);
        }

        return result;
    }


    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2010, 8, 9);
    }

}
