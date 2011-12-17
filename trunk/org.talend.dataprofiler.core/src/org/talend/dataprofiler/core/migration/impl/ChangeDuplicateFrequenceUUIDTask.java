// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceService;


/**
 * DOC zshen  class global comment. Detailled comment
 */
public class ChangeDuplicateFrequenceUUIDTask extends AbstractWorksapceUpdateTask {

    Logger log = Logger.getLogger(ChangeDuplicateFrequenceUUIDTask.class);
    /* (non-Javadoc)
     * @see org.talend.dataprofiler.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2011, 11, 28);
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
        boolean result = true;
         File indicatorfileIndicator = getWorkspacePath()
         .append(EResourceConstant.SYSTEM_INDICATORS_ADVANCED_STATISTICS.getPath()).toFile();
         File anafileIndicator = getWorkspacePath().append(EResourceConstant.ANALYSIS.getPath()).toFile();
         try {
         String[] indicatorFileExtentionNames = { FactoriesUtil.DEFINITION, FactoriesUtil.PROPERTIES_EXTENSION };
         String[] anaFileExtentionNames = { FactoriesUtil.ANA };
         Map<String, String> indicatorStringMap=new HashMap<String,String>();
         indicatorStringMap.putAll(initIndicatorReplaceMap());
         indicatorStringMap.putAll(initAnaReplaceMap());
         result &= FilesUtils.migrateFolder(anafileIndicator, anaFileExtentionNames, initAnaReplaceMap(), log)
         && FilesUtils.migrateFolder(indicatorfileIndicator, indicatorFileExtentionNames, indicatorStringMap,
         log);
         if (isWorksapcePath()) {
         for (IRepositoryViewObject viewObject : ProxyRepositoryFactory.getInstance().getAll(
         ERepositoryObjectType.SYSTEM_INDICATORS_ADVANCED_STATISTICS)) {
         ProxyRepositoryFactory.getInstance().reload(viewObject.getProperty());
         }
         }
            // FolderHelper folderHelper =
            // getFolderHelper(ProjectManager.getInstance().getCurrentProject().getEmfProject());
            // folderHelper.getFolder(((IFolder) objectFolder)
            //
            // ((org.talend.dataquality.properties.TDQIndicatorDefinitionItem)org.talend.core.repository.model.ProxyRepositoryFactory.getInstance().getAll(org.talend.core.model.repository.ERepositoryObjectType.SYSTEM_INDICATORS_ADVANCED_STATISTICS)
         ResourceService.refreshStructure();
         } catch (Exception e) {
         result = false;
         log.error(e, e);
         }
        return result;
    }


    /**
     * DOC zshen Comment method "initIndicatorReplaceMap".
     */
    private Map<String, String> initIndicatorReplaceMap() {

        Map<String, String> replaceStringMap = new HashMap<String, String>();

        replaceStringMap.put("xmi:id=\"_ccIR4hF2Ed2PKb6nEJEvhw\" name=\"Bin Frequency Table\"",
                "xmi:id=\"_OCCCEBl4EeGMPdtm417kpw\" name=\"Bin Frequency Table\"");
        replaceStringMap.put("xmi:id=\"_-0C00JOtEd2Iyo0dtkB9pA\" name=\"Bin Low Frequency Table\"",
                "xmi:id=\"_LAwpcBl4EeGMPdtm417kpw\" name=\"Bin Low Frequency Table\"");

        return replaceStringMap;
    }

    /**
     * DOC zshen Comment method "initAnaReplaceMap".
     */
    private Map<String, String> initAnaReplaceMap() {
        Map<String, String> replaceStringMap = new HashMap<String, String>();
        replaceStringMap.put("Bin_Low_Frequency_Table_0.1.definition#_-0C00JOtEd2Iyo0dtkB9pA",
                "Bin_Low_Frequency_Table_0.1.definition#_LAwpcBl4EeGMPdtm417kpw");
        replaceStringMap.put("Bin_Frequency_Table_0.1.definition#_ccIR4hF2Ed2PKb6nEJEvhw",
                "Bin_Frequency_Table_0.1.definition#_OCCCEBl4EeGMPdtm417kpw");
        return replaceStringMap;
    }


}
