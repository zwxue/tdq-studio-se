// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis.parameters;

import java.util.HashMap;

import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataquality.analysis.AnalysisType;


/**
 * DOC zqin  class global comment. Detailled comment
 * <br/>
 *
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 *
 */
public class ConnectionParameter {

    private HashMap<String, String> metadate;

    private FolderProvider folderProvider;
    
    /**
     * Sets the analysisMetadate.
     * @param analysisMetadate the analysisMetadate to set
     */
    public void setMetadate(HashMap<String, String> analysisMetadate) {
        this.metadate = analysisMetadate;
    }
    
    public String getName() {
        
        return metadate.get(IParameterConstant.ANALYSIS_NAME);
    }
    
    public String getPurpose() {
        
        return metadate.get(IParameterConstant.ANALYSIS_PURPOSE);
    }
    
    public String getDescription() {
        
        return metadate.get(IParameterConstant.ANALYSIS_DESCRIPTION);
    }

    public String getAuthor() {
        
        return metadate.get(IParameterConstant.ANALYSIS_AUTHOR);
    }
    
    public String getStatus() {
        
        return metadate.get(IParameterConstant.ANALYSIS_STATUS);
    }

    public String getVersion() {
        
        return metadate.get(IParameterConstant.ANALYSIS_VERSION);
    }
    
    /**
     * Getter for folderProvider.
     * @return the folderProvider
     */
    public FolderProvider getFolderProvider() {
        return this.folderProvider;
    }


    
    /**
     * Sets the folderProvider.
     * @param folderProvider the folderProvider to set
     */
    public void setFolderProvider(FolderProvider folderProvider) {
        this.folderProvider = folderProvider;
    }

}
